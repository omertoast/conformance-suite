/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package io.fintechlabs.testframework.condition.as;

import com.google.gson.JsonElement;

import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PostEnvironment;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

public class GetStaticResourceConfiguration extends AbstractCondition {

	/**
	 * @param testId
	 * @param log
	 */
	public GetStaticResourceConfiguration(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.Condition#evaluate(io.fintechlabs.testframework.testmodule.Environment)
	 */
	@Override
	@PreEnvironment(required = "config")
	@PostEnvironment(required = "resource", strings = "resource_id")
	public Environment evaluate(Environment env) {

		if (!env.containsObject("config")) {
			throw error("Couldn't find a configuration");
		}

		// make sure we've got a client object
		JsonElement resource = env.getElementFromObject("config", "resource");
		if (resource == null || !resource.isJsonObject()) {
			throw error("Definition for resource not present in supplied configuration");
		} else {
			// we've got a client object, put it in the environment
			env.put("resource", resource.getAsJsonObject());

			// pull out the resource ID and put it in the root environment for easy access
			env.putString("resource_id", env.getString("resource", "resource_id"));

			logSuccess("Found a static resource object", resource.getAsJsonObject());
			return env;
		}
	}

}
