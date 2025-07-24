package gift.product.commons.interceptors;



import gift.product.commons.annotations.Authenticated;
import gift.product.commons.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

	private final JwtUtil jwtUtil;

	public AuthenticationInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(!(handler instanceof HandlerMethod)) { // 정적 리소스는 곧바로 통과
			return true;
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		if(handlerMethod.hasMethodAnnotation(Authenticated.class)){
			String token = jwtUtil.extractJwtTokenFromHeader(request);

			if(token == null || !jwtUtil.validateJwtToken(token)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
				return false;
			}

			String userId = jwtUtil.getSubject(token);
			if(userId == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}

			request.setAttribute("userId", userId);
			return true;
		}
		return true;
	}


	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}


	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
