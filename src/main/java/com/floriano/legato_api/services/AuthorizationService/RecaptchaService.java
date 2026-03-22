package com.floriano.legato_api.services.AuthorizationService;

import com.floriano.legato_api.dto.AuthDTO.RecaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean validateToken(String recaptchaToken) {
        if (recaptchaToken == null || recaptchaToken.trim().isEmpty()) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", recaptchaSecret);
        body.add("response", recaptchaToken);

        try {
            ResponseEntity<RecaptchaResponse> response = restTemplate.postForEntity(
                    GOOGLE_RECAPTCHA_VERIFY_URL, body, RecaptchaResponse.class);

            RecaptchaResponse recaptchaResponse = response.getBody();
            
            return recaptchaResponse != null && recaptchaResponse.isSuccess();
            
        } catch (Exception e) {
            System.err.println("Erro ao validar reCAPTCHA: " + e.getMessage());
            return false;
        }
    }
}