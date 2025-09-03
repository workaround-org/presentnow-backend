package com.github.presentnow.entity;

import java.util.UUID;

public class ActiveWishList
{
	private final UUID id;
	private final String name;
	private final String description;

	public ActiveWishList(WishList wishList)
	{
		this.id = wishList.getId();
		this.name = wishList.getName();
		this.description = wishList.getDescription();
	}

	public UUID getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}
}
