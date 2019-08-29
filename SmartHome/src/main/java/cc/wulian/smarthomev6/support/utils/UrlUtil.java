package cc.wulian.smarthomev6.support.utils;


/**
 * created by huxc  on 2018/11/9.
 * func：   微信相框url
 * email: hxc242313@qq.com
 */

public class UrlUtil {

    public static String URL_PREFIX = "https://mp.weixin.qq.com/a/";
    public static boolean isWechatAlbumUrl(String url){
        if(url.startsWith(URL_PREFIX)){
            return  true;
        }else {
            return false;
        }
    }

    /**
     * 短网址生成方法
     * 这个方法会生成四个短字符串,每一个字符串的长度为6
     * @param url
     * @return
     */
    public static String[] shortUrl(String url) {
        String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h",

                "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",

                "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",

                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",

                "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",

                "U", "V", "W", "X", "Y", "Z"
        };

        // 对传入网址进行 MD5 加密
        String sMD5EncryptResult = MD5Util.encrypt(url);

        String hex = sMD5EncryptResult;

        String[] resUrl = new String[4];

        for (int i = 0; i < 4; i++) {
            //把加密字符按照8位一组16进制与0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);

            //这里需要使用long型来转换,因为Inteper.parseInt()只能处理31位,首位为符号位,如果不用long则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);

            String outChars = "";

            for (int j = 0; j < 6; j++) {
                //把得到的值与0x0000003D进行位与运算,取得字符数组chars索引
                long index = 0x0000003D & lHexLong;

                //把取得的字符相加
                outChars += chars[(int) index];

                //每次循环按位右移5位
                lHexLong = lHexLong >> 5;
            }

            //把字符串存入对应索引的输出数组
            resUrl[i] = outChars;
        }

        return resUrl;
    }

    /**
     * 获取我想要的字符串,将生成的前两个相加,得到我想要的12位字符
     * @param url
     * @return
     */
    public static String getShortUrl(String url){
        String[] aResult = shortUrl(url);

        return aResult[0] + aResult[1];
    }
}
