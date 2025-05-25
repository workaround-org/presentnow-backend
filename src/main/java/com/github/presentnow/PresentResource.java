package com.github.presentnow;

import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.PresentIdea;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("present")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PresentResource
{
	@Inject
	WishListRepository wishListRepository;

	@GET
	public List<PresentIdea> getPresentIdeasByList(@QueryParam("list-id") Long listId)
	{
		return null;
		// return wishListRepository.findById(listId).getPresentIdeas();
	}
}
