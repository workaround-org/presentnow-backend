package com.github.presentnow;

import com.github.presentnow.entity.PresentIdea;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(PublicResource.class)
public class PublicResourceTest
{
	@Test
	void getPresentById()
	{
		given()
			.when()
			.get("present/22222222-2222-2222-2222-222222222222")
			.then()
			.statusCode(200)
			.body("name", is("Wireless Noise-Canceling Headphones"));
	}

	@Test
	void getPresentByIdInvalidUUID()
	{
		given()
			.when()
			.get("present/INVALID-UUID")
			.then()
			.statusCode(404);
	}

	@Test
	void getPresentByIdNotFound()
	{
		given()
			.when()
			.get("present/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
			.then()
			.statusCode(204);
	}

	@Test
	void testGetWishListById()
	{
		String testId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";

		given()
			.when()
			.get("lists/" + testId)
			.then()
			.statusCode(200)
			.body("id", is(testId))
			.body("name", is("Birthday Gift Wishlist"));
	}

	@Test
	@TestTransaction
	void claimPresent()
	{
		PresentIdea claimRequest = new PresentIdea();
		claimRequest.setClaimerName("Test User");

		given()
			.when()
			.contentType(ContentType.JSON)
			.body(claimRequest)
			.post("22222222-2222-2222-2222-222222222222/claim")
			.then()
			.statusCode(200)
			.body("claimed", is(true))
			.body("claimerName", is("Test User"));
	}

	@Test
	@TestTransaction
	void claimPresentNotFound()
	{
		PresentIdea claimRequest = new PresentIdea();
		claimRequest.setClaimerName("Test User");

		given()
			.when()
			.contentType(ContentType.JSON)
			.body(claimRequest)
			.post("ffffffff-ffff-ffff-ffff-ffffffffffff/claim")
			.then()
			.statusCode(404);
	}
}