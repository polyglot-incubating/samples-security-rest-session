package org.chiwooplatform.samples.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import org.chiwooplatform.context.support.DateUtils;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;
import lombok.ToString;

@SuppressWarnings("serial")
@Data
@ToString
@JsonRootName("authenticationUser")
@Document(collection = "authentications")
public class AuthenticationUser implements Serializable {

    @Id
    private String username;

    private String token; /* token. It can be modified with new token */

    /* UTC Timestamp "2014-01-31T15:30", It can be modified with new token */
    private Long expires;

    private Integer userId;

    private Collection<GrantedAuthority> authorities;

    private Collection<UserToken> tokens = new HashSet<>();

    public void authentication(final String token, final Long expires) {
        if (StringUtils.isEmpty(token) || DateUtils.isExpired(expires)) {
            return;
        }
        final UserToken ut = new UserToken(token, expires);
        tokens.add(ut);
    }

    public Collection<UserToken> activeTokens() {
        if (this.tokens.size() > 0) {
            final List<UserToken> tokens = this.tokens.stream()
                    .filter((v) -> !DateUtils.isExpired(v.getExpires()))
                    .collect(Collectors.toList());
            return tokens;
        }
        return this.tokens;
    }

}
