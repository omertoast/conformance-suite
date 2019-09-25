package io.fintechlabs.testframework.openid;

import com.google.common.base.Strings;

import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.client.EnsureRefreshTokenContainsAllowedCharactersOnly;
import io.fintechlabs.testframework.condition.client.EnsureServerConfigurationDoesNotSupportRefreshToken;
import io.fintechlabs.testframework.condition.client.EnsureServerConfigurationSupportsRefreshToken;
import io.fintechlabs.testframework.condition.client.ExtractRefreshTokenFromTokenResponse;
import io.fintechlabs.testframework.sequence.client.RefreshTokenRequestExpectingErrorSteps;
import io.fintechlabs.testframework.sequence.client.RefreshTokenRequestSteps;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.Variant;

@PublishTestModule(
	testName = "oidcc-refresh-token",
	displayName = "OIDCC: obtain an id token using a refresh token",
	summary = "This test uses a refresh_token to obtain an id token and ensures that claims satisfy the requirements.",
	profile = "OIDCC",
	configurationFields = {
		"response_type",
		"server.discoveryUrl",
		"client.client_id",
		"client.scope",
		"client2.client_id",
		"client2.scope",
		"resource.resourceUrl"
	}
)
public class OIDCCRefreshToken extends AbstractOIDCCMultipleClient {

	@Variant(name = variant_none)
	public void setupNone() {
		super.setupNone();
	}

	@Variant(
		name = variant_client_secret_basic,
		configurationFields =  {
			"client.client_secret",
			"client2.client_secret"
		})
	public void setupClientSecretBasic() {
		super.setupClientSecretBasic();
	}

	@Variant(
		name = variant_client_secret_post,
		configurationFields =  {
			"client.client_secret",
			"client2.client_secret"
		})
	public void setupClientSecretPost() {
		super.setupClientSecretPost();
	}

	@Variant(
		name = variant_client_secret_jwt,
		configurationFields =  {
			"client.client_secret",
			"client.client_secret_jwt_alg",
			"client2.client_secret",
			"client2.client_secret_jwt_alg",
		})
	public void setupClientSecretJwt() {
		super.setupClientSecretJwt();
	}

	@Variant(
		name = variant_private_key_jwt,
		configurationFields =  {
			"client.jwks",
			"client2.jwks",
		})
	public void setupPrivateKeyJwt() {
		super.setupPrivateKeyJwt();
	}

	@Variant(
		name = variant_mtls,
		configurationFields =  {
			"mtls.key",
			"mtls.cert",
			"mtls.ca",
			"mtls2.key",
			"mtls2.cert",
			"mtls2.ca",
		})
	public void setupMtls() {
		super.setupMtls();
	}

	@Override
	protected void performPostAuthorizationFlow() {
		createAuthorizationCodeRequest();

		// Store the original access token and ID token separately (see RefreshTokenRequestSteps)
		env.mapKey("access_token", "first_access_token");
		env.mapKey("id_token", "first_id_token");

		requestAuthorizationCode();

		// Set up the mappings for the refreshed access and ID tokens
		env.mapKey("access_token", "second_access_token");
		env.mapKey("id_token", "second_id_token");

		sendRefreshTokenRequestAndCheckIdTokenClaims();

		requestProtectedResource();

		onPostAuthorizationFlowComplete();
	}

	protected void sendRefreshTokenRequestAndCheckIdTokenClaims() {
		callAndContinueOnFailure(ExtractRefreshTokenFromTokenResponse.class, Condition.ConditionResult.INFO);
		//stop if no refresh token is returned
		if(Strings.isNullOrEmpty(env.getString("refresh_token"))) {
			callAndContinueOnFailure(EnsureServerConfigurationDoesNotSupportRefreshToken.class, Condition.ConditionResult.WARNING, "OIDCD-3");
			// This throws an exception: the test will stop here
			fireTestSkipped("Refresh tokens cannot be tested. No refresh token was issued.");
		}
		callAndContinueOnFailure(EnsureServerConfigurationSupportsRefreshToken.class, Condition.ConditionResult.WARNING, "OIDCD-3");
		callAndContinueOnFailure(EnsureRefreshTokenContainsAllowedCharactersOnly.class, Condition.ConditionResult.FAILURE, "RFC6749-A.17");
		call(new RefreshTokenRequestSteps(isSecondClient(), addTokenEndpointClientAuthentication));
	}

	@Override
	protected void onPostAuthorizationFlowComplete() {
		if (!isSecondClient()) {
			//remove refresh token from 1st client
			env.removeNativeValue("refresh_token");

			// Remove token mappings
			// (This must be done before restarting the authorization flow, because
			// handleSuccessfulAuthorizationEndpointResponse extracts an id token)
			env.unmapKey("access_token");
			env.unmapKey("id_token");
		}

		super.onPostAuthorizationFlowComplete();
	}

	@Override
	protected void performSecondClientTests() {
		// try client 2's refresh_token with client 1
		unmapClient();
		eventLog.startBlock("Attempting to use refresh_token issued to client 2 with client 1");
		call(new RefreshTokenRequestExpectingErrorSteps(isSecondClient(), addTokenEndpointClientAuthentication));
		eventLog.endBlock();
	}
}