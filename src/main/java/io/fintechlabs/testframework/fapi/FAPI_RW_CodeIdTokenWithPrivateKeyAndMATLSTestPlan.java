package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.plan.PublishTestPlan;
import io.fintechlabs.testframework.plan.TestPlan;

@PublishTestPlan(
	testPlanName = "fapi-rw-code-id-token-with-private-key-and-matls-test-plan",
	displayName = "FAPI-RW: code id_token with private key and matls Test Plan",
	profile = "FAPI-RW",
	testModuleNames = {
		"fapi-rw-ensure-client-id-in-token-endpoint-with-private-key-and-matls",
		"fapi-rw-ensure-request-object-without-exp-fails-with-private-key-and-matls",
		"fapi-rw-ensure-request-object-without-scope-fails-with-private-key-and-matls",
		"fapi-rw-ensure-request-object-without-state-fails-with-private-key-and-matls",
		"fapi-rw-ensure-request-object-without-nonce-fails-with-private-key-and-matls",
		"fapi-rw-ensure-request-object-without-redirect-uri-fails-with-private-key-and-matls",
		"fapi-rw-ensure-request-object-with-multiple-aud-succeeds-with-private-key-and-matls",
		"fapi-rw-ensure-expired-request-object-fails-with-private-key-and-matls",
		"fapi-rw-ensure-different-nonce-inside-and-outside-request-object-with-private-key-and-matls",
		"fapi-rw-ensure-response-type-code-fails-with-private-key-and-matls",
		"fapi-rw-ensure-request-object-with-bad-aud-fails-with-private-key-and-matls",
	}
)
public class FAPI_RW_CodeIdTokenWithPrivateKeyAndMATLSTestPlan implements TestPlan {

}