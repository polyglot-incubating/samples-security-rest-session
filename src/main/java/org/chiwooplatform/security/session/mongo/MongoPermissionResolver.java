package org.chiwooplatform.security.session.mongo;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.chiwooplatform.context.Constants;
import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.security.authentication.AuthenticationUser;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.chiwooplatform.security.core.PermissionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoPermissionResolver implements PermissionResolver {

    private final transient Logger logger = LoggerFactory
            .getLogger(MongoPermissionResolver.class);

    private final MongoTemplate mongoTemplate;

    private final String collectionName = "authenticationUsers";

    private Query query(final Object id, final Object permCd) {
        return new Query(Criteria.where("_id").is(id).and("authorities").is(permCd));
    }

    @Autowired
    public MongoPermissionResolver(MongoTemplate mongoTemplate) {
        super();
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean hasPermission(Map<String, Object> args) {
        logger.debug("args: {}", args);
        final String principal = (String) args.get(Constants.PRINCIPAL);
        final String permCd = (String) args.get(Constants.PERM_CODE);
        final String token = (String) args.get(Constants.TOKEN);

        AuthenticationUser user = mongoTemplate.findOne(query(principal, permCd),
                AuthenticationUser.class, collectionName);
        if (user != null && user.getAuthorities() != null) {
            Collection<SimpleToken> tokens = user.getTokens();
            if (tokens == null) {
                return false;
            }
            boolean matcher = false;
            for (SimpleToken stoken : tokens) {
                if (token.equals(stoken.getToken())) {
                    long expires = (Long) stoken.getExpires();
                    if (!DateUtils.isExpired(expires)) {
                        matcher = true;
                    }
                    break;
                }
            }
            if (matcher) {
                return true;
            }
        }
        return false;
    }

}
