package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GetDynamicServerConfiguration_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	@ClassRule
	public static HoverflyRule hoverfly = HoverflyRule.inSimulationMode(dsl(
		service("good.example.com")
			.get("/.well-known/openid-configuration")
			.willReturn(success("{\"jwks_uri\":\"https://good.example.com/jwks.json\"}", "application/json")),
		service("bad.example.com")
			.get("/.well-known/openid-configuration")
			.willReturn(success("This is not JSON!", "text/plain")),
		service("empty.example.com")
			.get("/.well-known/openid-configuration")
			.willReturn(success("", "application/json"))));

	private GetDynamicServerConfiguration cond;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		hoverfly.resetJournal();

		cond = new GetDynamicServerConfiguration();

		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);
	}

	/**
	 * Test method for {@link GetDynamicServerConfiguration#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_noError() {

		JsonObject config = JsonParser.parseString("{"
			+ "\"server\":{"
			+ "\"discoveryUrl\":\"https://good.example.com/.well-known/openid-configuration\""
			+ "}}").getAsJsonObject();
		env.putObject("config", config);

		cond.execute(env);

		hoverfly.verify(service("good.example.com").get("/.well-known/openid-configuration"));

		verify(env, atLeastOnce()).getString("config", "server.discoveryUrl");

		assertThat(env.getObject("server")).isNotNull();
		assertThat(env.getString("server", "jwks_uri")).isEqualTo("https://good.example.com/jwks.json");
	}

	/**
	 * Test method for {@link GetDynamicServerConfiguration#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_fallbackToIssuer() {

		JsonObject config = JsonParser.parseString("{"
			+ "\"server\":{"
			+ "\"discoveryIssuer\":\"https://good.example.com\""
			+ "}}").getAsJsonObject();
		env.putObject("config", config);

		cond.execute(env);
	}

	/**
	 * Test method for {@link GetDynamicServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_discoveryUrlTakesPriority() {

		JsonObject config = JsonParser.parseString("{"
			+ "\"server\":{"
			+ "\"discoveryUrl\":\"https://good.example.com/not-here\","
			+ "\"issuer\":\"https://good.example.com\""
			+ "}}").getAsJsonObject();
		env.putObject("config", config);

		cond.execute(env);
	}

	/**
	 * Test method for {@link GetDynamicServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_noServer() {

		JsonObject config = JsonParser.parseString("{"
			+ "\"server\":{"
			+ "\"discoveryIssuer\":\"https://nonexisting.example.com\""
			+ "}}").getAsJsonObject();
		env.putObject("config", config);

		cond.execute(env);
	}

	/**
	 * Test method for {@link GetDynamicServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_badResponse() {

		JsonObject config = JsonParser.parseString("{"
			+ "\"server\":{"
			+ "\"discoveryIssuer\":\"https://bad.example.com\""
			+ "}}").getAsJsonObject();
		env.putObject("config", config);

		cond.execute(env);
	}

	/**
	 * Test method for {@link GetDynamicServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_emptyResponse() {

		JsonObject config = JsonParser.parseString("{"
			+ "\"server\":{"
			+ "\"discoveryIssuer\":\"https://empty.example.com\""
			+ "}}").getAsJsonObject();
		env.putObject("config", config);

		cond.execute(env);
	}

	/**
	 * Test method for {@link GetDynamicServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingUrls() {

		JsonObject config = JsonParser.parseString("{\"server\":{}}").getAsJsonObject();
		env.putObject("config", config);

		cond.execute(env);
	}

	/**
	 * Test method for {@link GetDynamicServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingConfig() {

		cond.execute(env);
	}
}
