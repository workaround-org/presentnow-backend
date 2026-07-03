package com.github.presentnow;

import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class MagicLinkFlowTest
{
	// RestAssured base path already contains quarkus.http.root-path in @QuarkusTest
	private static final String AUTH_PATH = "/public/auth";
	private static final String LISTS_PATH = "/lists";
	private static final String COOKIE_NAME = "presentnow_session";

	@Inject
	MockMailbox mailbox;

	@BeforeEach
	void clearMailbox()
	{
		mailbox.clear();
	}

	@Test
	void fullMagicLinkLoginFlow()
	{
		given()
			.when()
			.body("{\"email\":\"frank@example.com\"}")
			.contentType(ContentType.JSON)
			.post(AUTH_PATH + "/magic-link")
			.then()
			.statusCode(202);

		String rawToken = AuthResourceTest.extractToken(mailbox.getMailsSentTo("frank@example.com").get(0));

		String sessionJwt = given()
			.when()
			.body("{\"token\":\"" + rawToken + "\"}")
			.contentType(ContentType.JSON)
			.post(AUTH_PATH + "/magic-link/verify")
			.then()
			.statusCode(204)
			.extract()
			.cookie(COOKIE_NAME);

		given()
			.when()
			.cookie(COOKIE_NAME, sessionJwt)
			.body("{\"name\":\"Email User List\",\"description\":\"created via magic link session\"}")
			.contentType(ContentType.JSON)
			.post(LISTS_PATH)
			.then()
			.statusCode(200)
			.body("username", is("email|frank@example.com"))
			.body("displayName", is("frank"));
	}

	@Test
	void listsRequireAuthentication()
	{
		given()
			.when()
			.body("{\"name\":\"No Auth List\"}")
			.contentType(ContentType.JSON)
			.post(LISTS_PATH)
			.then()
			.statusCode(401);
	}
}
