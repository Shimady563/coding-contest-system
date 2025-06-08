package com.shimady563.contest.manager.endtoend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shimady563.contest.manager.TestcontainersConfiguration;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Import(TestcontainersConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndTests {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${jwt.token.access.expiration}")
    private Long accessTokenExpiration;

    private Key accessSecret;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private Integer port;

    @Autowired
    private ObjectMapper mapper;

    private String teacherToken;
    private String studentToken;

    @BeforeAll
    public void setUpClass(
            @Value("${jwt.token.access.secret}") String accessSecret) {
        this.accessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.teacherToken = generateAccessToken(userRepository.findById(1L).orElseThrow());
        this.studentToken = generateAccessToken(userRepository.findById(2L).orElseThrow());
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = contextPath;
    }

    @ParameterizedTest
    @MethodSource("getPositiveTestCases")
    void runPositiveEndToEndTests(EndToEndTestCase testCase) throws Exception {
        sendRequest(testCase);
    }

    @ParameterizedTest
    @MethodSource("getNegativeTestCases")
    void runNegativeEndToEndTests(EndToEndTestCase testCase) throws Exception {
        sendRequest(testCase);
    }

    private void sendRequest(EndToEndTestCase testCase) throws Exception {
        String response = given()
                .headers(testCase.headers())
                .queryParams(testCase.queryParams())
                .cookies(testCase.cookies())
                .body(testCase.requestBody())
                .when()
                .request(testCase.method(), testCase.path())
                .then()
                .statusCode(testCase.statusCode())
                .extract()
                .asString();
        assertEquals(mapper.readTree(testCase.responseBody), mapper.readTree(response));
    }

    private Stream<EndToEndTestCase> getPositiveTestCases() {
        return Stream.of(
                new EndToEndTestCase(
                        Map.of(
                                "Authorization", "Bearer " + teacherToken
                        ),
                        Map.of(
                                "name", ""
                        ),
                        Map.of(),
                        "",
                        Method.GET,
                        "/contests",
                        200,
                        """
                                {"content":[{"id":1,"name":"Контрольная работа по C++: Основы","description":"Контрольная работа по базовому синтаксису C++.","startTime":"2025-04-01T10:00:00","endTime":"2025-04-01T12:00:00","groupId":1},{"id":2,"name":"Контрольная работа по C++: STL","description":"Контрольная работа по библиотеке стандартных шаблонов C++.","startTime":"2025-04-10T10:00:00","endTime":"2025-04-10T12:00:00","groupId":2},{"id":3,"name":"Контрольная работа по C++: ООП","description":"Контрольная работа по объектно-ориентированному программированию в C++.","startTime":"2025-04-20T10:00:00","endTime":"2025-04-20T12:00:00","groupId":3}],"page":{"size":10,"number":0,"totalElements":3,"totalPages":1}}
                                """

                )
        );
    }

    private Stream<EndToEndTestCase> getNegativeTestCases() {
        return Stream.of(
                new EndToEndTestCase(
                        Map.of(
                                "Authorization", "Bearer " + studentToken
                        ),
                        Map.of(
                                "name", ""
                        ),
                        Map.of(),
                        "",
                        Method.GET,
                        "/contests",
                        403,
                        ""
                )
        );
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessTokenExpiration);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(accessSecret, SignatureAlgorithm.HS512)
                .compact();
    }

    private record EndToEndTestCase(
            Map<String, Object> headers,
            Map<String, Object> queryParams,
            Map<String, Object> cookies,
            String requestBody,
            Method method,
            String path,
            Integer statusCode,
            String responseBody
    ) {
    }
}
