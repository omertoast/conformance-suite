package net.openid.conformance.openpayments;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.CheckDiscoveryEndpointReturnedJsonContentType;
import net.openid.conformance.condition.client.EnsureDiscoveryEndpointResponseStatusCodeIs200;
import net.openid.conformance.openpayments.condition.GetPaymentPointer;
import net.openid.conformance.testmodule.AbstractTestModule;
import net.openid.conformance.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "open-payments-query-payment-pointer",
	displayName = "Open Payments: Query Payment Pointer",
	summary = "This test ensures that the server's configuration (including scopes, response_types, grant_types etc) contains values required by the specification",
	profile = "openpaymentstest",
	configurationFields = {
		"server.discoveryUrl",
	}
)
public class OpenPaymentsQueryPaymentPointer extends AbstractTestModule {
	@Override
	public void configure(JsonObject config, String baseUrl, String externalUrlOverride) {

		env.putString("base_url", baseUrl);
		env.putObject("config", config);

		callAndStopOnFailure(GetPaymentPointer.class);
		callAndContinueOnFailure(EnsureDiscoveryEndpointResponseStatusCodeIs200.class, Condition.ConditionResult.FAILURE, "OIDCD-4");
		callAndContinueOnFailure(CheckDiscoveryEndpointReturnedJsonContentType.class, Condition.ConditionResult.FAILURE, "OIDCD-4");
		setStatus(Status.CONFIGURED);
		fireSetupDone();
}

	@Override
	public void start() {
		setStatus(Status.RUNNING);
		fireTestFinished();
	}
}
