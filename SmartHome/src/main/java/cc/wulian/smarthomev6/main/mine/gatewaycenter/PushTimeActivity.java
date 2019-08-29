package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.os.Bundle;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;

/**
 * 作者: mamengchao
 * 时间: 2017/4/5 0005
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class PushTimeActivity extends BaseTitleActivity {

    @Override
    protected void initTitle() {
//        setToolBarTitle(getString(R.string.PushLevelItem_SystemState_Time));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_time, true);
    }
}
