package net.openid.conformance.openpayments;

import com.google.gson.JsonObject;
import net.openid.conformance.openpayments.condition.GetPaymentPointerInformation;
import net.openid.conformance.openpayments.condition.paymentpointer.FetchClientKeys;
import net.openid.conformance.openpayments.condition.paymentpointer.GetStaticPaymentPointer2Config;
import net.openid.conformance.openpayments.condition.paymentpointer.GetStaticPaymentPointerConfig;
import net.openid.conformance.testmodule.AbstractTestModule;
import net.openid.conformance.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "open-payments-request-grant",
	displayName = "Open Payments: Request Grant",
	summary = "Open Payments request grant",
	profile = "openpaymentstest",
	configurationFields = {
		"server.paymentPointerUrl",
	}
)
public class OpenPaymentsRequestGrant extends AbstractTestModule {
	@Override
	public void configure(JsonObject config, String baseUrl, String externalUrlOverride) {
		env.putString("base_url", baseUrl);
		env.putObject("config", config);

		callAndStopOnFailure(GetPaymentPointerInformation.class);
		callAndStopOnFailure(GetStaticPaymentPointerConfig.class);
		callAndStopOnFailure(GetStaticPaymentPointer2Config.class);
		callAndStopOnFailure(FetchClientKeys.class);
		setStatus(Status.CONFIGURED);
		fireSetupDone();
	}

	@Override
	public void start() {
		setStatus(Status.RUNNING);
		fireTestFinished();
	}
}
