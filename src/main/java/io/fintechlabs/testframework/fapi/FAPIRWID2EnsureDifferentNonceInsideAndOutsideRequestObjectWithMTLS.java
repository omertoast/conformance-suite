package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.condition.client.AddClientIdToTokenEndpointRequest;
import io.fintechlabs.testframework.condition.client.CreateTokenEndpointRequestForAuthorizationCodeGrant;
import io.fintechlabs.testframework.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-rw-id2-ensure-different-nonce-inside-and-outside-request-object-with-mtls",
	displayName = "FAPI-RW-ID2: ensure different nonce inside and outside request object (MTLS authentication)",
	summary = "This test passes a different nonce in the authorization_endpoint parameters to the one inside the signed request object. The authorization server must either return an invalid_request error back to the client, and must show an error page (saying the request object is invalid as the 'nonce' value in the request object and outside it are different - upload a screenshot of the error page), or must successfully authenticate and return the nonce from inside the request object in the id_token.",
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
		"resource.resourceUrl",
		"resource.institution_id"
	}
)
public class FAPIRWID2EnsureDifferentNonceInsideAndOutsideRequestObjectWithMTLS extends AbstractFAPIRWID2EnsureDifferentNonceInsideAndOutsideRequestObject {

	@Override
	protected void createAuthorizationCodeRequest() {
		callAndStopOnFailure(CreateTokenEndpointRequestForAuthorizationCodeGrant.class);

		callAndStopOnFailure(AddClientIdToTokenEndpointRequest.class);
	}

}