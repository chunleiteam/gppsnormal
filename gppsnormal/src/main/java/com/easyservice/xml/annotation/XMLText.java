package com.easyservice.xml.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 该标注使得对应的Java属性和XML中的#text内容映射，一个Java类最多只能有一个这样的属性
 * @author zhang yuexiang
 *
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface XMLText {

}
