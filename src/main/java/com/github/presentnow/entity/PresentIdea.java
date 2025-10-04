package com.github.presentnow.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PresentIdea
{
	@Id
	private UUID id;
	private UUID listId;
	private String name;
	private String url;
	private String description;
	private int importance;
	private boolean claimed;
	private String claimerName;

	public boolean isClaimed()
	{
		return claimed;
	}

	public void setClaimed(boolean claimed)
	{
		this.claimed = claimed;
	}

	public String getClaimerName()
	{
		return claimerName;
	}

	public void setClaimerName(String claimerName)
	{
		this.claimerName = claimerName;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getListId()
	{
		return listId;
	}

	public void setListId(UUID listId)
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
