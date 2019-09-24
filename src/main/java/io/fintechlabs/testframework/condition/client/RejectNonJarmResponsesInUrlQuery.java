package io.fintechlabs.testframework.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.testmodule.Environment;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class RejectNonJarmResponsesInUrlQuery extends AbstractCondition {

	@Override
	@PreEnvironment(strings = "redirect_uri", required = "callback_query_params")
	public Environment evaluate(Environment env) {
		var params = (JsonObject) env.getObject("callback_query_params").deepCopy();

		params.remove("response");

		// ignore items we require to be in the registered redirect url query for the second client
		UriComponents redirectUri = UriComponentsBuilder.fromHttpUrl(env.getString("redirect_uri")).build();

		Map<String, String> expectedParams = redirectUri.getQueryParams().toSingleValueMap();

		for (Map.Entry<String, String> entry : expectedParams.entrySet()) {
			String key = entry.getKey();
			params.remove(key);
		}

		if (params.size() > 0) {
			throw error("When using JARM, The authorization endpoint response should only contain 'response' containing a JWT.", params);
		}

		logSuccess("Authorization endpoint response only includes the JARM JWT.");

		return env;
	}

}