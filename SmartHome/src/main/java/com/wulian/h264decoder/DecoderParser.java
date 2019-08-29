package com.wulian.h264decoder;
/**
 * 采用生产者消费者模式FILO
 * @ClassName:  DecoderParser
 * @Function:   编码转换
 * @date:       2015年10月17日
 * @author      Puml
 * @email       puml@wuliangroup.cn
 */
public class DecoderParser {
	public static native int InitDecoder();

	public static native int UninitDecoder();

	public static native int DecoderNal(byte[] in, int insize,boolean isReverse, byte[] out,int[] resolution);
}
