package com.github.presentnow;

import com.github.presentnow.entity.PresentIdea;
import io.quarkus.oidc.UserInfo;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
@TestHTTPEndpoint(PresentResource.class)
@TestSecurity(user = "user")
public class PresentResourceTest
{
	@BeforeAll
	public static void setup()
	{
		String sub = "test-user";
		String username = "Test User";
		mockUserInfo(sub, username);
	}

	private static void mockUserInfo(String sub, String username)
	{
		UserInfo mock = Mockito.mock(UserInfo.class);
		Mockito.when(mock.getSubject()).thenReturn(sub);
		Mockito.when(mock.getName()).thenReturn(username);
		QuarkusMock.installMockForType(mock, UserInfo.class);
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

	@Test
	@TestTransaction
	void unclaimPresent()
	{
		// Now unclaim it
		given()
			.when()
			.contentType(ContentType.JSON)
			.delete("33333333-3333-3333-3333-333333333333/claim")
			.then()
			.statusCode(200)
			.body("claimed", is(false))
			.body("claimerName", is(nullValue()));
	}

	@Test
	@TestTransaction
	void unclaimPresentForbidden()
	{
		mockUserInfo("bob", "Bob");

		// Now try unclaim it
		given()
			.when()
			.contentType(ContentType.JSON)
			.delete("33333333-3333-3333-3333-333333333333/claim")
			.then()
			.statusCode(403);
	}

	@Test
	@TestTransaction
	void createPresentWithDefaultClaimedFlag()
	{
		PresentIdea newPresent = new PresentIdea();
		newPresent.setName("New Present");
		newPresent.setDescription("New Present Description");
		newPresent.setUrl("https://www.example.com/");
		newPresent.setImportance(3);
		newPresent.setListId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));

		given()
			.when()
			.contentType(ContentType.JSON)
			.body(newPresent)
			.post()
			.then()
			.statusCode(200)
			.body("claimed", is(false))
			.body("claimerName", is(nullValue()));
	}

	@Test
	@TestTransaction
	void unclaimPresentNotFound()
	{
		given()
			.when()
			.contentType(ContentType.JSON)
			.delete("ffffffff-ffff-ffff-ffff-ffffffffffff/claim")
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
