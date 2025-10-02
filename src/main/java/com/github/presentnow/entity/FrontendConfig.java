package com.github.presentnow.entity;

public record FrontendConfig(
	String authServerUrl,
	String authClientId,
	String adminRole
)
{
}
