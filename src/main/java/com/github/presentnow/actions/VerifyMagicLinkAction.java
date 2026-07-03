package com.github.presentnow.actions;

import com.github.presentnow.db.MagicLinkTokenRepository;
import com.github.presentnow.entity.MagicLinkToken;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import java.time.Duration;

@ApplicationScoped
public class VerifyMagicLinkAction
{
	@Inject
	MagicLinkTokenRepository magicLinkTokenRepository;

	@ConfigProperty(name = "com.github.presentnow.auth.issuer")
	String issuer;

	@ConfigProperty(name = "com.github.presentnow.auth.session-expiry-days")
	long sessionExpiryDays;

	@Transactional
	public String run(String rawToken)
	{
		if (rawToken == null || rawToken.isBlank())
		{
			throw new NotAuthorizedException("Invalid magic link token", "Bearer");
		}
		long now = System.currentTimeMillis();
		String tokenHash = RequestMagicLinkAction.sha256Hex(rawToken);
		MagicLinkToken token = magicLinkTokenRepository.findValidByHash(tokenHash, now);
		if (token == null)
		{
			throw new NotAuthorizedException("Invalid magic link token", "Bearer");
		}
		token.setUsed(true);

		String email = token.getEmail();
		String displayName = email.substring(0, email.indexOf('@'));
		return Jwt.issuer(issuer)
			.subject("email|" + email)
			.upn(email)
			.claim(Claims.email, email)
			.claim("name", displayName)
			.expiresIn(Duration.ofDays(sessionExpiryDays))
			.sign();
	}
}
