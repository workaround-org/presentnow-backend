package com.github.presentnow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.UUID;

@Entity
public class WishList
{
	@Id
	private UUID id;
	private String name;
	private String description;
	private String username;
	private String displayName; // prettier name for display
	private Boolean active;
	@OneToMany(mappedBy = "listId")
	private List<PresentIdea> presentIdeas;
	// ToDo: Add cron job to deactivate expired WishLists
	private Long expires;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
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

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public List<PresentIdea> getPresentIdeas()
	{
		return presentIdeas;
	}

	public void setPresentIdeas(List<PresentIdea> presentIdeas)
	{
		this.presentIdeas = presentIdeas;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayname)
	{
		this.displayName = displayname;
	}
}
