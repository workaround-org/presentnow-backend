package com.github.presentnow;

import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.WishList;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestHTTPEndpoint(WishListResource.class)
class WishListResourceTest
{
	public static final long NEW_TEST_ID = 3L;

	@Inject
	WishListRepository wishListRepository;

	@AfterEach
	@Transactional
	void cleanUp()
	{
		wishListRepository.deleteById(NEW_TEST_ID);
	}

	@Test
	void testCreateWishList()
	{
		WishList myList = getWishList();

		Response response = given()
			.when()
			.body(myList)
			.contentType(ContentType.JSON)
			.post();

		response.then().statusCode(200);
		WishList wishList = response.body().as(WishList.class);

		assertEquals(NEW_TEST_ID, wishList.getId());
	}

	@Test
	void testGetWishListsByUser()
	{
		Response response = given()
			.when()
			.get();

		response.then().statusCode(200);
		WishList[] wishList = response.body().as(WishList[].class);

		int expectedLists = 2;
		assertEquals(expectedLists, wishList.length);
	}

	@Test
	void testGetWishListById()
	{
		long testId = 2;
		String testTitle = "Birthday Gift Wishlist";

		Response response = given()
			.when()
			.get("/" + testId);

		response.then().statusCode(200);
		WishList wishList = response.body().as(WishList.class);

		assertNotNull(wishList);
		assertEquals(testTitle, wishList.getName());
		assertEquals(testId, wishList.getId());
	}

	private static WishList getWishList()
	{
		WishList myList = new WishList();
		myList.setName("Test List");
		myList.setDescription("Nothing special here!");
		return myList;
	}
}