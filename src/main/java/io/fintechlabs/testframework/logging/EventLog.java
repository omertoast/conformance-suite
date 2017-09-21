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

package io.fintechlabs.testframework.logging;

import com.google.gson.JsonObject;

/**
 * @author jricher
 *
 */
public interface EventLog {

	/**
	 * @param source The source of the event
	 * @param msg The message to log
	 */
	void log(String source, String msg);

	/**
	 * @param source The source of the event
	 * @param obj The message to log
	 */
	void log(String source, JsonObject obj);
}