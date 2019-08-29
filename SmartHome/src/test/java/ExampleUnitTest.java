import android.text.SpannableString;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.wulian.smarthomev6.support.utils.StringUtil;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test() throws Exception {
        String html = "<font>afontsd</font>font sadfsd<font>是的</font>按时";
//        System.out.print(StringUtil.addColorOrSize(html, new String[]{"0xff123456", "0xff1sdfsd", "0xffsdf"}, 20));
        Pattern p = Pattern.compile("<[/]?font>");
        Matcher m = p.matcher(html);
        String result = null;
        List<Integer> index = new ArrayList<Integer>();
        while (m.find()) {
            System.out.println(m.start());
            index.add(m.start());
            m.replaceFirst("");
            result = m.replaceFirst("");
            m = p.matcher(result);
        }
        for(int i: index){
            System.out.println(i);
        }
    }
}