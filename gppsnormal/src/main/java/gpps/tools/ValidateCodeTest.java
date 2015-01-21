package gpps.tools;

import java.io.IOException;
import java.util.Date;

public class ValidateCodeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphValidateCode vCode = new GraphValidateCode(120,40,5,1000);
		try {
			String path="D:/"+new Date().getTime()+".png";
			System.out.println(vCode.getCode()+" >"+path);
			vCode.write(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
