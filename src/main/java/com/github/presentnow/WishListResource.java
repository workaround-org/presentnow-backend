package com.github.presentnow;

import com.github.presentnow.actions.WishListUpdateAction;
import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.ActiveWishList;
import com.github.presentnow.entity.WishList;
import com.github.presentnow.entity.WishListUpdate;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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

	@Inject
	WishListUpdateAction wishListUpdateAction;

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

	@PUT
	@Path("{id}")
	@Transactional
	public WishListUpdate updateWishList(@PathParam("id") UUID id, WishListUpdate updatedList)
	{
		return wishListUpdateAction.run(id, updatedList);
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public void deleteWishList(@PathParam("id") UUID id)
	{
		WishList existingList = wishListRepository.find("id", id).firstResult();
		if (existingList == null)
		{
			throw new NotFoundException("Wish list not found");
		}

		// Only allow the owner to delete their list
		if (!DUMMY_USER.equals(existingList.getUsername()))
		{
			throw new ForbiddenException("Not authorized to delete this list");
		}

		// Instead of hard delete, mark as inactive for data integrity
		existingList.setActive(false);
		wishListRepository.persist(existingList);
	}
}
