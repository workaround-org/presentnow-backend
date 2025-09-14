package com.github.presentnow.actions;

import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.WishList;
import com.github.presentnow.entity.WishListUpdate;
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

	public void run(UUID id, WishListUpdate updatedList)
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
		if (updatedList.name() != null)
		{
			existingList.setName(updatedList.name());
		}
		if (updatedList.description() != null)
		{
			existingList.setDescription(updatedList.description());
		}
		if (updatedList.expires() != null)
		{
			existingList.setExpires(updatedList.expires());
		}
		if (updatedList.active() != null)
		{
			existingList.setActive(updatedList.active());
		}

		wishListRepository.persist(existingList);
	}
}
