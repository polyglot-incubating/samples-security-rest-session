package org.chiwooplatform.samples.rest;

import java.util.HashMap;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.chiwooplatform.security.core.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class ApiController {

    /**
     * 인증만 되어 있다면 OK
     * @param principal
     * @return
     */
    @RequestMapping(value = "/api/get", method = RequestMethod.GET, consumes = { MediaType.APPLICATION_JSON_VALUE })
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

    /**
     * ADMIN 권한이 있어야만 접근
     * @param principal
     * @return
     */
    @RequestMapping(value = "/api/admin/get", method = RequestMethod.GET, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> adminGet(Principal principal) {
        log.info("principal: {}", principal);
        HashMap<String, Object> m = new HashMap<>();
        m.put("key1", "admin value1");
        m.put("key2", "admin value2");
        m.put("key3", "admin value3");
        m.forEach((k, v) -> {
            log.debug("k: {}, v: {}", k, v);
        });
        return ResponseEntity.status(HttpStatus.OK).body(m);
    }

    /**
     * MANAGER 권한이 있어야만 접근
     * @param principal
     * @return
     */
    @RequestMapping(value = "/api/manager/get", method = RequestMethod.GET, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> managerGet(Principal principal) {
        log.info("principal: {}", principal);
        HashMap<String, Object> m = new HashMap<>();
        m.put("key1", "manager value1");
        m.put("key2", "manager value2");
        m.put("key3", "manager value3");
        m.forEach((k, v) -> {
            log.debug("k: {}, v: {}", k, v);
        });
        return ResponseEntity.status(HttpStatus.OK).body(m);
    }

    /**
     * ADMIN or MANAGER or USER 권한이 있어야만 접근
     * @param principal
     * @return
     */
    @RequestMapping(value = "/api/user/get", method = RequestMethod.GET, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> userGet(Principal principal) {
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

    // ('API_ComCode.add', '공통', 1),
    // ('API_ComCode.get', '공통', 1),
    // ('API_ComCode.query', '공통', 1),
    // ('API_ComCode.modify', '공통', 1),
    // ('API_ComCode.remove', '공통', 1),
    // ('API_ComCode.enable', '공통', 1),
    // ('API_ComCode.disable', '공통', 1),
    // ('PORTAL_Usr.get', '포탈', 1),
    // ('PORTAL_Usr.query', '포탈', 1);

    /**
     * 퍼미션 코드를 통한 접근 제어
     * @return
     */
    @PreAuthorize("hasPermission(#token, 'API_ComCode.get')")
    @RequestMapping(value = "/api/com-code/get", method = RequestMethod.GET, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> comCodeGet(@RequestHeader(Constants.AUTH_TOKEN) String token) {
        HashMap<String, Object> m = new HashMap<>();
        m.put("key1", "API_ComCode.get value1");
        m.put("key2", "API_ComCode.get value2");
        m.put("key3", "API_ComCode.get value3");
        m.forEach((k, v) -> {
            log.debug("k: {}, v: {}", k, v);
        });
        return ResponseEntity.status(HttpStatus.OK).body(m);
    }

    /**
     * 퍼미션 코드를 통한 접근 제어
     * @return
     */
    @PreAuthorize("hasPermission(#token, 'API_ComCode.query')")
    @RequestMapping(value = "/api/com-code/query", method = RequestMethod.GET, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> comCodeQuery(@RequestHeader(Constants.AUTH_TOKEN) String token) {
        HashMap<String, Object> m = new HashMap<>();
        m.put("key1", "API_ComCode.query value1");
        m.put("key2", "API_ComCode.query value2");
        m.put("key3", "API_ComCode.query value3");
        m.forEach((k, v) -> {
            log.debug("k: {}, v: {}", k, v);
        });
        return ResponseEntity.status(HttpStatus.OK).body(m);
    }

    /**
     * 퍼미션 코드를 통한 접근 제어
     * @return
     */
    @PreAuthorize("hasPermission(#token, 'PORTAL_Usr.get')")
    @RequestMapping(value = "/portal/usr/get", method = RequestMethod.GET, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> protalUsrGet(@RequestHeader(Constants.AUTH_TOKEN) String token) {
        HashMap<String, Object> m = new HashMap<>();
        m.put("key1", "PORTAL_Usr.get value1");
        m.put("key2", "PORTAL_Usr.get value2");
        m.put("key3", "PORTAL_Usr.get value3");
        m.forEach((k, v) -> {
            log.debug("k: {}, v: {}", k, v);
        });
        return ResponseEntity.status(HttpStatus.OK).body(m);
    }
}
