package io.fintechlabs.testframework.condition.client;

import com.google.gson.JsonParser;
import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.ConditionError;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CheckErrorFromTokenEndpointResponseErrorInvalidClientOrInvalidRequest_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private CheckErrorFromTokenEndpointResponseErrorInvalidClientOrInvalidRequest cond;

	@Before
	public void setUp() throws Exception {
		cond = new CheckErrorFromTokenEndpointResponseErrorInvalidClientOrInvalidRequest();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
	}

	@Test
	public void testEvaluate_caseInvalidRequest() {
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_request\"}").getAsJsonObject());

		cond.evaluate(env);
	}

	@Test
	public void testEvaluate_caseInvalidClient() {
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_client\"}").getAsJsonObject());

		cond.evaluate(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseAccessDenied() {
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"access_denied\"}").getAsJsonObject());

		cond.evaluate(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseInvalidRequestObject() {
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_request_object\"}").getAsJsonObject());

		cond.evaluate(env);
	}

}