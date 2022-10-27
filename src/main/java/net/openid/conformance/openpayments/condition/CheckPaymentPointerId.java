package net.openid.conformance.openpayments.condition;

import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.openpayments.condition.paymentpointer.AbstractJsonUriIsValidAndHttps;
import net.openid.conformance.testmodule.Environment;

public class CheckPaymentPointerId extends AbstractJsonUriIsValidAndHttps {

	@Override
	@PreEnvironment(required = "paymentPointer")
	public Environment evaluate(Environment env) {
		return validate(env, "id");
	}
}
