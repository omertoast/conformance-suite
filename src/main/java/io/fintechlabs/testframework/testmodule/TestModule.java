/** *****************************************************************************
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
 ****************************************************************************** */
package io.fintechlabs.testframework.testmodule;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;

import io.fintechlabs.testframework.frontChannel.BrowserControl;
import io.fintechlabs.testframework.logging.EventLog;

/**
 * @author jricher
 *
 */
public interface TestModule {

    public static enum Status {
        CREATED, // test has been instantiated 
        CONFIGURED, // configuration files have been sent and set up
        RUNNING, // test is executing
        WAITING, // test is waiting for external input
        FINISHED // test is no longer running 
    }
    
    public static enum Result {
    	PASSED, // test has passed successfully
    	FAILED, // test has failed
    	WARNING // test has warnings
    }

    /**
     * *
     * Method is called to pass configuration parameters
     *
     * @param config A JSON object consisting of details that the testRunner
     * doesn't need to know about
     * @param eventLog The event Logging object to log details of the test
     * @param id The id of this test
     * @param browser The "front channel" control
     * @param baseUrl The base of the URL that will need to be appended to any
     * URL construction.
     */
    void configure(JsonObject config, EventLog eventLog, String id, BrowserControl browser, String baseUrl);

    /**
     * *
     *
     * @return The name of the test.
     */
    String getName();

    /**
     * @return the id of this test
     */
    String getId();

    /**
     * @return The current status of the test
     */
    Status getStatus();

    /**
     * Called by the TestRunner to start the test
     */
    void start();

    /**
     * Called by the test runner to stop the test
     */
    void stop();

    /**
     * *
     * Adds an event listener to the test module. This provides communication
     * Back to the listener regarding the current status of the test
     *
     * @param The testModuleEventLister to be added
     * @return True if successful
     */
    boolean addListener(TestModuleEventListener e);

    /**
     * Remove an event listener
     *
     * @param o
     * @return
     */
    boolean removeListener(TestModuleEventListener o);

    /**
     * Called when a the test runner calls a URL
     *
     * @param path The path that was called
     * @param req The request that passed to the server
     * @param res A response that will be sent from the server
     * @param session Session details
     * @param params Parameters from the request
     * @param m
     * @return A ModelAndView for the response.
     */
    ModelAndView handleHttp(String path,
            HttpServletRequest req, HttpServletResponse res,
            HttpSession session,
            @RequestParam MultiValueMap<String, String> params,
            Model m);

}