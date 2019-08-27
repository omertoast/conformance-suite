package io.fintechlabs.testframework.condition.client;

import com.google.common.base.Strings;
import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.testmodule.Environment;

public abstract class AbstractEnsureSpecifiedErrorFromAuthorizationEndpointResponse extends AbstractCondition {

	protected abstract String getExpectedError();

	@Override
	@PreEnvironment(required = "authorization_endpoint_response")
	public Environment evaluate(Environment env) {

		String error = env.getString("authorization_endpoint_response", "error");
		String expected = getExpectedError();

		if (Strings.isNullOrEmpty(error)) {
			throw error("Expected 'error' field not found");
		} else if (!error.equals(expected)) {
			throw error("'error' field has unexpected value", args("expected", expected, "actual", error));
		} else {
			logSuccess("Authorization endpoint returned expected 'error' of '"+expected+"'", args("error", error));

			return env;
		}
	}
}