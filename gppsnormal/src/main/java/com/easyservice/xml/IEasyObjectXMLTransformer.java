package com.easyservice.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * 将普通Java对象（POJO）和XML进行互相转换的服务，转换的方式依赖于Java类以及属性或getter声明上面的标注（@XMLMapping）定义, XMLMapping的用法详见：{@link com.easyservice.xml.annotation.cnebula.common.annotations.xml.XMLMapping} 
 * 中各个属性的注释
 * 最简单的用法示例如下(假设xtf为IEasyObjectXMLTransformer的服务实例)：
 * <pre>
 *  //将MyObj myobj (name=zyx, id=001) 转换为 XML 串
 *  String xml = xtf.export(myobj);
 *  
 *  //将xml内容转换为MyObj对象
 *  MyObj myobj = xtf.parse(xml, MyObj.class);
 * </pre>
 * @author zhang yuexiang
 * @version 1.5
 *
 */
public interface IEasyObjectXMLTransformer {
	
	/**
	 * 将URL对应的XML数据，解析为给定类型的Java对象
	 * @param <T> 返回类型
	 * @param url XML数据对应的URL，比如new File("my.xml").toURL()
	 * @param type 返回Java对象的类型
	 * @return 如果成功解析，则返回类型为 <code>type</code>的Java对象，否则抛出异常XMLParseException
	 * @throws XMLParseException
	 */
	public <T> T parse(URL url, Class<T> type) throws XMLParseException;
	
	/**
	 * 将inputStream对应的XML数据，解析为给定类型的Java对象
	 * @param <T> 返回类型
	 * @param in XML数据对应的InputStream，比如new FileInputStream("my.xml")
	 * @param type 返回Java对象的类型
	 * @return 如果成功解析，则返回类型为 <code>type</code>的Java对象，否则抛出异常XMLParseException
	 * @throws XMLParseException
	 */
	public <T> T parse(InputStream in, Class<T> type) throws XMLParseException;
	
	/**
	 * 将str表示的XML数据，解析为给定类型的Java对象
	 * @param <T> 返回类型
	 * @param str  XML数据串
	 * @param type 返回Java对象的类型
	 * @return 如果成功解析，则返回类型为 <code>type</code>的Java对象，否则抛出异常XMLParseException
	 * @throws XMLParseException
	 */
	public <T> T parse(String str, Class<T> type) throws XMLParseException;
	
	
	/**
	 * 将对象o根据给定的编码encode导出到指定的流out中去
	 * @param o 需要转化成XML的对象
	 * @param tag 生成的XML的根元素的TAG
	 * @param out 给定的输出流
	 * @param encode 给定的编码方式
	 * @param needHead 第一行是否包含XML文件的声明头,，比如 &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
	 * @return 和参数out代表的内容一样
	 */
	public PrintStream export(Object o, String tag, PrintStream out, String encode, boolean needHead);
	
	/**
	 * 将对象o根据给定的编码encode导出到指定的流out中去
	 * @param o 需要转化成XML的对象
	 * @param tag 生成的XML的根元素的名称
	 * @param out 给定的输出流
	 * @param encode 给定的编码方式
	 * @param needHead 第一行是否包含XML文件的声明头,，比如 &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
	 * @return 和参数out代表的内容一样
	 */
	public OutputStream export(Object o, String tag, OutputStream out, String encode, boolean needHead) throws UnsupportedEncodingException;
	
	/**
	 * 将对象o导出成XML字符串, 其中映射关系信息将使用o.getClass()对应的@XMLMapping的定义，如果@XMLMapping不存在，将使用缺省的映射方式
	 * @param o 需要转化成XML的对象
	 * @param tag 生成的XML的根元素的名称
	 * @param encode  编码方式
	 * @return 根据o生成的XML内容
	 * @throws UnsupportedEncodingException
	 */
	public String export(Object o, String tag, String encode) throws UnsupportedEncodingException;
	
	/**
	 * 将对象o导出成XML字符串，等价于 <code>export(o, o.getClass().getSimpleName(), "UTF-8")</code>
	 * @param o 
	 * @return 根据o生成的XML内容
	 */
	public String export(Object o);
	
	/**
	 * 设置是否export的XML有比较好的可读性，可读性好的XML会有多余的换行和空格，性能稍微有点损失
	 * @param prettyPrint
	 */
	public void setPrettyPrint(boolean prettyPrint);
	
	/**
	 * 根据当前服务克隆一个实例，比如某些需要setPrettyPrint(true)的情况，为了不影响其他引用此服务的模块，采用clone方法后再设置:
	 * <pre>
	 * IEasyObjectXMLTransformer xtf2 = xtf.createNewInstance();
	 * xtf2.setPrettyPrint(true);
	 * ....
	 * </pre>
	 * @return 返回一个新的服务实例
	 */
	public IEasyObjectXMLTransformer createNewInstance();
	
}
