package net.openid.conformance.openpayments.condition;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class GetPaymentPointerInformation extends AbstractCondition {

@Override
	@PreEnvironment(required = "config")
	@PostEnvironment(required = { "paymentPointer", "discovery_endpoint_response" } )
	public Environment evaluate(Environment env) {

		if (!env.containsObject("config")) {
			throw error("Couldn't find a configuration");
		}

		String paymentPointerUrl = env.getString("config", "paymentPointer.paymentPointerUrl");

		if (Strings.isNullOrEmpty(paymentPointerUrl)) {
			throw error("Couldn't find paymentPointerUrl field for discovery purposes");
		}

		// get out the server configuration component
		if (!Strings.isNullOrEmpty(paymentPointerUrl)) {
			// do an auto-discovery here

			// fetch the value
			String jsonString;
			try {
				RestTemplate restTemplate = createRestTemplate(env);
				// add host header to request
				HttpHeaders headers = new HttpHeaders();
				headers.add("Host", "backend");
				ResponseEntity<String> response = restTemplate.exchange(paymentPointerUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
				JsonObject responseInfo = convertResponseForEnvironment("discovery", response);

				env.putObject("discovery_endpoint_response", responseInfo);

				jsonString = response.getBody();
			} catch (UnrecoverableKeyException | KeyManagementException | CertificateException | InvalidKeySpecException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
				throw error("Error creating HTTP client", e);
			} catch (RestClientException e) {
				String msg = "Unable to fetch public information from " + paymentPointerUrl;
				if (e.getCause() != null) {
					msg += " - " +e.getCause().getMessage();
				}
				throw error(msg, e);
			}

			if (!Strings.isNullOrEmpty(jsonString)) {
				try {
					JsonObject paymentPointer = JsonParser.parseString(jsonString).getAsJsonObject();

					logSuccess("Successfully parsed payment pointer", paymentPointer);

					env.putObject("paymentPointer", paymentPointer);

					return env;
				} catch (JsonSyntaxException e) {
					throw error(e, args("json", jsonString));
				}

			} else {
				throw error("empty paymentPointer configuration");
			}

		} else {
			throw error("Couldn't find or construct a discovery URL");
		}

	}

}
