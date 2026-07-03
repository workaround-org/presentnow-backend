package com.github.presentnow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class MagicLinkToken
{
	@Id
	private UUID id;
	private String email;
	private String tokenHash;
	private Long createdAt;
	private Long expiresAt;
	private Boolean used;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getTokenHash()
	{
		return tokenHash;
	}

	public void setTokenHash(String tokenHash)
	{
		this.tokenHash = tokenHash;
	}

	public Long getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(Long createdAt)
	{
		this.createdAt = createdAt;
	}

	public Long getExpiresAt()
	{
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt)
	{
		this.expiresAt = expiresAt;
	}

	public Boolean getUsed()
	{
		return used;
	}

	public void setUsed(Boolean used)
	{
		this.used = used;
	}
}
