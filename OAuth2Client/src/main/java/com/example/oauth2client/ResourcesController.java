package com.example.oauth2client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
public class ResourcesController {

    Logger logger = LoggerFactory.getLogger(ResourcesController.class);

    private final WebClient webClient;

    public ResourcesController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8090").build();
    }

    @GetMapping(value = "/resources")
    public Mono<String[]> getResources(
            @RegisteredOAuth2AuthorizedClient("resources-client-oidc") OAuth2AuthorizedClient authorizedClient) throws ExecutionException, InterruptedException {

        return this.webClient
                .get()
                .uri("/resources")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                .retrieve()
                .bodyToMono(String[].class)
                .doOnSuccess(result -> logger.info("Received resources: {}", Arrays.toString(result)));
    }

    
}
