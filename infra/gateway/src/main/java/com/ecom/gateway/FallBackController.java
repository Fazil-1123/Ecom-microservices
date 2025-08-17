package com.ecom.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class FallBackController {

    @RequestMapping(value = "/fallback/users")
    public ResponseEntity<List<String>> userFallBack(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("User service is down, please try again later"));
    }

    @RequestMapping(value = "/fallback/products")
    public ResponseEntity<List<String>> productFallBack(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Product service is down, please try again later"));
    }
}
