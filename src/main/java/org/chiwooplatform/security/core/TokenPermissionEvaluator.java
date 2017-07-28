package org.chiwooplatform.security.core;

import java.util.HashMap;
import java.util.Map;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import org.chiwooplatform.context.Constants;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenPermissionEvaluator implements PermissionEvaluator {

    private final transient Logger logger = LoggerFactory
            .getLogger(TokenPermissionEvaluator.class);

    private final PermissionResolver permissionResolver;

    public TokenPermissionEvaluator(PermissionResolver permissionResolver) {
        super();
        this.permissionResolver = permissionResolver;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object requestArgs,
            Object permCode) {
        if (!(authentication instanceof RestAuthenticationToken)) {
            return false;
        }
        RestAuthenticationToken auth = (RestAuthenticationToken) authentication;
        final String principal = auth.getName();
        final String token = (String) requestArgs;
        final String permCd = (String) permCode;
        logger.debug("principal: {}, requestToken: {}, permissionId: {}", principal,
                token, permCd);
        Map<String, Object> param = new HashMap<>();
        param.put(Constants.PRINCIPAL, principal);
        param.put(Constants.TOKEN, token);
        param.put(Constants.PERM_CODE, permCd);
        boolean valid = permissionResolver.hasPermission(param);
        return valid;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
            String targetType, Object permission) {
        logger.warn("principal: {}, method: {}, permissionId", authentication, targetId,
                targetType);
        throw new UnsupportedOperationException(
                "ID based permission evaluation currently not supported.");
    }
}
