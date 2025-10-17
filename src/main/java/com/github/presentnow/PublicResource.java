package com.github.presentnow;

import com.github.presentnow.actions.GetFrontendConfigAction;
import com.github.presentnow.db.PresentIdeaRepository;
import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.FrontendConfig;
import com.github.presentnow.entity.PresentIdea;
import com.github.presentnow.entity.WishList;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.UUID;

@Path("public")
@Produces(MediaType.APPLICATION_JSON)
public class PublicResource
{
	@Inject
	WishListRepository wishListRepository;

	@Inject
	PresentIdeaRepository presentIdeaRepository;

	@Inject
	GetFrontendConfigAction configAction;

	@GET
	@Path("lists/{id}")
	@Operation(summary = "Get wish list by ID")
	@APIResponse(responseCode = "200", description = "Wish list found")
	@APIResponse(responseCode = "404", description = "Wish list not found")
	public WishList getListById(@PathParam("id") UUID id)
	{
		return wishListRepository.find("id", id).firstResult();
	}

	@GET
	@Path("present/{id}")
	@Operation(summary = "Get present idea by ID")
	@APIResponse(responseCode = "200", description = "Present idea found")
	@APIResponse(responseCode = "404", description = "Present idea not found")
	public PresentIdea getPresentByList(@PathParam("id") UUID id)
	{
		return presentIdeaRepository.find("id", id).firstResult();
	}

	@GET
	@Path("config")
	@Operation(summary = "Get frontend configuration")
	@APIResponse(responseCode = "200", description = "Frontend configuration retrieved successfully")
	public FrontendConfig getFrontendConfig()
	{
		return configAction.getFrontendConfig();
	}

	@POST
	@Path("{id}/claim")
	@Transactional
	@Operation(summary = "Claim a present idea using the claimerName in body")
	@APIResponse(responseCode = "200", description = "Present idea claimed successfully")
	@APIResponse(responseCode = "404", description = "Present idea not found")
	public PresentIdea claimPresentIdea(@PathParam("id") UUID id, PresentIdea idea)
	{
		PresentIdea existingIdea = presentIdeaRepository.find("id", id).firstResult();
		if (existingIdea == null)
		{
			throw new NotFoundException("Present idea not found");
		}
		existingIdea.setClaimerName(idea.getClaimerName());
		existingIdea.setClaimed(true);
		presentIdeaRepository.persist(existingIdea);
		return existingIdea;
	}
}