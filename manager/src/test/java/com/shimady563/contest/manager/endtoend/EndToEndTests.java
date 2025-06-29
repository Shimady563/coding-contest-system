package com.shimady563.contest.manager.endtoend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shimady563.contest.manager.TestcontainersConfiguration;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Testcontainers
@Import(TestcontainersConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndTests {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${jwt.token.access.cookie.name}")
    private String tokenCookieName;

    @LocalServerPort
    private Integer port;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ResourceLoader resourceLoader;

    private String token;
    private String studentToken;

    @BeforeAll
    void setUpClass(@Value("${jwt.token.access.secret}") String secret,
                    @Value("${jwt.token.access.expiration}") Long tokenExpiration,
                    @Autowired UserRepository userRepository) {
        Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.studentToken = generateAccessToken(userRepository.findById(2L).orElseThrow(), secretKey, tokenExpiration);
        this.token = generateAccessToken(userRepository.findById(1L).orElseThrow(), secretKey, tokenExpiration);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = contextPath;
        RestAssured.defaultParser = Parser.JSON;
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Positive cases")
    @MethodSource("getPositiveTestCases")
    void runPositiveEndToEndTests(EndToEndTestCase testCase) {
        sendRequest(preparePositiveTestCase(testCase));
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Negative cases")
    @MethodSource("getNegativeTestCases")
    void runNegativeEndToEndTests(EndToEndTestCase testCase) {
        sendRequest(prepareNegativeTestCase(testCase));
    }

    @SneakyThrows
    private void sendRequest(EndToEndTestCase testCase) {
        String response = withOptionalBody(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParams(testCase.getQueryParams())
                .cookies(testCase.getCookies()), testCase.getRequestBody())
                .when()
                .request(testCase.getMethod(), getPath(testCase))
                .then()
                .statusCode(testCase.getStatusCode())
                .extract()
                .asString();
        log.info(response);
        assertResponse(testCase.responseBody, response);
    }

    @SneakyThrows
    private void assertResponse(String expected, String actual) {
        if (StringUtils.hasText(expected) && !StringUtils.hasText(actual)
                || !StringUtils.hasText(expected) && StringUtils.hasText(actual)) {
            throw new AssertionError("Responses don't match:\nexpected:\n" + expected + "\nactual:\n" + actual);
        }

        if (expected.trim().startsWith("[")) {
            JSONAssert.assertEquals(new JSONArray(expected), new JSONArray(actual), JSONCompareMode.LENIENT);
        } else {
            JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
        }
    }

    private EndToEndTestCase prepareTestCase(EndToEndTestCase testCase, String filePrefix) {
        String fullPath = filePrefix + testCase.path + testCase.filePathPostfix;
        return testCase.toBuilder()
                .cookies(testCase.cookies == null ? Map.of(tokenCookieName, token) : testCase.cookies)
                .queryParams(loadToMap(fullPath + "/queryParams.json"))
                .requestBody(loadRaw(fullPath + "/requestBody.json"))
                .responseBody(loadRaw(fullPath + "/responseBody.json"))
                .build();
    }

    private EndToEndTestCase prepareNegativeTestCase(EndToEndTestCase testCase) {
        return prepareTestCase(testCase, "/negative");
    }

    @SneakyThrows
    private EndToEndTestCase preparePositiveTestCase(EndToEndTestCase testCase) {
        return prepareTestCase(testCase, "/positive");
    }

    @SneakyThrows
    private Map<String, Object> loadToMap(String path) {
        String raw = loadRaw(path);
        return StringUtils.hasText(raw) ? mapper.readValue(raw, HashMap.class) : Map.of();
    }

    @SneakyThrows
    private String loadRaw(String path) {
        Resource resource = resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "json" + path);
        return resource.exists() ? resource.getContentAsString(UTF_8) : "";
    }

    private String getPath(EndToEndTestCase testCase) {
        return testCase.getPath() + testCase.getPathParams();
    }

    private RequestSpecification withOptionalBody(RequestSpecification spec, String body) {
        return StringUtils.hasText(body) ? spec.body(body) : spec;
    }

    private String generateAccessToken(User user, Key secretKey, Long tokenExpiration) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(tokenExpiration);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    private Stream<Arguments> getPositiveTestCases() {
        return Stream.of(
                //contest controller
                Arguments.of(
                        Named.of(
                                "Getting contest by name",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getContestsByName")
                                        .method(Method.GET)
                                        .path("/contests")
                                        .statusCode(200)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Creating contest",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/createContest")
                                        .method(Method.POST)
                                        .path("/contests")
                                        .statusCode(201)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Updating contest",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/updateContest")
                                        .method(Method.PUT)
                                        .path("/contests")
                                        .pathParams("/1")
                                        .statusCode(204)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Getting contest by group id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getContestsByGroupId")
                                        .method(Method.GET)
                                        .path("/contests/group")
                                        .statusCode(200)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Deleting contest by id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/deleteContestById")
                                        .method(Method.DELETE)
                                        .path("/contests")
                                        .pathParams("/1")
                                        .statusCode(204)
                                        .build()
                        )
                ),
                //contest version controller
                Arguments.of(
                        Named.of(
                                "Getting contest versions by contest id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getContestVersionsByContestId")
                                        .method(Method.GET)
                                        .path("/contest-versions")
                                        .statusCode(200)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Creating contest version",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/createContestVersion")
                                        .method(Method.POST)
                                        .path("/contest-versions")
                                        .statusCode(201)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Deleting contest version by id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/deleteContestVersionById")
                                        .method(Method.DELETE)
                                        .path("/contest-versions")
                                        .pathParams("/1")
                                        .statusCode(204)
                                        .build()
                        )
                ),
                //group controller
                Arguments.of(
                        Named.of(
                                "Getting all groups",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getAllGroups")
                                        .method(Method.GET)
                                        .path("/groups")
                                        .statusCode(200)
                                        .build()
                        )
                ),
                //test case controller
                Arguments.of(
                        Named.of(
                                "Deleting test cases by ids",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/deleteTestCasesByIds")
                                        .method(Method.DELETE)
                                        .path("/test-cases")
                                        .statusCode(200)
                                        .build()
                        )
                ),
                //task controller
                Arguments.of(
                        Named.of(
                                "Getting tasks",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/searchForTasks")
                                        .method(Method.GET)
                                        .path("/tasks")
                                        .statusCode(200)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Creating task",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/createTask")
                                        .method(Method.POST)
                                        .path("/tasks")
                                        .statusCode(201)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Updating task",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/updateTask")
                                        .method(Method.PUT)
                                        .path("/tasks")
                                        .pathParams("/1")
                                        .statusCode(204)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Getting tasks by contest version id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getTasksByContestVersionId")
                                        .cookies(Map.of(tokenCookieName, studentToken))
                                        .method(Method.GET)
                                        .path("/tasks/contest-version")
                                        .statusCode(200)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Deleting task",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/deleteTaskById")
                                        .method(Method.DELETE)
                                        .path("/tasks")
                                        .pathParams("/1")
                                        .statusCode(204)
                                        .build()
                        )
                )
        );
    }

    private Stream<Arguments> getNegativeTestCases() {
        return Stream.of(
                //contest controller
                Arguments.of(
                        Named.of(
                                "Getting contest by name",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getContestsByName")
                                        .method(Method.GET)
                                        .path("/contests")
                                        .statusCode(403)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Creating contest",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/createContest")
                                        .method(Method.POST)
                                        .path("/contests")
                                        .statusCode(404)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Updating contest",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/updateContest")
                                        .method(Method.PUT)
                                        .path("/contests")
                                        .pathParams("/1")
                                        .statusCode(404)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Getting contest by group id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getContestsByGroupId")
                                        .method(Method.GET)
                                        .path("/contests/group")
                                        .statusCode(404)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Deleting contest by id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/deleteContestById")
                                        .method(Method.DELETE)
                                        .path("/contests")
                                        .pathParams("/-1")
                                        .statusCode(404)
                                        .build()
                        )
                ),
                //contest version controller
                Arguments.of(
                        Named.of(
                                "Getting contest versions by contest id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getContestVersionsByContestId")
                                        .method(Method.GET)
                                        .path("/contest-versions")
                                        .statusCode(404)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Creating contest version",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/createContestVersion")
                                        .method(Method.POST)
                                        .path("/contest-versions")
                                        .statusCode(404)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Deleting contest version by id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/deleteContestVersionById")
                                        .method(Method.DELETE)
                                        .path("/contest-versions")
                                        .pathParams("/-1")
                                        .statusCode(404)
                                        .build()
                        )
                ),
                //task controller
                Arguments.of(
                        Named.of(
                                "Updating task",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/updateTask")
                                        .method(Method.PUT)
                                        .path("/tasks")
                                        .pathParams("/-1")
                                        .statusCode(404)
                                        .build()
                        )
                ),
                Arguments.of(
                        Named.of(
                                "Getting tasks by contest version id",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getTasksByContestVersionId")
                                        .cookies(Map.of(tokenCookieName, studentToken))
                                        .method(Method.GET)
                                        .path("/tasks/contest-version")
                                        .statusCode(403)
                                        .build()
                        )
                )
        );
    }

    @Data
    @Builder(toBuilder = true)
    private static class EndToEndTestCase {
        @Builder.Default
        private String filePathPostfix = "";
        private Map<String, Object> queryParams;
        @Builder.Default
        private String pathParams = "";
        private Map<String, Object> cookies;
        private String requestBody;
        private Method method;
        private String path;
        private Integer statusCode;
        private String responseBody;
    }
}
