package org.chiwooplatform.security.authentication;

public class SimpleToken
{
    private final String token;

    private final Object expires;

    public SimpleToken( String token, Object expires )
    {
        super();
        this.token = token;
        this.expires = expires;
    }

    @Override
    public String toString()
    {
        return "SimpleToken [token=" + token + ", expires=" + expires + "]";
    }

    public String getId()
    {
        return token;
    }

    public Object getExpires()
    {
        return expires;
    }
}
