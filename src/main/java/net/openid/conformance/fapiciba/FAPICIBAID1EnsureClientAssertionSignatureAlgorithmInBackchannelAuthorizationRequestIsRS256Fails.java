package net.openid.conformance.fapiciba;

import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.AddAlgorithmAsRS256;
import net.openid.conformance.condition.client.CheckBackchannelAuthenticationEndpointErrorHttpStatus;
import net.openid.conformance.condition.client.CheckErrorDescriptionFromBackchannelAuthenticationEndpointContainsCRLFTAB;
import net.openid.conformance.condition.client.CheckErrorFromBackchannelAuthenticationEndpointError;
import net.openid.conformance.condition.client.ValidateErrorDescriptionFromBackchannelAuthenticationEndpoint;
import net.openid.conformance.condition.client.ValidateErrorResponseFromBackchannelAuthenticationEndpoint;
import net.openid.conformance.condition.client.ValidateErrorUriFromBackchannelAuthenticationEndpoint;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.variant.ClientAuthType;
import net.openid.conformance.variant.VariantNotApplicable;

@PublishTestModule(
	testName = "fapi-ciba-id1-ensure-client-assertion-signature-algorithm-in-backchannel-authorization-request-is-RS256-fails",
	displayName = "FAPI-CIBA-ID1: Ensure client_assertion signature algorithm in backchannel authorization request is RS256 fails",
	summary = "This test passed client_assertion that was signed with algorithm as RS256 to the backchannel authorization endpoint, and should end with the server returning an access_denied or invalid_request or invalid_client error.",
	profile = "FAPI-CIBA-ID1",
	configurationFields = {
		"server.discoveryUrl",
		"client.scope",
		"client.jwks",
		"client.hint_type",
		"client.hint_value",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl"
	}
)
@VariantNotApplicable(parameter = ClientAuthType.class, values = { "mtls" })
public class FAPICIBAID1EnsureClientAssertionSignatureAlgorithmInBackchannelAuthorizationRequestIsRS256Fails extends AbstractFAPICIBAID1 {

	@Override
	protected void addClientAuthenticationToBackchannelRequest() {
		callAndStopOnFailure(AddAlgorithmAsRS256.class, "FAPI-RW-8.6");

		super.addClientAuthenticationToBackchannelRequest();
	}

	@Override
	protected void performAuthorizationFlow() {
		performPreAuthorizationSteps();

		eventLog.startBlock(currentClientString() + "Call backchannel authentication endpoint");

		createAuthorizationRequest();

		performAuthorizationRequest();

		eventLog.endBlock();

		validateErrorFromBackchannelAuthorizationRequestResponse();

		cleanupAfterBackchannelRequestShouldHaveFailed();
	}

	@Override
	protected void validateErrorFromBackchannelAuthorizationRequestResponse() {

		callAndContinueOnFailure(ValidateErrorResponseFromBackchannelAuthenticationEndpoint.class, Condition.ConditionResult.FAILURE, "CIBA-13");

		callAndContinueOnFailure(ValidateErrorUriFromBackchannelAuthenticationEndpoint.class, Condition.ConditionResult.FAILURE, "CIBA-13");
		callAndContinueOnFailure(CheckErrorDescriptionFromBackchannelAuthenticationEndpointContainsCRLFTAB.class, Condition.ConditionResult.WARNING, "RFC6749-5.2");
		callAndContinueOnFailure(ValidateErrorDescriptionFromBackchannelAuthenticationEndpoint.class, Condition.ConditionResult.FAILURE, "CIBA-13");

		callAndContinueOnFailure(CheckErrorFromBackchannelAuthenticationEndpointError.class, Condition.ConditionResult.FAILURE, "CIBA-13");
		callAndContinueOnFailure(CheckBackchannelAuthenticationEndpointErrorHttpStatus.class, Condition.ConditionResult.FAILURE, "CIBA-13");

	}

	@Override
	protected void waitForAuthenticationToComplete(long delaySeconds) {
		//Not called in this test
	}

}
