package cc.wulian.smarthomev6.support.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.customview.PBWebView;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * Created by hxc on 2017/5/9.
 */

public class DialogUtil {
    public static Dialog showCommonInstructionsWebViewTipDialog(
            Context mContext, String title, String name) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_config_tips,
                null);
        final Dialog dialog = new Dialog(mContext, R.style.dialog_style_v5);
        final PBWebView wv_info = (PBWebView) layout.findViewById(R.id.wv_info);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        Button bt_dialog_close = (Button) layout
                .findViewById(R.id.bt_dialog_close);
        if (bt_dialog_close != null) {
            bt_dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (wv_info != null) {
                        wv_info.removeAllViews();
                        wv_info.destroy();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (!TextUtils.isEmpty(title)) {
            TextView title_tv = (TextView) layout
                    .findViewById(R.id.tv_dialog_title);
            title_tv.setText(title);
        }
        if (!TextUtils.isEmpty(name)) {

            String language = Locale.getDefault().getLanguage();
            String country = Locale.getDefault().getCountry();
            String temp = (language + "_" + country).toLowerCase(Locale
                    .getDefault());
            if (temp.equalsIgnoreCase("zh_cn")
                    || temp.equalsIgnoreCase("pt_br")) {
                wv_info.loadUrl("file:///android_asset/help/" + temp + "/"
                        + name + ".html");
            } else {
                wv_info.loadUrl("file:///android_asset/help/" + "en/" + name
                        + ".html");
            }
        }
        return dialog;
    }

    public static Dialog showBarcodeConfigTipDialog(Context mContext, String devId, String deviceType) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_barcode_config_tip_alertdialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        ImageView image = (ImageView) layout.findViewById(R.id.iv_normal_set_background);
        switch (deviceType) {
            case "CMICA1":
                image.setImageResource(R.drawable.icon_barcode_camera_1);
                break;
            case "CMICA2":
                image.setImageResource(R.drawable.icon_barcode_camera_2);
                break;
            case "CMICA3":
            case "CMICA6":
                image.setImageResource(R.drawable.icon_barcode_camera_3);
                break;
            case "CMICA4":
                image.setImageResource(R.drawable.icon_barcode_camera_4);
                break;
            default:
                break;
        }

        dialog.show();
        changeDialogWidth(dialog, mContext);
        TextView textView_dialog_close = (TextView) layout
                .findViewById(R.id.tv_dialog_close);
        if (textView_dialog_close != null) {
            textView_dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        return dialog;
    }

    /**
     * 设置对话框的宽度
     **/
    public static void changeDialogWidth(Dialog dialog, Context mContext) {
        if (null != dialog) {
            WindowManager wm = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            int screenWidth = wm.getDefaultDisplay().getWidth();
            WindowManager.LayoutParams params = dialog.getWindow()
                    .getAttributes();
            int width = mContext.getResources().getDimensionPixelSize(
                    R.dimen.register_margin_2);
            params.width = screenWidth - 2 * width;
            dialog.getWindow().setAttributes(params);
        }
    }

    /**
     * V6通用Dialog (pos 和 neg)
     *
     * @param mContext
     * @param title
     * @param message
     * @param ok
     * @param cancel
     * @param listen
     * @return
     */
    public static WLDialog showCommonDialog(Context mContext,
                                            String title, String message, String ok, String cancel,
                                            WLDialog.MessageListener listen) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(ok)
                .setNegativeButton(cancel)
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }


    /**
     * V6通用提示Dialog（底部只有一个button）
     *
     * @param mContext
     * @param tip
     * @param message
     * @param ok
     * @param listen
     * @return
     */
    public static WLDialog showTipsDialog(Context mContext,
                                          String tip, String message, String ok,
                                          WLDialog.MessageListener listen) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setTitle(tip)
                .setMessage(message)
                .setPositiveButton(ok)
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }

    public static Dialog showMsgListDialog(Context mContext, boolean isCancel,
                                           String tip, String msg, String ok, String cancel,
                                           final View.OnClickListener okOnclick, final View.OnClickListener cancelOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_alertdialog, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        if (!TextUtils.isEmpty(tip)) {
            TextView title_tv = (TextView) layout.findViewById(R.id.tv_title);
            title_tv.setText(tip);
        }
        if (msg != null) {
            TextView message_tv = (TextView) layout.findViewById(R.id.tv_info);
            message_tv.setText(msg);
        }
        Button okBtn = (Button) layout.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
        }
        Button cancleBtn = (Button) layout.findViewById(R.id.btn_negative);
        if (!TextUtils.isEmpty(ok)) {
            cancleBtn.setText(cancel);
        }
        // 绑定事件
        if (okOnclick != null) {
            okBtn.setOnClickListener(okOnclick);
        }
        if (cancelOnclick != null) {
            cancleBtn.setOnClickListener(cancelOnclick);
        }
        return dialog;
    }


    //摄像机配网界面提示框
    public static Dialog showCommonTipDialog(Context mContext,
                                             boolean isCancel, String tip, String message, String ok,
                                             final View.OnClickListener okOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_tip_alertdialog, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        if (!TextUtils.isEmpty(tip)) {
            TextView title_tv = (TextView) layout.findViewById(R.id.tv_title);
            title_tv.setText(tip);
        }
        if (!TextUtils.isEmpty(message)) {
            TextView message_tv = (TextView) layout.findViewById(R.id.tv_info);
            message_tv.setMovementMethod(new ScrollingMovementMethod());
            message_tv.setText(message);
        }
        Button okBtn = (Button) layout.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
        }
        // 绑定事件
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(v);
                }
            }
        });
        return dialog;
    }

    public static Dialog showProtectAreaTipDialog(Context mContext,
                                                  boolean isCancel,
                                                  final View.OnClickListener okOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.protect_area_tips, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        TextView okBtn = (TextView) layout.findViewById(R.id.btn_positive);
        // 绑定事件
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(v);
                }
            }
        });
        return dialog;
    }

    public static WLDialog showWifiHintDialog(Context mContext, WLDialog.MessageListener listen) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(mContext.getString(R.string.CateyePlayVideo_Download_Hint))
                .setPositiveButton(mContext.getResources().getString(R.string.CateyePlayVideo_Play))
                .setNegativeButton(mContext.getResources().getString(R.string.Cancel))
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }

    public static WLDialog showConfigOrBindDialog(Context mContext, String msg, String posMsg, String negMSg, WLDialog.MessageListener listen) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(posMsg)
                .setNegativeButton(negMSg)
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }

    public static WLDialog showUnknownDeviceTips(Context mContext, WLDialog.MessageListener listen, String msg) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(mContext.getString(R.string.Sure))
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }

    public static WLDialog showOtherUserBindTips(Context mContext, WLDialog.MessageListener listen, String msg) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(mContext.getString(R.string.Tip_I_Known))
                .setListener(listen);
        dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        return dialog;
    }


    public static WLDialog showCommonTips(Context mContext,
                                          final View.OnClickListener closeOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_tips, null);
        WLDialog dialog = new WLDialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        ImageView ivClose = layout.findViewById(R.id.iv_close);
        // 绑定事件
        if (closeOnclick != null) {
            ivClose.setOnClickListener(closeOnclick);
        }
        return dialog;
    }


    public static WLDialog showIF02LearningDialog(final Context mContext,
                                                  final View.OnClickListener closeOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_learn_content, null);
        WLDialog dialog = new WLDialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        final TextView tvDelay = layout.findViewById(R.id.tv_delay);
        Button btn = layout.findViewById(R.id.dialog_btn_positive);
        // 绑定事件
        if (closeOnclick != null) {
            btn.setOnClickListener(closeOnclick);
        }
        CountDownTimer countDownTimer = new CountDownTimer(16 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = String.valueOf((int) (millisUntilFinished / 1000));
                tvDelay.setText(mContext.getResources().getString(R.string.Infraredrelay_Custom_Matching) + "(" + value + "s)");
            }

            @Override
            public void onFinish() {
            }
        };
        countDownTimer.start();

        return dialog;
    }

    public static WLDialog showGatewayIntroductionDialog(Context mContext) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_gateway_introduce_alertdialog, null);
        final WLDialog dialog = new WLDialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        TextView textView_dialog_close = (TextView) layout
                .findViewById(R.id.tv_dialog_close);
        if (textView_dialog_close != null) {
            textView_dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        return dialog;
    }

    public static WLDialog showLcNoGreenLightDialog(Context mContext) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_lc_no_green_light, null);
        final WLDialog dialog = new WLDialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        TextView textView_dialog_close = (TextView) layout
                .findViewById(R.id.tv_dialog_close);
        if (textView_dialog_close != null) {
            textView_dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        return dialog;
    }

    public static WLDialog showDeviceForbiddenDialog(Context mContext, String msg) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_device_forbidden, null);
        final WLDialog dialog = new WLDialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if (!dialog.isShowing()) {
            dialog.show();
        }
        changeDialogWidth(dialog, mContext);
        TextView tvClose = (TextView) layout
                .findViewById(R.id.tv_dialog_close);
        TextView tvTips = (TextView) layout
                .findViewById(R.id.tv_tips);
        tvTips.setText(msg);
        if (tvClose != null) {
            tvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        return dialog;
    }
}
