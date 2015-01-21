package com.easyservice.support.convert.basic;

import java.util.regex.Pattern;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;
import com.easyservice.utils.ByteUtil;
import com.easyservice.utils.LongTools;

public class StringLongConverter implements IConverter<String, Long, Integer> {

	public final static String FORMAT_IPV4_STR = "xxx.xxx.xxx.xxx";
	public final static int FORMAT_IPV4 = -100;
	public final static int FORMAT_DATETIME = -102;
	private static final String regex = "((25[0-5]|2[0-4]\\d|1\\d{2}|0?[1-9]\\d|0?0?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|0?[1-9]\\d|0?0?\\d)";   
	private static final Pattern pattern = Pattern.compile(regex);   
	static int defaultFormat = 10;
	int format = defaultFormat;
	
	public Long convert(String source) throws ConvertException {
		return convert(source, format);
	}

	public Long convert(String source, Integer format) throws ConvertException {
			if (format > 0){
				return Long.parseLong(source, format);
			}else {
				if (format == FORMAT_IPV4){
					String[] sp = source.split("\\.");
					if (sp.length != 4){ //only support IPv4
						throw new  ConvertException(source + " is illegal ipaddress");
					}
					if(!pattern.matcher(source).matches()){
						throw new  ConvertException(source + " is illegal ipaddress");
					}
					byte[] lbts = new byte[]{0,0,0,0,0,0,0,0};
					int start = 4;
					for (String s : sp){
						lbts[start++] = (byte)Integer.parseInt(s);
					}
					return LongTools.bytesToLong(lbts);
				}else{
					throw new ConvertException("not support format " + format);
				}
			}
			
	}

	public Integer getDefalutFormat() {
		return defaultFormat;
	}

	public Integer getFormat() {
		return format;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<Long> getTargetType() {
		return Long.class;
	}

	public String restore(Long target) throws ConvertException {
		return restore(target, format);
	}

	public String restore(Long target, Integer format) throws ConvertException {
		if (format > 0){
			return Long.toString(target, format);
		}else {
			if (format == FORMAT_IPV4){
				return ByteUtil.getIpV4(LongTools.longToBytes(target), 4);
			}else{
				throw new ConvertException("not support format " + format);
			}
		}
	}

	public void setFormat(Integer format) {
		this.format = format;
	}

	public Integer parseFormat(String s) throws ConvertException {
		if (FORMAT_IPV4_STR.equals(s)){
			return FORMAT_IPV4;
		}
		return Integer.parseInt(s);
	}

}
