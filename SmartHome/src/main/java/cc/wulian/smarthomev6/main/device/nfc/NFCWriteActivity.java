package cc.wulian.smarthomev6.main.device.nfc;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.IOException;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.tools.NFCTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class NFCWriteActivity extends BaseTitleActivity {

    private static final String DATA_SCENE = "DATA_SCENE";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;

    private String sceneId;
    private String mPackNmae;
    private String payloadText = "";

    public static void start(Context context, SceneInfo sceneInfo) {
        Intent intent = new Intent(context, NFCWriteActivity.class);
        intent.putExtra(DATA_SCENE, sceneInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_write, true);
        //初始化PendingIntent
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        mPackNmae = getPackageName();

        checkPermission();
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.NFC},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean hasPermission = true;
            if (grantResults != null) {
                for (int grantResult : grantResults) {
                    hasPermission = hasPermission & (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }
            if (!hasPermission) {
                // Permission Denied
                ToastUtil.show(R.string.Toast_Permission_Denied);
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void initTitle() {
        setTitleText(getString(R.string.NFC_write));
    }

    @Override
    protected void initData() {
        // 获取默认的NFC控制器
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            ToastUtil.show(R.string.NFC_Nonsupport);
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            ToastUtil.show(R.string.NFC_Open);
            return;
        }
        SceneInfo sceneInfo = (SceneInfo) getIntent().getSerializableExtra(DATA_SCENE);
        if (sceneInfo != null) {
            sceneId = sceneInfo.getSceneID();
            payloadText = NFCTool.getSceneJson(Preference.getPreferences().getCurrentGatewayID(), sceneId);
        }
    }

    //当设置android:launchMode="singleTop"时调用
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //1.获取Tag对象
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //写入信息
        writeNFC(tag);
    }

    //NFC写入
    private void writeNFC(Tag tag) {
        //null不执行操作，强调写程序的逻辑性
        if (tag == null || payloadText == null) {
            return;
        }

        NdefMessage ndefMessage = null;
        try {
            ndefMessage = new NdefMessage(
                    new NdefRecord[]{
                            NdefRecord.createExternal("wulian", "scene", payloadText.getBytes("UTF-8"))
//                            NdefRecord.createMime("text/plain","zbltext".getBytes("UTF-8")),
//                            NdefRecord.createUri("wlsmarthome://cc.wulian.smarthomev6/nfc"),
//                            new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT, new byte[0], "zzz.zbl".getBytes("UTF-8")),
//                            NdefRecord.createApplicationRecord(mPackNmae)
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获得写入大小
        int size = ndefMessage.toByteArray().length;
        //2.判断是否是NDEF标签
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                //说明是NDEF标签,开始连接
                ndef.connect();
                //判断是否可写
                if (!ndef.isWritable()) {
                    ToastUtil.show(R.string.NFC_Nonsupport);
                    return;
                }
                //判断大小
                if (ndef.getMaxSize() < size) {
                    ToastUtil.show(R.string.NFC_Romsmall);
                    return;
                }
                //写入
                try {
                    ndef.writeNdefMessage(ndefMessage);
                    ToastUtil.show(R.string.NFC_Success);
                    finish();
                } catch (FormatException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            ToastUtil.show(R.string.NFC_Failure);
            e.printStackTrace();

        }
    }

    //使当前窗口置顶，权限高于三重过滤
    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            //设置当前activity为栈顶
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //恢复栈
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
}
