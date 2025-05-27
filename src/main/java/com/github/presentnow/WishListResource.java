package com.github.presentnow;

import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.ActiveWishList;
import com.github.presentnow.entity.WishList;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/lists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WishListResource
{
	private final String DUMMY_USER = "dummyUser";

	@Inject
	WishListRepository wishListRepository;

	@GET
	public List<ActiveWishList> getListsForUser()
	{
		// ToDo: Add Auth
		return wishListRepository.getActive(DUMMY_USER).stream()
			.map(ActiveWishList::new)
			.toList();
	}

	@GET
	@Path("{id}")
	public WishList getListById(@PathParam("id") Long id)
	{
		return wishListRepository.findById(id);
	}

	@POST
	@Transactional
	public WishList saveList(WishList list)
	{
		list.setUsername(DUMMY_USER);
		list.setActive(true);
		list.setId(null);
		wishListRepository.persist(list);
		return list;
	}
}
