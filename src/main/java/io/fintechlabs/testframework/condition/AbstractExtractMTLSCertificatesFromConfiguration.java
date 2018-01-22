/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package io.fintechlabs.testframework.condition;

import java.util.Base64;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

/**
 * @author jricher
 *
 */
public abstract class AbstractExtractMTLSCertificatesFromConfiguration extends AbstractCondition {

	private static final String PEM_HEADER = ".*?-----BEGIN [^-]+-----";

	private static final String PEM_FOOTER = "-----END [^-]+-----.*";

	/**
	 * @param testId
	 * @param log
	 * @param optional
	 */
	public AbstractExtractMTLSCertificatesFromConfiguration(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	protected Environment extractMTLSCertificatesFromConfiguration(Environment env, String key) {
		// mutual_tls_authentication
		
		String certString = env.getString("config", key + ".cert");
		String keyString = env.getString("config", key + ".key");
		String caString = env.getString("config", key + ".ca");
		
		if (Strings.isNullOrEmpty(certString) || Strings.isNullOrEmpty(keyString)) {
			return error("Couldn't find TLS client certificate or key for MTLS");
		}
		
		if (Strings.isNullOrEmpty(caString)) {
			// Not an error; we just won't send a CA chain
			log("No certificate authority found for MTLS");
		}

		try {
			certString = stripPEM(certString);
			Base64.getDecoder().decode(certString);

			keyString = stripPEM(keyString);
			Base64.getDecoder().decode(keyString);

			if (caString != null) {
				caString = stripPEM(caString);
				Base64.getDecoder().decode(caString);
			}
		} catch (IllegalArgumentException e) {
			return error("Couldn't decode certificate, key, or CA chain from Base64", e, args("cert", certString, "key", keyString, "ca", Strings.emptyToNull(caString)));
		}

		JsonObject mtls = new JsonObject();
		mtls.addProperty("cert", certString);
		mtls.addProperty("key", keyString);
		if (caString != null) {
			mtls.addProperty("ca", caString);
		}
		
		env.put("mutual_tls_authentication", mtls);
		
		logSuccess("Mutual TLS authentication credentials loaded", mtls);
		
		return env;

	}

	private String stripPEM(String cert) {

		return cert.replaceAll(PEM_HEADER, "")
				.replaceAll(PEM_FOOTER, "")
				.replaceAll("[\r\n]", "");
	}

}
