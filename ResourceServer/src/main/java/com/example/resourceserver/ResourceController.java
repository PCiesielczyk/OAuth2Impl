package com.example.resourceserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @GetMapping("/resources")
    public String[] getResources(){
        return new String[] { "Resource1", "Resource2", "Resource3" };
    }
}
