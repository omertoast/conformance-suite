package io.fintechlabs.testframework.condition.client;

import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.condition.Condition.ConditionResult;
import io.fintechlabs.testframework.condition.client.ValidateHash;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

public class ValidateCHash extends ValidateHash {

	public ValidateCHash(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
		super.HashName = "c_hash";
	}

	@Override
	@PreEnvironment(strings = "state", required = "c_hash")
	public Environment evaluate(Environment env) {
		return super.evaluate(env);
	}

	
}
