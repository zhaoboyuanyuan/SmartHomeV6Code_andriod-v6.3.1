package cc.wulian.smarthomev6.support.core.apiunit;

import android.text.TextUtils;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * 作者: mamengchao
 * 时间: 2017/4/20 0020
 * 描述: http返回结果错误处理
 * 联系方式: 805901025@qq.com
 */

public class HttpResponseDicionary {

    /**
     * 根据错误码和错误描述，获取错误提示字符串 <p>
     * 如果错误描述不为空，则优先显示，否则根据错误代码解析
     *
     * @param resultCode
     * @param resultDesc
     * @return
     */
    public static String getResultDescByCode(String resultCode, String resultDesc) {

        if (StringUtil.equals(resultCode, "0")) {
//            resultDesc = "sucessful";
        } else if (StringUtil.equals(resultCode, "20101")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20101);
        } else if (StringUtil.equals(resultCode, "20102")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20102);
        } else if (StringUtil.equals(resultCode, "20103")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20103);
        } else if (StringUtil.equals(resultCode, "20104")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20104);
        } else if (StringUtil.equals(resultCode, "20105")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20105);
        } else if (StringUtil.equals(resultCode, "20106")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20106);
        } else if (StringUtil.equals(resultCode, "20107")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20107);
        } else if (StringUtil.equals(resultCode, "20108")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20108);
        } else if (StringUtil.equals(resultCode, "20109")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20109);
        } else if (StringUtil.equals(resultCode, "20110")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20110);
        } else if (StringUtil.equals(resultCode, "20111")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20111);
        } else if (StringUtil.equals(resultCode, "20112")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20112);
        } else if (StringUtil.equals(resultCode, "20113")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20113);
        } else if (StringUtil.equals(resultCode, "20114")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20114);
        } else if (StringUtil.equals(resultCode, "20115")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20115);
        } else if (StringUtil.equals(resultCode, "20116")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20116);
        } else if (StringUtil.equals(resultCode, "20118")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20118);
        } else if (StringUtil.equals(resultCode, "20119")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_20119);
        } else if (StringUtil.equals(resultCode, "10601")) {
            resultDesc = MainApplication.getApplication().getString(R.string.http_error_10601);
        } else if (!TextUtils.isEmpty(resultDesc)) {
//            return resultDesc;
        } else {
            resultDesc = MainApplication.getApplication().getString(R.string.http_unknow_error);
        }

        return resultDesc;
    }
}
