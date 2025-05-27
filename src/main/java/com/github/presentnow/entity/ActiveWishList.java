package com.github.presentnow.entity;

public class ActiveWishList
{
	private Long id;
	private String name;
	private String description;

	public ActiveWishList(WishList wishList)
	{
		this.id = wishList.getId();
		this.name = wishList.getName();
		this.description = wishList.getDescription();
	}

	public Long getId()
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
