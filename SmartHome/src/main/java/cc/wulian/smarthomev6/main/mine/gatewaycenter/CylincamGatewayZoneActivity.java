package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.CameraZoneAdapter;
import cc.wulian.smarthomev6.main.device.adapter.CylincamZoneAdapter;
import cc.wulian.smarthomev6.main.device.lookever.bean.ZoneBean;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.KeyboardUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.Trans2PinYin;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/12/7.
 * func： 小物网关时区
 * email: hxc242313@qq.com
 */

public class CylincamGatewayZoneActivity extends BaseTitleActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;

    private CylincamZoneAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private String[] zoneArray = {"GMT±0", "GMT+1", "GMT+2", "GMT+3", "GMT+4", "GMT+5", "GMT+6", "GMT+7", "GMT+8", "GMT+9", "GMT+10", "GMT+11", "GMT±12"
            , "GMT-11", "GMT-10", "GMT-9", "GMT-8", "GMT-7", "GMT-6", "GMT-5", "GMT-4", "GMT-3", "GMT-2", "GMT-1"};
    private String[] zoneArrayValue = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
            "-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1"};

    private List<String> zoneData;
    private String zoneName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylincam_gateway_zone, true);
    }


    @Override
    protected void initView() {
        super.initView();
        mRecyclerView = (RecyclerView) findViewById(R.id.camera_zone_recycler);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Time_Zone));
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mAdapter.setOnItemClickListener(new CylincamZoneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.setSelectItem(position);
                mAdapter.notifyDataSetChanged();
                sendCmd(zoneArrayValue[position]);
                Intent it = new Intent();
                it.putExtra("zoneName", zoneArrayValue[position]);
                setResult(RESULT_OK, it);
                finish();
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        zoneName = getIntent().getStringExtra("zoneName");
        mAdapter = new CylincamZoneAdapter();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        mAdapter.setData(getData());
        setDefaultSelectedItem(zoneName);
    }

    private List<String> getData() {
        zoneData = new ArrayList<>();
        Collections.addAll(zoneData, zoneArray);
        return zoneData;
    }

    /**
     * 默认设置地区显示
     * 取消键 显示设置的地区
     */
    private void setDefaultSelectedItem(String zoneName) {
        mAdapter.setSelectItem(-1);
        for (int i = 0; i < zoneArrayValue.length; i++) {
            if (TextUtils.equals(zoneArrayValue[i], zoneName)) {
                mAdapter.setSelectItem(i);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void sendCmd(String zone) {
        final JSONObject object = new JSONObject();
        object.put("cmd", "512");
        object.put("gwID", preference.getCurrentGatewayID());
        object.put("mode", 1);//模式
        object.put("appID", ((MainApplication) getApplication()).getLocalInfo().appID);
        object.put("zone", zone);
        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }


}
