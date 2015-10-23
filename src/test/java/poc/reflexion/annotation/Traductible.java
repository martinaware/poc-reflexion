package poc.reflexion.annotation;

import java.lang.annotation.*;

/**
 * Created by martinaware on 23/10/15.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Traductible {
}
