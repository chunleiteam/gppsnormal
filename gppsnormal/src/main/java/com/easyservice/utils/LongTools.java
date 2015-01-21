package com.easyservice.utils;

public abstract class LongTools {
	
/*	public static final byte[] longToCTimeBytes(long l){
		l = (long)(int)l;
		byte[] ba = new byte[4];
		ba[0] = (byte) (l >> 24); 
		ba[1] = (byte) (l >> 16);
		ba[2] = (byte) (l >> 8);
		ba[3] = (byte) (l & 0xff);	
		return ba;
	}
*/	
	public static final byte[] longToCTimeBytes(long l){
		l = (long)(int)l;
		byte[] ba = new byte[4];
		ba[3] = (byte) (l >> 24); 
		ba[2] = (byte) (l >> 16);
		ba[1] = (byte) (l >> 8);
		ba[0] = (byte) (l & 0xff);	
		return ba;
	}
	public static final byte[] longToBytes(long l) {
		byte[] ba = new byte[8];
		ba[0] = (byte) (l >> 56);
		ba[1] = (byte) (l >> 48);
		ba[2] = (byte) (l >> 40);
		ba[3] = (byte) (l >> 32);
		ba[4] = (byte) (l >> 24);
		ba[5] = (byte) (l >> 16);
		ba[6] = (byte) (l >> 8);
		ba[7] = (byte) (l & 0xff);
		return ba;
	}

	public static final byte[] longToCTimeBytes2(long l){
		byte[] ba = new byte[8];
		ba[7] = (byte) (l >> 56);
		ba[6] = (byte) (l >> 48);
		ba[5] = (byte) (l >> 40);
		ba[4] = (byte) (l >> 32);
		ba[3] = (byte) (l >> 24);
		ba[2] = (byte) (l >> 16);
		ba[1] = (byte) (l >> 8);
		ba[0] = (byte) (l & 0xff);
		return ba;
	}
	
	public static final long bytesToLong(byte[] b) {
		return (((long) IntTools.bytesToInt(b[0], b[1], b[2], b[3])) << 32)
			| (IntTools.bytesToInt(b[4], b[5], b[6], b[7]) & 0xffffffffL);
	}

	public static final long bytesToLong(byte[] b, int start) {
		return (
			((long) IntTools.bytesToInt(b[start++], b[start++], b[start++], b[start++])) << 32)
			| (IntTools.bytesToInt(b[start++], b[start++], b[start++], b[start++])& 0xffffffffL);
	}

	public static void main(String[] a) {
		long[] la = { 1231232344l, -219402l, 899888899, -329840233289898942l };
		for (int i = 0; i < la.length; i++)
			System.out.println(
				LongTools.bytesToLong(LongTools.longToBytes(la[i])));
	}
}
