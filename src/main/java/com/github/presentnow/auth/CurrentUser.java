package com.github.presentnow.auth;

import io.quarkus.oidc.UserInfo;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Mechanism-agnostic view on the authenticated user. Supports both the
 * Auth0 OIDC bearer flow (UserInfo) and the magic link session JWT cookie.
 */
@RequestScoped
public class CurrentUser
{
	@Inject
	SecurityIdentity securityIdentity;

	@Inject
	Instance<UserInfo> userInfo;

	@ConfigProperty(name = "com.github.presentnow.auth.issuer")
	String magicLinkIssuer;

	public String getSub()
	{
		JsonWebToken sessionJwt = sessionJwt();
		if (sessionJwt != null)
		{
			return sessionJwt.getSubject();
		}
		String username = userInfo.get().getSubject();
		if (username == null && ConfigUtils.getProfiles().contains("dev"))
		{
			username = "test-user";
		}
		return username;
	}

	public String getDisplayName()
	{
		JsonWebToken sessionJwt = sessionJwt();
		if (sessionJwt != null)
		{
			return sessionJwt.getClaim("name");
		}
		String name = userInfo.get().getName();
		if (name == null && ConfigUtils.getProfiles().contains("dev"))
		{
			name = "Test User";
		}
		return name;
	}

	private JsonWebToken sessionJwt()
	{
		if (securityIdentity.getPrincipal() instanceof JsonWebToken jwt
			&& magicLinkIssuer.equals(jwt.getIssuer()))
		{
			return jwt;
		}
		return null;
	}
}
