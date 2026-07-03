package com.github.presentnow;

import com.github.presentnow.actions.RequestMagicLinkAction;
import com.github.presentnow.db.MagicLinkTokenRepository;
import com.github.presentnow.entity.MagicLinkToken;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(AuthResource.class)
class AuthResourceTest
{
	@Inject
	MockMailbox mailbox;

	@Inject
	MagicLinkTokenRepository magicLinkTokenRepository;

	@BeforeEach
	void clearMailbox()
	{
		mailbox.clear();
	}

	@Test
	void requestMagicLinkSendsMail()
	{
		given()
			.when()
			.body("{\"email\":\"alice@example.com\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link")
			.then()
			.statusCode(202);

		List<Mail> mails = mailbox.getMailsSentTo("alice@example.com");
		Assertions.assertEquals(1, mails.size());
		Assertions.assertTrue(mails.get(0).getText().contains("token="));
	}

	@Test
	@TestTransaction
	void requestMagicLinkStoresOnlyHash()
	{
		given()
			.when()
			.body("{\"email\":\"bob@example.com\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link")
			.then()
			.statusCode(202);

		String rawToken = extractToken(mailbox.getMailsSentTo("bob@example.com").get(0));
		MagicLinkToken stored = magicLinkTokenRepository.find("email", "bob@example.com").firstResult();
		Assertions.assertNotNull(stored);
		Assertions.assertNotEquals(rawToken, stored.getTokenHash());
		Assertions.assertFalse(stored.getUsed());
		Assertions.assertTrue(stored.getExpiresAt() > System.currentTimeMillis());
	}

	@Test
	void requestMagicLinkInvalidEmail()
	{
		given()
			.when()
			.body("{\"email\":\"not-an-email\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link")
			.then()
			.statusCode(400);
	}

	@Test
	void requestMagicLinkRateLimited()
	{
		for (int i = 0; i < 4; i++)
		{
			given()
				.when()
				.body("{\"email\":\"ratelimit@example.com\"}")
				.contentType(ContentType.JSON)
				.post("/magic-link")
				.then()
				.statusCode(202);
		}

		List<Mail> mails = mailbox.getMailsSentTo("ratelimit@example.com");
		Assertions.assertEquals(3, mails.size());
	}

	@Test
	void verifyIssuesSessionCookie()
	{
		String rawToken = requestToken("carol@example.com");

		io.restassured.http.Cookie cookie = given()
			.when()
			.body("{\"token\":\"" + rawToken + "\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link/verify")
			.then()
			.statusCode(204)
			.extract()
			.detailedCookie("presentnow_session");

		Assertions.assertNotNull(cookie);
		Assertions.assertFalse(cookie.getValue().isEmpty());
		Assertions.assertTrue(cookie.isHttpOnly());
		Assertions.assertTrue(cookie.isSecured());
		Assertions.assertEquals("Lax", cookie.getSameSite());
	}

	@Test
	void verifyRejectsReusedToken()
	{
		String rawToken = requestToken("dave@example.com");

		given()
			.when()
			.body("{\"token\":\"" + rawToken + "\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link/verify")
			.then()
			.statusCode(204);

		given()
			.when()
			.body("{\"token\":\"" + rawToken + "\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link/verify")
			.then()
			.statusCode(401);
	}

	@Test
	void verifyRejectsExpiredToken()
	{
		String rawToken = "expired-raw-token-for-test";
		io.quarkus.narayana.jta.QuarkusTransaction.requiringNew().run(() -> {
			MagicLinkToken token = new MagicLinkToken();
			token.setId(java.util.UUID.randomUUID());
			token.setEmail("erin@example.com");
			token.setTokenHash(RequestMagicLinkAction.sha256Hex(rawToken));
			token.setCreatedAt(System.currentTimeMillis() - 60 * 60_000);
			token.setExpiresAt(System.currentTimeMillis() - 30 * 60_000);
			token.setUsed(false);
			magicLinkTokenRepository.persist(token);
		});

		given()
			.when()
			.body("{\"token\":\"" + rawToken + "\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link/verify")
			.then()
			.statusCode(401);
	}

	@Test
	void verifyRejectsGarbageToken()
	{
		given()
			.when()
			.body("{\"token\":\"garbage\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link/verify")
			.then()
			.statusCode(401);
	}

	@Test
	void logoutClearsSessionCookie()
	{
		io.restassured.http.Cookie cookie = given()
			.when()
			.post("/logout")
			.then()
			.statusCode(204)
			.extract()
			.detailedCookie("presentnow_session");

		Assertions.assertNotNull(cookie);
		Assertions.assertTrue(cookie.getValue().isEmpty());
		Assertions.assertEquals(0, cookie.getMaxAge());
	}

	private String requestToken(String email)
	{
		given()
			.when()
			.body("{\"email\":\"" + email + "\"}")
			.contentType(ContentType.JSON)
			.post("/magic-link")
			.then()
			.statusCode(202);
		return extractToken(mailbox.getMailsSentTo(email).get(0));
	}

	static String extractToken(Mail mail)
	{
		String text = mail.getText();
		int start = text.indexOf("token=") + "token=".length();
		int end = start;
		while (end < text.length() && !Character.isWhitespace(text.charAt(end)))
		{
			end++;
		}
		return text.substring(start, end);
	}
}
