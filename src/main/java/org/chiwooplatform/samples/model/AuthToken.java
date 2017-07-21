package org.chiwooplatform.samples.model;

import java.io.Serializable;

import org.springframework.security.core.Authentication;

import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.core.UserPrincipal;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;
import lombok.ToString;

@SuppressWarnings("serial")
@Data
@ToString
@JsonRootName("authToken")
public class AuthToken
    implements Serializable {

    private Integer id;

    private String username;

    private String token; /* token. It can be modified with new token */

    private String expires; /* UTC Timestamp "2014-01-31T15:30:58Z", It can be modified with new token */

    private UserPrincipal user;

    public void authentication( final Authentication authentication ) {
        if ( authentication instanceof RestAuthenticationToken ) {
            RestAuthenticationToken auth = (RestAuthenticationToken) authentication;
            this.token = auth.getToken();
            this.id = ( (UserPrincipal) auth.getDetails() ).getId();
            this.username = authentication.getName();
            this.expires = auth.getExpires();
        }
    }
}
