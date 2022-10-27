package net.openid.conformance.openpayments.condition;

import com.google.gson.JsonElement;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.testmodule.Environment;

public class CheckPaymentPointerAssetScale extends AbstractCondition {

	@Override
	@PostEnvironment(required = { "paymentPointer", "config" } )
	public Environment evaluate(Environment env) {
		JsonElement assetScaleElement = env.getElementFromObject("paymentPointer", "assetScale");

		if (assetScaleElement == null || assetScaleElement.isJsonObject()) {
			throw error("assetScale is missing from payment pointer.");
		}

		// check if assetScale is a number
		try {
			Integer.parseInt(assetScaleElement.getAsString());
		} catch (NumberFormatException e) {
			throw error("assetScale is not a number.");
		}

		logSuccess("assetScale", args("actual", assetScaleElement));

		return env;
	}

}
