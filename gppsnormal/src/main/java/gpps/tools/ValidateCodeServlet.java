package gpps.tools;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ValidateCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest reqeust,
			HttpServletResponse response) throws ServletException, IOException {
		// ������Ӧ�����͸�ʽΪͼƬ��ʽ
		response.setContentType("image/jpeg");
		//��ֹͼ�񻺴档
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		HttpSession session = reqeust.getSession();
		
		GraphValidateCode vCode = new GraphValidateCode(120,40,5,100);
		session.setAttribute("code", vCode.getCode());
		vCode.write(response.getOutputStream());
	}
/**
 * web.xml ���servlet
	<servlet>
		<servlet-name>validateCodeServlet</servlet-name>
		<servlet-class>cn.dsna.util.images.ValidateCodeServlet</servlet-class>
	</servlet>	
	<servlet-mapping>
		<servlet-name>validateCodeServlet</servlet-name>
		<url-pattern>*.images</url-pattern>
	</servlet-mapping>

�ڵ�ַ������XXX/dsna.images ����
 */

}