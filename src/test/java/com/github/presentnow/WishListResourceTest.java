package com.github.presentnow;

import com.github.presentnow.entity.WishList;
import com.github.presentnow.entity.WishListUpdate;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(WishListResource.class)
@TestSecurity(user = "test-user")
class WishListResourceTest
{
	@BeforeAll
	public static void setup()
	{
		UserInfo mock = Mockito.mock(UserInfo.class);
		Mockito.when(mock.getSubject()).thenReturn("test-user");
		QuarkusMock.installMockForType(mock, UserInfo.class);
	}

	@Test
	@TestTransaction
	void testCreateWishList()
	{
		WishList myList = getWishList();

		given()
			.when()
			.body(myList)
			.contentType(ContentType.JSON)
			.post()
			.then()
			.statusCode(200);
	}

	@Test
	@TestTransaction
	void testGetWishListsByUser()
	{
		given()
			.when()
			.get()
			.then()
			.statusCode(200)
			.body("size()", is(2));
	}

	@Test
	@TestTransaction
	void testUpdateWishList()
	{
		String testId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
		WishListUpdate updateData = new WishListUpdate(
			"Updated Birthday Wishlist",
			"Updated description for my birthday gifts",
			null,
			null,
			null
		);

		given()
			.when()
			.body(updateData)
			.contentType(ContentType.JSON)
			.put("/" + testId)
			.then()
			.statusCode(200)
			.body("name", is(updateData.name()))
			.body("description", is(updateData.description()));
	}

	@Test
	@TestTransaction
	void testUpdateWishListNameOnly()
	{
		String testId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
		WishListUpdate updateData = new WishListUpdate(
			"Just Name Change",
			null,
			null,
			null,
			null
		);

		given()
			.when()
			.body(updateData)
			.contentType(ContentType.JSON)
			.put("/" + testId)
			.then()
			.statusCode(200)
			.body("name", is(updateData.name()));
	}

	@Test
	@TestTransaction
	void testUpdateWishListDescriptionOnly()
	{
		String testId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
		WishListUpdate updateData = new WishListUpdate(
			null,
			"Just description change",
			null,
			null,
			null
		);

		given()
			.when()
			.body(updateData)
			.contentType(ContentType.JSON)
			.put("/" + testId)
			.then()
			.statusCode(200)
			.body("description", is(updateData.description()));
	}

	@Test
	void testUpdateNonExistentWishList()
	{
		String nonExistentId = "ffffffff-ffff-ffff-ffff-ffffffffffff";
		WishListUpdate updateData = new WishListUpdate(
			"Updated Name",
			"Updated Description",
			null,
			null,
			null
		);

		given()
			.when()
			.body(updateData)
			.contentType(ContentType.JSON)
			.put("/" + nonExistentId)
			.then()
			.statusCode(404);
	}

	@Test
	@TestTransaction
	void testDeleteWishList()
	{
		String testId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

		// First verify the list exists
		given()
			.when()
			// need because for the HTTPTestResource annotation
			.basePath("/api/present-now/v1/public/lists")
			.get(testId)
			.then()
			.statusCode(200)
			.body("id", is(testId));

		// Delete the list
		given()
			.when()
			.delete("/" + testId)
			.then()
			.statusCode(204);

		// Verify the list has been deleted
		given()
			.when()
			// need because for the HTTPTestResource annotation
			.basePath("/api/present-now/v1/public/lists")
			.get(testId)
			.then()
			.statusCode(204);
	}

	@Test
	void testDeleteNonExistentWishList()
	{
		String nonExistentId = "ffffffff-ffff-ffff-ffff-ffffffffffff";

		given()
			.when()
			.delete("/" + nonExistentId)
			.then()
			.statusCode(404);
	}

	private WishList getWishList()
	{
		WishList myList = new WishList();
		myList.setName("Test List");
		myList.setDescription("Nothing special here!");
		return myList;
	}
}