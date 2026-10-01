package com.liys.oaback.Interceptor;

import com.liys.oaback.dao.EmpMapper;
import com.liys.oaback.domain.Emp;
import com.liys.oaback.domain.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A Spring component that implements the HandlerInterceptor interface to provide authentication and authorization logic.
 * This interceptor checks if a user is logged in and has the necessary permissions to access certain resources.
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private EmpMapper empMapper;

    /**
     * This method is called before the actual handler is executed. It performs authentication and authorization checks.
     *
     * @param request The current HTTP request
     * @param response The current HTTP response
     * @param handler The handler method that will be executed
     * @return true if the request should proceed to the handler, false if it should be short-circuited
     * @throws Exception if an error occurs during the pre-processing phase
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 CORS 预检请求（OPTIONS），预检不带 cookie，如果拦截会导致跨域失败
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Attempt to get the session and check if a user is logged in
        HttpSession session = request.getSession(false);
        Object obj = session == null ? null : session.getAttribute("loginUser");

        // If no user is found in the session, return 401 Unauthorized
        if (obj == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\",\"data\":null}");
            return false;
        }

        if (!(obj instanceof LoginUser loginUser)) {
            writeUnauthorized(response, "登录状态无效，请重新登录");
            return false;
        }

        // Recheck account status and role so disabling an account takes effect immediately.
        Emp currentUser = empMapper.selectById(loginUser.getId());
        if (currentUser == null || currentUser.getStatus() == null || currentUser.getStatus() != 1) {
            session.invalidate();
            writeUnauthorized(response, "账号已失效，请重新登录");
            return false;
        }
        loginUser.setRole(currentUser.getRole());
        session.setAttribute("loginUser", loginUser);

        // Check if the request URI starts with restricted paths and verify admin role
        String uri = request.getRequestURI().substring(request.getContextPath().length());
        if (isManagementPath(uri)) {
            // If the user is not an admin, return 403 Forbidden
            if (!"admin".equals(loginUser.getRole())) {
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问该资源\",\"data\":null}");
                return false;
            }
        }

        // If all checks pass, allow the request to proceed
        return true;
    }

    private boolean isManagementPath(String uri) {
        return uri.equals("/emp") || uri.startsWith("/emp/")
                || uri.equals("/dept") || uri.startsWith("/dept/")
                || uri.equals("/job") || uri.startsWith("/job/")
                || uri.equals("/ai") || uri.startsWith("/ai/");
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws java.io.IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"" + message + "\",\"data\":null}");
    }
}
