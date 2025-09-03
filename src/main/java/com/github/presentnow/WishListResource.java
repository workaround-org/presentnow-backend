package com.github.presentnow;

import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.ActiveWishList;
import com.github.presentnow.entity.WishList;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@Path("/lists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WishListResource
{
	public static final String DUMMY_USER = "dummyUser";

	@Inject
	WishListRepository wishListRepository;

	@GET
	public List<ActiveWishList> getListsForUser()
	{
		// ToDo: Add Auth via Mail
		return wishListRepository.getActive(DUMMY_USER).stream()
			.map(ActiveWishList::new)
			.toList();
	}

	@POST
	@Transactional
	public WishList saveList(WishList list)
	{
		list.setUsername(DUMMY_USER);
		list.setActive(true);
		list.setId(UUID.randomUUID());
		wishListRepository.persist(list);
		return list;
	}

	@GET
	@Path("{id}")
	public WishList getListById(@PathParam("id") UUID id)
	{
		return wishListRepository.find("id", id).firstResult();
	}
}
