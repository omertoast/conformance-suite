package net.openid.conformance.fapiciba.rp;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.testmodule.Environment;

public class CreateAuthorizationPendingResponse extends AbstractCondition {

	@Override
	@PostEnvironment(required = { "token_endpoint_response" })
	public Environment evaluate(Environment env) {

		Integer pollCount = env.getInteger("token_poll_count");
		if(pollCount == null) {
			pollCount = 0;
		}
		pollCount++;
		env.putInteger("token_poll_count", pollCount);

		JsonObject tokenEndpointResponse = new JsonObject();
		tokenEndpointResponse.addProperty("error", "authorization_pending");

		env.putObject("token_endpoint_response", tokenEndpointResponse);

		logSuccess("Created token endpoint response", tokenEndpointResponse);

		return env;

	}

}
