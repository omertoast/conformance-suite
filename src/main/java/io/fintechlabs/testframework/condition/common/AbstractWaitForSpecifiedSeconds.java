package io.fintechlabs.testframework.condition.common;

import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

import java.util.concurrent.TimeUnit;

public abstract class AbstractWaitForSpecifiedSeconds extends AbstractCondition {

	protected abstract long getExpectedWaitSeconds(Environment env);

	public AbstractWaitForSpecifiedSeconds(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	@Override
	public Environment evaluate(Environment env) {
		try {
			long expectedWaitSeconds = getExpectedWaitSeconds(env);
			logSuccess("Pausing for " + expectedWaitSeconds + " seconds");

			TimeUnit.SECONDS.sleep(expectedWaitSeconds);

			logSuccess("Paused for " + expectedWaitSeconds + " seconds");
		} catch (InterruptedException e) {
			throw error("Interrupted while sleeping", e);
		}
		return env;
	}


}