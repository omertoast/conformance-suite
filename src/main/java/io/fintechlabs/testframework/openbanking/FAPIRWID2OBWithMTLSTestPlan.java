package io.fintechlabs.testframework.openbanking;

import io.fintechlabs.testframework.plan.PublishTestPlan;
import io.fintechlabs.testframework.plan.TestPlan;

@PublishTestPlan (
	testPlanName = "fapi-rw-id2-ob-with-mtls-test-plan",
	displayName = "FAPI-RW-ID2-OB: with mtls Test Plan",
	profile = "FAPI-RW-ID2-OB",
	testModuleNames = {
		"fapi-rw-id2-ob-discovery-end-point-verification",
		"fapi-rw-id2-ob-with-mtls",
		"fapi-rw-id2-ob-ensure-mtls-holder-of-key-required-with-mtls",
		"fapi-rw-id2-ob-ensure-matching-key-in-authorization-request-with-mtls",
		"fapi-rw-id2-ob-ensure-redirect-uri-in-authorization-request-with-mtls",
		"fapi-rw-id2-ob-ensure-registered-certificate-for-authorization-code-with-mtls",
		"fapi-rw-id2-ob-ensure-registered-redirect-uri-with-mtls",
		"fapi-rw-id2-ob-ensure-request-object-signature-algorithm-is-not-none-with-mtls",
		"fapi-rw-id2-ob-user-rejects-authentication-with-mtls",
		"fapi-rw-id2-ob-ensure-server-handles-non-matching-intent-id-with-mtls",
		"fapi-rw-id2-ob-ensure-request-object-without-exp-fails-with-mtls",
		"fapi-rw-id2-ob-ensure-request-object-without-scope-fails-with-mtls",
		"fapi-rw-id2-ob-ensure-request-object-without-state-with-mtls",
		"fapi-rw-id2-ob-ensure-request-object-without-nonce-fails-with-mtls",
		"fapi-rw-id2-ob-ensure-request-object-without-redirect-uri-fails-with-mtls",
		"fapi-rw-id2-ob-ensure-request-object-with-multiple-aud-succeeds-with-mtls",
		"fapi-rw-id2-ob-ensure-wrong-client-id-in-token-endpoint-fails-with-mtls",
		"fapi-rw-id2-ob-ensure-expired-request-object-fails-with-mtls",
		"fapi-rw-id2-ob-ensure-different-nonce-inside-and-outside-request-object-with-mtls",
		"fapi-rw-id2-ob-ensure-response-type-code-fails-with-mtls",
		"fapi-rw-id2-ob-ensure-request-object-with-bad-aud-fails-with-mtls",
		"fapi-rw-id2-ob-ensure-signed-request-object-with-RS256-fails-with-mtls",
	}
)
public class FAPIRWID2OBWithMTLSTestPlan implements TestPlan {

}
