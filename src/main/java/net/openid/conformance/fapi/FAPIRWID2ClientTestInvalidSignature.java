package net.openid.conformance.fapi;

import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.condition.as.SignIdTokenInvalid;
import net.openid.conformance.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-rw-id2-client-test-invalid-signature",
	displayName = "FAPI-RW-ID2: client test - invalid signature in id_token from authorization_endpoint, should be rejected",
	summary = "This test should end with the client displaying an error message that the signature in the id_token from the authorization_endpoint does not match the signature value in the request object",
	profile = "FAPI-RW-ID2",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	}
)

public class FAPIRWID2ClientTestInvalidSignature extends AbstractFAPIRWID2ClientExpectNothingAfterAuthorizationEndpoint {

	@Override
	protected void addCustomValuesToIdToken() {
		//Do Nothing
	}

	protected void addCustomSignatureOfIdToken(){

		callAndStopOnFailure(SignIdTokenInvalid.class, "OIDCC-3.1.3.7-6");

	}

	@Override
	protected Object authorizationCodeGrantType(String requestId) {

		throw new ConditionError(getId(), "Client has incorrectly called token_endpoint after receiving an id_token with an invalid signature from the authorization_endpoint.");

	}

}