package gpps.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

/**
 * 辅助类
 * 
 */
public class Common
{
	public static final String privateKeyPKCS8 = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMBCwTnO1scMGM4YIxhf/oXeE6W1" + "6jJYsA1skxHzbVgPIb/Qv4Fu6UVXydjKJBPVvMH5407Jy9pSbMlWwXCsTJpCBdTeRD4XcbiQeVLH" + "DFQON8QdziH7H1zG5ge6YpIBVPYBIuY6iynl/5TPD/20vb3skrqZOc9Q0eqW5G5xtSgFAgMBAAEC" + "gYAix/DM1G5mc/nIzvUKY9NXxGUphU9o7EJtK0cv6CnP1Gz2ln0OnVH2CXuqjGcab4BGVz6X8km+" + "pUqo4dj82S7CKKjFkcVfbdLpsnlfWFLCPqw7wJKEOeFIfTVnSTOGZTRFc8QEVg0+a9okRoqeqQKC" + "Zn5kWIgp3OcAIdMOetrvwQJBAPSOjIKfiqo5vMtOUoGRUlunDqAhye7aAC0m1USgPKj6gqEs/oPq" + "HB1necQANHj5l2a20ZQikflr7qi3J8/G3I8CQQDJQcWKC/kk9oaJVXSpR01Tl2S3wEcygArBLU7R" + "xMKsa36aJF6vr3emNPNMYY1jICWjNSe/YR7UcXLuG6ZgFiQrAkAocG3xp5oRXezHHZNtE2+v8ibr" + "+cpfcbL3xGUdrPV657m0FzGa9JpjjlnHPFVw76zGclKjkTfcK6nSQj8WD4cnAkBk23QucT+jOXRE" + "oLG9H4Ft8cHEoDRN54L8OkN0tmFE3P3uK2nUK2APyBthXMXpNjQGbV4E95vmpRAOqYagQphPAkAY" + "Txc8LB407H6JktpNj0tGpXNeUdfIZWUybcIiHfc1Wc0rTUaUv+tBe2JCv2WGGmanRune+mkz+qZi" + "UuXawXs7";
	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDAQsE5ztbHDBjOGCMYX/6F3hOlteoyWLANbJMR" + "821YDyG/0L+BbulFV8nYyiQT1bzB+eNOycvaUmzJVsFwrEyaQgXU3kQ+F3G4kHlSxwxUDjfEHc4h" + "+x9cxuYHumKSAVT2ASLmOosp5f+Uzw/9tL297JK6mTnPUNHqluRucbUoBQIDAQAB";
	
	/**
	 * 字符串编码
	 * 
	 * @param sStr
	 * @param sEnc
	 * @return String
	 */
	public final static String UrlEncoder(String sStr, String sEnc)
	{
		String sReturnCode = "";
		try
		{
			sReturnCode = URLEncoder.encode(sStr, sEnc);
		}
		catch (UnsupportedEncodingException ex)
		{
			
		}
		return sReturnCode;
	}
	
	/**
	 * 字符串解码
	 * 
	 * @param sStr
	 * @param sEnc
	 * @return String
	 */
	public final static String UrlDecoder(String sStr, String sEnc)
	{
		String sReturnCode = "";
		try
		{
			sReturnCode = URLDecoder.decode(sStr, sEnc);
		}
		catch (UnsupportedEncodingException ex)
		{
			
		}
		return sReturnCode;
	}
	
	/**
	 * 将模型进行JSON编码
	 * 
	 * @param obModel
	 * @return String
	 */
	public final static String JSONEncode(Object obModel)
	{
		Gson gson = new Gson();
		return gson.toJson(obModel);
	}
	
	/**
	 * 将模型进行JSON解码
	 * 
	 * @param sJson
	 * @param classOfT
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public final static Object JSONDecode(String sJson, Class classOfT)
	{
		Gson gson = new Gson();
		return gson.fromJson(sJson, classOfT);
	}
	
	/**
	 * 将模型列表进行JSON解码
	 * 
	 * @param sJson
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public final static List<Object> JSONDecodeList(String sJson, Class classOfT)
	{
		if (sJson.equals("[]"))
		{
			return null;
		}
		List<String> lstsfs = dealJsonStr(sJson);
		List<Object> lst = new ArrayList<Object>();
		
		for (String str : lstsfs)
		{
			// 使用JSON作为传输
			Object o = JSONDecode(str, classOfT);
			lst.add(o);
		}
		return lst;
	}
	
	/**
	 * 将json列表转换为字符串列表,每个字符串为一个对象
	 * 
	 * @param json
	 * @return List<String>
	 */
	public static List<String> dealJsonStr(String json)
	{
		List<String> lst = new ArrayList<String>();
		try
		{
			String[] sfs = json.split("\"\\},\\{\"");
			for (String str : sfs)
			{
				if (str.startsWith("["))
				{
					str = str.substring(1);
				}
				else if (str.startsWith("{\""))
				{
					
				}
				else
				{
					str = "{\"" + str;
				}
				if (str.endsWith("\\\"}]"))
				{
					str += "\"}";
				}
				else
				{
					if (str.endsWith("]"))
					{
						str = str.substring(0, str.length() - 1);
					}
					else if (str.endsWith("\"}"))
					{
						
					}
					else
					{
						str += "\"}";
					}
				}
				
				lst.add(str);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return lst;
	}
	
	/**
	 * 将接收的字符串转换成图片保存
	 * 
	 * @param imgStr
	 *            二进制流转换的字符串
	 * @param imgPath
	 *            图片的保存路径
	 * @param imgName
	 *            图片的名称
	 * @return 1：保存正常 0：保存失败
	 */
	public static int saveToImgByStr(String imgStr, String imgPath, String imgName)
	{
		int stateInt = 1;
		try
		{
			// System.out.println("===imgStr.length()====>" + imgStr.length() + "=====imgStr=====>" + imgStr);
			File savedir = new File(imgPath);
			if (!savedir.exists())
			{
				savedir.mkdirs();
			}
			if (imgStr != null && imgStr.length() > 0)
			{
				// 将字符串转换成二进制，用于显示图片
				// 将上面生成的图片格式字符串 imgStr，还原成图片显示
				byte[] imgByte = hex2byte(imgStr);
				
				InputStream in = new ByteArrayInputStream(imgByte);
				
				File file = new File(imgPath, imgName);// 可以是任何图片格式.jpg,.png等
				FileOutputStream fos = new FileOutputStream(file);
				
				byte[] b = new byte[1024];
				int nRead = 0;
				while ((nRead = in.read(b)) != -1)
				{
					fos.write(b, 0, nRead);
				}
				fos.flush();
				fos.close();
				in.close();
			}
		}
		catch (Exception e)
		{
			stateInt = 0;
			e.printStackTrace();
		}
		
		return stateInt;
	}
	
	/**
	 * 字符串转二进制
	 * 
	 * @param str
	 *            要转换的字符串
	 * @return 转换后的二进制数组
	 */
	public static byte[] hex2byte(String str)
	{ // 字符串转二进制
		if (str == null)
			return null;
		str = str.trim();
		int len = str.length();
		if (len == 0 || len % 2 == 1)
			return null;
		byte[] b = new byte[len / 2];
		try
		{
			for (int i = 0; i < str.length(); i += 2)
			{
				b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
			}
			return b;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 将二进制转换成图片保存
	 * 
	 * @param imgStr
	 *            二进制流转换的字符串
	 * @param imgPath
	 *            图片的保存路径
	 * @param imgName
	 *            图片的名称
	 * @return 1：保存正常 0：保存失败
	 */
	public static int saveToImgByBytes(File imgFile, String imgPath, String imgName)
	{
		
		int stateInt = 1;
		if (imgFile.length() > 0)
		{
			try
			{
				File savedir = new File(imgPath);
				if (!savedir.exists())
				{
					savedir.mkdirs();
				}
				File file = new File(imgPath, imgName);// 可以是任何图片格式.jpg,.png等
				FileOutputStream fos = new FileOutputStream(file);
				
				FileInputStream fis = new FileInputStream(imgFile);
				
				byte[] b = new byte[1024];
				int nRead = 0;
				while ((nRead = fis.read(b)) != -1)
				{
					fos.write(b, 0, nRead);
				}
				fos.flush();
				fos.close();
				fis.close();
				
			}
			catch (Exception e)
			{
				stateInt = 0;
				e.printStackTrace();
			}
			finally
			{
			}
		}
		return stateInt;
	}
	
	/**
	 * 将图片转换成字符串
	 * 
	 * @param imgPath
	 * @param imgName
	 * @return
	 */
	public static String saveToStrByImg(File file)
	{
		String result = "";
		try
		{
			byte[] by = saveToBytesByImg(file);
			result = byte2hex(by);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 将图片转换成二进制
	 * 
	 * @param imgPath
	 * @param imgName
	 * @return
	 */
	public static byte[] saveToBytesByImg(File file)
	{
		byte[] by = null;
		try
		{
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			by = new byte[fis.available()];
			bis.read(by);
			fis.close();
			bis.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return by;
	}
	
	/**
	 * 二进制转字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b)
	{
		StringBuffer sb = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++)
		{
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
			{
				sb.append("0" + stmp);
			}
			else
			{
				sb.append(stmp);
			}
			
		}
		return sb.toString();
	}
	
	public static String getRandomNum(int length)
	{
		try
		{
			if (length <= 0)
			{
				return "";
			}
			Random r = new Random();
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < length; i++)
			{
				result.append(Integer.toString(r.nextInt(10)));
			}
			return result.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

}
