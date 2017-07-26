[[overview]]

The samples-security-rest-session project is very light poc demonstrate for accessing data from mongodb, memorydb(h2):

### Environment Properties

"application-gen.yml" is the environment file.
~~~~
server:
  port: ${port:8080}
spring:
  profiles:
    active:  default
  session:
    store-type: hash_map
  datasource:
    driverClassName: org.h2.Driver
    url: jdbc:h2:mem:SAMPLES;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;
    name: SAMPLES
    username: sa
    password:
    validationQuery: SELECT 1
    platform: h2
    initialize: true
    schema: classpath:/schema-h2.sql
    data: classpath:/data-h2.sql
  jackson:
    serialization-inclusion: non_nul
    serialization.indent_output: true

  data:
    mongodb:
      host: 192.168.30.198
      port: 27017
      database: SAMPLES
# H2 Web Console
  h2:
    console:
      path: /h2-console
      enabled: true
      settings:
        web-allow-others: true
  jpa:
    hibernate:
      ddl-auto: none
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true
logging:
  pattern:
    console: "%d %-5level %logger : %msg%n"
  level:
    root: info
    org.springframework: info
    org.chiwooplatform.samples: debug

---
spring:
  profiles: dev

logging:
  config: classpath:logback-dev.xml

server:
  port: ${port:8082}}
~~~~


### H2 Web Console
You can manage data with query in H2 Web Console.
~~~
http://localhost:8080/h2-consol
~~~

ddl file is schema-h2.sql "schema-h2.sql" and initilize data file is "data-h2.sq" .

### Test Authentication
You can test authentication with cURL or Postman.
~~~
curl -X POST \
  http://localhost:8080/auth/token \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 76b66090-1965-0fb0-d2fa-a16ed46bf8b7' \
  -d '{
    "username" : "lamp.java@gmail.com",
    "password" : "qwer1234"
}'
~~~

### Redis CLI

#### REDIS 세션 조회 
~~~
$ redis-cli
redis 127.0.0.1:6379> keys *
1) "spring:session:sessions:2825010a-9a17-4877-b1c2-8bb59551613c"
2) "spring:session:expirations:1500550860000"
3) "spring:session:sessions:expires:2825010a-9a17-4877-b1c2-8bb59551613c"
4) "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:abc@abc" 
~~~

#### REDIS 세션 항목 조회 
~~~
$ redis-cli
redis 127.0.0.1:6379> hkeys "spring:session:sessions:53b834b6-bbcf-4a94-9701-46c1585e06ad"
1) "maxInactiveInterval"
2) "sessionAttr:SPRING_SECURITY_CONTEXT"
3) "creationTime"
4) "lastAccessedTime"
~~~

#### REDIS 세션 속성 조회 
~~~
$ redis-cli
redis 127.0.0.1:6379> hget "spring:session:sessions:53b834b6-bbcf-4a94-9701-46c1585e06ad" "lastAccessedTime"
"\xac\xed\x00\x05sr\x00\x0ejava.lang.Long;\x8b\xe4\x90\xcc\x8f#\xdf\x02\x00\x01J\x00\x05valuexr\x00\x10java.lang.Number\x86\xac\x95\x1d\x0b\x94\xe0\x8b\x02\x00\x00xp\x00\x00\x01]_\xec\xbc\x8c"
~~~


### Spring Security
spring-security 는 filter-chainning 전략으로 모든 요청에 대해 보안 처리를 한다.
그 중에서 특히 AbstractAuthenticationProcessingFilter 는 사용자 인증을 담당 하고 있다.
가장 핵심이 되는 코드를 참고 하자.
~~~

public abstract Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException, IOException,
        ServletException {
    ...
    Authentication authResult;
    try {
        authResult = attemptAuthentication(request, response);
        if (authResult == null) {
            // return immediately as subclass has indicated that it hasn't completed authentication
            return;
        }
        sessionStrategy.onAuthentication(authResult, request, response);
    }
    catch (InternalAuthenticationServiceException failed) {
        logger.error( "An internal error occurred while trying to authenticate the user.", failed);
        unsuccessfulAuthentication(request, response, failed);
        return;
    }
    catch (AuthenticationException failed) {
        // Authentication failed
        unsuccessfulAuthentication(request, response, failed);
        return;
    }
    ...
}

~~~
