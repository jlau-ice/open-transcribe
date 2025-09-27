package com.carbon.common;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.carbon.model.entity.User;

public class UserContext {

    private static final TransmittableThreadLocal<User> USER_HOLDER = new TransmittableThreadLocal<>();

    public static void set(User user) {
        USER_HOLDER.set(user);
    }

    public static User get() {
        return USER_HOLDER.get();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
