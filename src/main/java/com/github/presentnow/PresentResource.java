package com.github.presentnow;

import com.github.presentnow.actions.PresentUpdateAction;
import com.github.presentnow.db.PresentIdeaRepository;
import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.PresentIdea;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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

	@POST
	@Transactional
	public PresentIdea savePresentIdea(PresentIdea idea)
	{
		boolean isValidWishList = wishListRepository.find("id", idea.getListId()) == null;
		if (isValidWishList)
		{
			throw new NotFoundException("List not found");
		}
		idea.setId(UUID.randomUUID());
		presentIdeaRepository.persist(idea);
		return idea;
	}

	@GET
	@Path("{id}")
	public PresentIdea getPresentByList(@PathParam("id") UUID id)
	{
		return presentIdeaRepository.find("id", id).firstResult();
	}

	@PUT
	@Path("{id}")
	@Transactional
	public PresentIdea updatePresentIdea(@PathParam("id") UUID id, PresentIdea updatedIdea)
	{
		return presentUpdateAction.run(id, updatedIdea);
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public void deletePresentIdea(@PathParam("id") UUID id)
	{
		PresentIdea existingIdea = presentIdeaRepository.find("id", id).firstResult();
		if (existingIdea == null)
		{
			throw new NotFoundException("Present idea not found");
		}
		presentIdeaRepository.delete(existingIdea);
	}
}
