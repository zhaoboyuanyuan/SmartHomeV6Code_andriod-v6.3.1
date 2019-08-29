package cc.wulian.smarthomev6.support.tools;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/4/12.
 * 正则表达式工具
 */

public class RegularTool {

    private static String SPECIAL_CHAR = "`\\\\~!@#$%^&*.()+=|{}':;'\\[\\],<>/?";
    public static int WLPassWordStrengthNone = 0;
    public static int WLPassWordStrengthIllegal = -1;
    public static int WLPassWordStrengthNoMatchRule = -2;
//    public static int WLPassWordStrengthLow = 1;
    public static int WLPassWordStrengthMiddle = 2;
    public static int WLPassWordStrengthHigh = 3;

    /**
     * 检测手机号是否合法
     *
     * @param text
     * @return
     */
    public static boolean isLegalChinaPhoneNumber(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
//        String regExp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$";
        String regExp = "^1\\d{10}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 检测邮箱地址是否合法
     *
     * @param text
     * @return
     */
    public static boolean isLegalEmailAddress(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
//        String regExp = "[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
        String regExp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 检测网关号是否合法
     *
     * @param text
     * @return
     */
    public static boolean isLegalGatewayID(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^[a-zA-Z0-9]{11,12}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 基本规则：6-16位，密码至少有数字、字母或符号的2种组合
     *
     * @param text
     * @return
     */
    private static boolean isBasicRule(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?![A-Za-z]+$)(?![0-9]+$)(?!["+ SPECIAL_CHAR+ "]+$)[A-Za-z0-9" + SPECIAL_CHAR+ "]{8,16}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 基本规则：8-32位，密码至少有数字、字母或符号的2种组合
     *
     * 乐橙设备的密码格式
     * @param text
     * @return
     */
    public static boolean isLcPwdRule(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?![A-Za-z]+$)(?![0-9]+$)(?!["+ SPECIAL_CHAR+ "]+$)[A-Za-z0-9" + SPECIAL_CHAR+ "]{8,32}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 是否包含非法字符
     *
     * @param text
     * @return
     */
    public static boolean isIllegal(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "[^0-9a-zA-Z"+ SPECIAL_CHAR+ "]";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        int count = 0;
        while (m.find()){
            count++;
        }
        return count > 0;
    }

    /**
     * 弃用
     * 密码强度：弱
     *
     * @param text
     * @return
     */
//    private static boolean isWeakPassword(String text) {
//        if (TextUtils.isEmpty(text)) {
//            return false;
//        }
//        String regExp = "^(?=.*[a-zA-Z])(?=.*[0-9])([a-zA-Z0-9]{6,9})$";
//        Pattern p = Pattern.compile(regExp);
//        Matcher m = p.matcher(text);
//        return m.matches();
//    }

    /**
     * 密码强度：中（含有字母和符号两种,6-16）
     *
     * @param text
     * @return
     */
    private static boolean isMiddleCharSymbol(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?=.*[a-zA-Z])(?=.*["+ SPECIAL_CHAR+ "])[a-zA-Z"+ SPECIAL_CHAR+ "]{8,16}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 密码强度：中（含有数字和符号两种.6-16）
     *
     * @param text
     * @return
     */
    private static boolean isMiddleNumSymbol(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?=.*[0-9])(?=.*["+ SPECIAL_CHAR+ "])[0-9"+ SPECIAL_CHAR+ "]{8,16}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 密码强度：中（含有字母和数字两种,10-16）
     *
     * @param text
     * @return
     */
    private static boolean isMiddleCharNum(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,16}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 密码强度：中（含有数字，字母和符号三种，密码长度6-13）
     *
     * @param text
     * @return
     */
    private static boolean isMiddleNumCharSymbol(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*["+ SPECIAL_CHAR+ "])[0-9a-zA-Z"+ SPECIAL_CHAR+ "]{8,13}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 密码强度：强（含有数字，字母和符号三种，密码长度14-16）
     *
     * @param text
     * @return
     */
    private static boolean isStrongAllAndMoreLen(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*["+ SPECIAL_CHAR+ "])[0-9a-zA-Z"+ SPECIAL_CHAR+ "]{14,16}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 密码强度：强（含有数字和符号两种.14-16）
     *
     * @param text
     * @return
     */
    private static boolean isStrongNumSymbolMoreLen(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?=.*[0-9])(?=.*["+ SPECIAL_CHAR+ "])[0-9"+ SPECIAL_CHAR+ "]{14,16}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 密码强度：强（含有字母和符号两种,14-16）
     *
     * @param text
     * @return
     */
    private static boolean isStrongCharSymbolMoreLan(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^(?=.*[a-zA-Z])(?=.*["+ SPECIAL_CHAR+ "])[a-zA-Z"+ SPECIAL_CHAR+ "]{14,16}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 密码强度：强（含有至少两个符号）
     *
     * @param text
     * @return
     */
    private static boolean isStrongCharMoreLanTwo(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "["+ SPECIAL_CHAR+ "]";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        int count = 0;
        while (m.find()){
            count++;
        }
        return count > 1;
    }

    /**
     *  验证是否需要修改密码（密码强度属于中，强）
     * @param password
     * @return
     * */
    public static boolean isNeedResetPwd(String password){
        if (TextUtils.isEmpty(password)) {
            return true;
        }
        if (password.length() == 0){
            return true;
        }

        if (isIllegal(password)){
            //非法字符
            return true;
        } else {
            if (isBasicRule(password)){
                if (isStrongAllAndMoreLen(password)||
                        ((isStrongCharSymbolMoreLan(password)|| isStrongNumSymbolMoreLen(password))&&
                                isStrongCharMoreLanTwo(password))){
                    //强
                    return false;
                } else if (isMiddleCharNum(password)||
                        isMiddleCharSymbol(password)||
                        isMiddleNumCharSymbol(password)||
                        isMiddleNumSymbol(password)){
                    //中
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    /**
     *  验证密码强度：非法字符->基本规则->强->中->弱
     * @param password
     * @return
     * */
    public static int CheckPassword(String password){
        if (TextUtils.isEmpty(password)) {
            return WLPassWordStrengthNone;
        }
        if (password.length() == 0){
            return WLPassWordStrengthNone;
        }

        if (isIllegal(password)){
            //非法字符
            return WLPassWordStrengthIllegal;
        } else {
            if (isBasicRule(password)){
                if (isStrongAllAndMoreLen(password)||
                        ((isStrongCharSymbolMoreLan(password)|| isStrongNumSymbolMoreLen(password))&&
                                isStrongCharMoreLanTwo(password))){
                    //强
                    return WLPassWordStrengthHigh;
                } else if (isMiddleCharNum(password)||
                        isMiddleCharSymbol(password)||
                        isMiddleNumCharSymbol(password)||
                        isMiddleNumSymbol(password)){
                    //中
                    return WLPassWordStrengthMiddle;
                }  else {
                    return WLPassWordStrengthNoMatchRule;
                }
            } else {
                return WLPassWordStrengthNoMatchRule;
            }
        }
    }
}
