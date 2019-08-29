package cc.wulian.smarthomev6.utils;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 封装外部接口
 */
public class InterfaceUtils {

	public static boolean cancelAccountRegistered(String account) {
		return false;
	}

	public static boolean restoreAccountPassword(String account, String newPassword) {
		return false;
	}

	public static boolean restoreGatewayPassword(String gatewayNumber, String newPassword) {
		return false;
	}

	public static boolean restoreAccountBindGateway(String account, String gatewayNum) {
		return false;
	}
}
