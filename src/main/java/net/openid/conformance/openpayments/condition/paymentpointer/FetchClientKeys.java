package net.openid.conformance.openpayments.condition.paymentpointer;

	import com.google.common.base.Strings;
	import com.google.gson.JsonObject;
	import com.google.gson.JsonParser;
	import com.google.gson.JsonSyntaxException;
	import net.openid.conformance.condition.AbstractCondition;
	import net.openid.conformance.condition.PostEnvironment;
	import net.openid.conformance.condition.PreEnvironment;
	import net.openid.conformance.testmodule.Environment;
	import org.springframework.http.HttpEntity;
	import org.springframework.http.HttpHeaders;
	import org.springframework.http.HttpMethod;
	import org.springframework.web.client.RestClientException;
	import org.springframework.web.client.RestTemplate;

	import java.io.IOException;
	import java.security.KeyManagementException;
	import java.security.KeyStoreException;
	import java.security.NoSuchAlgorithmException;
	import java.security.UnrecoverableKeyException;
	import java.security.cert.CertificateException;
	import java.security.spec.InvalidKeySpecException;

public class FetchClientKeys extends AbstractCondition {

	@Override
	@PreEnvironment(required = "paymentPointer")
	@PostEnvironment(required = "client_jwks")
	public Environment evaluate(Environment env) {

		String jwksUri = env.getString("paymentPointer", "paymentPointerUrl");

		if (!Strings.isNullOrEmpty(jwksUri)) {
			// do the fetch

			log("Fetching client key", args("jwks_uri", jwksUri));

			try {
				RestTemplate restTemplate = createRestTemplate(env);

				// add host header to request
				HttpHeaders headers = new HttpHeaders();
				headers.add("Host", "backend");
				String jwkString = restTemplate.exchange(jwksUri + "/jwks.json", HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();

				log("Found JWK set string", args("jwk_string", jwkString));

				JsonObject jwkSet = JsonParser.parseString(jwkString).getAsJsonObject();
				env.putObject("server_jwks", jwkSet);

				logSuccess("Found server JWK set", args("server_jwks", jwkSet));
				return env;

			} catch (UnrecoverableKeyException | KeyManagementException | CertificateException | InvalidKeySpecException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
				throw error("Error creating HTTP client", e);
			} catch (RestClientException e) {
				String msg = "Fetching server keys from " + jwksUri + " failed";
				if (e.getCause() != null) {
					msg += " - " +e.getCause().getMessage();
				}
				throw error(msg, e);
			} catch (JsonSyntaxException e) {
				throw error("Server JWKs set string is not JSON", e);
			}

		} else {
			throw error("Didn't find jwks_uri in the server configuration");
		}

	}

}
