package com.shimady.auth.service;

import com.shimady.auth.exception.CaptchaValidationException;
import com.shimady.auth.model.dto.CaptchaValidationRequest;
import com.shimady.auth.model.dto.CaptchaValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Profile("prod")
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    @Value("${captcha.server.key}")
    private String captchaServerKey;

    private final RestClient restClient;

    public CaptchaValidationResponse verifyCaptcha(CaptchaValidationRequest request, String userIp) {
        Map<String, List<String>> body = new HashMap<>();
        body.put("secret", List.of(captchaServerKey));
        body.put("token", List.of(request.token()));
        body.put("ip", List.of(userIp));
        return restClient.post().uri("/validate")
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(new MultiValueMapAdapter<>(body))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, resp) -> {
                            throw new CaptchaValidationException(resp.getStatusCode());
                        }
                )
                .body(CaptchaValidationResponse.class);
    }
}

