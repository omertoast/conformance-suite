package io.fintechlabs.testframework.condition.client;

import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

public class CheckTokenEndpointHttpStatus401 extends AbstractCondition {

	public CheckTokenEndpointHttpStatus401(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	@Override
	public Environment evaluate(Environment env) {
		Integer httpStatus = env.getInteger("token_endpoint_response_http_status");

		if (httpStatus == null) {
			throw error("Http status can not be null.");
		}

		if (httpStatus != 401) {
			throw error("Invalid http status " + httpStatus);
		}

		logSuccess("Token endpoint http status code was 401");

		return env;
	}

}