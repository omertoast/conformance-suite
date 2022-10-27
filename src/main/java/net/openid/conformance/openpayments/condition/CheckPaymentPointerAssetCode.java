package net.openid.conformance.openpayments.condition;

import com.google.gson.JsonElement;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.testmodule.Environment;

public class CheckPaymentPointerAssetCode extends AbstractCondition {

	@Override
	@PostEnvironment(required = { "paymentPointer", "config" } )
	public Environment evaluate(Environment env) {
		JsonElement assetCodeElement = env.getElementFromObject("paymentPointer", "assetCode");

		if (assetCodeElement == null || assetCodeElement.isJsonObject()) {
			throw error("assetCode is missing from payment pointer.");
		}

		// check if assetCode is a string
		try {
			assetCodeElement.getAsString();
		} catch (NumberFormatException e) {
			throw error("assetCode is not a string.");
		}

		logSuccess("assetScale", args("actual", assetCodeElement));

		return env;
	}

}
