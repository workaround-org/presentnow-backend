package com.github.presentnow.actions;

import com.github.presentnow.db.MagicLinkTokenRepository;
import com.github.presentnow.entity.MagicLinkToken;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import java.util.regex.Pattern;

@ApplicationScoped
public class RequestMagicLinkAction
{
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final int MAX_MAILS_PER_WINDOW = 3;
	private final SecureRandom random = new SecureRandom();

	@Inject
	MagicLinkTokenRepository magicLinkTokenRepository;

	@Inject
	Mailer mailer;

	@ConfigProperty(name = "com.github.presentnow.auth.frontend-url")
	String frontendUrl;

	@ConfigProperty(name = "com.github.presentnow.auth.magic-link-expiry-minutes")
	long expiryMinutes;

	@Transactional
	public void run(String email)
	{
		if (email == null || !EMAIL_PATTERN.matcher(email).matches())
		{
			throw new BadRequestException("Invalid email address");
		}
		String normalizedEmail = email.trim().toLowerCase();
		long now = System.currentTimeMillis();
		magicLinkTokenRepository.deleteExpired(now);

		long windowStart = now - expiryMinutes * 60_000;
		if (magicLinkTokenRepository.countRecentForEmail(normalizedEmail, windowStart) >= MAX_MAILS_PER_WINDOW)
		{
			// Silently drop to avoid a rate-limit oracle; caller still gets 202
			return;
		}

		byte[] tokenBytes = new byte[32];
		random.nextBytes(tokenBytes);
		String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

		MagicLinkToken token = new MagicLinkToken();
		token.setId(UUID.randomUUID());
		token.setEmail(normalizedEmail);
		token.setTokenHash(sha256Hex(rawToken));
		token.setCreatedAt(now);
		token.setExpiresAt(now + expiryMinutes * 60_000);
		token.setUsed(false);
		magicLinkTokenRepository.persist(token);

		String link = frontendUrl + "/auth/verify?token=" + rawToken;
		mailer.send(Mail.withText(normalizedEmail, "Your PresentNow login link",
			"Hello,\n\nclick the link below to log in to PresentNow. "
				+ "It is valid for " + expiryMinutes + " minutes and can be used once.\n\n"
				+ link + "\n\n"
				+ "If you did not request this email, you can ignore it."));
	}

	public static String sha256Hex(String value)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException("SHA-256 not available", e);
		}
	}
}
