package com.github.presentnow.actions;

import com.github.presentnow.entity.FrontendConfig;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GetFrontendConfigAction
{
	@ConfigProperty(name = "quarkus.oidc.auth-server-url")
	String authServerUrl;

	@ConfigProperty(name = "quarkus.oidc.client-id")
	String authClientId;

	@ConfigProperty(name = "quarkus.http.auth.policy.admin-policy.roles-allowed")
	String adminRole;

	@ConfigProperty(name = "com.github.presentnow.audience")
	String audience;

	@ConfigProperty(name = "com.github.presentnow.searchengine")
	String searchEngine;

	public FrontendConfig getFrontendConfig()
	{
		return new FrontendConfig(
			authServerUrl,
			authClientId,
			adminRole,
			audience,
			searchEngine
		);
	}
}
