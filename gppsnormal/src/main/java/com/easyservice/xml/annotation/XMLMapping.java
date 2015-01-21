package com.easyservice.xml.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * 该标注定义了一个Java类和XML的映射信息
 * @author zhang yuexiang
 *
 */
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface XMLMapping {
	/**
	 * XML元素的名称,如不指定，则使用对应类的简单名称为XML元素名
	 */
	String tag() default "";
	/**
	 * 子对象对应的XML子元素的名称，仅仅对Array，Collection(List, Set)，Map有效
	 */
	String childTag() default "";
	
	/**
	 * 如果子对象是简单类型，该属性指定在XML子元素中用哪个属性来表示该子对象的值，仅仅对Array，Collection(List, Set)，Map有效
	 */
	String childSimpleObjValueAttr() default "";
	
	/**
	 * 仅仅对Map有效，该属性指定XML子元素中用哪个属性来表示该map中元素的key
	 */
	String keyTag() default "";
	
	/**
	 * 仅仅对Map有效，该属性指定XML子元素中用哪个属性来表示该map中元素的value
	 */
	String valueTag() default "";
	
	/**
	 * 指定一个类型和元素名称的映射关系，通常用在类型不确定的情况下，比如：
	 * <pre>@XMLMapping(tagTypeMapping={&quot;book=com.test.BookInfo&quot;, &quot;journal=com.test.JournalInfo&quot;})
	 *  public Object getObject();
	 * </pre>
	 * 这样在返回值是<code>com.test.BookInfo</code>的时候，生成的XML对应的元素名是book，而com.test.JournalInfo的时候，元素名为journal
	 */
	String[] tagTypeMapping() default {};
	
	/**
	 * 子对象的类型列表，仅仅对Array，Collection(List, Set)，Map有效,对于Map，如果想同时显示key和value，并且key的内容不在value对应的对象中，
	 * 应该使用 itemTypes={com.cnebula.common.xml.MapEntry.class, keyClass, valueClass}
	 */
	Class[] itemTypes() default {};
	
	/**
	 * 按给定的属性的顺序生成XML，如果指定了该属性，则不在该列表内的属性将会被忽略
	 */
	String[] orderedProperties() default {};
	
	/**
	 * 格式，含义通IConverter中的Format的串表示形式，比如对于日期，可以使用 "yyyy-MM-dd"等多种格式
	 */
	String format() default "";
	
	/**
	 * 和字符串的转化器，如果一个值需要特殊的转化，可以指定一个Converter，它必须是com.cnebula.common.convert.IConverter的子类,
	 * 在生成XML时，restore方法会被调用，从XML解析成Java对象时候，convert方法会被调用。
	 */
	String converter() default "";
	
	/**
	 * 导出XML的紧凑方式
	 */
	ExportCompressType exportCompressType() default ExportCompressType.INHERIT;
	
	/**
	 * 集合的风格，如果是INHERIT则由父层对象决定，如果是FLAT,表示集合本身也要使用一个子元素，如果是EMBED，则集合本身不占用一个子元素，比如
	
	<pre>@XMLMapping(collectionStyle=CollectionStyleType.EMBED, childTag=&quot;p&quot;&gt;
List&lt;Person&gt;personList;   </dd></dl>
</pre>
则对应的XML格式如下：
<pre> &lt;personList&gt;
     &lt;p id=&quot;001&quot; name=&quot;张三&quot; /&gt;
     &lt;p id=&quot;002&quot; name=&quot;李四&quot; /&gt;
 &lt;/personList&gt;   </pre>
<pre>@XMLMapping(collectionStyle=CollectionStyleType.FLAT, childTag=&quot;p&quot;)
List&lt;Person&gt;personList; 
</pre>
则对应的XML格式如下：
<pre> &lt;p id=&quot;001&quot; name=&quot;张三&quot; /&gt;
 &lt;p id=&quot;002&quot; name=&quot;李四&quot; /&gt;  </pre>
	
	 */
	CollectionStyleType collectionStyle() default CollectionStyleType.INHERIT;
	
	/**
	 * 属性的风格，如果是INHERIT则有父层对象决定，如果是TEXT,则所有的Java属性都采用子元素方式描述，值采用子元素中的文本信息描述。
	 * 如果是ATTR，则简单属性采用XML 属性方式描述，复杂子对象采用子元素方式描述，如果是ATTR_INITSUPERCASE，和ATTR相比多了个首字母大写
	 * 的特点。
	 */
	FieldStyleType fieldStyle() default FieldStyleType.INHERIT;
}
