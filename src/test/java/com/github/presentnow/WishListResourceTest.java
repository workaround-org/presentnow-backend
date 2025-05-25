package com.github.presentnow;

import com.github.presentnow.entity.WishList;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(WishListResource.class)
class WishListResourceTest
{
	@Test
	void testHelloEndpoint()
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

	private static WishList getWishList()
	{
		WishList myList = new WishList();
		myList.setId(1L);
		myList.setName("Test List");
		myList.setDescription("Nothing special here!");
		return myList;
	}
}