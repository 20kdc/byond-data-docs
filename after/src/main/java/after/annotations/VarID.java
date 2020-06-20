package after.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Almost certainly won't be used as an actual annotation.
 * Exists as a marker.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface VarID {

}
