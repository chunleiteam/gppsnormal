package com.easyservice.utils;

public class ByteUtil {
	
	public final static int getInt(byte[] b){
		return getInt(b, 0);
	}
	
	public final static int getBitInt(byte b, int start, int end){
		 return ((int)((byte)(byteToInt(b) << start)) >> start) >> (8-end);
	}
	
	public final static int getInt(byte[] b, int start, int end){
		int rt = 0;
		for (int i = start; i < end; i++){
			rt <<= 8;
			rt |= byteToInt(b[i]);
		}
		return rt;
	}
	
	public  final static int byteToInt(byte b) {
		return ((b >> 0) & 0x80000000) >>> 24 | ((b << 0) & 0x7f);
	}
	
	public final static int getInt(byte[] b, int start){
		return (byteToInt(b[start++]) << 24)
		| (byteToInt(b[start++]) << 16)
		| (byteToInt(b[start++]) << 8)
		| (byteToInt(b[start++]));
	}
	
	public final static long getLong(byte[] b, int start){
		return getLong(b, start, start + 8);
	}
	
	public final static short getShort(byte[] b, int start){
		return  (short)((short)byteToInt(b[start])<<8 | b[start+1]);
	}
	
	public final static long getLong(byte[] b, int start , int end){
		long rt = 0;
		for (int i = start; i < end; i++){
			rt <<= 8;
			rt |= byteToInt(b[i]);
		}
		return rt;
	}
	
	public final static String getHex(byte[] b, int start ,int end){
		StringBuilder rt = new StringBuilder();
		for (int i = start; i < end; i++){
			String h = Integer.toHexString(byteToInt(b[i]));
			if (h.length() < 2){
				rt.append('0');
			}
			rt.append(h);
		}
		return rt.toString();
	}
	
	
    public static  String getIpV4(byte[] ip, int start){
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < 4; i++){
    		byte b = ip[i + start];
    		sb.append(byteToInt(b));
    		if ( i < 3) 
    			sb.append('.');
    	}
    	return sb.toString();
    }
    
    public static String getBitMeaning(byte b, int start, String[] meanings){
    	StringBuilder sb = new StringBuilder();
    	int ib = byteToInt(b);
    	for (int i = start; i < start + meanings.length; i++){
    		if ( (ib & (1 << (8 - i - 1))) != 0){
    			sb.append(meanings[i - start]).append(";");
    		}
    	}
    	return sb.toString();
    }
    
    public static int checkString(byte[] b, int start, String check, boolean caseSensive){
    	
    	while ( start < b.length && Character.isWhitespace(b[start]) ){
    		start ++;
    	}
    	if (b.length <= start + check.length()){
    		return -1;
    	}
    	for (int i = start; i < start + check.length(); i++ ){
    		boolean t = false;
    		int k = i -start;
    		if (caseSensive){
    			t = ( (char)b[i] == check.charAt(k) );
    		}else{
    			t = (  (char)b[i] == check.charAt(k) || (char)b[i] == Character.toLowerCase(check.charAt(k))); 
    		}
    		if (!t){
    			return -1;
    		}
    	}
    	return start;
    }
    
    public static int skipWhiteSpace(byte[] b, int start){
    	while ( start < b.length && Character.isWhitespace(b[start]) ){
    		start ++;
    	}
    	return start;
    }
    
    public static int indexOf(byte[] b, int start, int end, int ch){
    	for (int i = start; i < end; i++){
    		if (b[i] == ch){
    			return i;
    		}
    	}
    	return -1;
    }
    
    public static int indexOf(byte[] b, int start, int ch){
    	for (int i = start; i < b.length; i++){
    		if (b[i] == ch){
    			return i;
    		}
    	}
    	return -1;
    }
    
    public static int lastIndexOf(byte[] b, int start, int end,  int ch){
    	for (int i = start; i < end; i++){
    		int index = end - (i - start);
    		if (b[ index  ] == ch){
    			return index;
    		}
    	}
    	return -1;
    }
    
}
