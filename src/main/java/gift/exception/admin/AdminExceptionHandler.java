package gift.exception.admin;

import gift.controller.AdminController;
import gift.dto.ProductRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = AdminController.class)
public class AdminExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e,
                                         Model model,
                                         HttpServletRequest request) {

        model.addAttribute("errorMessage", "서버 오류가 발생했습니다. 다시 시도해주세요.");
        return prepareErrorResponse(model, request);
    }

    private String prepareErrorResponse(Model model, HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        if (requestUri.contains("/products-add")) {
            if (!model.containsAttribute("product")) {
                model.addAttribute("product", new ProductRequestDto(null, null, null, null));
            }
            return "admin/product-add";
        } else {
            String[] segments = requestUri.split("/");
            for (String segment : segments) {
                if (segment.matches("\\d+")) {
                    model.addAttribute("id", Long.valueOf(segment));
                    break;
                }
            }
            if (!model.containsAttribute("product")) {
                model.addAttribute("product", new ProductRequestDto(null, null, null, null));
            }
            return "admin/product-edit";
        }
    }
}
