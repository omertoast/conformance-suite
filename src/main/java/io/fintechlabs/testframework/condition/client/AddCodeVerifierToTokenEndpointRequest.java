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

package io.fintechlabs.testframework.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PostEnvironment;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

/**
 * @author srmoore
 *
 */
public class AddCodeVerifierToTokenEndpointRequest extends AbstractCondition {

	/**
	 * @param testId
	 * @param log
	 * @param optional
	 */
	public AddCodeVerifierToTokenEndpointRequest(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.condition.Condition#evaluate(io.fintechlabs.testframework.testmodule.Environment)
	 */
	@Override
	@PreEnvironment(required = "token_endpoint_request_form_parameters", strings = "code_verifier")
	@PostEnvironment(required = "token_endpoint_request_form_parameters")
	public Environment evaluate(Environment env) {

		String code_verifier = env.getString("code_verifier");
		if (Strings.isNullOrEmpty(code_verifier)) {
			return error("Couldn't find code_verifier value");
		}

		if (!env.containsObj("token_endpoint_request_form_parameters")) {
			return error("Couldn't find request form");
		}

		JsonObject o = env.get("token_endpoint_request_form_parameters");

		o.addProperty("code_verifier", code_verifier);

		env.put("token_endpoint_request_form_parameters", o);

		log(o);

		return env;

	}

}