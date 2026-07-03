package com.github.presentnow;

import com.github.presentnow.actions.PresentUpdateAction;
import com.github.presentnow.auth.CurrentUser;
import com.github.presentnow.db.PresentIdeaRepository;
import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.PresentIdea;
import com.github.presentnow.entity.WishList;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Optional;
import java.util.UUID;

@Path("present")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PresentResource
{
	@Inject
	PresentIdeaRepository presentIdeaRepository;

	@Inject
	WishListRepository wishListRepository;

	@Inject
	PresentUpdateAction presentUpdateAction;

	@Inject
	CurrentUser currentUser;

	@POST
	@Transactional
	public PresentIdea savePresentIdea(PresentIdea idea)
	{
		wishListCheck(idea);
		idea.setId(UUID.randomUUID());
		presentIdeaRepository.persist(idea);
		return idea;
	}

	@PUT
	@Path("{id}")
	@Transactional
	public PresentIdea updatePresentIdea(@PathParam("id") UUID id, PresentIdea updatedIdea)
	{
		PresentIdea existingIdea = presentIdeaRepository.find("id", id).firstResult();
		wishListCheck(existingIdea);
		updatedIdea.setId(id);
		boolean isNowClaimed = !existingIdea.isClaimed() && updatedIdea.isClaimed();
		if (isNowClaimed)
		{
			updatedIdea.setClaimerName(currentUser.getDisplayName());
		}
		boolean isNowUnclaimed = existingIdea.isClaimed() && !updatedIdea.isClaimed();
		if (isNowUnclaimed)
		{
			updatedIdea.setClaimerName(null);
		}
		return presentUpdateAction.run(id, updatedIdea);
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public void deletePresentIdea(@PathParam("id") UUID id)
	{
		PresentIdea existingIdea = presentIdeaRepository.find("id", id).firstResult();
		wishListCheck(existingIdea);
		presentIdeaRepository.delete(existingIdea);
	}

	@DELETE
	@Path("{id}/claim")
	@Transactional
	public PresentIdea unclaimPresentIdea(@PathParam("id") UUID id)
	{
		PresentIdea existingIdea = presentIdeaRepository.find("id", id).firstResult();
		wishListCheck(existingIdea);
		existingIdea.setClaimerName(null);
		existingIdea.setClaimed(false);
		presentIdeaRepository.persist(existingIdea);
		return existingIdea;
	}

	private void wishListCheck(PresentIdea idea)
	{
		if (idea == null)
		{
			throw new NotFoundException("Present idea not found");
		}
		Optional<WishList> wishList = wishListRepository.find("id", idea.getListId()).firstResultOptional();
		if (wishList.isEmpty())
		{
			throw new NotFoundException("List not found");
		}
		boolean isOwner = wishList.get().getUsername().equals(currentUser.getSub());
		if (!isOwner && !ConfigUtils.getProfiles().contains("dev"))
		{
			throw new ForbiddenException("You are not owner of this list");
		}
	}
}
