package com.github.presentnow;

import com.github.presentnow.actions.RequestMagicLinkAction;
import com.github.presentnow.actions.VerifyMagicLinkAction;
import com.github.presentnow.entity.MagicLinkRequest;
import com.github.presentnow.entity.MagicLinkVerify;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("public/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource
{
	@Inject
	RequestMagicLinkAction requestMagicLinkAction;

	@Inject
	VerifyMagicLinkAction verifyMagicLinkAction;

	@ConfigProperty(name = "com.github.presentnow.auth.cookie-name")
	String cookieName;

	@ConfigProperty(name = "com.github.presentnow.auth.session-expiry-days")
	long sessionExpiryDays;

	@POST
	@Path("magic-link")
	@Operation(summary = "Request a magic login link via email", description = "Always returns 202 to avoid leaking whether an address is rate limited")
	@APIResponse(responseCode = "202", description = "Request accepted, email sent if allowed")
	@APIResponse(responseCode = "400", description = "Invalid email address")
	public Response requestMagicLink(MagicLinkRequest request)
	{
		requestMagicLinkAction.run(request == null ? null : request.email());
		return Response.accepted().build();
	}

	@POST
	@Path("magic-link/verify")
	@Operation(summary = "Exchange a magic link token for a session cookie")
	@APIResponse(responseCode = "204", description = "Session cookie set")
	@APIResponse(responseCode = "401", description = "Token invalid, expired or already used")
	public Response verifyMagicLink(MagicLinkVerify verify)
	{
		String jwt = verifyMagicLinkAction.run(verify == null ? null : verify.token());
		return Response.noContent()
			.cookie(sessionCookie(jwt, (int) (sessionExpiryDays * 24 * 3600)))
			.build();
	}

	@POST
	@Path("logout")
	@Consumes(MediaType.WILDCARD)
	@Operation(summary = "Clear the magic link session cookie")
	@APIResponse(responseCode = "204", description = "Session cookie cleared")
	public Response logout()
	{
		return Response.noContent()
			.cookie(sessionCookie("", 0))
			.build();
	}

	private NewCookie sessionCookie(String value, int maxAge)
	{
		return new NewCookie.Builder(cookieName)
			.value(value)
			.path("/")
			.httpOnly(true)
			.secure(true)
			.sameSite(NewCookie.SameSite.LAX)
			.maxAge(maxAge)
			.build();
	}
}
