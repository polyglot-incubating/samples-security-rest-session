package org.chiwooplatform.security.session.mongo;

import java.util.Map;

import org.chiwooplatform.context.Constants;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.chiwooplatform.security.core.PermissionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * <pre>
 * PermissionResolver 는 권한이 있는지 확인 하며, PermissionEvaluator 매처에 의해 사용 된다.
 * token 검증을 위한 PermissionEvaluator 매처로는 TokenPermissionEvaluator 을 사용 한다.
 * 
 * 아래는 설정 예제 이다.
 * <code>
    &#64;Bean
    public PermissionResolver permissionResolver(MongoTemplate mongoTemplate) {
        PermissionResolver permissionResolver = new MongoPermissionResolver(
                mongoTemplate);
        return permissionResolver;
    }

    &#64;Bean
    public TokenPermissionEvaluator permissionEvaluator(
            PermissionResolver permissionResolver) {
        TokenPermissionEvaluator permissionEvaluator = new TokenPermissionEvaluator(
                permissionResolver);
        return permissionEvaluator;
    }
 * </code>
 * </pre>
 * 
 * @see org.chiwooplatform.security.support.TokenPermissionEvaluator
 * 
 * 
 * @author aider
 *
 */
public class MongoPermissionResolver implements PermissionResolver {

    private final transient Logger logger = LoggerFactory
            .getLogger(MongoPermissionResolver.class);

    private final MongoTemplate mongoTemplate;

    private final String collectionName = AuthenticationRepository.SECURITY_MONGO_COLLECTION_NAME;

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
        final long currentDtm = System.currentTimeMillis();
        if (token == null || principal == null || permCd == null) {
            return false;
        }
        Query query = new Query(Criteria.where("_id").is(principal).and("authorities")
                .is(permCd).and("tokens").elemMatch(Criteria.where("token").is(token)
                        .and("expires").gte(currentDtm)));
        boolean hasPermission = mongoTemplate.exists(query, collectionName);
        return hasPermission;
    }

}
