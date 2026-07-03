package com.github.presentnow;

import com.github.presentnow.actions.WishListUpdateAction;
import com.github.presentnow.auth.CurrentUser;
import com.github.presentnow.db.PresentIdeaRepository;
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

@Path("lists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WishListResource
{
	@Inject
	WishListRepository wishListRepository;

	@Inject
	PresentIdeaRepository presentIdeaRepository;

	@Inject
	WishListUpdateAction wishListUpdateAction;

	@Inject
	CurrentUser currentUser;

	@GET
	public List<ActiveWishList> getListsForUser()
	{
		return wishListRepository.getActive(getSub()).stream()
			.map(ActiveWishList::new)
			.toList();
	}

	@POST
	@Transactional
	public WishList saveList(WishList list)
	{
		list.setUsername(getSub());
		list.setDisplayName(getUsername());
		list.setActive(true);
		list.setId(UUID.randomUUID());
		wishListRepository.persist(list);
		return list;
	}

	@PUT
	@Path("{id}")
	@Transactional
	public WishListUpdate updateWishList(@PathParam("id") UUID id, WishListUpdate updatedList)
	{
		return wishListUpdateAction.run(id, updatedList, getSub());
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
		boolean isOwner = getSub().equals(existingList.getUsername());
		if (!isOwner)
		{
			throw new ForbiddenException("Not authorized to delete this list");
		}
		wishListRepository.delete(existingList);
		presentIdeaRepository.delete("listId", id);
	}

	private String getSub()
	{
		return currentUser.getSub();
	}

	private String getUsername()
	{
		return currentUser.getDisplayName();
	}
}
