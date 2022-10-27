package net.openid.conformance.openpayments.condition.paymentpointer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.testmodule.OIDFJSON;

public class GetStaticPaymentPointerConfig extends AbstractCondition {

	@Override
	@PreEnvironment(required = "config")
	@PostEnvironment(required = "paymentPointer", strings = "paymentPointerUrl")
	public Environment evaluate(Environment env) {
		// make sure we've got a client object
		JsonElement paymentPointerElement = env.getElementFromObject("config", "paymentPointer");
		if (paymentPointerElement == null || !paymentPointerElement.isJsonObject()) {
			throw error("As static paymentPointer was selected, the test configuration must contain a paymentPointer configuration");
		} else {
			JsonObject paymentPointer = paymentPointerElement.getAsJsonObject();
			// we've got a client object, put it in the environment
			env.putObject("paymentPointer", paymentPointer);

			JsonElement paymentPointerUrl = paymentPointer.get("paymentPointerUrl");
			if (paymentPointerUrl == null) {
				throw error("As static paymentPointer was selected, the test configuration must contain a paymentPointerUrl");
			}
			if (!paymentPointerUrl.isJsonPrimitive() || !paymentPointerUrl.getAsJsonPrimitive().isString()) {
				throw error("paymentPointerUrl in test configuration is not a string");
			}

			// pull out the client ID and put it in the root environment for easy access
			env.putString("paymentPointerUrl", OIDFJSON.getString(paymentPointerUrl));

			logSuccess("Found a static paymentPointer object", paymentPointer);
			return env;
		}
	}

}
