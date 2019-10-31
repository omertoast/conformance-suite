package net.openid.conformance.condition.as;

import com.google.common.base.Strings;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class EnsureResponseTypeIsCodeIdToken extends AbstractCondition {

	@Override
	@PreEnvironment(required = "authorization_endpoint_request")
	public Environment evaluate(Environment env) {

		String responseType = env.getString("authorization_endpoint_request", "params.response_type");

		if (Strings.isNullOrEmpty(responseType)) {
			throw error("Could not find response type in request");
		} else if (!responseType.equals("code id_token")) {
			throw error("Response type is not expected value", args("expected", "id_token", "actual", responseType));
		} else {
			logSuccess("Response type is expected value", args("expected", "code id_token"));
			return env;
		}

	}

}