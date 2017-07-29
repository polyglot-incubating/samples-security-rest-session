package org.chiwooplatform.security.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.chiwooplatform.context.Constants;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.core.PermissionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 * 
 * <pre>
 * 
 * TokenPermissionEvaluator 의 역할은, token 이 유효 한지를 검증 한다.
 * token 이 유효한지를 검증 하기 위해서는, PermissionResolver 를 필요로 한다.
 *   
 * 아래는 spring configuration 설정의 한 부분 이다.
 * <code>
    &#64;Bean
    public TokenPermissionEvaluator permissionEvaluator(
            PermissionResolver permissionResolver) {
        TokenPermissionEvaluator permissionEvaluator = new TokenPermissionEvaluator(
                permissionResolver);
        return permissionEvaluator;
    }
 * </code>
 * 
 * 위와 같이 설정 되어 있다면, M-V-C 어느 컴포넌트 에서든지, 사용자의 Request 의 인증 정보를 식별 하여,
 * 인증이 확인된 요청인지를 검증 할 수 있다.
 * 
 * 아래는 spring controller 에서 인증 및 권한에 대한 검증을 하라는 어노테이션 이다.
 <code>
    &#64;PreAuthorize("hasPermission(#token, 'API_ComCode.query')")
    &#64;RequestMapping(value = "/api/users", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody processUser(@ResponseBody user) {...}
 * </code>
 * 
 * </pre>
 */
public class TokenPermissionEvaluator implements PermissionEvaluator {

    private final transient Logger logger = LoggerFactory
            .getLogger(TokenPermissionEvaluator.class);

    private final PermissionResolver permissionResolver;

    @Autowired
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
        final String token = (requestArgs != null ? (String) requestArgs
                : auth.getToken());
        final String permCd = (String) permCode;
        logger.debug("principal: {}, requestToken: {}, permissionId: {}", principal,
                token, permCd);
        Map<String, Object> param = new HashMap<>();
        param.put(Constants.PRINCIPAL, principal);
        param.put(Constants.TOKEN, token);
        param.put(Constants.PERM_CODE, permCd);
        boolean valid = permissionResolver.hasPermission(param);
        logger.debug("hasPermission: {}", valid);
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
