package com.github.presentnow.actions;

import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.WishList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

import static com.github.presentnow.WishListResource.DUMMY_USER;

@ApplicationScoped
public class WishListUpdateAction
{
	@Inject
	WishListRepository wishListRepository;

	public WishList run(UUID id, WishList updatedList)
	{
		WishList existingList = wishListRepository.find("id", id).firstResult();
		if (existingList == null)
		{
			throw new NotFoundException("Wish list not found");
		}

		// Only allow the owner to update their list
		if (!existingList.getUsername().equals(DUMMY_USER))
		{
			throw new ForbiddenException("Not authorized to update this list");
		}

		// Update fields
		if (updatedList.getName() != null)
		{
			existingList.setName(updatedList.getName());
		}
		if (updatedList.getDescription() != null)
		{
			existingList.setDescription(updatedList.getDescription());
		}
		if (updatedList.getExpires() != null)
		{
			existingList.setExpires(updatedList.getExpires());
		}
		if (updatedList.getActive() != null)
		{
			existingList.setActive(updatedList.getActive());
		}

		wishListRepository.persist(existingList);
		return existingList;
	}
}
