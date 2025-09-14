package com.github.presentnow.actions;

import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.WishList;
import com.github.presentnow.entity.WishListUpdate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class WishListUpdateAction
{
	@Inject
	WishListRepository wishListRepository;

	public WishListUpdate run(UUID id, WishListUpdate updateData)
	{
		WishList existingList = wishListRepository.find("id", id).firstResult();
		if (existingList == null)
		{
			throw new NotFoundException("Wish list not found");
		}

		// Update only non-null fields
		if (updateData.name() != null)
		{
			existingList.setName(updateData.name());
		}
		if (updateData.description() != null)
		{
			existingList.setDescription(updateData.description());
		}
		if (updateData.username() != null)
		{
			existingList.setUsername(updateData.username());
		}
		if (updateData.active() != null)
		{
			existingList.setActive(updateData.active());
		}
		if (updateData.expires() != null)
		{
			existingList.setExpires(updateData.expires());
		}

		wishListRepository.persist(existingList);
		return updateData;
	}
}
