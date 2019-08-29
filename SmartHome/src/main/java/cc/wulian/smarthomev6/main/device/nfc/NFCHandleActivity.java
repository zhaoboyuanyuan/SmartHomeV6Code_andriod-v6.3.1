package cc.wulian.smarthomev6.main.device.nfc;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.text.TextUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.welcome.SplashActivity;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class NFCHandleActivity extends BaseActivity {

    private SoundPool soundPool;

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //播放音效
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        int snapshot_sound_id = soundPool.load(this, R.raw.zxing_beep, 1);
        soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
        //若HomeActivity没有在运行，则启动app
        List<Activity> activityList = MainApplication.getApplication().getActivities();
        boolean isNeedStartApp = true;
        for (Activity activity : activityList) {
            if (TextUtils.equals(activity.getClass().getSimpleName(), HomeActivity.class.getSimpleName())) {
                if (activity.isDestroyed() || activity.isFinishing()) {

                } else {
                    isNeedStartApp = false;
                }
                break;
            }
        }
        if (isNeedStartApp) {
            Intent intent = getIntent();
            intent.setClass(this, SplashActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (isHandle()) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                //处理该intent
                processIntent(getIntent());
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        super.onDestroy();
    }

    private boolean isHandle() {
        if (Preference.getPreferences().isLogin()) {
            if (!TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses the NDEF Message from the intent
     */
    private void processIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tagFromIntent == null) {
            return;
        }
        try {
            Ndef ndef = Ndef.get(tagFromIntent);
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if (ndefMessage != null) {
                NdefRecord[] ndefRecords = ndefMessage.getRecords();
                if (ndefRecords != null) {
                    for (NdefRecord record : ndefRecords) {
                        String type = new String(record.getType(), "UTF-8").trim();
                        if (TextUtils.equals("wulian:scene", type)) {
                            handleSceneCmd(new String(record.getPayload(), "UTF-8").trim());
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private void handleSceneCmd(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            String gatewayId = jsonObject.optString("gatewayId");
            String sceneId = jsonObject.optString("sceneId");
            if (TextUtils.equals(gatewayId, Preference.getPreferences().getCurrentGatewayID()) && !TextUtils.isEmpty(sceneId)) {
                SceneInfo sceneInfo = new SceneManager(this).getSceneById(sceneId);
                String sceneName = null, sceneIcon = null, groupID = null;
                if (sceneInfo != null) {
                    sceneName = sceneInfo.getName();
                    sceneIcon = sceneInfo.getIcon();
                    groupID = sceneInfo.getGroupID();
                }
                final String finalSceneName = sceneName;
                MainApplication.getApplication().getMqttManager()
                        .publishEncryptedMessage(
                                MQTTCmdHelper.createSetSceneInfo(
                                        gatewayId,
                                        0,
                                        sceneId,
                                        sceneName,
                                        sceneIcon,
                                        "2",
                                        groupID
                                ),
                                MQTTManager.MODE_GATEWAY_FIRST);
                ToastUtil.show(String.format(getString(R.string.Help_Open_Scene), finalSceneName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
