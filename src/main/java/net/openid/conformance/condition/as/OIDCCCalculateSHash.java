package net.openid.conformance.condition.as;

import com.nimbusds.jose.util.Base64URL;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CalculateSHash gets state from authorization_request_object
 * this one gets it from authorization_endpoint_request
 */
public class OIDCCCalculateSHash extends AbstractCondition {

	@Override
	@PreEnvironment(strings = "signing_algorithm", required = "authorization_endpoint_request")
	@PostEnvironment(strings = "s_hash")
	public Environment evaluate(Environment env) {

		String algorithm = env.getString("signing_algorithm");
		String state = env.getString("authorization_endpoint_request", "params.state");


		MessageDigest digester;

		try {
			Matcher matcher = Pattern.compile("^(HS|RS|ES|PS)(256|384|512)$").matcher(algorithm);
			if (!matcher.matches()) {
				throw error("Invalid algorithm", args("algorithm", algorithm));
			}

			String digestAlgorithm = "SHA-" + matcher.group(2);
			digester = MessageDigest.getInstance(digestAlgorithm);

		} catch (NoSuchAlgorithmException e) {
			throw error("Unsupported digest for algorithm", e, args("alg", algorithm));
		}

		byte[] stateDigest = digester.digest(state.getBytes(StandardCharsets.US_ASCII));

		byte[] halfDigest = new byte[stateDigest.length / 2];
		System.arraycopy(stateDigest, 0, halfDigest, 0, halfDigest.length);

		String hashValue = Base64URL.encode(halfDigest).toString();

		env.putString("s_hash", hashValue);

		logSuccess("Successful s_hash encoding", args("s_hash", hashValue));

		return env;
	}

}
