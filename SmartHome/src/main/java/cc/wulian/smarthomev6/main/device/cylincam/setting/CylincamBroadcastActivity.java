package cc.wulian.smarthomev6.main.device.cylincam.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tutk.IOTC.AVIOCTRLDEFs;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.main.device.cylincam.bean.BroadcastBean;
import cc.wulian.smarthomev6.main.device.cylincam.server.IotSendOrder;
import cc.wulian.smarthomev6.main.device.cylincam.server.helper.CameraHelper;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.cylincam.CylincamPlayActivity.cameaHelper;

/**
 * created by huxc  on 2017/9/13.
 * func：  小物摄像机语音播报
 * email: hxc242313@qq.com
 */

public class CylincamBroadcastActivity extends BaseTitleActivity implements View.OnClickListener, CameraHelper.Observer {
    private RelativeLayout rlBroadcast;
    private ListView lvBroadcastVoice;
    private RelativeLayout rlEnglish;
    private RelativeLayout rlChinese;
    private ImageView ivEnglishChecked;
    private ImageView ivChineseChecked;

    private VoiceAdapter adapter;
    private List<String> mData;
    private BroadcastBean broadcastBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylincam_broadcast, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Camera_Voice_Set));
    }

    @Override
    protected void initView() {
        super.initView();
        rlChinese = (RelativeLayout) findViewById(R.id.rl_ch);
        rlEnglish = (RelativeLayout) findViewById(R.id.rl_en);
        lvBroadcastVoice = (ListView) findViewById(R.id.lv_broadcast_voice);
        ivChineseChecked = (ImageView) findViewById(R.id.iv_checked_ch);
        ivEnglishChecked = (ImageView) findViewById(R.id.iv_checked_en);

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlChinese.setOnClickListener(this);
        rlEnglish.setOnClickListener(this);
        lvBroadcastVoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectItem(position);
                adapter.notifyDataSetInvalidated();
                IotSendOrder.setCameraVolume(cameaHelper.getmCamera(), getSelectVolume(position));
            }
        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_en:
                ivEnglishChecked.setVisibility(View.VISIBLE);
                ivChineseChecked.setVisibility(View.GONE);
                IotSendOrder.setLanguage(cameaHelper.getmCamera(), 1);
                break;
            case R.id.rl_ch:
                ivChineseChecked.setVisibility(View.VISIBLE);
                ivEnglishChecked.setVisibility(View.GONE);
                IotSendOrder.setLanguage(cameaHelper.getmCamera(), 2);
                break;
            case R.id.img_left:
                setResult(RESULT_OK);
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (cameaHelper != null) {
            cameaHelper.attach(this);
        }
        broadcastBean = (BroadcastBean) getIntent().getSerializableExtra("broadcastBean");
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new VoiceAdapter(this, mData);
        showSelectedLanguage(broadcastBean);
        showSelectedVolume(broadcastBean);
        lvBroadcastVoice.setAdapter(adapter);
    }

    private void getData() {
        mData = new ArrayList<>();
        mData.add(getString(R.string.Cateyemini_Humandetection_Mute));
        mData.add(getString(R.string.Low));
        mData.add(getString(R.string.Cateyemini_Humandetection_Middle));
        mData.add(getString(R.string.High));
        mData.add(getString(R.string.Camera_Ultrahigh));
    }

    private void showSelectedLanguage(BroadcastBean bean) {
        switch (bean.getVolume()) {
            case 10://静音
                setSelectItem(0);
                break;
            case -68://低
                setSelectItem(1);
                break;
            case -36://中
                setSelectItem(2);
                break;
            case -24://高
                setSelectItem(3);
                break;
            case -2://超高
                setSelectItem(4);
                break;
            default:
                break;
        }

    }

    private void showSelectedVolume(BroadcastBean bean) {
        switch (bean.getLanguage()) {
            case 1:
                ivEnglishChecked.setVisibility(View.VISIBLE);
                break;
            case 2:
                ivChineseChecked.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private int getSelectVolume(int position) {
        int volume = 0;
        switch (position) {
            case 0:
                volume = 10;
                break;
            case 1:
                volume = -68;
                break;
            case 2:
                volume = -36;
                break;
            case 3:
                volume = -24;
                break;
            case 4:
                volume = -2;
                break;
        }
        return volume;
    }

    @Override
    public void avIOCtrlOnLine() {

    }

    @Override
    public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (avIOCtrlMsgType) {
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VOLUME_RESP:
                        WLog.i(TAG, "音量播报设置成功");
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_LANGUAGE_RESP:
                        WLog.i(TAG, "语言设置成功");
                        break;
                }
            }
        });
    }

    @Override
    public void avIOCtrlMsg(int resCode, String method) {

    }


    public class VoiceAdapter extends WLBaseAdapter<String> {
        public VoiceAdapter(Context context, List<String> data) {
            super(context, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CylincamBroadcastActivity.ViewHolder holder = null;
            if (convertView == null) {
                holder = new CylincamBroadcastActivity.ViewHolder();
                convertView = mInflater.inflate(R.layout.item_broadcast_voice, null);
                holder.ivCheck = (ImageView) convertView.findViewById(R.id.iv_checked);
                holder.tvBroadcastLanguage = (TextView) convertView.findViewById(R.id.tv_broadcast_language);
                holder.tvBroadcastLanguage.setText(mData.get(position));
                convertView.setTag(holder);
            } else {
                holder = (CylincamBroadcastActivity.ViewHolder) convertView.getTag();
            }
            if (position == selectItem) {
                holder.ivCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivCheck.setVisibility(View.GONE);
            }
            return convertView;
        }

    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    private int selectItem = -1;

    public final class ViewHolder {
        public ImageView ivCheck;
        public TextView tvBroadcastLanguage;
    }
}
