package aedium;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * RUNTIME retention. Checked by ALFRED.
 * DO NOT JUST BLINDLY REPLACE THIS WITH ECLIPSE JDT ANNOTATIONS.
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE })
public @interface Nullable {

}
