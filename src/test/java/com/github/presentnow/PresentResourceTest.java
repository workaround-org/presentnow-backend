package com.github.presentnow;

import com.github.presentnow.entity.PresentIdea;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(PresentResource.class)
public class PresentResourceTest
{
	public static final int NEXT_ENTITY_ID = 6;

	@Test
	void getPresentById()
	{
		given()
			.when()
			.get("/1")
			.then()
			.statusCode(200)
			.body("name", is("Personalized Star Map"));
	}

	@Test
	void savePresent()
	{
		given()
			.when()
			.contentType(ContentType.JSON)
			.body(getTestPresent())
			.post()
			.then()
			.statusCode(200)
			.body("id", is(NEXT_ENTITY_ID));
	}

	private PresentIdea getTestPresent()
	{
		PresentIdea presentIdea = new PresentIdea();
		presentIdea.setName("Test Present");
		presentIdea.setDescription("Test Present");
		presentIdea.setUrl("https://www.presentnow.com/");
		presentIdea.setImportance(1);
		presentIdea.setListId(2L);
		return presentIdea;
	}
}
