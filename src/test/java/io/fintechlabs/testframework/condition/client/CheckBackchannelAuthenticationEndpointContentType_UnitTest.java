package io.fintechlabs.testframework.condition.client;

import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.ConditionError;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CheckBackchannelAuthenticationEndpointContentType_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private CheckBackchannelAuthenticationEndpointContentType cond;

	@Before
	public void setUp() throws Exception {

		cond = new CheckBackchannelAuthenticationEndpointContentType("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);

	}

	private void setHeader(Environment env, String value) {

		JsonObject headers = new JsonObject();
		headers.addProperty("content-type", value);
		env.putObject("backchannel_authentication_endpoint_response_headers", headers);

	}

	@Test
	public void testEvaluate_noError() {
		setHeader(env, "application/json");

		cond.evaluate(env);
	}

	@Test
	public void testEvaluate_withCharset() {
		setHeader(env, "application/json   ; charset=UTF-8");

		cond.evaluate(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_invalid() {
		setHeader(env, "application/jsonmoo");

		cond.evaluate(env);
	}

}
