package org.chiwooplatform.samples.rest;

import java.util.Optional;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.chiwooplatform.samples.model.User;
import org.chiwooplatform.web.support.WebUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class SignupController {

    protected static final String BASE_URI = "/identity";

    private final NamedParameterJdbcTemplate template;

    @Autowired
    public SignupController(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    private final String INSERT_SQL = insertSql();

    private String insertSql() {
        StringBuilder b = new StringBuilder();
        b.append("insert into USER (");
        b.append(
                " id, username, password, firstname, lastname, email, country_code, zip_code, address, cellphone, birthdate, gender )");
        b.append(" values ( ");
        b.append(
                " :id, :username, :password, :firstname, :lastname, :email, :countryCode, :zipCode, :address, :cellphone, :birthdate, :gender )");
        return b.toString();
    }

    /**
     * <pre>
     * </pre>
     * 
     * @param user
     * @return
     */
    @PostMapping(value = BASE_URI
            + "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> signup(@RequestBody User user) throws Exception {
        log.debug("{}", user);
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        Optional.ofNullable(authentication).ifPresent((v) -> {
            log.debug("authentication: {}, isAuthenticated: {}", v, v.isAuthenticated());
        });

        try {
            SqlParameterSource parameters = new BeanPropertySqlParameterSource(user);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(INSERT_SQL, parameters, keyHolder);
            log.debug("keys: {}", keyHolder.getKeys());
            log.debug("KeyList: {}", keyHolder.getKeyList());
            log.debug("keyHolder.getKey(): {}", keyHolder.getKey());
            Integer id = Optional.ofNullable(keyHolder.getKey()).map((v) -> v.intValue())
                    .orElse(null);
            user.setId(id);
            final URI location = WebUtils.uriLocation("/{id}", user.getId());
            return ResponseEntity.created(location).body(user);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

}
