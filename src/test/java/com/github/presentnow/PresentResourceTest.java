package com.github.presentnow;

import com.github.presentnow.entity.PresentIdea;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(PresentResource.class)
public class PresentResourceTest
{
	@Test
	void getPresentById()
	{
		given()
			.when()
			.get("11111111-1111-1111-1111-111111111111")
			.then()
			.statusCode(200)
			.body("name", is("Personalized Star Map"));
	}

	@Test
	void getPresentByIdInvalidUUID()
	{
		given()
			.when()
			.get("INVALID-UUID")
			.then()
			.statusCode(404);
	}

	@Test
	void getPresentByIdNotFound()
	{
		given()
			.when()
			.get("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
			.then()
			.statusCode(204);
	}

	@Test
	@TestTransaction
	void savePresent()
	{
		given()
			.when()
			.contentType(ContentType.JSON)
			.body(getTestPresent())
			.post()
			.then()
			.statusCode(200);
	}

	@Test
	@TestTransaction
	void updatePresent()
	{
		PresentIdea updatedPresent = new PresentIdea();
		updatedPresent.setName("Updated Present Name");
		updatedPresent.setDescription("Updated Description");
		updatedPresent.setUrl("https://updated.example.com");
		updatedPresent.setImportance(5);
		updatedPresent.setListId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));

		given()
			.when()
			.contentType(ContentType.JSON)
			.body(updatedPresent)
			.put("11111111-1111-1111-1111-111111111111")
			.then()
			.statusCode(200)
			.body("name", is("Updated Present Name"))
			.body("description", is("Updated Description"))
			.body("url", is("https://updated.example.com"))
			.body("importance", is(5));
	}

	@Test
	@TestTransaction
	void updatePresentNotFound()
	{
		PresentIdea updatedPresent = new PresentIdea();
		updatedPresent.setName("Updated Present Name");
		updatedPresent.setDescription("Updated Description");

		given()
			.when()
			.contentType(ContentType.JSON)
			.body(updatedPresent)
			.put("ffffffff-ffff-ffff-ffff-ffffffffffff")
			.then()
			.statusCode(404);
	}

	@Test
	@TestTransaction
	void deletePresent()
	{
		// Delete the present
		given()
			.when()
			.delete("11111111-1111-1111-1111-111111111111")
			.then()
			.statusCode(204);
	}

	@Test
	@TestTransaction
	void deletePresentNotFound()
	{
		given()
			.when()
			.delete("ffffffff-ffff-ffff-ffff-ffffffffffff")
			.then()
			.statusCode(404);
	}

	private PresentIdea getTestPresent()
	{
		PresentIdea presentIdea = new PresentIdea();
		presentIdea.setName("Test Present");
		presentIdea.setDescription("Test Present");
		presentIdea.setUrl("https://www.presentnow.com/");
		presentIdea.setImportance(1);
		presentIdea.setListId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
		return presentIdea;
	}
}
