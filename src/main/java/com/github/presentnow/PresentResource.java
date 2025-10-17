package com.github.presentnow;

import com.github.presentnow.actions.PresentUpdateAction;
import com.github.presentnow.db.PresentIdeaRepository;
import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.PresentIdea;
import com.github.presentnow.entity.WishList;
import io.quarkus.oidc.UserInfo;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
	UserInfo userInfo;

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
			updatedIdea.setClaimerName(userInfo.getName());
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
		if (existingIdea == null)
		{
			throw new NotFoundException("Present idea not found");
		}
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
		boolean isOwner = wishList.get().getUsername().equals(userInfo.getSubject());
		if (!isOwner && !ConfigUtils.getProfiles().contains("dev"))
		{
			throw new ForbiddenException("You are not owner of this list");
		}
	}
}
