package cc.wulian.smarthomev6.main.home.voicecontrol;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.entity.VoiceControlResultBean;
import cc.wulian.smarthomev6.entity.VoiceControlResultItemBean;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.view.AutoScrollTextView;
import cc.wulian.smarthomev6.main.home.view.VoiceControlHelpDetailView;
import cc.wulian.smarthomev6.main.home.view.WaveView;
import cc.wulian.smarthomev6.support.core.apiunit.VoicecontrolApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.TTSTool;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/9/19.
 */

public class VoiceControlActivity extends BaseActivity implements View.OnClickListener {

    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final String[] HELP_TITLE_DATA = {
            "开关照明",
            "插座",
            "窗帘",
            "安防设备",
            "空调",
            "场景"
    };
    private static final String[][] HELP_DATA = {
            {"关闭开关", "打开开关", "打开开关名称", "关闭开关名称"},
            {"关闭插座", "打开插座", "打开插座名称", "关闭插座名称"},
            {"关闭窗帘", "打开窗帘", "关闭窗帘名称", "打开窗帘名称"},
            {"打开水浸检测器", "关闭红外入侵探测器", "打开设备名称", "关闭设备名称"},
            {"关闭空调", "打开空调"},//, "空调进入除湿模式", "把空调温度调到26度", "风向设置为上下扫风", "把风速调成强劲风"},
            {"开启场景", "开启场景名称"}
    };

    private static final String[][] SUPPORT_DATA = {
            {"内嵌式零火线单路开关", "内嵌式零火线两路开关", "金属单路开关", "金属两路开关", "金属三路开关", "金属窗帘控制器", "单火线单路开关", "单火线单路触摸开关", "零火线单路开关", "火线单路触摸开关", "单火/零火一路网络开关", "单火线两路开关", "单火线两路触摸开关", "零火线两路开关", "零火线两路触摸开关", "零火两路网络开关", "单火线三路开关", "单火线三路触摸开关", "零火线三路开关", "零火线三路触摸开关", "单火线单路触摸调光开关", "一位DIN开关计量版", "炫彩灯"},
            {"墙面插座", "移动插座", "移动插座计量版"},
            {"窗帘电机", "窗帘控制器"},
            {"门窗磁探测器", "红外入侵探测器", "水浸监测器", "烟雾火警探测器", "可燃气泄漏探测器", "独立式可燃气体探测器", "独立式光电感烟火灾探测报警器", "机械手"},
            {"空调翻译器"},
            {}
    };

    private static final int STATE_HELP_INFO_ALL = 0;
    private static final int STATE_HELP_LIST = 1;
    private static final int STATE_VOICE = 2;
    private static final int STATE_HELP_INFO_ONE = 3;

    private LayoutInflater inflater;
    private ImageView btn_exit, btn_help;
    private View btn_voice;
    private WaveView waveView;
    private View layout_help_info;
    private AutoScrollTextView tv_autoscroll;
    private VoiceControlHelpDetailView helpDetailView;
    private ListView listview_help;
    private ListView listview_conversation;
    private ConversationAdapter conversationAdapter;

    private ArrayList<ConversationBean> conversationList = new ArrayList<>();

    // 语音识别对象
    private SpeechRecognizer mAsr;
    private String grammarID;
    private GrammarManager grammarManager;

    private VoicecontrolApiUnit voicecontrolApiUnit;

    private int state = STATE_HELP_INFO_ALL;
    private int lastState = -1;

    private ArrayList<String> textList = new ArrayList<>();

    //二次会话相关变量
    private boolean isEnd = true;
    private String savedSessionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //取消状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        inflater = LayoutInflater.from(this);
        setContentView(R.layout.acitivity_voice_control);
        voicecontrolApiUnit = new VoicecontrolApiUnit(this);
        initView();
        initListener();
        initData();
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tv_autoscroll.stop();
        if (null != mAsr) {
            // 退出时释放连接
            mAsr.cancel();
            mAsr.destroy();
        }
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.toptobottom_out);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                PERMISSION_RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSION_RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
//            startWork();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startWork();
            } else {
                // Permission Denied
                ToastUtil.singleCenter(R.string.Toast_Permission_Denied);
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        btn_exit = (ImageView) findViewById(R.id.btn_exit);
        btn_help = (ImageView) findViewById(R.id.btn_help);
        btn_voice = findViewById(R.id.btn_voice);

        waveView = (WaveView) findViewById(R.id.view_wave);

        layout_help_info = findViewById(R.id.layout_help_info);
        tv_autoscroll = (AutoScrollTextView) findViewById(R.id.tv_autoscroll);
        helpDetailView = (VoiceControlHelpDetailView) findViewById(R.id.view_help_info_detail);

        listview_help = (ListView) findViewById(R.id.listview_help);
        listview_help.setAdapter(new HelpAdapter());

        listview_conversation = (ListView) findViewById(R.id.listview_conversation);
        conversationAdapter = new ConversationAdapter();
        listview_conversation.setAdapter(conversationAdapter);
    }

    private void initListener() {
        btn_exit.setOnClickListener(this);
        btn_help.setOnClickListener(this);
        btn_voice.setOnClickListener(this);

        listview_help.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setState(STATE_HELP_INFO_ONE);
                helpDetailView.setContent(Arrays.asList(HELP_DATA[position]), Arrays.asList(SUPPORT_DATA[position]));
//                showHelpInfo(Arrays.asList(HELP_DATA[position]), false);
            }
        });
    }

    private void initData() {
        textList.clear();
        for (String[] data : HELP_DATA) {
            textList.addAll(Arrays.asList(data));
        }
        setState(STATE_HELP_INFO_ALL);
        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(this, new InitListener() {

            @Override
            public void onInit(int code) {
                WLog.i(TAG, "SpeechRecognizer init() code = " + code);
                if (code == ErrorCode.SUCCESS) {
                } else {

                }
            }
        });
        grammarManager = new GrammarManager();
        grammarManager.setGrammarTextParseCallback(new GrammarManager.GrammarTextParseCallback() {
            @Override
            public void onControlDevice(String action, Device device, String input_text) {
                if (TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayID()) || MainApplication.getApplication().getDeviceCache().getDevices().size() == 0) {
                    showErrorResult(R.string.Account_No_Device);
                    return;
                }
                String sequence = "android_sequence" + System.currentTimeMillis();
                String session_id = "android_session_id" + System.currentTimeMillis();
                savedSessionId = session_id;
                voicecontrolApiUnit.doPostControlDevice(action, device, input_text, true, sequence, session_id, new VoicecontrolApiUnit.CommonListener<VoiceControlResultBean>() {
                    @Override
                    public void onSuccess(VoiceControlResultBean bean) {
                        isEnd = bean.is_end;
                        showControlResult(bean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        showErrorResult(R.string.VoiceAssistant_Cannot);
                    }
                });
            }

            @Override
            public void onControlScene(String action, SceneInfo sceneInfo, String input_text) {
                String sequence = "android_sequence" + System.currentTimeMillis();
                String session_id = "android_session_id" + System.currentTimeMillis();
                savedSessionId = session_id;
                voicecontrolApiUnit.doPostControlScene(action, sceneInfo, input_text, true, sequence, session_id, new VoicecontrolApiUnit.CommonListener<VoiceControlResultBean>() {
                    @Override
                    public void onSuccess(VoiceControlResultBean bean) {
                        isEnd = bean.is_end;
                        showControlResult(bean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        showErrorResult(R.string.VoiceAssistant_Cannot);
                    }
                });
            }

            @Override
            public void onFail() {
                showErrorResult(R.string.VoiceAssistant_Cannot);
            }
        });
        buildGrammar();
    }

    private void buildGrammar() {
        String mContent = grammarManager.buildGrammar();//获取语法文件
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        //设置返回结果为json格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
        int ret = mAsr.buildGrammar("abnf", mContent, new GrammarListener() {
            @Override
            public void onBuildFinish(String grammarId, SpeechError error) {
                if (error == null) {
                    grammarID = grammarId;
                    WLog.i("语法构建成功：" + grammarId);
                } else {
                    WLog.e("语法构建失败,错误码：" + error.getErrorDescription());
                }
            }
        });
        if (ret != ErrorCode.SUCCESS) {
            WLog.e("语法构建失败,错误码：" + ret);
        }
    }

    private void startListening() {
        mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
        mAsr.setParameter(SpeechConstant.AUDIO_SOURCE, "1");
//        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, FileUtil.getMscPath() + "/asr.wav");
        int ret = mAsr.startListening(mRecognizerListener);
    }

    private ArrayList<byte[]> voiceDataList = new ArrayList<>();

    /**
     * 语音听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            WLog.i("当前正在说话，音量大小：" + volume);
            WLog.i(TAG, "返回音频数据：" + data.length);
//            if (volume > 1) {
//            }
            voiceDataList.add(data);
            waveView.setAimAmplitude(waveView.getMax_amplitude() * volume / 28);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                WLog.i(TAG, "recognizer result：" + result.getResultString());
                final ConversationBean bean = new ConversationBean();
                bean.text = JsonParser.parseIatResult(result.getResultString());
                if (isLast && bean.text.length() < 2) {
                    return;
                }
                bean.from = 0;
                conversationList.add(bean);
                conversationAdapter.notifyDataSetChanged();
                setState(STATE_VOICE);
                listview_conversation.post(new Runnable() {
                    @Override
                    public void run() {
                        listview_conversation.setSelection(conversationAdapter.getCount() - 1);
                        if (isEnd) {
                            startListeningUnderstand();
                        } else {
                            doControlTheSecond(bean.text);
                        }
                    }
                });
            } else {
                WLog.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            WLog.i("结束说话");
            waveView.setVisibility(View.GONE);
            btn_voice.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            WLog.i("开始说话");
            voiceDataList.clear();
        }

        @Override
        public void onError(SpeechError error) {
            WLog.e("onError Code：" + error.getErrorDescription());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

    };

    private void doControlTheSecond(String text) {
        String sequence = "android_sequence" + System.currentTimeMillis();
        voicecontrolApiUnit.doPostTheSecond(text, text, false, sequence, savedSessionId, new VoicecontrolApiUnit.CommonListener<VoiceControlResultBean>() {
            @Override
            public void onSuccess(VoiceControlResultBean bean) {
                isEnd = bean.is_end;
                showControlResult(bean);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void showControlResult(VoiceControlResultBean bean) {
        if (bean.directive != null && bean.directive.directive_items != null && bean.directive.directive_items.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (VoiceControlResultItemBean itemBean : bean.directive.directive_items) {
                sb.append(itemBean.content);
                sb.append("\n");
            }
            sb.setLength(sb.length() - 1);
            ConversationBean conversationBean = new ConversationBean();
            conversationBean.text = sb.toString();
            conversationBean.from = 1;
            conversationList.add(conversationBean);
            conversationAdapter.notifyDataSetChanged();
            TTSTool.getInstance().addTTSReadTask(conversationBean.text);
            grammarManager.parseControlText(conversationBean.text);
            listview_conversation.post(new Runnable() {
                @Override
                public void run() {
                    listview_conversation.setSelection(conversationAdapter.getCount() - 1);
                }
            });
        } else {
            showErrorResult(R.string.VoiceAssistant_Cannot);
        }
    }

    private void showErrorResult(int stringResId) {
        ConversationBean conversationBean = new ConversationBean();
        conversationBean.text = getString(stringResId);
        conversationBean.from = 1;
        conversationList.add(conversationBean);
        conversationAdapter.notifyDataSetChanged();
        TTSTool.getInstance().addTTSReadTask(conversationBean.text);
        grammarManager.parseControlText(conversationBean.text);
        listview_conversation.post(new Runnable() {
            @Override
            public void run() {
                listview_conversation.setSelection(conversationAdapter.getCount() - 1);
            }
        });
    }

    private void startListeningUnderstand() {
        if (grammarID != null) {
            mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarID);
        }
        mAsr.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
//        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, FileUtil.getMscPath() + "/Iat.wav");
        mAsr.startListening(mUnderstandListener);
        for (byte[] data : voiceDataList) {
            mAsr.writeAudio(data, 0, data.length);
        }
//        byte[] data = readAudioFile(FileUtil.getMscPath() + "/asr.wav");
//        if (data != null) {
//            mAsr.writeAudio(data, 0, data.length);
//        }
        mAsr.stopListening();
    }

    public static byte[] readAudioFile(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return null;
            }
            InputStream ins = new FileInputStream(file);
            byte[] data = new byte[ins.available()];

            ins.read(data);
            ins.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 命令识别监听器。
     */
    private RecognizerListener mUnderstandListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                WLog.i(TAG, "recognizer result：" + result.getResultString());
                String text = JsonParser.parseGrammarResult(result.getResultString());
                if (TextUtils.isEmpty(text)) {
                    showErrorResult(R.string.VoiceAssistant_Cannot);
                } else {
                    grammarManager.parseControlText(text);
                }
            } else {
                WLog.e(TAG, "understand recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            WLog.e(TAG, "understand onEndOfSpeech");
            waveView.setVisibility(View.GONE);
            btn_voice.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBeginOfSpeech() {
            WLog.e(TAG, "understand onBeginOfSpeech");
        }

        @Override
        public void onError(SpeechError error) {
            WLog.e(TAG, "understand recognizer onError " + error.getErrorDescription());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

    };

    @Override
    public void onClick(View v) {
        if (v == btn_exit) {
            onBackPressed();
        } else if (v == btn_help) {
            this.lastState = this.state;
            setState(STATE_HELP_LIST);
        } else if (v == btn_voice) {
            if (grammarID != null) {
                waveView.setVisibility(View.VISIBLE);
                btn_voice.setVisibility(View.GONE);
                TTSTool.getInstance().clearAllTask();
                TTSTool.getInstance().stopReading();
                startListening();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (state == STATE_HELP_INFO_ONE) {
            setState(STATE_HELP_LIST);
        } else if (state == STATE_HELP_LIST) {
            setState(lastState);
        } else {
            super.onBackPressed();
        }
    }

    private void setState(int state) {
        if (state == STATE_HELP_INFO_ALL) {
            showHelpInfo(textList, true);
            layout_help_info.setVisibility(View.VISIBLE);
            listview_help.setVisibility(View.GONE);
            helpDetailView.setVisibility(View.GONE);
            listview_conversation.setVisibility(View.GONE);
            btn_exit.setVisibility(View.VISIBLE);
            btn_exit.setImageResource(R.drawable.icon_lock_exit);
            btn_help.setVisibility(View.VISIBLE);
        } else if (state == STATE_HELP_LIST) {
            layout_help_info.setVisibility(View.GONE);
            listview_help.setVisibility(View.VISIBLE);
            helpDetailView.setVisibility(View.GONE);
            listview_conversation.setVisibility(View.GONE);
            btn_exit.setVisibility(View.VISIBLE);
            btn_exit.setImageResource(R.drawable.icon_back);
            btn_help.setVisibility(View.GONE);
        } else if (state == STATE_VOICE) {
            layout_help_info.setVisibility(View.GONE);
            listview_help.setVisibility(View.GONE);
            helpDetailView.setVisibility(View.GONE);
            listview_conversation.setVisibility(View.VISIBLE);
            btn_exit.setVisibility(View.VISIBLE);
            btn_exit.setImageResource(R.drawable.icon_lock_exit);
            btn_help.setVisibility(View.VISIBLE);
        } else if (state == STATE_HELP_INFO_ONE) {
            layout_help_info.setVisibility(View.GONE);
            listview_help.setVisibility(View.GONE);
            helpDetailView.setVisibility(View.VISIBLE);
            listview_conversation.setVisibility(View.GONE);
            btn_exit.setVisibility(View.VISIBLE);
            btn_exit.setImageResource(R.drawable.icon_back);
            btn_help.setVisibility(View.GONE);
        }
        this.state = state;
    }

    private void showHelpInfo(List<String> infoStrings, boolean isAutoScroll) {
        tv_autoscroll.setText(infoStrings);
        if (isAutoScroll) {
            tv_autoscroll.start();
        } else {
            tv_autoscroll.stop();
        }
    }

    private class HelpAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return HELP_TITLE_DATA.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HelpAdapterHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_voicecontrol_helptype, null);
                holder = new HelpAdapterHolder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (HelpAdapterHolder) convertView.getTag();
            }
            holder.tv_name.setText(HELP_TITLE_DATA[position]);
            return convertView;
        }
    }

    private class HelpAdapterHolder {
        TextView tv_name;
    }

    private class ConversationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return conversationList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ConversationAdapterHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_voicecontrol_conversation, null);
                holder = new ConversationAdapterHolder();
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            } else {
                holder = (ConversationAdapterHolder) convertView.getTag();
            }
            ConversationBean bean = conversationList.get(position);
            if (bean.from == 0) {
                holder.tv_content.setText("\"" + bean.text + "\"");
                holder.tv_content.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                holder.tv_content.setTextColor(0xffffffff);
            } else {
                holder.tv_content.setText(bean.text);
                holder.tv_content.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                holder.tv_content.setTextColor(0xff999999);
            }
            return convertView;
        }
    }

    private class ConversationAdapterHolder {
        TextView tv_content;
    }

    private class ConversationBean {
        /**
         * 0 来自用户，1 来自机器
         */
        public int from;
        public String text;
    }
}
