package net.openid.conformance.openpayments.condition;

import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.openpayments.condition.paymentpointer.AbstractJsonUriIsValidAndHttps;
import net.openid.conformance.testmodule.Environment;

public class CheckAuthServerUri extends AbstractJsonUriIsValidAndHttps {
	private static final String environmentVariable = "authServer";

	@Override
	@PreEnvironment(required = "paymentPointer")
	public Environment evaluate(Environment env) {
		return validate(env, environmentVariable);
	}
}
