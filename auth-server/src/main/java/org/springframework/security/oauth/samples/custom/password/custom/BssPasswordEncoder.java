/*
 * 
 */

package org.springframework.security.oauth.samples.custom.password.custom;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Default {@link PasswordEncoder} for BSS.
 *
 * @author 
 * @since 1.0.0
 */
public class BssPasswordEncoder extends AbstractPasswordEncoder {

	private static final Logger logger = LoggerFactory.getLogger(BssPasswordEncoder.class);

	private final MessageDigest digester;

	public BssPasswordEncoder() {
		super(32);
		MessageDigest digester;
		try {
			digester = MessageDigest.getInstance("SHA3-256");
		}
		catch (NoSuchAlgorithmException e) {
			//try {
			//	digester = MessageDigest.getInstance("SHA3-256", "BC");
			//}
			//catch (NoSuchAlgorithmException | NoSuchProviderException e1) {
			//	throw new RuntimeException(e1);
			//}
			digester = new SHA3.Digest256();
		}
		this.digester = digester;

		// Try unlimited cryptographic policy first
		try {
			this.digester.digest(generateSalt());
		}
		catch (Exception e) {
			//noinspection ConstantConditions
			if (e instanceof InvalidKeyException) {
				throw new RuntimeException(
						"You need enable unlimited cryptographic policy in Oracle JDK.");
			}
		}
	}

	@Override
	public String encode(CharSequence rawPassword) {
		byte[] salt = generateSalt();
		byte[] digested = encode(rawPassword, salt);
		return Base64.getEncoder().encodeToString(digested);
	}

	private byte[] encode(CharSequence rawPassword, byte[] salt) {
		byte[] salt2 = null;
		if (rawPassword instanceof String) {
			String input = (String) rawPassword;
			// Salt again with user id if exists, to prevent password clone by dba.
			if (input.contains("::")) {
				String[] inputs = input.split("::", 2);
				salt2 = this.digester.digest(concatenate(salt, Utf8.encode(inputs[0])));
				rawPassword = inputs[1];
			}
		}

		byte[] digested = this.digester.digest(concatenate(salt2 != null ? salt2 : salt,
				Utf8.encode(rawPassword)));

		return concatenate(salt, digested);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		try {
			byte[] digested = Base64.getDecoder().decode(encodedPassword);
			byte[] salt = EncodingUtils.subArray(digested, 0, getSaltLength());
			return matches(digested, encode(rawPassword, salt));
		}
		catch (Exception e) {
			logger.error(
					"Encoded password is invalid: " + String.valueOf(encodedPassword), e);
			return false;
		}
	}
}
