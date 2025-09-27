package com.carbon.common;

import com.carbon.model.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.carbon.constant.UserConstant.USER_LOGIN_STATE;

@Component
public class UserContextInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 假设用户信息存在 session 里
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user != null) {
            UserContext.set(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        UserContext.clear();
    }
}
