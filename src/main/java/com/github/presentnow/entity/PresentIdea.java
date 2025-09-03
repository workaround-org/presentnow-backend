package com.github.presentnow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class PresentIdea
{
	@Id
	private UUID id;
	private Long listId;
	private String name;
	private String url;
	private String description;
	private int importance;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public Long getListId()
	{
		return listId;
	}

	public void setListId(Long listId)
	{
		this.listId = listId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getImportance()
	{
		return importance;
	}

	public void setImportance(int importance)
	{
		this.importance = importance;
	}
}
