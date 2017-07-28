package org.chiwooplatform.security.session.mongo;

import java.util.List;

import org.chiwooplatform.security.authentication.SimpleToken;

/**
 * <pre>
 * Mongodb 도메인 객체는 데이터 범위를 좁혀서 다루기위한 하위 객체를 별도로 구성 할  필요가 없다.
 * ProjectionToken 과 같은 클래스는 의미가 없다.
 * </pre>
 */
public class ProjectionToken {

    private String id;

    private List<SimpleToken> tokens;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProjectionToken [id=");
        builder.append(id);
        builder.append(", tokens=");
        builder.append(tokens);
        builder.append("]");
        return builder.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SimpleToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<SimpleToken> tokens) {
        this.tokens = tokens;
    }

}
