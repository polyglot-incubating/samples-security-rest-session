package org.chiwooplatform.samples.rest;

import java.util.HashMap;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class ApiController {

    @RequestMapping(value = "/api/get", method = RequestMethod.GET, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> get(Principal principal) {
        log.info("principal: {}", principal);
        HashMap<String, Object> m = new HashMap<>();
        m.put("key1", "value1");
        m.put("key2", "value2");
        m.put("key3", "value3");
        m.forEach((k, v) -> {
            log.debug("k: {}, v: {}", k, v);
        });
        return ResponseEntity.status(HttpStatus.OK).body(m);
    }
}
