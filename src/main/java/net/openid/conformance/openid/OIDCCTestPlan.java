package net.openid.conformance.openid;

import net.openid.conformance.plan.PublishTestPlan;
import net.openid.conformance.plan.TestPlan;

@PublishTestPlan(
	testPlanName = "oidcc-test-plan",
	displayName = "OpenID Connect Core: Authorization server test (not currently part of certification program)",
	profile = TestPlan.ProfileNames.optest,
	testModules = {
		OIDCCDiscoveryEndpointVerification.class,

		// Dynamic registration tests
		OIDCCRegistrationJwksUri.class,
		OIDCCRegistrationLogoUri.class,
		OIDCCRegistrationPolicyUri.class,
		OIDCCRegistrationSectorUri.class,
		OIDCCRegistrationTosUri.class,

		// Positive tests
		OIDCCServerTest.class,
		OIDCCAuthCodeReuse.class,
		OIDCCRefreshToken.class,
		OIDCCEnsureRequestWithoutNonceSucceedsForCodeFlow.class,
		OIDCCIdTokenRS256.class,
		OIDCCIdTokenSignature.class,
		OIDCCIdTokenUnsigned.class,
		OIDCCScopeAddress.class,
		OIDCCScopeAll.class,
		OIDCCScopeEmail.class,
		OIDCCScopePhone.class,
		OIDCCScopeProfile.class,
		OIDCCPromptLogin.class,
		OIDCCPromptNoneLoggedIn.class,
		OIDCCPromptNoneNotLoggedIn.class,
		OIDCCRedirectUriQueryOK.class,
		OIDCCClaimsLocales.class,
		OIDCCIdTokenHint.class,
		OIDCCLoginHint.class,
		OIDCCMaxAge1.class,
		OIDCCUserInfoGet.class,
		OIDCCUserInfoPostBody.class,
		OIDCCUserInfoPostHeader.class,
		OIDCCEnsureRequestWithAcrValuesSucceeds.class,

		// negative tests
		OIDCCEnsureRedirectUriInAuthorizationRequest.class,
		OIDCCEnsureRegisteredRedirectUri.class,
		OIDCCEnsureRequestObjectWithRedirectUri.class,
		OIDCCEnsureRequestWithoutNonceFails.class,
		OIDCCRedirectUriQueryAdded.class,
		OIDCCRedirectUriQueryMismatch.class,
		OIDCCRedirectUriRegFrag.class,
		OIDCCRegistrationSectorBad.class,
		OIDCCResponseTypeMissing.class,
	}
)
public class OIDCCTestPlan implements TestPlan {

}
