package cc.wulian.smarthomev6.main.mine.platform.whiterobot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.mine.platform.ControlPlatformActivity;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.tools.zxing.encoding.EncodingUtils;

/**
 * Created by zbl on 2017/12/19.
 * 小白机器人，二维码界面
 */

public class WhiteRobotQRCodeActivity extends BaseTitleActivity {

    private ImageView iv_qrcode;
    private Button btn_done;

    private String code;
    private String wifiName;
    private String wifiPsw;

    public static void start(Context context, String code, String wifiName, String wifiPsw) {
        Intent intent = new Intent(context, WhiteRobotQRCodeActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("wifiName", wifiName);
        intent.putExtra("wifiPsw", wifiPsw);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiterobot_qrcode, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Config_Barcode));
    }

    @Override
    protected void initView() {
        iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
        btn_done = (Button) findViewById(R.id.btn_done);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btn_done, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btn_done, SkinResouceKey.COLOR_BUTTON_TEXT);
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        btn_done.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            code = intent.getStringExtra("code");
            wifiName = intent.getStringExtra("wifiName");
            wifiPsw = intent.getStringExtra("wifiPsw");

            final ViewTreeObserver viewTreeObserver = iv_qrcode.getViewTreeObserver();
            viewTreeObserver.addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
                @Override
                public void onWindowFocusChanged(final boolean hasFocus) {
                    // do your stuff here
                    if (hasFocus) {
                        showQRImage();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_done) {
            Intent intent = new Intent(this, ControlPlatformActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showQRImage() {
        int width = iv_qrcode.getWidth();
        int height = iv_qrcode.getHeight();

        Bitmap bitmap = null;
        bitmap = EncodingUtils.createQRCode(getQRContent(), width, height, null);

        iv_qrcode.setImageBitmap(bitmap);
    }

    public String getQRContent() {
        JSONObject json = new JSONObject();
        try {
            JSONObject dataJson = new JSONObject();
            dataJson.put("code", code);
            dataJson.put("gateway_id", Preference.getPreferences().getCurrentGatewayID());
            json.put("data", dataJson);
            json.put("company", "wulian");
            json.put("psw", wifiPsw);
            json.put("ssid", wifiName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
