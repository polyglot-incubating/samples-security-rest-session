package org.chiwooplatform.context;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.security.core.UserProfile;

public class ContextHolder {

    private static final ThreadLocal<ContextHolder> CONTEXT = new ThreadLocal<ContextHolder>();

    protected Map<String, Object> holder = new HashMap<String, Object>();

    public static ContextHolder get() {
        ContextHolder ctx = CONTEXT.get();
        if (ctx == null) {
            ctx = new ContextHolder();
            CONTEXT.set(ctx);
        }
        return ctx;
    }

    public static Long tXID() {
        return tXID(false);
    }

    public static Long tXID(boolean generate) {
        Long txid = (Long) ContextHolder.get().value(Constants.TXID);
        if (txid != null) {
            return txid;
        }
        if (generate) {
            final long tXID = UUIDGenerator.tXID();
            ContextHolder.get().put(Constants.TXID, tXID);
            return tXID;
        }
        else {
            return null;
        }
    }

    public static void removeTXID() {
        ContextHolder.get().remove(Constants.TXID);
    }

    public Object put(String key, Object value) {
        return this.holder.put(key, value);
    }

    public Object remove(String key) {
        return this.holder.remove(key);
    }

    public Object value(String key) {
        return this.holder.get(key);
    }

    public void clear() {
        Map<String, Object> map = CONTEXT.get().holder;
        if (map != null) {
            map.clear();
        }
        CONTEXT.remove();
    }

    /**
     * @return principal
     */
    public static UserProfile principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            Object obj = auth.getPrincipal();
            if (obj instanceof UserProfile) {
                return (UserProfile) obj;
            }
        }
        return null;
    }

    /**
     * @return id It's internal userid.
     */
    public static Integer id() {
        UserProfile principal = ContextHolder.principal();
        if (principal != null) {
            return principal.getId();
        }
        return null;
    }
}
