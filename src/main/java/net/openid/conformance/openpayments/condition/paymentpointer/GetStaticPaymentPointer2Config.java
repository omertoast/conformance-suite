package net.openid.conformance.openpayments.condition.paymentpointer;

	import com.google.gson.JsonElement;
	import net.openid.conformance.condition.AbstractCondition;
	import net.openid.conformance.condition.PostEnvironment;
	import net.openid.conformance.condition.PreEnvironment;
	import net.openid.conformance.testmodule.Environment;

public class GetStaticPaymentPointer2Config extends AbstractCondition {

	@Override
	@PreEnvironment(required = "config")
	@PostEnvironment(required = "paymentPointer2")
	public Environment evaluate(Environment env) {
		if (!env.containsObject("config")) {
			throw error("Couldn't find a configuration");
		}

		// make sure we've got a client object
		JsonElement paymentPointer = env.getElementFromObject("config", "paymentPointer2");
		if (paymentPointer == null || !paymentPointer.isJsonObject()) {
			throw error("Definition for paymentPointer2 not present in supplied configuration");
		} else {
			// we've got a client object, put it in the environment
			env.putObject("paymentPointer2", paymentPointer.getAsJsonObject());

			logSuccess("Found a static second paymentPointer object", paymentPointer.getAsJsonObject());
			return env;
		}
	}

}
