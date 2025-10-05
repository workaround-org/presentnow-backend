package com.github.presentnow.actions;

import com.github.presentnow.db.PresentIdeaRepository;
import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.PresentIdea;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PathParam;

import java.util.UUID;

@ApplicationScoped
public class PresentUpdateAction
{
	@Inject
	PresentIdeaRepository presentIdeaRepository;

	@Inject
	WishListRepository wishListRepository;

	public PresentIdea run(@PathParam("id") UUID id, PresentIdea updatedIdea)
	{
		PresentIdea existingIdea = presentIdeaRepository.find("id", id).firstResult();
		if (existingIdea == null)
		{
			throw new NotFoundException("Present idea not found");
		}

		// Validate the wishlist if listId is being changed
		if (updatedIdea.getListId() != null && !updatedIdea.getListId().equals(existingIdea.getListId()))
		{
			boolean isValidWishList = wishListRepository.find("id", updatedIdea.getListId()).firstResult() != null;
			if (!isValidWishList)
			{
				throw new NotFoundException("List not found");
			}
		}

		// Update fields
		if (updatedIdea.getName() != null)
		{
			existingIdea.setName(updatedIdea.getName());
		}
		if (updatedIdea.getDescription() != null)
		{
			existingIdea.setDescription(updatedIdea.getDescription());
		}
		if (updatedIdea.getUrl() != null)
		{
			existingIdea.setUrl(updatedIdea.getUrl());
		}
		if (updatedIdea.getListId() != null)
		{
			existingIdea.setListId(updatedIdea.getListId());
		}
		if (updatedIdea.getImportance() != 0)
		{
			existingIdea.setImportance(updatedIdea.getImportance());
		}
		existingIdea.setClaimed(updatedIdea.isClaimed());
		// Always update claimerName when claimed status changes
		// When claiming, it's set to the user's name in PresentResource
		// When unclaiming, it's set to null in PresentResource
		if (!existingIdea.isClaimed() || updatedIdea.getClaimerName() != null)
		{
			existingIdea.setClaimerName(updatedIdea.getClaimerName());
		}

		presentIdeaRepository.persist(existingIdea);
		return existingIdea;
	}
}
