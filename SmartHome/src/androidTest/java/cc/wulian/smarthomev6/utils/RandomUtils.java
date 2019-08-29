package cc.wulian.smarthomev6.utils;

import java.util.Random;

/**
 * Created by 赵永健 on 2017/5/16.
 *
 * 封装随机数支持类
 */
public class RandomUtils {

	public static int randomFour() {
		return new Random().nextInt(9999-1000+1)+1000;
	}
}
