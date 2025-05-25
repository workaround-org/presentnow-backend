package com.github.presentnow;

import com.github.presentnow.db.PresentIdeaRepository;
import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.PresentIdea;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("present")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PresentResource
{
	@Inject
	PresentIdeaRepository presentIdeaRepository;

	@Inject
	WishListRepository wishListRepository;

	@GET
	public List<PresentIdea> getPresentIdeasByList(@QueryParam("list-id") Long listId)
	{
		return presentIdeaRepository.list("listId", listId);
	}

	@POST
	@Transactional
	public PresentIdea savePresentIdea(PresentIdea idea)
	{
		boolean isValidWishList = wishListRepository.findById(idea.getListId()) == null;
		if (isValidWishList)
		{
			throw new NotFoundException("List not found");
		}
		idea.setId(null);
		presentIdeaRepository.persist(idea);
		return idea;
	}
}
