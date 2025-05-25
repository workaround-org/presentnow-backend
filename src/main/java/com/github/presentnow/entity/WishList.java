package com.github.presentnow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class WishList
{
	@Id
	@GeneratedValue
	private Long id;

	//@OneToMany
	//private List<PresentIdea> presentIdeas;

	private String name;

	private String description;

	private Boolean active;

	// ToDo: Add cron job to deactivate expired WishLists
	private Long expires;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Boolean getActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	public Long getExpires()
	{
		return expires;
	}

	public void setExpires(Long expires)
	{
		this.expires = expires;
	}
}
