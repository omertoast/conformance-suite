package net.openid.conformance.openid;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.client.CallUserInfoEndpointWithBearerToken;
import net.openid.conformance.condition.client.EnsureUserInfoContainsSub;
import net.openid.conformance.condition.client.ExtractUserInfoFromUserInfoEndpointResponse;

public abstract class AbstractOIDCCUserInfoTest extends AbstractOIDCCServerTest {

	@Override
	protected void onPostAuthorizationFlowComplete() {
		callUserInfoEndpoint();
		callAndStopOnFailure(ExtractUserInfoFromUserInfoEndpointResponse.class);
		validateUserInfoResponse();
		fireTestFinished();
	}

	protected void callUserInfoEndpoint() {
		callAndStopOnFailure(CallUserInfoEndpointWithBearerToken.class, "OIDCC-5.3.1");
	}

	protected void validateUserInfoResponse() {
		callAndContinueOnFailure(EnsureUserInfoContainsSub.class, ConditionResult.FAILURE, "OIDCC-5.3.2");
	}

}
