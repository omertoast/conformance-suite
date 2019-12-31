package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class AddCibaTokenDeliveryModePollToDynamicRegistrationRequest extends AbstractCondition {

	@Override
	@PreEnvironment(required = "dynamic_registration_request")
	@PostEnvironment(required = "dynamic_registration_request")
	public Environment evaluate(Environment env) {

		JsonObject dynamicRegistrationRequest = env.getObject("dynamic_registration_request");

		dynamicRegistrationRequest.addProperty("backchannel_token_delivery_mode", "poll");

		env.putObject("dynamic_registration_request", dynamicRegistrationRequest);

		log("Added CIBA token delivery mode poll to dynamic registration request", args("dynamic_registration_request", dynamicRegistrationRequest));

		return env;
	}
}