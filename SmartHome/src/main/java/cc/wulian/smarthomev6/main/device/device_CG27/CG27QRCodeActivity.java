package cc.wulian.smarthomev6.main.device.device_CG27;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.shidean.qrcode.QRCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.customview.BottomMenu;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

public class CG27QRCodeActivity extends BaseTitleActivity {
    private ImageView ivQrCode;
    private ImageView ivRefresh;
    private TextView tvRefresh;
    private Button btnShare;

    private String communityId;
    private String uc;
    private String mainUc;
    private String qrCodeInfo;
    private long validTime;
    private long minute;
    private QRCode qrCode;
    private ArrayList<String> EPuc;
    private ArrayList<String> timeData;
    private Handler handler;
    private BottomMenu bottomMenu;

    public static void start(Context context, String communityId, String uc, String mainUc) {
        Intent intent = new Intent(context, CG27QRCodeActivity.class);
        intent.putExtra("communityId", communityId);
        intent.putExtra("uc", uc);
        intent.putExtra("mainUc", mainUc);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cg27_qrcode, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
    }

    @Override
    protected void initData() {
        super.initData();
        communityId = getIntent().getStringExtra("communityId");
        uc = getIntent().getStringExtra("uc");
        mainUc = getIntent().getStringExtra("mainUc");
        qrCode = new QRCode();
        EPuc = new ArrayList<>();
        handler = new Handler();
        EPuc.add(mainUc);
        minute = 60;
        validTime = minute * 10;
        getQrCode();
        initBottomMenu();
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnShare, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnShare, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initView() {
        super.initView();
        ivQrCode = (ImageView) findViewById(R.id.iv_qr_code);
        ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        tvRefresh = (TextView) findViewById(R.id.tv_refresh);
        btnShare = (Button) findViewById(R.id.btn_share);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tvRefresh.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }

    private void initBottomMenu() {
        bottomMenu = new BottomMenu(this, new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                validTime = getValidTime(bottomMenu.getCurrent());
                shareQRCode(getQrCode());
            }

            @Override
            public void onCancel() {

            }
        });
        timeData = new ArrayList<>();
        timeData.add(0, "10" + getString(R.string.Minute));
        timeData.add(1, "30" + getString(R.string.Minute));
        timeData.add(2, "60" + getString(R.string.Minute));
        timeData.add(3, "240" + getString(R.string.Minute));
        timeData.add(4, "1" + getString(R.string.device_CG27_day));
        timeData.add(5, "3" + getString(R.string.device_CG27_day));
        timeData.add(6, "7" + getString(R.string.device_CG27_day));
        bottomMenu.setData(timeData);
    }

    private long getValidTime(int index) {
        switch (index) {
            case 0:
                validTime = minute * 10;
                break;
            case 1:
                validTime = minute * 30;
                break;
            case 2:
                validTime = minute * 60;
                break;
            case 3:
                validTime = minute * 240;
                break;
            case 4:
                validTime = minute * 60 * 24;
                break;
            case 5:
                validTime = minute * 60 * 24 * 3;
                break;
            case 6:
                validTime = minute * 60 * 24 * 7;
                break;

        }
        return validTime;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_refresh:
                getQrCode();
                updateQRCodeView();
                break;
            case R.id.btn_share:
                bottomMenu.show(v);
                break;
        }
    }

    private Bitmap getQrCode() {
        qrCodeInfo = qrCode.getEncryptedStringForEPVersionAboveOneOneOnethreeOnesix(communityId, String.valueOf(validTime), "", uc, EPuc);
        Bitmap bitmap1 = createQRImage(qrCodeInfo, dp2px(this, 260), dp2px(this, 260));
        if (bitmap1 != null) {
            ivQrCode.setImageBitmap(bitmap1);
        }
        return bitmap1;
    }

    private void updateQRCodeView() {
        tvRefresh.setEnabled(false);
        tvRefresh.setText(getString(R.string.device_CG27_Refreshed));
        tvRefresh.setTextColor(getResources().getColor(R.color.v6_text_gray));
        ivRefresh.setImageResource(R.drawable.icon_refresh_ok);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvRefresh.setEnabled(true);
                tvRefresh.setTextColor(getResources().getColor(R.color.v6_refresh));
                tvRefresh.setText(getString(R.string.device_CG27_Refresh));
                ivRefresh.setImageResource(R.drawable.icon_qr_refresh);
            }
        }, 10 * 1000);
    }


    private void shareQRCode(Bitmap bitmap) {
        Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, null, null));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    /**
     * 生成二维码Bitmap
     *
     * @param content   内容
     * @param widthPix  图片宽度
     * @param heightPix 图片高度
     * @return 生成二维码图片
     */
    public static Bitmap createQRImage(String content, int widthPix, int heightPix) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }

            //配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
//            hints.put(EncodeHintType.MARGIN, 2); //default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
