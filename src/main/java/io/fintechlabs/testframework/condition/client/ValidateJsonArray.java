package io.fintechlabs.testframework.condition.client;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

public class ValidateJsonArray extends AbstractCondition {

	public ValidateJsonArray(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	public Environment validate(Environment env, String environmentVariable,
			List<String> setValues, Integer minimumMatchesRequired,
			String errorMessageNotEnough) {

		JsonElement serverValues = env.getElementFromObject("server", environmentVariable);
		String errorMessage = null;
		int foundCount = 0;

		if (serverValues == null) {
			errorMessage = environmentVariable + ": not found";
		} else {

			if (!serverValues.isJsonArray()) {
				errorMessage = "'" + environmentVariable + "' should be an array";
			} else {

				int viableSize = setValues.size();
				int serverSize = serverValues.getAsJsonArray().size();

				JsonArray serverData = serverValues.getAsJsonArray();

				for (int viableIndex = 0; viableIndex < viableSize; viableIndex++) {
					for (int serverIndex = 0; serverIndex < serverSize; serverIndex++) {
						if (setValues.get(viableIndex).equals(serverData.get(serverIndex).getAsString())) {
							foundCount++;
							break;
						}
					}
				}

				if (foundCount < minimumMatchesRequired) {
					errorMessage = errorMessageNotEnough;
				}
			}
		}

		if (errorMessage != null) {
			if (minimumMatchesRequired == 1) {
				throw error(errorMessage, args("discovery_metadata_key", environmentVariable, "expected_at_least_one_of", setValues, "actual", serverValues));
			}
			throw error(errorMessage, args("discovery_metadata_key", environmentVariable, "expected", setValues, "actual", serverValues));
		}

		logSuccess(environmentVariable, args("actual", serverValues, "expected", serverValues));

		return env;
	}

	@Override
	public Environment evaluate(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}
