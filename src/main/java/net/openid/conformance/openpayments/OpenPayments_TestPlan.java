package net.openid.conformance.openpayments;

import net.openid.conformance.plan.PublishTestPlan;
import net.openid.conformance.plan.TestPlan;

@PublishTestPlan (
	testPlanName = "open-payments-test-plan",
	displayName = "Open Payments Test Plan",
	profile = TestPlan.ProfileNames.openpaymentstest,
	testModules = {
		// Normal well behaved client cases
		OpenPaymentsQueryPaymentPointer.class,
		OpenPaymentsRequestGrant.class
	}
)
public class OpenPayments_TestPlan implements TestPlan {

}
