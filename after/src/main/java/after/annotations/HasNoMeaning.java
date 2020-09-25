package after.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marker type for use by DMBValue
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface HasNoMeaning {

}
