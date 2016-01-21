package com.yo1000.whisker.component;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import java.util.UUID;

/**
 * Created by yoichi.kikuchi on 2016/01/20.
 */
public class Identifier {
    private Object salt;

    public Identifier() {
        this(System.currentTimeMillis());
    }

    public Identifier(Object salt) {
        this.salt = salt;
    }

    public String generate() {
        return new ShaPasswordEncoder().encodePassword(
                UUID.randomUUID().toString(), this.getSalt());
    }

    protected Object getSalt() {
        return salt;
    }
}
