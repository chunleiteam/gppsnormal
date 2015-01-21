package com.easyservice.xml.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 定义该标注的属性将被忽略
 * @author zhang yuexiang
 *
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface XMLIgnore {

}
