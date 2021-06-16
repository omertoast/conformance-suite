package net.openid.conformance.fapirwid2;

import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.AddClientAssertionToTokenEndpointRequest;
import net.openid.conformance.condition.client.AddExpIs5MinutesInPastToClientAssertionClaims;
import net.openid.conformance.condition.client.CallTokenEndpointAndReturnFullResponse;
import net.openid.conformance.condition.client.CheckErrorDescriptionFromTokenEndpointResponseErrorContainsCRLFTAB;
import net.openid.conformance.condition.client.CheckErrorFromTokenEndpointResponseErrorInvalidClientOrInvalidRequest;
import net.openid.conformance.condition.client.CheckTokenEndpointHttpStatusForInvalidRequestOrInvalidClientError;
import net.openid.conformance.condition.client.CheckTokenEndpointReturnedJsonContentType;
import net.openid.conformance.condition.client.CreateClientAuthenticationAssertionClaims;
import net.openid.conformance.condition.client.SignClientAuthenticationAssertion;
import net.openid.conformance.condition.client.ValidateErrorDescriptionFromTokenEndpointResponseError;
import net.openid.conformance.condition.client.ValidateErrorFromTokenEndpointResponseError;
import net.openid.conformance.condition.client.ValidateErrorUriFromTokenEndpointResponseError;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.variant.ClientAuthType;
import net.openid.conformance.variant.VariantNotApplicable;

@PublishTestModule(
	testName = "fapi-rw-id2-ensure-client-assertion-with-exp-is-5-minutes-in-past-fails",
	displayName = "FAPI-RW-ID2: ensure client_assertion with exp is 5 minutes in the past fails",
	summary = "This test passes client assertion where 'exp' is 5 minutes ago. The server must be rejected at the token endpoint and returning an error message that the expiration time ('exp') of the client assertion is invalid.",
	profile = "FAPI-RW-ID2",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.scope",
		"client.jwks",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.client_id",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl"
	}
)
@VariantNotApplicable(parameter = ClientAuthType.class, values = { "mtls" })
public class FAPIRWID2EnsureClientAssertionWithExpIs5MinutesInPastFails extends AbstractFAPIRWID2PerformTokenEndpoint {

	@Override
	protected void addClientAuthenticationToTokenEndpointRequest() {
		callAndStopOnFailure(CreateClientAuthenticationAssertionClaims.class);

		callAndStopOnFailure(AddExpIs5MinutesInPastToClientAssertionClaims.class);

		callAndStopOnFailure(SignClientAuthenticationAssertion.class);

		callAndStopOnFailure(AddClientAssertionToTokenEndpointRequest.class);
	}

	@Override
	protected void requestAuthorizationCode() {
		/* If we get an error back from the token endpoint server:
		 * - It must be a 'invalid_client' or 'invalid_request' error
		 */
		callAndContinueOnFailure(CallTokenEndpointAndReturnFullResponse.class, Condition.ConditionResult.FAILURE, "FAPI-R-5.2.2-19");
		callAndContinueOnFailure(CheckTokenEndpointReturnedJsonContentType.class, Condition.ConditionResult.FAILURE, "OIDCC-3.1.3.4");
		callAndContinueOnFailure(ValidateErrorFromTokenEndpointResponseError.class, Condition.ConditionResult.FAILURE, "RFC6749-5.2");
		callAndContinueOnFailure(CheckErrorDescriptionFromTokenEndpointResponseErrorContainsCRLFTAB.class, Condition.ConditionResult.WARNING, "RFC6749-5.2");
		callAndContinueOnFailure(ValidateErrorDescriptionFromTokenEndpointResponseError.class, Condition.ConditionResult.FAILURE,"RFC6749-5.2");
		callAndContinueOnFailure(ValidateErrorUriFromTokenEndpointResponseError.class, Condition.ConditionResult.FAILURE,"RFC6749-5.2");
		callAndContinueOnFailure(CheckTokenEndpointHttpStatusForInvalidRequestOrInvalidClientError.class, Condition.ConditionResult.FAILURE, "RFC6749-5.2");
		callAndContinueOnFailure(CheckErrorFromTokenEndpointResponseErrorInvalidClientOrInvalidRequest.class, Condition.ConditionResult.FAILURE, "RFC6749-5.2");

		fireTestFinished();
	}
}