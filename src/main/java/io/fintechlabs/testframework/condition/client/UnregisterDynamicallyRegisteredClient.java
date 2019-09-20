package io.fintechlabs.testframework.condition.client;

import com.google.common.base.Strings;
import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.testmodule.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;

public class UnregisterDynamicallyRegisteredClient extends AbstractCondition {

	private static final Logger logger = LoggerFactory.getLogger(UnregisterDynamicallyRegisteredClient.class);

	@Override
	@PreEnvironment(required = "client")
	public Environment evaluate(Environment env) {

		String accessToken = env.getString("client", "registration_access_token");
		if (Strings.isNullOrEmpty(accessToken)){
			throw error("Couldn't find registration access token.");
		}

		String registrationClientUri = env.getString("client", "registration_client_uri");
		if (Strings.isNullOrEmpty(registrationClientUri)){
			throw error("Couldn't find registration client uri");
		}

		try {

			RestTemplate restTemplate = createRestTemplate(env);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
			headers.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
			headers.set("Authorization", String.join(" ", "Bearer", accessToken));

			HttpEntity<?> request = new HttpEntity<>(headers);
			try {
				ResponseEntity<?> response = restTemplate.exchange(registrationClientUri, HttpMethod.DELETE, request, String.class);
				if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
					logSuccess("Client successfully unregistered");
				}
			} catch (RestClientResponseException e) {
				throw error("Error from the registration client endpoint", e, args("code", e.getRawStatusCode(), "status", e.getStatusText(), "body", e.getResponseBodyAsString()));
			}

		} catch (NoSuchAlgorithmException | KeyManagementException | CertificateException | InvalidKeySpecException | KeyStoreException | IOException | UnrecoverableKeyException e) {
			logger.warn("Error creating HTTP Client", e);
			throw error("Error creating HTTP Client", e);
		}

		return env;
	}
}
