package com.github.presentnow;

import com.github.presentnow.entity.WishList;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(WishListResource.class)
class WishListResourceTest
{
	public static final int NEXT_ENTITY_ID = 3;
	public static final int ENTITIES_IN_DB = 3;

	@Test
	void testCreateWishList()
	{
		WishList myList = getWishList();

		given()
			.when()
			.body(myList)
			.contentType(ContentType.JSON)
			.post()
			.then()
			.statusCode(200)
			.body("id", is(NEXT_ENTITY_ID));
	}

	@Test
	void testGetWishListsByUser()
	{
		given()
			.when()
			.get()
			.then()
			.statusCode(200)
			.body("size()", is(ENTITIES_IN_DB));
	}

	@Test
	void testGetWishListById()
	{
		int testId = 2;

		given()
			.when()
			.get("/" + testId)
			.then()
			.statusCode(200)
			.body("id", is(testId))
			.body("name", is("Birthday Gift Wishlist"));
	}

	private static WishList getWishList()
	{
		WishList myList = new WishList();
		myList.setName("Test List");
		myList.setDescription("Nothing special here!");
		return myList;
	}
}