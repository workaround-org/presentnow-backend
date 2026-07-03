package com.github.presentnow;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

/**
 * Native-compatible magic link tests. Runs against the packaged application
 * (native image in CI), so no injection or mocking is available. The mailbox
 * of the packaged app cannot be inspected, therefore the verify flow uses a
 * token seeded via import.sql instead of one requested through the mailer.
 *
 * Covers the runtime paths that are sensitive in a native image: SecureRandom
 * token generation, SHA-256 hashing, JWT signing and cookie-based JWT
 * verification.
 */
@QuarkusIntegrationTest
public class MagicLinkFlowIT
{
	private static final String AUTH_PATH = "/public/auth";
	private static final String LISTS_PATH = "/lists";
	private static final String COOKIE_NAME = "presentnow_session";
	// Matches the hash seeded for 'seeded-it@example.com' in import.sql
	private static final String SEEDED_TOKEN = "native-it-magic-token-4XxUvxDbFyJ9";

	@Test
	void requestMagicLinkAccepted()
	{
		given()
			.when()
			.body("{\"email\":\"native-it@example.com\"}")
			.contentType(ContentType.JSON)
			.post(AUTH_PATH + "/magic-link")
			.then()
			.statusCode(202);
	}

	@Test
	void requestMagicLinkInvalidEmail()
	{
		given()
			.when()
			.body("{\"email\":\"not-an-email\"}")
			.contentType(ContentType.JSON)
			.post(AUTH_PATH + "/magic-link")
			.then()
			.statusCode(400);
	}

	@Test
	void fullSessionFlowWithSeededToken()
	{
		String sessionJwt = given()
			.when()
			.body("{\"token\":\"" + SEEDED_TOKEN + "\"}")
			.contentType(ContentType.JSON)
			.post(AUTH_PATH + "/magic-link/verify")
			.then()
			.statusCode(204)
			.extract()
			.cookie(COOKIE_NAME);

		given()
			.when()
			.cookie(COOKIE_NAME, sessionJwt)
			.body("{\"name\":\"Native IT List\",\"description\":\"created via seeded magic link session\"}")
			.contentType(ContentType.JSON)
			.post(LISTS_PATH)
			.then()
			.statusCode(200)
			.body("username", is("email|seeded-it@example.com"))
			.body("displayName", is("seeded-it"));

		// A magic link token is single use
		given()
			.when()
			.body("{\"token\":\"" + SEEDED_TOKEN + "\"}")
			.contentType(ContentType.JSON)
			.post(AUTH_PATH + "/magic-link/verify")
			.then()
			.statusCode(401);
	}

	@Test
	void verifyUnknownToken()
	{
		given()
			.when()
			.body("{\"token\":\"definitely-not-a-valid-token\"}")
			.contentType(ContentType.JSON)
			.post(AUTH_PATH + "/magic-link/verify")
			.then()
			.statusCode(401);
	}

	@Test
	void listsWithoutSessionUnauthorized()
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
