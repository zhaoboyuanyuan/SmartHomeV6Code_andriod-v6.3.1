package cc.wulian.smarthomev6.main.device.gateway_mini.device_d8;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.gateway_mini.VoiceAdapter;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 声音设置
 */
public class DeviceD8VoiceFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "DeviceD8VoiceFragment";
    private ListView listView;
    private SeekBar sbVoice;
    private VoiceAdapter voiceAdapter;
    private ImageView ivMute;
    private String voiceType;//声音曲目
    private int progress;//音量大小
    private String param;//app->网关 所需参数
    private int selectPosition;
    private String deviceId;
    private Device device;

    public static DeviceD8VoiceFragment start(String did) {
        DeviceD8VoiceFragment f = new DeviceD8VoiceFragment();
        Bundle b = new Bundle();
        b.putString("deviceID", did);
        f.setArguments(b);
        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getArguments().getString("deviceID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_d8_voice, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
        initData();

    }

    private void initView() {
        sbVoice = (SeekBar) getActivity().findViewById(R.id.sb_voice);
        listView = (ListView) getActivity().findViewById(R.id.lv_voice);
        ivMute = (ImageView) getActivity().findViewById(R.id.iv_mute);
    }

    private void initListener() {
        sbVoice.setOnSeekBarChangeListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                voiceAdapter.setSelectItem(position);
                voiceAdapter.notifyDataSetInvalidated();

                sendCmd(getSelectVoiceTypeParam(position));
            }
        });

    }

    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        voiceAdapter = new VoiceAdapter(getActivity(), getData());
        voiceAdapter.setSelectItem(0);//设置默认曲目
        listView.setAdapter(voiceAdapter);

    }

    private List<String> getData() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.Minigateway_Voice_DingDong));
        list.add(getString(R.string.Minigateway_Voice_Jingle));
        list.add(getString(R.string.Minigateway_Voice_Crisp));
        list.add(getString(R.string.Minigateway_Voice_Longsound));
        list.add(getString(R.string.Minigateway_Voice_Wave));
        list.add(getString(R.string.Minigateway_Voice_Cuckoo));
        list.add(getString(R.string.Minigateway_Voice_Didi));
        list.add(getString(R.string.Minigateway_Voice_Fierce));
        list.add(getString(R.string.Minigateway_Voice_Quick));
        list.add(getString(R.string.Minigateway_Voice_Sharp));
        list.add(getString(R.string.Minigateway_Voice_Policecar));
        return list;
    }

    private String getSelectVoiceTypeParam(int position) {
        selectPosition = position;
        progress = sbVoice.getProgress();
        if (position < 9 || progress < 10) {
            if (position < 9 && progress > 10) {
                voiceType = "0" + (position + 1);
                param = voiceType + progress;
            } else if (position > 9 && progress < 10) {
                voiceType = position + 1 + "";
                param = voiceType + "0" + progress;
            } else if (position < 9 && progress < 10) {
                voiceType = "0" + (position + 1);
                param = voiceType + "0" + progress;
            }
        } else {
            voiceType = position + 1 + "";
            param = voiceType + "" + progress;
        }
        return param;

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress == 0) {
            ivMute.setBackgroundResource(R.drawable.icon_mute_off);
        } else {
            ivMute.setBackgroundResource(R.drawable.icon_mute_on);
        }

    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        WLog.i(TAG, "onStartTrackingTouch: " + seekBar.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //手指离开滑竿时，mini网关同步发出声音预览，默认选择叮咚曲目
        if (StringUtil.isNullOrEmpty(voiceType)) {
            param = getSelectVoiceTypeParam(0);
        } else {
            param = getSelectVoiceTypeParam(selectPosition);
        }
        sendCmd(param);
    }

    private void sendCmd(String param) {
//        ToastUtil.show(param);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", device.gwID);
            object.put("devID", deviceId);
            object.put("cluster", 0x0500);
            object.put("endpointNumber", 1);
            object.put("endpointType", 0x0402);
            object.put("commandType", 1);
            object.put("commandId", 0x8002);
            JSONArray array = new JSONArray();
            array.put(param);
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
