package gpps.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class ServletUtils {
	public static final  String CHARSET="UTF-8";
	public static void write(HttpServletResponse resp,String text)
	{
		resp.setContentType("text/html");
		resp.setCharacterEncoding(CHARSET);
		PrintWriter writer=null;
		try
		{
			resp.getWriter();
			resp.getWriter().write(text);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(writer!=null)
				writer.close();
		}
	}
}
