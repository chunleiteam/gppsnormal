package gpps.servlet;

import gpps.service.ILenderService;
import gpps.service.ILoginService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class LoginServlet {
	@Autowired
	ILenderService lenderService;
	@RequestMapping(value={"/"})
	public void entry(HttpServletRequest req, HttpServletResponse resp)
	{
		try {
			resp.sendRedirect("/index.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value={"/login/graphValidateCode"})
	public void thirdPartyRegist(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("image/jpeg");
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		try {
			lenderService.writeGraphValidateCode(resp.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value={"/messageValidateCode"})
	public void getMessageValidateCode(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		PrintWriter writer=null;
		try {
			writer=resp.getWriter();
			StringBuilder text=new StringBuilder();
			text.append("您好，您的***网校验码为:");
			text.append(String.valueOf(lenderService.getCurrentSession().getAttribute(ILoginService.SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE)));
			text.append(",").append("请您在").append(ILoginService.MESSAGEVALIDATECODEEXPIRETIME/60/1000).append("分钟内使用，过期请重新获取。");
			writer.write("<font size=36>"+text.toString()+"</font>");
		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			if(writer!=null)
			{
				writer.flush();
				writer.close();
			}
		}
	}
}
