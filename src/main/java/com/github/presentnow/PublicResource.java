package com.github.presentnow;

import com.github.presentnow.db.PresentIdeaRepository;
import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.PresentIdea;
import com.github.presentnow.entity.WishList;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("public")
@Produces(MediaType.APPLICATION_JSON)
public class PublicResource
{
	@Inject
	WishListRepository wishListRepository;

	@Inject
	PresentIdeaRepository presentIdeaRepository;

	@GET
	@Path("lists/{id}")
	public WishList getListById(@PathParam("id") UUID id)
	{
		return wishListRepository.find("id", id).firstResult();
	}

	@GET
	@Path("present/{id}")
	public PresentIdea getPresentByList(@PathParam("id") UUID id)
	{
		return presentIdeaRepository.find("id", id).firstResult();
	}
}
