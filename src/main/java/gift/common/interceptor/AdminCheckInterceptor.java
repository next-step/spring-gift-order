package gift.common.interceptor;

import gift.domain.Role;
import gift.dto.user.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean hasAnnotation = handlerMethod.getMethodAnnotation(AdminOnly.class) != null || handlerMethod.getBeanType().isAnnotationPresent(AdminOnly.class);

        if (!hasAnnotation) {
            return true;
        }

        UserInfo userInfo = (UserInfo) request.getAttribute("userInfo");
        if (userInfo.role() != Role.ADMIN) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("권한이 없습니다.");
            return false;
        }

        return true;
    }
}
