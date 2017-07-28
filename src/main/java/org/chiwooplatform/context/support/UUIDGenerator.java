package org.chiwooplatform.context.support;

import java.util.UUID;

import java.nio.ByteBuffer;

public class UUIDGenerator {

    /**
     * 36 Bytes 체계의 UUID 를 생성 하여 반환
     * 
     * @see <a href="https://en.wikipedia.org/wiki/Universally_unique_identifier">UUID on
     * Wiki</a>
     * @return UUID 36 Bytes 체계의 UUID
     */
    public static final String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 32 Bytes 체계의 UUID 를 생성 하여 반환 (하이픈 '-' 제거)
     * 
     * @return UUID
     */
    public static final String get() {
        return uuid().replace("-", "");
    }

    /**
     * 요청에 대한 서비스의 처리 내역 추척을 위한 트랜잭션 아이디를 리턴
     * 
     * @return Global Transaction ID
     */
    public static long tXID() {
        return ByteBuffer.wrap(uuid().getBytes()).asLongBuffer().get();
    }

    public static String getTXID() {
        return Long.toString(tXID());
    }
}
