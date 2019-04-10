package io.fintechlabs.testframework.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateAuthenticationRequestId extends AbstractCondition {

	public ValidateAuthenticationRequestId(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	@Override
	@PreEnvironment(required = "backchannel_authentication_endpoint_response")
	public Environment evaluate(Environment env) {
		JsonObject backchannelResponse = env.getObject("backchannel_authentication_endpoint_response");

		if (backchannelResponse == null || !backchannelResponse.isJsonObject()) {
			throw error("Backchannel Authentication Endpoint did not return a JSON object");
		}

		JsonElement authReqIdElement = backchannelResponse.get("auth_req_id");
		if (authReqIdElement == null) {
			throw error("auth_req_id in backchannel authentication endpoint can not be null.");
		}

		String authReqId = authReqIdElement.getAsString();
		if (Strings.isNullOrEmpty(authReqId)) {
			throw error("auth_req_id in backchannel authentication endpoint can not be empty.");
		}

		Matcher matcher = Pattern.compile("[A-Za-z0-9\\-_\\.]+").matcher(authReqId);
		if (!matcher.matches()) {
			throw error("auth_req_id contains characters other than A-Z, a-z, 0-9, '_', '-' and '.'.", args("auth_req_id", authReqId));
		}

		logSuccess("auth_req_id passed all validation checks");

		return env;
	}
}