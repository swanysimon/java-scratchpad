package com.github.scratchpad

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

/**
 * Significantly improves the default test logging from gradle commands. Notable features:
 *  - always prints out when a test failed
 *  - when not in quiet mode, prints a full stacktrace, as well
 *  - debug mode shows output sent to stdout and stderr
 *  - when not in quiet mode, at end of a given collection of tests, prints a brief summary of what happened
 *      - if you ran a single test, the collection will be the single test
 *      - if you ran a test class, the collection will be the tests in the class
 *      - if you ran a suite, the collection will be all the tests in all the test classes in the suite
 *      - if you ran :<module>:test in gradle, the collection will be all tests in the module
 */
class TestProgressLoggingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.tasks.withType(Test.class) {

            testLogging {

                // default logging
                events "FAILED"
                exceptionFormat "FULL"

                quiet {
                    // doesn't get overriden by default logging
                    events "FAILED"
                }

                info {
                    events "SKIPPED", "FAILED"
                }

                debug {
                    events "STARTED", "SKIPPED", "PASSED", "FAILED"
                    showStandardStreams true
                }
            }

            afterSuite { desc, result ->
                if (!desc.parent && result.testCount > 0) { // will match module
                    logger.lifecycle(
                            "Test Results: {} {} tests, {} passed, {} skipped, {} failed",
                            result.resultType,
                            result.testCount,
                            result.successfulTestCount,
                            result.skippedTestCount,
                            result.failedTestCount)
                }
            }
        }
    }
}
