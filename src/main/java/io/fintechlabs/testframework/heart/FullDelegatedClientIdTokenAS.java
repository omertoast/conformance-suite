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

package io.fintechlabs.testframework.heart;

import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.Condition.ConditionResult;
import io.fintechlabs.testframework.condition.client.*;
import io.fintechlabs.testframework.condition.common.*;
import io.fintechlabs.testframework.frontChannel.BrowserControl;
import io.fintechlabs.testframework.info.TestInfoService;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.AbstractTestModule;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.TestFailureException;
import io.fintechlabs.testframework.testmodule.UserFacing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author jricher
 *
 */
@PublishTestModule(
	testName = "heart-oidc-id-token",
	displayName = "HEART OIDC IDP: Id Token",
	profile = "HEART",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.jwks",
		"client.scope",
		"tls.testHost",
		"tls.testPort"
	}
)
public class FullDelegatedClientIdTokenAS extends AbstractTestModule {

	public static Logger logger = LoggerFactory.getLogger(FullDelegatedClientIdTokenAS.class);

	/**
	 *
	 */
	public FullDelegatedClientIdTokenAS(String id, Map<String, String> owner, TestInstanceEventLog eventLog, BrowserControl browser, TestInfoService testInfo) {
		super(id, owner, eventLog, browser, testInfo);
	}

	/* (non-Javadoc)
	 * @see io.bspk.selenium.TestModule#configure(com.google.gson.JsonObject)
	 */
	@Override
	public void configure(JsonObject config, String baseUrl) {
		env.putString("base_url", baseUrl);
		env.put("config", config);

		callAndStopOnFailure(SetTLSTestHostFromConfig.class);
		callAndStopOnFailure(EnsureTLS12.class, "HEART-OAuth2-6");
		call(DisallowTLS10.class, "HEART-OAuth2-6");
		call(DisallowTLS11.class, "HEART-OAuth2-6");

		callAndStopOnFailure(CreateRedirectUri.class);

		// this is inserted by the create call above, expose it to the test environment for publication
		exposeEnvString("redirect_uri");

		// Get the server's configuration
		call(GetDynamicServerConfiguration.class, "HEART-OAuth2-3.1.5");

		// make sure the server configuration passes some basic sanity checks
		callAndStopOnFailure(CheckHeartServerConfiguration.class, "HEART-OAuth2-3.1.5");

		// fetch or load the server's keys as needed
		callAndStopOnFailure(FetchServerKeys.class, "HEART-OAuth2-3.1.5");
		callAndStopOnFailure(CheckHeartServerJwksFields.class, "HEART-OAuth2-3.1.5");
		callAndStopOnFailure(CheckForKeyIdInJWKs.class, "OIDCC-10.1");

		// Set up the client configuration
		callAndStopOnFailure(GetStaticClientConfiguration.class);

		callAndStopOnFailure(ExtractJWKsFromClientConfiguration.class, "HEART-OAuth2-2.1.5");

		callAndStopOnFailure(CreateJwksUri.class);
		exposeEnvString("jwks_uri");

		callAndStopOnFailure(CheckRedirectUri.class);

		exposeEnvString("client_id");

		setStatus(Status.CONFIGURED);
		fireSetupDone();
	}

	/* (non-Javadoc)
	 * @see io.bspk.selenium.TestModule#start()
	 */
	@Override
	public void start() {

		setStatus(Status.RUNNING);

		callAndStopOnFailure(CreateAuthorizationEndpointRequestFromClientInformation.class);

		callAndStopOnFailure(CreateRandomStateValue.class);
		exposeEnvString("state");
		callAndStopOnFailure(AddStateToAuthorizationEndpointRequest.class);

		callAndStopOnFailure(CreateRandomNonceValue.class);
		exposeEnvString("nonce");
		callAndStopOnFailure(AddNonceToAuthorizationEndpointRequest.class);

		callAndStopOnFailure(SetAuthorizationEndpointRequestResponseTypeToCode.class);

		callAndStopOnFailure(BuildPlainRedirectToAuthorizationEndpoint.class);

		String redirectTo = env.getString("redirect_to_authorization_endpoint");

		eventLog.log(getName(), args("msg", "Redirecting to authorization endpoint",
			"redirect_to", redirectTo,
			"http", "redirect"));

		browser.goToUrl(redirectTo);

		setStatus(Status.WAITING);
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.TestModule#handleHttp(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession, org.springframework.util.MultiValueMap, org.springframework.ui.Model)
	 */
	@Override
	public Object handleHttp(String path, HttpServletRequest req, HttpServletResponse res, HttpSession session, JsonObject requestParts) {

		logIncomingHttpRequest(path, requestParts);

		// dispatch based on the path
		if (path.equals("callback")) {
			return handleCallback(requestParts);
		} else if (path.equals("jwks")) {
			return handleJwks(requestParts);
		} else {
			throw new TestFailureException(getId(), "Got unexpected HTTP call to " + path);
		}

	}

	/**
	 * @param requestParts
	 * @return
	 */
	private Object handleJwks(JsonObject requestParts) {
		setStatus(Status.RUNNING);
		JsonObject jwks = env.get("public_jwks");

		setStatus(Status.WAITING);

		return new ResponseEntity<Object>(jwks, HttpStatus.OK);
	}

	/**
	 * @param path
	 * @param req
	 * @param res
	 * @param session
	 * @param params
	 * @param m
	 * @return
	 */
	@UserFacing
	private Object handleCallback(JsonObject requestParts) {

		// process the callback
		setStatus(Status.RUNNING);

		env.put("callback_params", requestParts.get("params").getAsJsonObject());
		callAndStopOnFailure(CheckIfAuthorizationEndpointError.class);

		callAndStopOnFailure(CheckMatchingStateParameter.class);

		callAndStopOnFailure(ExtractAuthorizationCodeFromAuthorizationResponse.class);

		callAndStopOnFailure(CreateTokenEndpointRequestForAuthorizationCodeGrant.class);

		// authenticate using a signed assertion
		callAndStopOnFailure(CreateClientAuthenticationAssertionClaims.class, "HEART-OAuth2-2.2.2");
		callAndStopOnFailure(SignClientAuthenticationAssertion.class, "HEART-OAuth2-2.2.2");
		callAndStopOnFailure(AddClientAssertionToTokenEndpointRequest.class, "HEART-OAuth2-2.2.2");

		callAndStopOnFailure(CallTokenEndpoint.class);

		callAndStopOnFailure(CheckIfTokenEndpointResponseError.class);

		// The following is for the Access Token
		callAndStopOnFailure(CheckForAccessTokenValue.class);

		callAndStopOnFailure(ExtractAccessTokenFromTokenResponse.class);

		callAndStopOnFailure(ParseAccessTokenAsJwt.class, "HEART-OAuth2-3.2.1");

		callAndStopOnFailure(ValidateAccessTokenSignature.class, "HEART-OAuth2-3.2.1");

		call(ValidateAccessTokenHeartClaims.class, ConditionResult.FAILURE, "HEART-OAuth2-3.2.1");

		call(CheckForScopesInTokenResponse.class);

		// The following is for the ID token HEART 3.1.3.6, 3.1.3.7

		callAndStopOnFailure(ExtractIdTokenFromTokenResponse.class);

		callAndStopOnFailure(ValidateIdTokenSignature.class, "HEART-OIDC-3.1");

		//callAndStopOnFailure(ValidateIdToken.class);
		call(ValidateIdTokenHeartClaims.class, ConditionResult.FAILURE, "HEART-OIDC-3.1");

		call(VerifyIdTokenExpHeart.class, ConditionResult.WARNING, "HEART-OIDC-3.1");


		// The following is for RefreshToken
		callAndStopOnFailure(EnsureNoRefreshToken.class, "HEART-OAuth2-2.1.4");

		fireTestFinished();
		stop();

		return redirectToLogDetailPage();

	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#handleHttpMtls(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession, com.google.gson.JsonObject)
	 */
	@Override
	public Object handleHttpMtls(String path, HttpServletRequest req, HttpServletResponse res, HttpSession session, JsonObject requestParts) {

		/*
		eventLog.log(getId(), getName() + " MTLS Routing", requestParts.get("headers").getAsJsonObject());

		return new ModelAndView("complete", ImmutableMap.of("test", this));
		*/
		throw new TestFailureException(getId(), "Got an HTTP response on a call we weren't expecting");

	}

}