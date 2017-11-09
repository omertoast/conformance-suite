package io.fintechlabs.testframework.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import io.fintechlabs.testframework.logging.EventLog;
import io.fintechlabs.testframework.testmodule.Environment;

@RunWith(MockitoJUnitRunner.class)
public class CreateBadRedirectUri_UnitTest {
	
	@Spy
	private Environment env = new Environment();
	
	@Mock
	private EventLog eventLog;
	
	private CreateBadRedirectUri cond;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		cond = new CreateBadRedirectUri("UNIT-TEST", eventLog, false);
	}

	/**
	 * Test method for {@link io.fintechlabs.testframework.condition.CreateBadRedirectUri#evaluate(io.fintechlabs.testframework.testmodule.Environment)}.
	 */
	@Test
	public void testEvaluate_valuePresent() {
		
		env.putString("base_url", "https://example.com");
		
		cond.evaluate(env);
		
		verify(env, atLeastOnce()).getString("base_url");
		
		assertThat(env.getString("redirect_uri")).isNotBlank();
	}

	/**
	 * Test method for {@link io.fintechlabs.testframework.condition.CreateBadRedirectUri#evaluate(io.fintechlabs.testframework.testmodule.Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_valueMissing() {
		
		cond.evaluate(env);
	}
}