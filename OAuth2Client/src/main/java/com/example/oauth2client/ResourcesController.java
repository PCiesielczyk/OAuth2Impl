package com.example.oauth2client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Controller
public class ResourcesController {

    Logger logger = LoggerFactory.getLogger(ResourcesController.class);

    private final WebClient webClient;

    public ResourcesController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8090").build();
    }

    @GetMapping(value = "/resources")
    @ResponseBody
    public Mono<String[]> getResources(
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {

        return this.webClient
                .get()
                .uri("/resources")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new ResponseStatusException(response.statusCode())))
                .bodyToMono(String[].class)
                .doOnSuccess(result -> logger.info("Received resources: {}", Arrays.toString(result)));
    }

    @GetMapping("/add_resource")
    public ResponseEntity<ClassPathResource> showForm() {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(new ClassPathResource("templates/form.html"));
    }

    @PostMapping(path="/add_resource",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<String>> handleSubmit(
            InputValue inputValue,
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {


        return this.webClient
                .post()
                .uri("/submit")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(inputValue.getInputValue())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new ResponseStatusException(response.statusCode())))
                .toEntity(String.class);
    }

    private static class InputValue {
        private String inputValue;

        public String getInputValue() {
            return inputValue;
        }

        public void setInputValue(String inputValue) {
            this.inputValue = inputValue;
        }
    }

    
}
