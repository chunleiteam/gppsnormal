package com.easyservice.xml.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 该标注用来定义XML Schema中指定的命名空间
 * e.g.
 * <pre>@XMLNamespace(prefix=&quot;saml&quot;, 
 * namespaces={&quot;xmlns:saml=\&quot;urn:oasis:names:tc:SAML:1.0:assertion\&quot;&quot;})
 * public class SAMLAssertion {
 *  String 	assertionID; 
 *  String	issueInstant; 
 * </pre>
 * 对应的XML示例为：
 * <pre>
    &lt;saml:assertion xmlns:saml=&quot;urn:oasis:names:tc:SAML:1.0:assertion&quot;
    	assertionID=&quot;sd3bc804975d4d3731c3464a7febeceead48adfdd01&quot;
    	issueInstant=&quot;2005-01-04T18:04:48Z&quot;&gt;</pre>
 * 
 * @author zhang yuexiang
 *
 */
@Target({ METHOD, FIELD, TYPE })
@Retention(RUNTIME)
public @interface XMLNamespace {

	/**
	 * 指定元素的修饰前缀，比如saml:Request, 其中saml就是prefix
	 */
	String prefix() default "";
	/**
	 * 指定schenma的声明，比如：xsi:schemaLocation=&quot;http://example.org/myapp/ http://example.org/myapp/schema.xsd&quot;
	 */
	String schemaDeclare() default "";
	
	/**
	 * 指定多个命名空间，比如 xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot;
	 */
	String[] namespaces() default {};
	
}
