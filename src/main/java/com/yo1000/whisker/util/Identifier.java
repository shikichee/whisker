package com.yo1000.whisker.util;

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

    public String get() {
        return this.get(UUID.randomUUID().toString());
    }

    public String get(String seed) {
        return new ShaPasswordEncoder().encodePassword(seed, this.getSalt());
    }

    protected Object getSalt() {
        return salt;
    }
}
