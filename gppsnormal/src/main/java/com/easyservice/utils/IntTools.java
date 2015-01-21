/*
 * @Source(IntTools.java)
 * Copyright 2003-5-10 Yuexiang Zhang All right reserved
 */
package com.easyservice.utils;
public class IntTools {
	
	public final static byte[] intToBytes(int v) {
		byte[] ba = new byte[4];
		ba[0] = (byte) (v >> 24);
		ba[1] = (byte) (v >> 16);
		ba[2] = (byte) (v >> 8);
		ba[3] = (byte) v;
		return ba;
	}

	public final static byte[] intToBytes(int v, byte[] ba, int from){
		ba[from] = (byte) (v >> 24);
		ba[from+1] = (byte) (v >> 16);
		ba[from+2] = (byte) (v >> 8);
		ba[from+3] = (byte) v;
		return ba;
	}
	
	public final static int bytesToInt(byte[] b) {
		return (byteToInt(b[0]) << 24)
			| (byteToInt(b[1]) << 16)
			| (byteToInt(b[2]) << 8)
			| (byteToInt(b[3]));
	}
	
	public final static int bytesToInt(byte b1, byte b2, byte b3, byte b4) {
		return (byteToInt(b1) << 24)
			| (byteToInt(b2) << 16)
			| (byteToInt(b3) << 8)
			| (byteToInt(b4));
	}
	
	public final static int bytesToInt(byte[] b, int start) {
		return (byteToInt(b[start++]) << 24)
			| (byteToInt(b[start++]) << 16)
			| (byteToInt(b[start++]) << 8)
			| (byteToInt(b[start++]));
	}
	
	public final static String intToHex(int i){
		String s = Integer.toHexString(i);
		while(s.length() < 8){
			s = "0" + s;
		}
		return s;
	}
	
	public final static String intToHex(byte[] i, int start){
		String s = Integer.toHexString(bytesToInt(i, start));
		while(s.length() < 8){
			s = "0" + s;
		}
		return s;
	}
	
	public  final static int byteToInt(byte b) {
		return ((b >> 0) & 0x80000000) >>> 24 | ((b << 0) & 0x7f);
	}

}
