package com.github.presentnow;

import com.github.presentnow.db.WishListRepository;
import com.github.presentnow.entity.WishList;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/lists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WishListResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(WishListResource.class);

	@Inject
	WishListRepository wishListRepository;

	@GET
	public List<WishList> getListsForUser()
	{
		// ToDo: Add Auth
		return wishListRepository.getActive();
	}

	@POST
	@Transactional
	public void saveList(WishList list)
	{
		LOGGER.info("Is persisted? {}", wishListRepository.isPersistent(list));
		WishList myList = new WishList();
		myList.setId(list.getId());
		myList.setName(list.getName());
		myList.setDescription(list.getDescription());
		wishListRepository.persist(myList);
	}
}
