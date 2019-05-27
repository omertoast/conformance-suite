package io.fintechlabs.testframework.fapiciba;

import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.client.AddClientNotificationTokenToAuthorizationEndpointRequestResponse;
import io.fintechlabs.testframework.condition.client.CreateLongRandomClientNotificationToken;
import io.fintechlabs.testframework.condition.client.CreateRandomClientNotificationToken;
import io.fintechlabs.testframework.condition.client.WaitForSuccessfulCibaAuthentication;
import io.fintechlabs.testframework.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-ciba-ping-ensure-authorization-request-with-potentially-bad-binding-message-with-mtls",
	displayName = "FAPI-CIBA: Ping mode - test with a potentially bad binding message, the server should authenticate successfully or return the invalid_binding_message error (MTLS client authentication)",
	summary = "This test tries sending a potentially bad binding message to authorization endpoint request. The server should either authenticate successfully showing the correct binding message (a screenshot/photo of which should be uploaded) or return the invalid_binding_message error.",
	profile = "FAPI-CIBA",
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
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl"
	}
)
public class FAPICIBAPingEnsureAuthorizationRequestWithPotentiallyBadBindingMessageWithMTLS extends AbstractFAPICIBAEnsureAuthorizationRequestWithPotentiallyBadBindingMessageWithMTLS {

	@Override
	protected void waitForAuthenticationToComplete(long delaySeconds) {

		callAndStopOnFailure(WaitForSuccessfulCibaAuthentication.class);

		setStatus(Status.WAITING);
	}

	@Override
	protected void processNotificationCallback(JsonObject requestParts) {

		processPingNotificationCallback(requestParts);

		handleSuccessfulTokenEndpointResponse();
	}

	@Override
	protected void modeSpecificAuthorizationEndpointRequest() {

		callAndStopOnFailure(CreateRandomClientNotificationToken.class, "CIBA-7.1");

		callAndStopOnFailure(AddClientNotificationTokenToAuthorizationEndpointRequestResponse.class, "CIBA-7.1");
	}
}