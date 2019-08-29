package cc.wulian.smarthomev6.app;

/**
 * Created by 赵永健 on 2017/8/10.
 */
public class PasswordChecking {
    /**
     * 错误的校验账号密码
     */
    public static final String Password1="12345";//输入5位密码
    public static final String Password2="123456";//6位纯数字
    public static final String Password3 ="1234 abcd";//密码中含有空格
    public static final String Password4 ="123abc";//密码强度弱
    public static final String Password5 ="1234。abcd"; //密码中含有非法字符（合法字符为西文键盘符号）
    public static final String Password6 ="abcdefg";//6位纯字母
    public static final String Password7 ="@!#$%^";//6位纯字符
    public static final String Password18 ="12345678";//8位纯字符

    /**
     * 正确的校验账号密码
     */
    public static final String Password8="123456aaaa";//10位密码，数字加字母
    public static final String Password9="123456@#$%";//10位密码，数字加字符
    public static final String Password10="1234@#$%ab";//10位密码，数字加字符加字母
    public static final String Password11="123456789@#$%ab";//15位密码，数字加字符加字母
    public static final String Password12="1234567890@#$%ab";//16位密码，数字加字符加字母
    public static final String Password14="12345@";//含有数字和符号两种，密码长度为6到16位
    public static final String Password15="aaaaa@";//含有字母和符号两种，密码长度为6到16位


    /**
     * 错误的网关密码
     */
    public static final String gatewayPassword1="123aa";//输入5位字符（字母和数字组合) 少于6个字符
    public static final String gatewayPassword2 ="111111";//输入纯6位数字
    public static final String gatewayPassword3 ="aaaaaa";//输入纯6位字母
    public static final String gatewayPassword4 ="@!#$%^";//6位纯字符
    public static final String gatewayPassword5 ="1234 abcd";//密码中含有空格
    public static final String gatewayPassword6 ="1234。abcd"; //密码中含有非法字符（合法字符为西文键盘符号）
    public static final String gatewayPassword7 ="123abc";//密码强度弱

    /**
     * 正确的网关密码
     */
    public static final String gatewayPassword8="123456abcc";//10位密码，数字加字母
    public static final String gatewayPassword9="123456@#$%";//10位密码，数字加字符
    public static final String gatewayPassword10="1234@#$%ab";//10位密码，数字加字符加字母
    public static final String gatewayPassword11="123456789@#$%ab";//15位密码，数字加字符加字母
    public static final String gatewayPassword12="1234567890@#$%ab";//16位密码，数字加字符加字母
    public static final String gatewayPassword13="123aa@";//6位数字，字母，符号的组合
    public static final String gatewayPassword14="1234567890aaa@#!";//16位数字，字母，符号的组合

}
