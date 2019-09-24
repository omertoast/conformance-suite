package io.fintechlabs.testframework.fapiciba;

import com.google.common.base.Strings;

import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.client.EnsureRefreshTokenContainsAllowedCharactersOnly;
import io.fintechlabs.testframework.condition.client.EnsureServerConfigurationDoesNotSupportRefreshToken;
import io.fintechlabs.testframework.condition.client.EnsureServerConfigurationSupportsRefreshToken;
import io.fintechlabs.testframework.condition.client.ExtractRefreshTokenFromTokenResponse;
import io.fintechlabs.testframework.sequence.client.AddPrivateKeyJWTClientAuthenticationToBackchannelRequest;
import io.fintechlabs.testframework.sequence.client.RefreshTokenRequestExpectingErrorSteps;
import io.fintechlabs.testframework.sequence.client.RefreshTokenRequestSteps;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.Variant;

@PublishTestModule(
		testName = "fapi-ciba-id1-refresh-token",
		displayName = "FAPI-CIBA-ID1: obtain an id token using a refresh token",
		summary = "This test uses a refresh_token to obtain an id token and ensures that claims satisfy the requirements.",
		profile = "FAPI-CIBA-ID1",
		configurationFields = {
			"server.discoveryUrl",
			"client.client_id",
			"client.scope",
			"client.jwks",
			"client.hint_type",
			"client.hint_value",
			"mtls.key",
			"mtls.cert",
			"mtls.ca",
			"client2.client_id",
			"client2.scope",
			"client2.jwks",
			"client2.acr_value",
			"mtls2.key",
			"mtls2.cert",
			"mtls2.ca",
			"resource.resourceUrl",
			"resource.institution_id"
		}
	)
public class FAPICIBAID1RefreshToken extends AbstractFAPICIBAID1MultipleClient {

	@Variant(name = variant_ping_mtls)
	public void setupPingMTLS() {
		super.setupPingMTLS();
	}

	@Variant(name = variant_ping_privatekeyjwt)
	public void setupPingPrivateKeyJwt() {
		super.setupPingPrivateKeyJwt();
		setAddBackchannelClientAuthentication(() -> new AddPrivateKeyJWTClientAuthenticationToBackchannelRequest(isSecondClient(), false));
	}

	@Variant(name = variant_poll_mtls)
	public void setupPollMTLS() {
		super.setupPollMTLS();
	}

	@Variant(name = variant_poll_privatekeyjwt)
	public void setupPollPrivateKeyJwt() {
		super.setupPollPrivateKeyJwt();
		setAddBackchannelClientAuthentication(() -> new AddPrivateKeyJWTClientAuthenticationToBackchannelRequest(isSecondClient(), false));
	}

	@Variant(name = variant_openbankinguk_ping_mtls)
	public void setupOpenBankingUkPingMTLS() {
		super.setupOpenBankingUkPingMTLS();
	}

	@Variant(name = variant_openbankinguk_ping_privatekeyjwt)
	public void setupOpenBankingUkPingPrivateKeyJwt() {
		super.setupOpenBankingUkPingPrivateKeyJwt();
	}

	@Variant(name = variant_openbankinguk_poll_mtls)
	public void setupOpenBankingUkPollMTLS() {
		super.setupOpenBankingUkPollMTLS();
	}

	@Variant(name = variant_openbankinguk_poll_privatekeyjwt)
	public void setupOpenBankingUkPollPrivateKeyJwt() {
		super.setupOpenBankingUkPollPrivateKeyJwt();
	}

	@Override
	protected void performAuthorizationFlow() {
		// Store the original access token and ID token separately (see RefreshTokenRequestSteps)
		env.mapKey("access_token", "first_access_token");
		env.mapKey("id_token", "first_id_token");
		super.performAuthorizationFlow();
	}

	protected void sendRefreshTokenRequestAndCheckIdTokenClaims() {
		// Set up the mappings for the refreshed access and ID tokens
		env.mapKey("access_token", "second_access_token");
		env.mapKey("id_token", "second_id_token");

		callAndContinueOnFailure(ExtractRefreshTokenFromTokenResponse.class, Condition.ConditionResult.INFO);
		//stop if no refresh token is returned
		if (Strings.isNullOrEmpty(env.getString("refresh_token"))) {
			callAndContinueOnFailure(EnsureServerConfigurationDoesNotSupportRefreshToken.class, Condition.ConditionResult.WARNING, "OIDCD-3");
			// This throws an exception: the test will stop here
			fireTestSkipped("Refresh tokens cannot be tested. No refresh token was issued.");
		}
		callAndContinueOnFailure(EnsureServerConfigurationSupportsRefreshToken.class, Condition.ConditionResult.WARNING, "OIDCD-3");
		callAndContinueOnFailure(EnsureRefreshTokenContainsAllowedCharactersOnly.class, Condition.ConditionResult.FAILURE, "RFC6749-A.17");
		call(new RefreshTokenRequestSteps(isSecondClient(), addTokenEndpointClientAuthentication));
	}

	@Override
	protected void performPostAuthorizationFlow(boolean finishTest) {
		sendRefreshTokenRequestAndCheckIdTokenClaims();

		requestProtectedResource();

		if (!isSecondClient()) {
			// Try the second client

			//remove refresh token from 1st client
			env.removeNativeValue("refresh_token");

			switchToSecondClient();

			performAuthorizationFlow();
		} else {
			unmapClient();

			// try client 2's refresh_token with client 1
			eventLog.startBlock("Attempting to use refresh_token issued to client 2 with client 1");
			call(new RefreshTokenRequestExpectingErrorSteps(isSecondClient(), addTokenEndpointClientAuthentication));
			eventLog.endBlock();

			fireTestFinished();
		}
	}
}