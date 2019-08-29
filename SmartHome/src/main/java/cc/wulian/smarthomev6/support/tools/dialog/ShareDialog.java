package cc.wulian.smarthomev6.support.tools.dialog;

/**
 * 分享dialog
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.UnsupportedEncodingException;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.tools.ShareTool;
import cc.wulian.smarthomev6.support.utils.SizeUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;


public class ShareDialog extends Dialog implements View.OnClickListener {
    public static final String TAG = ShareDialog.class.getSimpleName();

    public static final String BASE_SHARE_URL = "https://sharewx.wulian.cc/";

    private Activity mContext;
    private String mUrl, picUrl, videoUrl;
    private String title = "";
    private String msg = "";
    private boolean isVideo;
    private ShareTool shareTool;

    private View btn_wxcircle, btn_wechat, btn_copyurl;

    public ShareDialog(Activity context) {
        super(context, R.style.ActionSheetDialogStyle);
        this.mContext = context;
        shareTool = new ShareTool(context);
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.dialog_share, null);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        setContentView(rootView);
        initSize();
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        initView(rootView);
    }

    private void initView(View rootView) {
        btn_wxcircle = rootView.findViewById(R.id.btn_wxcircle);
        btn_wechat = rootView.findViewById(R.id.btn_wechat);
        btn_copyurl = rootView.findViewById(R.id.btn_copyurl);

        btn_wxcircle.setOnClickListener(this);
        btn_wechat.setOnClickListener(this);
        btn_copyurl.setOnClickListener(this);
    }

    private void initSize() {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画

        DisplayMetrics dm = SizeUtil.getScreenSize(mContext);
        int screenWidth = dm.widthPixels;
        WindowManager.LayoutParams winParams = dialogWindow.getAttributes();
        winParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        winParams.width = screenWidth;
        winParams.x = 0; // 新位置X坐标
        winParams.y = 0; // 新位置Y坐标

        getWindow().setAttributes(winParams);
    }

    public void setShareUrl(String picUrl, String videoUrl, String type) {
        this.picUrl = picUrl;
        this.videoUrl = videoUrl;
        if (videoUrl != null) {
            isVideo = true;
            try {
                mUrl = BASE_SHARE_URL
                        + "?videoUrl="
                        + new String(Base64.encode(videoUrl.getBytes("utf-8"), Base64.NO_WRAP), "utf-8")
                        + "&imgUrl="
                        + new String(Base64.encode(picUrl.getBytes("utf-8"), Base64.NO_WRAP), "utf-8")
                        + "&deviceType="
                        + type;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            isVideo = false;
            try {
                mUrl = BASE_SHARE_URL
                        + "?imgUrl="
                        + new String(Base64.encode(picUrl.getBytes("utf-8"), Base64.NO_WRAP), "utf-8")
                        + "&deviceType="
                        + type;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void setShareTitle(String title) {
        this.title = title;
    }

    public void setShareMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void onClick(View v) {
        if (v == btn_wxcircle) {
            if (mUrl != null) {
                shareTool.shareUrl(mUrl, picUrl, title, msg, isVideo, SHARE_MEDIA.WEIXIN_CIRCLE);
                dismiss();
            }
        } else if (v == btn_wechat) {
            if (mUrl != null) {
                shareTool.shareUrl(mUrl, picUrl, title, msg, isVideo, SHARE_MEDIA.WEIXIN);
                dismiss();
            }
        } else if (v == btn_copyurl) {
            if (mUrl != null) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setPrimaryClip(ClipData.newPlainText("wulianShare", mUrl));
                ToastUtil.show(R.string.Multi_Platform_Copied);
                dismiss();
            }
        }
    }
}
