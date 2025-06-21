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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInstance;
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

    private String teacherToken;
    private String studentToken;

    @BeforeAll
    void setUpClass(@Value("${jwt.token.access.secret}") String secret,
                    @Value("${jwt.token.access.expiration}") Long tokenExpiration,
                    @Autowired UserRepository userRepository) {
        Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        log.info(String.valueOf(userRepository.findById(1L).get().getRole()));
        log.info(String.valueOf(userRepository.findById(2L).get().getRole()));
        this.teacherToken = generateAccessToken(userRepository.findById(1L).orElseThrow(), secretKey, tokenExpiration);
        this.studentToken = generateAccessToken(userRepository.findById(2L).orElseThrow(), secretKey, tokenExpiration);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = contextPath;
        RestAssured.defaultParser = Parser.JSON;
    }

    @ParameterizedTest(name = "Позитивные кейсы")
    @MethodSource("getPositiveTestCases")
    void runPositiveEndToEndTests(EndToEndTestCase testCase) {
        sendRequest(preparePositiveTestCase(testCase));
    }

    @ParameterizedTest(name = "Негативные кейсы")
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
                .request(testCase.getMethod(), testCase.getPath())
                .then()
                .statusCode(testCase.getStatusCode())
                .extract()
                .asString();
        assertResponse(testCase.responseBody, response);
    }

    @SneakyThrows
    private void assertResponse(String responseBody, String response) {
        if (StringUtils.hasText(responseBody) && !StringUtils.hasText(response)
                || !StringUtils.hasText(responseBody) && StringUtils.hasText(response)) {
            throw new AssertionError("Responses don't match");
        }
        JSONAssert.assertEquals(responseBody, response, JSONCompareMode.LENIENT);
    }

    private RequestSpecification withOptionalBody(RequestSpecification spec, String body) {
        return StringUtils.hasText(body) ? spec.body(body) : spec;
    }

    private EndToEndTestCase prepareTestCase(EndToEndTestCase testCase, String filePrefix) {
        String fullPath = filePrefix + testCase.path + testCase.filePathPostfix;
        return testCase.toBuilder()
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
                Arguments.of(
                        Named.of(
                                "Positive test test case",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getContestsByName")
                                        .cookies(Map.of(tokenCookieName, teacherToken))
                                        .method(Method.GET)
                                        .path("/contests")
                                        .statusCode(200)
                                        .build()
                        )
                )
        );
    }

    private Stream<Arguments> getNegativeTestCases() {
        return Stream.of(
                Arguments.of(
                        Named.of(
                                "Negative test test case",
                                EndToEndTestCase.builder()
                                        .filePathPostfix("/getContestsByName")
                                        .cookies(Map.of(tokenCookieName, studentToken))
                                        .method(Method.GET)
                                        .path("/contests")
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
        private Map<String, Object> cookies = Map.of();
        private String requestBody;
        private Method method;
        private String path;
        private Integer statusCode;
        private String responseBody;
    }
}
