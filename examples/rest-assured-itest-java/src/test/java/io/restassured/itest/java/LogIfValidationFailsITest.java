/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.restassured.itest.java;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.io.StringWriter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class LogIfValidationFailsITest extends WithJetty {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Before
    public void setup() throws Exception {
        RestAssured.config = RestAssuredConfig.config().logConfig(LogConfig.logConfig().enablePrettyPrinting(false));
    }

    @After
    public void teardown() throws Exception {
        RestAssured.reset();
    }

    @Test
    public void worksForRequestSpecificationsUsingGivenWhenThenSyntax() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor))).
                    log().ifValidationFails().
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
            when().
                    get("/greet").
            then().
                    statusCode(400);

            fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(writer.toString(), equalTo("Request method:\tGET" + LINE_SEPARATOR + "Request URI:\thttp://localhost:8080/greet?firstName=John&lastName=Doe" + LINE_SEPARATOR +
                    "Proxy:\t\t\t<none>" + LINE_SEPARATOR + "Request params:\tfirstName=John" + LINE_SEPARATOR + "\t\t\t\tlastName=Doe" + LINE_SEPARATOR + "Query params:\t<none>" + LINE_SEPARATOR +
                    "Form params:\t<none>" + LINE_SEPARATOR + "Path params:\t<none>" + LINE_SEPARATOR +
                    "Headers:\t\tAccept=*/*" + LINE_SEPARATOR + "\t\t\t\tContent-Type=application/json; charset=" + RestAssured.config().getEncoderConfig().defaultCharsetForContentType(ContentType.JSON) + LINE_SEPARATOR + "Cookies:\t\t<none>" + LINE_SEPARATOR + "Multiparts:\t\t<none>" + LINE_SEPARATOR + "Body:\t\t\t<none>" + LINE_SEPARATOR));
        }
    }

    @Test
    public void worksForRequestSpecificationsUsingLegacySyntax() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor))).
                    log().ifValidationFails().
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
            expect().
                    statusCode(400).
            when().
                    get("/greet");

            fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(writer.toString(), equalTo("Request method:\tGET" + LINE_SEPARATOR + "Request URI:\thttp://localhost:8080/greet?firstName=John&lastName=Doe" + LINE_SEPARATOR +
                    "Proxy:\t\t\t<none>" + LINE_SEPARATOR + "Request params:\tfirstName=John" + LINE_SEPARATOR + "\t\t\t\tlastName=Doe" + LINE_SEPARATOR + "Query params:\t<none>" + LINE_SEPARATOR +
                    "Form params:\t<none>" + LINE_SEPARATOR + "Path params:\t<none>" + LINE_SEPARATOR +
                    "Headers:\t\tAccept=*/*" + LINE_SEPARATOR + "\t\t\t\tContent-Type=application/json; charset=" + RestAssured.config().getEncoderConfig().defaultCharsetForContentType(ContentType.JSON) + LINE_SEPARATOR + "Cookies:\t\t<none>" + LINE_SEPARATOR + "Multiparts:\t\t<none>" + LINE_SEPARATOR + "Body:\t\t\t<none>" + LINE_SEPARATOR));
        }
    }

    @Test
    public void doesntLogRequestSpecificationsUsingGivenWhenThenSyntaxWhenValidationSucceeds() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        RestAssured.given().
                config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor))).
                log().ifValidationFails().
                param("firstName", "John").
                param("lastName", "Doe").
                header("Content-type", "application/json").
        when().
                get("/greet").
        then().
                statusCode(200);

        assertThat(writer.toString(), isEmptyString());
    }

    @Test
    public void doesntLogRequestSpecificationsUsingLegacySyntaxWhenValidationSucceeds() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        RestAssured.given().
                config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor))).
                log().ifValidationFails().
                param("firstName", "John").
                param("lastName", "Doe").
                header("Content-type", "application/json").
        expect().
                statusCode(200).
        when().
                get("/greet");

        assertThat(writer.toString(), isEmptyString());
    }

    @Test
    public void worksForResponseSpecificationsUsingGivenWhenThenSyntax() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssuredConfig.config().logConfig(new LogConfig(captor, false))).
                    pathParam("firstName", "John").
                    pathParam("lastName", "Doe").
            when().
                    get("/{firstName}/{lastName}").
            then().
                    log().ifValidationFails(LogDetail.BODY).
                    body("fullName", equalTo("John Doe2"));

            fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(writer.toString(), equalTo("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"fullName\":\"John Doe\"}" + LINE_SEPARATOR));
        }
    }

    @Test
    public void worksForResponseSpecificationsUsingLegacySyntax() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssuredConfig.config().logConfig(new LogConfig(captor, false))).
                    pathParam("firstName", "John").
                    pathParam("lastName", "Doe").
            expect().
                    log().ifValidationFails(LogDetail.BODY).
                    body("fullName", equalTo("John Doe2")).
            when().
                    get("/{firstName}/{lastName}");

            fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(writer.toString(), equalTo("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"fullName\":\"John Doe\"}" + LINE_SEPARATOR));
        }
    }

    @Test
    public void doesntLogUsingGivenWhenThenSyntaxWhenValidationSucceeds() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        RestAssured.given().
                config(RestAssuredConfig.config().logConfig(new LogConfig(captor, false))).
                pathParam("firstName", "John").
                pathParam("lastName", "Doe").
        when().
                get("/{firstName}/{lastName}").
        then().
                log().ifValidationFails(LogDetail.BODY).
                body("fullName", equalTo("John Doe"));
        assertThat(writer.toString(), isEmptyString());
    }

    @Test
    public void doesntLogResponseSpecUsingLegacySyntaxWhenValidationSucceeds() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        RestAssured.given().
                config(RestAssuredConfig.config().logConfig(new LogConfig(captor, false))).
                pathParam("firstName", "John").
                pathParam("lastName", "Doe").
        expect().
                log().ifValidationFails(LogDetail.BODY).
                body("fullName", equalTo("John Doe")).
        when().
                get("/{firstName}/{lastName}");

        assertThat(writer.toString(), isEmptyString());
    }

    @Test
    public void configuringLogConfigToEnableLoggingOfRequestAndResponseIfValidationFailsWorksAsExpected() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor).and().enableLoggingOfRequestAndResponseIfValidationFails())).
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
            when().
                    get("/greet").
            then().
                    statusCode(400);

              fail("Should throw AssertionError");
          } catch (AssertionError e) {
              assertThat(writer.toString(), equalTo("Request method:\tGET"+LINE_SEPARATOR+"Request URI:\thttp://localhost:8080/greet?firstName=John&lastName=Doe"+LINE_SEPARATOR+
                      "Proxy:\t\t\t<none>"+LINE_SEPARATOR+"Request params:\tfirstName=John"+LINE_SEPARATOR+"\t\t\t\tlastName=Doe"+LINE_SEPARATOR+"Query params:\t<none>"+LINE_SEPARATOR+
                      "Form params:\t<none>"+LINE_SEPARATOR+"Path params:\t<none>"+LINE_SEPARATOR+"Headers:\t\tAccept=*/*"+LINE_SEPARATOR+
                      "\t\t\t\tContent-Type=application/json; charset="+ RestAssured.config().getEncoderConfig().defaultCharsetForContentType(ContentType.JSON)+LINE_SEPARATOR+
                      "Cookies:\t\t<none>" + LINE_SEPARATOR + "Multiparts:\t\t<none>"+LINE_SEPARATOR+"Body:\t\t\t<none>" + LINE_SEPARATOR+LINE_SEPARATOR+
                      "HTTP/1.1 200 OK"+LINE_SEPARATOR+"Content-Type: application/json;charset=utf-8"+LINE_SEPARATOR+"Content-Length: 33"+LINE_SEPARATOR+"Server: Jetty(9.3.2.v20150730)"+LINE_SEPARATOR+""+LINE_SEPARATOR+"{"+LINE_SEPARATOR+"    \"greeting\": \"Greetings John Doe\""+LINE_SEPARATOR+"}"+LINE_SEPARATOR+""));
          }
    }

    @Test
    public void configuringLogConfigToEnableLoggingOfRequestAndResponseIfValidationFailsWorksAsExpected2() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor).and().enableLoggingOfRequestAndResponseIfValidationFails())).
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
            when().
                    get("/greet").
            then().
                    body("room.size()", is(2));

              fail("Should throw IllegalArgumentException");
          } catch (IllegalArgumentException e) {
              assertThat(writer.toString(), equalTo("Request method:\tGET"+LINE_SEPARATOR+"Request URI:\thttp://localhost:8080/greet?firstName=John&lastName=Doe"+LINE_SEPARATOR+
                      "Proxy:\t\t\t<none>"+LINE_SEPARATOR+"Request params:\tfirstName=John"+LINE_SEPARATOR+"\t\t\t\tlastName=Doe"+LINE_SEPARATOR+"Query params:\t<none>"+LINE_SEPARATOR+
                      "Form params:\t<none>"+LINE_SEPARATOR+"Path params:\t<none>"+LINE_SEPARATOR+"Headers:\t\tAccept=*/*"+LINE_SEPARATOR+
                      "\t\t\t\tContent-Type=application/json; charset="+ RestAssured.config().getEncoderConfig().defaultCharsetForContentType(ContentType.JSON)+LINE_SEPARATOR+
                      "Cookies:\t\t<none>" + LINE_SEPARATOR + "Multiparts:\t\t<none>"+LINE_SEPARATOR+"Body:\t\t\t<none>" + LINE_SEPARATOR+LINE_SEPARATOR+
                      "HTTP/1.1 200 OK"+LINE_SEPARATOR+"Content-Type: application/json;charset=utf-8"+LINE_SEPARATOR+"Content-Length: 33"+LINE_SEPARATOR+"Server: Jetty(9.3.2.v20150730)"+LINE_SEPARATOR+""+LINE_SEPARATOR+"{"+LINE_SEPARATOR+"    \"greeting\": \"Greetings John Doe\""+LINE_SEPARATOR+"}"+LINE_SEPARATOR+""));
          }
    }

    @Test
    public void configuringLogConfigToEnableLoggingOfRequestAndResponseIfValidationFailsWorksAsExpectedWhenSpecifyingLogDetail() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor).and().enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.HEADERS))).
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
            when().
                    get("/greet").
            then().
                    statusCode(400);

              fail("Should throw AssertionError");
          } catch (AssertionError e) {
            assertThat(writer.toString(), equalTo("Headers:\t\tAccept=*/*" + LINE_SEPARATOR +
                    "\t\t\t\tContent-Type=application/json; charset=" + RestAssured.config().getEncoderConfig().defaultCharsetForContentType(ContentType.JSON) + LINE_SEPARATOR + LINE_SEPARATOR +
                    "Content-Type: application/json;charset=utf-8" + LINE_SEPARATOR + "Content-Length: 33" + LINE_SEPARATOR + "Server: Jetty(9.3.2.v20150730)" + LINE_SEPARATOR));
          }
    }

    @Test
    public void configuredLoggingInGivenOverwritesTheLoggingSpecifiedInLogConfig() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor).and().enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.HEADERS))).
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
                    log().all().
            when().
                    get("/greet").
            then().
                    statusCode(400);

              fail("Should throw AssertionError");
          } catch (AssertionError e) {
            assertThat(writer.toString(), equalTo("Request method:\tGET" + LINE_SEPARATOR + "Request URI:\thttp://localhost:8080/greet?firstName=John&lastName=Doe" + LINE_SEPARATOR +
                    "Proxy:\t\t\t<none>" + LINE_SEPARATOR + "Request params:\tfirstName=John" + LINE_SEPARATOR + "\t\t\t\tlastName=Doe" + LINE_SEPARATOR + "Query params:\t<none>" + LINE_SEPARATOR +
                    "Form params:\t<none>" + LINE_SEPARATOR + "Path params:\t<none>" + LINE_SEPARATOR + "Headers:\t\tAccept=*/*" + LINE_SEPARATOR +
                    "\t\t\t\tContent-Type=application/json; charset=" + RestAssured.config().getEncoderConfig().defaultCharsetForContentType(ContentType.JSON) + LINE_SEPARATOR +
                    "Cookies:\t\t<none>" + LINE_SEPARATOR + "Multiparts:\t\t<none>" + LINE_SEPARATOR + "Body:\t\t\t<none>" + LINE_SEPARATOR +
                    "Content-Type: application/json;charset=utf-8" + LINE_SEPARATOR + "Content-Length: 33" + LINE_SEPARATOR + "Server: Jetty(9.3.2.v20150730)" + LINE_SEPARATOR));
          }
    }

    @Test public void
    logging_of_both_request_and_response_validation_works_when_test_fails_when_using_static_response_and_request_specs_declared_before_enable_logging() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        RestAssured.responseSpecification = new ResponseSpecBuilder().expectStatusCode(200).build();
        RestAssured.requestSpecification = new RequestSpecBuilder().setConfig(RestAssuredConfig.config().logConfig(new LogConfig(captor, true))).
                addHeader("Api-Key", "1234").build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.HEADERS);

        try {
            RestAssured.given().
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
            when().
                    get("/greet").
            then().
                    body("firstName", equalTo("Hello, Johan2!"));

            fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(writer.toString(), equalTo("Headers:\t\t" +
                    "Api-Key=1234" + LINE_SEPARATOR + "\t\t\t\tAccept=*/*" + LINE_SEPARATOR +
                    "\t\t\t\tContent-Type=application/json; charset=" + RestAssured.config().getEncoderConfig().defaultCharsetForContentType(ContentType.JSON) + LINE_SEPARATOR + LINE_SEPARATOR +
                    "Content-Type: application/json;charset=utf-8\nContent-Length: 33\nServer: Jetty(9.3.2.v20150730)\n"));
        }
    }

    @Test public void
    doesnt_log_request_or_response_when_test_fails_when_using_non_static_request_spec_declared_before_enable_logging_since_config_is_immutable_and_spec_config_has_precedence_over_global_config() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        RequestSpecification specification = new RequestSpecBuilder().
                setConfig(RestAssured.config().logConfig(RestAssured.config().getLogConfig().defaultStream(captor).and().enablePrettyPrinting(true))).
                addHeader("Api-Key", "1234").build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.HEADERS);

        try {
            RestAssured.given().
                    spec(specification).
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
            when().
                    get("/greet").
            then().
                    body("firstName", equalTo("Hello, Johan2!"));

            fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(writer.toString(), isEmptyString());
        }
    }

    @Test public void
    logging_of_both_request_and_response_validation_works_when_test_fails_when_using_non_static_request_spec_declared_after_enable_logging() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.HEADERS);

        RequestSpecification specification = new RequestSpecBuilder().
                setConfig(RestAssured.config().logConfig(RestAssured.config().getLogConfig().defaultStream(captor).and().enablePrettyPrinting(true))).
                addHeader("Api-Key", "1234").build();

        try {
            RestAssured.given().
                    spec(specification).
                    param("firstName", "John").
                    param("lastName", "Doe").
                    header("Content-type", "application/json").
            when().
                    get("/greet").
            then().
                    body("firstName", equalTo("Hello, Johan2!"));

            fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(writer.toString(), equalTo("Headers:\t\tApi-Key=1234\n\t\t\t\tAccept=*/*\n\t\t\t\tContent-Type=application/json; charset="+ RestAssured.config().getEncoderConfig().defaultCharsetForContentType(ContentType.JSON)+"\n\nContent-Type: application/json;charset=utf-8\nContent-Length: 33\nServer: Jetty(9.3.2.v20150730)\n"));
        }
    }

    @Test public void
    logging_doesnt_change_original_content_by_pretty_printing() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        RestAssured.given().
                config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor).and().enableLoggingOfRequestAndResponseIfValidationFails())).
                param("name", "Johan").
        when().
                get("/mimeTypeWithPlusHtml").
        then().
                body("html.head.title", equalTo("my title"));
    }

    @Test public void
    logging_is_applied_when_using_non_static_response_specifications() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);

        try {
            RestAssured.given().
                    config(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(captor).and().enableLoggingOfRequestAndResponseIfValidationFails())).
                    param("firstName", "John").
                    param("lastName", "Doe").
            when().
                    get("/greet").
            then().
                    spec(new ResponseSpecBuilder().expectStatusCode(400).build());

            fail("Test out to have failed by now");
        } catch (AssertionError e) {
            assertThat(writer.toString(), not(isEmptyOrNullString()));
        }
    }
}