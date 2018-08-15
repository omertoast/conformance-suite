/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package io.fintechlabs.testframework.condition.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PostEnvironment;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.openbanking.OBGetResourceEndpoint;
import io.fintechlabs.testframework.testmodule.Environment;
import io.fintechlabs.testframework.openbanking.OBGetResourceEndpoint.Endpoint;

public class CallAccountsEndpointWithBearerToken extends AbstractCondition {

	private static final String ACCOUNTS_RESOURCE = "accounts";

	public CallAccountsEndpointWithBearerToken(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.condition.Condition#evaluate(io.fintechlabs.testframework.testmodule.Environment)
	 */
	@Override
	@PreEnvironment(required = { "access_token", "resource" })
	@PostEnvironment(required = "resource_endpoint_response_headers", strings = "resource_endpoint_response")
	public Environment evaluate(Environment env) {

		String accessToken = env.getString("access_token", "value");
		if (Strings.isNullOrEmpty(accessToken)) {
			throw error("Access token not found");
		}

		String tokenType = env.getString("access_token", "type");
		if (Strings.isNullOrEmpty(tokenType)) {
			throw error("Token type not found");
		} else if (!tokenType.equalsIgnoreCase("Bearer")) {
			throw error("Access token is not a bearer token", args("token_type", tokenType));
		}

		String resourceEndpoint = OBGetResourceEndpoint.getBaseResourceURL(env, Endpoint.ACCOUNTS_RESOURCE);
		if (Strings.isNullOrEmpty(resourceEndpoint)) {
			throw error("Resource endpoint not found");
		}

		JsonObject requestHeaders = env.getObject("resource_endpoint_request_headers");

		// Build the endpoint URL
		String accountRequestsUrl = UriComponentsBuilder.fromUriString(resourceEndpoint)
			.path(ACCOUNTS_RESOURCE)
			.toUriString();

		try {
			RestTemplate restTemplate = createRestTemplate(env);

			HttpHeaders headers = new HttpHeaders();

			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
			headers.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
			headers.set("Authorization", String.join(" ", "Bearer", accessToken));

			if (requestHeaders != null) {
				for (Map.Entry<String, JsonElement> header : requestHeaders.entrySet()) {
					headers.set(header.getKey(), header.getValue().getAsString());
				}
			}

			HttpEntity<?> request = new HttpEntity<>(headers);

			ResponseEntity<String> response = restTemplate.exchange(accountRequestsUrl, HttpMethod.GET, request, String.class);

			String responseBody = response.getBody();
			JsonObject responseHeaders = new JsonObject();

			for (Map.Entry<String, String> entry : response.getHeaders().toSingleValueMap().entrySet()) {
				responseHeaders.addProperty(entry.getKey(), entry.getValue());
			}

			env.putString("resource_endpoint_response", responseBody);
			env.put("resource_endpoint_response_headers", responseHeaders);

			logSuccess("Got a response from the resource endpoint", args("body", responseBody, "headers", responseHeaders));

			return env;
		} catch (RestClientResponseException e) {
			throw error("Error from the resource endpoint", e, args("code", e.getRawStatusCode(), "status", e.getStatusText()));
		} catch (UnrecoverableKeyException | KeyManagementException | CertificateException | InvalidKeySpecException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
			throw error("Error creating HTTP client", e);
		}
	}

}
