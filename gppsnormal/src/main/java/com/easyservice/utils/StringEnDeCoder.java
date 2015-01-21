/*
 * @Source(StringEnDeCoder.java)
 * Copyright 2004-2-24 Yuexiang Zhang All right reserved
 */
package com.easyservice.utils;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

class Base64
{

	public Base64()
	{
	}

	static StringBuilder byteArrayToBase64(byte abyte0[])
	{
		return byteArrayToBase64(abyte0, false);
	}

	static StringBuilder byteArrayToAltBase64(byte abyte0[])
	{
		return byteArrayToBase64(abyte0, true);
	}

	private static StringBuilder byteArrayToBase64(byte abyte0[], boolean flag)
	{
		int i = abyte0.length;
		int j = i / 3;
		int k = i - 3 * j;
		int l = 4 * ((i + 2) / 3);
		StringBuilder stringbuffer = new StringBuilder(l);
		char ac[] = flag ? intToAltBase64 : intToBase64;
		int i1 = 0;
		for(int j1 = 0; j1 < j; j1++)
		{
			int k1 = abyte0[i1++] & 0xff;
			int i2 = abyte0[i1++] & 0xff;
			int k2 = abyte0[i1++] & 0xff;
			stringbuffer.append(ac[k1 >> 2]);
			stringbuffer.append(ac[k1 << 4 & 0x3f | i2 >> 4]);
			stringbuffer.append(ac[i2 << 2 & 0x3f | k2 >> 6]);
			stringbuffer.append(ac[k2 & 0x3f]);
		}

		if(k != 0)
		{
			int l1 = abyte0[i1++] & 0xff;
			stringbuffer.append(ac[l1 >> 2]);
			if(k == 1)
			{
				stringbuffer.append(ac[l1 << 4 & 0x3f]);
				stringbuffer.append("==");
			} else
			{
				int j2 = abyte0[i1++] & 0xff;
				stringbuffer.append(ac[l1 << 4 & 0x3f | j2 >> 4]);
				stringbuffer.append(ac[j2 << 2 & 0x3f]);
				stringbuffer.append('=');
			}
		}
		return stringbuffer;
	}

	static byte[] base64ToByteArray(String s)
	{
		return base64ToByteArray(s, false);
	}

	static byte[] altBase64ToByteArray(String s)
	{
		return base64ToByteArray(s, true);
	}

	private static byte[] base64ToByteArray(String s, boolean flag)
	{
		byte abyte0[] = flag ? altBase64ToInt : base64ToInt;
		int i = s.length();
		int j = i / 4;
		if(4 * j != i)
			throw new IllegalArgumentException("String length must be a multiple of four.");
		int k = 0;
		int l = j;
		if(i != 0)
		{
			if(s.charAt(i - 1) == '=')
			{
				k++;
				l--;
			}
			if(s.charAt(i - 2) == '=')
				k++;
		}
		byte abyte1[] = new byte[3 * j - k];
		int i1 = 0;
		int j1 = 0;
		for(int k1 = 0; k1 < l; k1++)
		{
			int l1 = base64toInt(s.charAt(i1++), abyte0);
			int j2 = base64toInt(s.charAt(i1++), abyte0);
			int l2 = base64toInt(s.charAt(i1++), abyte0);
			int j3 = base64toInt(s.charAt(i1++), abyte0);
			abyte1[j1++] = (byte)(l1 << 2 | j2 >> 4);
			abyte1[j1++] = (byte)(j2 << 4 | l2 >> 2);
			abyte1[j1++] = (byte)(l2 << 6 | j3);
		}

		if(k != 0)
		{
			int i2 = base64toInt(s.charAt(i1++), abyte0);
			int k2 = base64toInt(s.charAt(i1++), abyte0);
			abyte1[j1++] = (byte)(i2 << 2 | k2 >> 4);
			if(k == 1)
			{
				int i3 = base64toInt(s.charAt(i1++), abyte0);
				abyte1[j1++] = (byte)(k2 << 4 | i3 >> 2);
			}
		}
		return abyte1;
	}

	private static int base64toInt(char c, byte abyte0[])
	{
		byte byte0 = abyte0[c];
		if(byte0 < 0)
			throw new IllegalArgumentException("Illegal character " + c);
		else
			return byte0;
	}

	public static void main(String args[])
	{
		int i = Integer.parseInt(args[0]);
		int j = Integer.parseInt(args[1]);
		Random random = new Random();
		for(int k = 0; k < i; k++)
		{
			for(int l = 0; l < j; l++)
			{
				byte abyte0[] = new byte[l];
				for(int i1 = 0; i1 < l; i1++)
					abyte0[i1] = (byte)random.nextInt();

				StringBuilder s = byteArrayToBase64(abyte0);
				byte abyte1[] = base64ToByteArray(s.toString());
				if(!Arrays.equals(abyte0, abyte1))
					System.out.println("Dismal failure!");
				s = byteArrayToAltBase64(abyte0);
				abyte1 = altBase64ToByteArray(s.toString());
				if(!Arrays.equals(abyte0, abyte1))
					System.out.println("Alternate dismal failure!");
			}

		}

	}

	private static final char intToBase64[] = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
		'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
		'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
		'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', '+', '/'
	};
	private static final char intToAltBase64[] = {
		'!', '"', '#', '$', '%', '&', '\'', '(', ')', ',', 
		'-', '.', ':', ';', '<', '>', '@', '[', ']', '^', 
		'`', '_', '{', '|', '}', '~', 'a', 'b', 'c', 'd', 
		'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
		'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', '+', '?'
	};
	private static final byte base64ToInt[] = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 
		54, 55, 56, 57, 58, 59, 60, 61, -1, -1, 
		-1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 
		5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
		25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 
		29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
		39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
		49, 50, 51
	};
	private static final byte altBase64ToInt[] = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 
		7, 8, -1, 62, 9, 10, 11, -1, 52, 53, 
		54, 55, 56, 57, 58, 59, 60, 61, 12, 13, 
		14, -1, 15, 63, 16, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
		-1, 17, -1, 18, 19, 21, 20, 26, 27, 28, 
		29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
		39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
		49, 50, 51, 22, 23, 24, 25
	};

}

public class StringEnDeCoder {
	
	public final static int CT_BASE64 = 0; 

	public final static StringBuilder Encode(int type, byte[] data) throws UnsupportedEncodingException{
		switch(type){
			case CT_BASE64: {
				return Base64.byteArrayToBase64(data);
			}
			default : throw new UnsupportedEncodingException("unsupported encoding type :" + type);
		}
	}
	
	public final static byte[] Decode(int type, String encodedString) throws UnsupportedEncodingException{
		switch(type){
			case CT_BASE64:{
				return Base64.base64ToByteArray(encodedString);
			}
			default :  throw new UnsupportedEncodingException("unsupported encoding type :" + type);
		}
	}
}
