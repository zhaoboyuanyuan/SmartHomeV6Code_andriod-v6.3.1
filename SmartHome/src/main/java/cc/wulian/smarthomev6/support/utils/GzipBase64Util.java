package cc.wulian.smarthomev6.support.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipBase64Util {
	private static class Base64
	{
	 private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	 
	 
	     public static String encode(byte[] data) {
	         int start = 0;
	         int len = data.length;
	         StringBuffer buf = new StringBuffer(data.length * 3 / 2);
	 
	         int end = len - 3;
	         int i = start;
	         while (i <= end) {
	             int d = ((((int) data[i]) & 0x0ff) << 16)
	                     | ((((int) data[i + 1]) & 0x0ff) << 8)
	                     | (((int) data[i + 2]) & 0x0ff);
	 
	             buf.append(legalChars[(d >> 18) & 63]);
	             buf.append(legalChars[(d >> 12) & 63]);
	             buf.append(legalChars[(d >> 6) & 63]);
	             buf.append(legalChars[d & 63]);
	             i += 3;
	         }
	 
	         if (i == start + len - 2) {
	             int d = ((((int) data[i]) & 0x0ff) << 16)
	                     | ((((int) data[i + 1]) & 255) << 8);
	 
	             buf.append(legalChars[(d >> 18) & 63]);
	             buf.append(legalChars[(d >> 12) & 63]);
	             buf.append(legalChars[(d >> 6) & 63]);
	             buf.append("=");
	         } else if (i == start + len - 1) {
	             int d = (((int) data[i]) & 0x0ff) << 16;
	 
	             buf.append(legalChars[(d >> 18) & 63]);
	             buf.append(legalChars[(d >> 12) & 63]);
	             buf.append("==");
	         }
	 
	         return buf.toString();
	     }
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static class Base64Helper {
		public static int type = 0;
		private static final int ANDROID = 1;
		
		private static Class<?> androidBase64 = null;
		private static int androidType;
		private static final int JAVA1_8 = 2;
		private static Method encoder = null;
		
		private static Method jdk1_8encoder = null;
		private static Object jdk1_8encoderObj = null;
		
		public static String encode(byte[] raw) {
			try {
			String b64 = "";
			switch(type){
			case ANDROID:
				b64 = (String) encoder.invoke(androidBase64, raw, androidType);
				return b64;
			case JAVA1_8:
				b64 = (String)jdk1_8encoder.invoke(jdk1_8encoderObj, raw);
				return b64;
			default:
				b64 = Base64.encode(raw);
				return b64.replaceAll("[ \r\n]", "");
			}
			} catch (Exception e) {
				Logger.error(e);
				throw new RuntimeException();
			}
		}
		
		static {
			try {
				androidBase64 = Class.forName("android.util.Base64");
				Class[] cArg = new Class[2];
				cArg[0] = byte[].class;
				cArg[1] = int.class;
				encoder = androidBase64.getMethod("encodeToString", cArg);
				androidType = androidBase64.getField("NO_WRAP").getInt(androidBase64);  
				type = ANDROID;
			} catch (ClassNotFoundException e) {
				//not Android, just go on.
			} catch (IllegalArgumentException e) {
				Logger.error(e);
			} catch (IllegalAccessException e) {
				Logger.error(e);
			} catch (NoSuchFieldException e) {
				Logger.error(e);
			} catch (SecurityException e) {
				Logger.error(e);
			} catch (NoSuchMethodException e) {
				Logger.error(e);
			}
			
			if(type == 0) {
				try {
					Class jdkBase64 = Class.forName("java.util.Base64");
					Class[] cArg0 = new Class[0];
					Method getjdkcoder = jdkBase64.getMethod("getEncoder", cArg0);
					jdk1_8encoderObj = getjdkcoder.invoke(jdkBase64, new Object[0]);
					Class[] cArg = new Class[1];
					cArg[0] = byte[].class;
					jdk1_8encoder =  jdk1_8encoderObj.getClass().getMethod("encodeToString", cArg);
					type = JAVA1_8;
				} catch (ClassNotFoundException e) {
					//not jdk1.8, just go on.
				} catch (NoSuchMethodException e) {
					Logger.error(e);
				} catch (SecurityException e) {
					Logger.error(e);
				} catch (IllegalAccessException e) {
					Logger.error(e);
				} catch (IllegalArgumentException e) {
					Logger.error(e);
				} catch (InvocationTargetException e) {
					Logger.error(e);
				}
			}
		}
	}
	
	public static String compress(String input)
			throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream ins = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(ins);
		gzip.write(input.getBytes("UTF-8"));
		gzip.close();
		byte[] in1 = ins.toByteArray();
		String in2 = Base64Helper.encode(in1);
		return in2;
	}
	
	public static String deCompress(String input)
			throws UnsupportedEncodingException, IOException {
		byte[] in1 = com.alibaba.fastjson.util.Base64.decodeFast(input);
		ByteArrayInputStream ins = new ByteArrayInputStream(in1);
		GZIPInputStream  gzip = new GZIPInputStream(ins);
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		byte[] buffer = new byte[256];
        int n;
        while ((n = gzip.read(buffer)) >= 0) { 
           out.write(buffer, 0, n);
        }
        return out.toString("UTF-8");
	}

}
