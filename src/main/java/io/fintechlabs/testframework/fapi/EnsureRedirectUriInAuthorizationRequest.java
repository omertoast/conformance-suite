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

package io.fintechlabs.testframework.fapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import io.fintechlabs.testframework.condition.AddNonceToAuthorizationEndpointRequest;
import io.fintechlabs.testframework.condition.AddStateToAuthorizationEndpointRequest;
import io.fintechlabs.testframework.condition.BuildPlainRedirectToAuthorizationEndpoint;
import io.fintechlabs.testframework.condition.CheckServerConfiguration;
import io.fintechlabs.testframework.condition.CreateAuthorizationEndpointRequestFromClientInformation;
import io.fintechlabs.testframework.condition.CreateRandomNonceValue;
import io.fintechlabs.testframework.condition.CreateRandomStateValue;
import io.fintechlabs.testframework.condition.CreateRedirectUri;
import io.fintechlabs.testframework.condition.ExpectRedirectUriMissingErrorPage;
import io.fintechlabs.testframework.condition.GetDynamicServerConfiguration;
import io.fintechlabs.testframework.condition.GetStaticClientConfiguration;
import io.fintechlabs.testframework.condition.SetAuthorizationEndpointRequestResponseTypeToCode;
import io.fintechlabs.testframework.testmodule.AbstractTestModule;
import io.fintechlabs.testframework.testmodule.TestFailureException;

public class EnsureRedirectUriInAuthorizationRequest extends AbstractTestModule {

	public EnsureRedirectUriInAuthorizationRequest() {
		super("ensure-redirect-uri-in-authorization-request");
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#configure(com.google.gson.JsonObject, io.fintechlabs.testframework.logging.EventLog, java.lang.String, io.fintechlabs.testframework.frontChannel.BrowserControl, java.lang.String)
	 */
	@Override
	public void configure(JsonObject config, String baseUrl) {
		env.putString("base_url", baseUrl);
		env.put("config", config);

		// Create a redirect URI (this will be removed from the actual request)
		require(CreateRedirectUri.class);

		// this is inserted by the create call above, expose it to the test environment for publication
		exposeEnvString("redirect_uri");

		// Make sure we're calling the right server configuration
		require(GetDynamicServerConfiguration.class);

		// Make sure the server configuration passes some basic sanity checks
		require(CheckServerConfiguration.class);

		// Set up the client configuration
		require(GetStaticClientConfiguration.class);

		exposeEnvString("client_id");

		// Create a valid authorization request
		require(CreateAuthorizationEndpointRequestFromClientInformation.class);

		// Remove the redirect URL
		env.get("authorization_endpoint_request").remove("redirect_uri");
		
		setStatus(Status.CONFIGURED);

		fireSetupDone();
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#start()
	 */
	@Override
	public void start() {
		setStatus(Status.RUNNING);

		require(CreateRandomStateValue.class);
		exposeEnvString("state");
		require(AddStateToAuthorizationEndpointRequest.class);

		require(CreateRandomNonceValue.class);
		exposeEnvString("nonce");
		require(AddNonceToAuthorizationEndpointRequest.class);
		
		require(SetAuthorizationEndpointRequestResponseTypeToCode.class);

		require(BuildPlainRedirectToAuthorizationEndpoint.class);

		String redirectTo = env.getString("redirect_to_authorization_endpoint");

		eventLog.log(getId(), getName(), "Redirecting to url " + redirectTo);

		require(ExpectRedirectUriMissingErrorPage.class);

		browser.goToUrl(redirectTo);

		/**
		 * We never expect the browser to come back from here, our test is done
		 */

		// someone needs to review this by hand
		setResult(Result.REVIEW);

		stop();
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#stop()
	 */
	@Override
	public void stop() {
		eventLog.log(getId(), getName(), "Finished");

		setStatus(Status.FINISHED);

		if (getResult().equals(Result.UNKNOWN)) {
			fireInterrupted();
		}
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#handleHttp(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession, org.springframework.util.MultiValueMap, org.springframework.ui.Model)
	 */
	@Override
	public Object handleHttp(String path, HttpServletRequest req, HttpServletResponse res, HttpSession session, JsonObject requestParts) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#handleHttpMtls(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession, com.google.gson.JsonObject)
	 */
	@Override
	public Object handleHttpMtls(String path, HttpServletRequest req, HttpServletResponse res, HttpSession session, JsonObject requestParts) {
		throw new TestFailureException(getId(), "Got an HTTP response on a call we weren't expecting");
	}

}