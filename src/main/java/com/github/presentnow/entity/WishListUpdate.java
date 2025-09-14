package com.github.presentnow.entity;

public record WishListUpdate(
	String name,
	String description,
	String username,
	Boolean active,
	Long expires
)
{
}
