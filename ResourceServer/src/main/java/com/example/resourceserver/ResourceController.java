package com.example.resourceserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ResourceController {

    Logger logger = LoggerFactory.getLogger(ResourceController.class);
    private final List<String> resources = new ArrayList<>(List.of("Resource1", "Resource2", "Resource3"));

    @GetMapping("/resources")
    public List<String> getResources(){
        return resources;
    }

    @PostMapping("/submit")
    public List<String> handleSubmit(@RequestBody String inputValue) {

        logger.info("New resource: {}", inputValue);
        resources.add(inputValue);
        return resources;
    }
}
