package gpps.tools;

public class ObjectUtil {
	public static <T> T checkNullObject(Class<T> T,Object value) throws IllegalArgumentException
	{
		if(value==null)
			throw new IllegalArgumentException(T.getSimpleName()+"must not be null.");
		return (T)value;
	}
	public static Object checkNullObject(String name,Object value) throws IllegalArgumentException
	{
		if(value==null)
			throw new IllegalArgumentException(name+"must not be null.");
		return value;
	}
}
