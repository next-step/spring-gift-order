package gift.config.Interceptor;

import java.lang.annotation.*;

@Target(value = ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {
}