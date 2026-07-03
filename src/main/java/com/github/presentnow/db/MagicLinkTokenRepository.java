package com.github.presentnow.db;

import com.github.presentnow.entity.MagicLinkToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

@ApplicationScoped
public class MagicLinkTokenRepository implements PanacheRepository<MagicLinkToken>
{

	public MagicLinkToken findValidByHash(String tokenHash, long now)
	{
		return find("tokenHash = ?1 and used = false and expiresAt > ?2", tokenHash, now)
			.withLock(LockModeType.PESSIMISTIC_WRITE)
			.firstResult();
	}

	public long countRecentForEmail(String email, long since)
	{
		return count("email = ?1 and createdAt > ?2", email, since);
	}

	public long deleteExpired(long now)
	{
		return delete("expiresAt <= ?1", now);
	}
}
