package cc.wulian.smarthomev6.support.customview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.home.adapter.SceneGroupAdapter;
import cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 添加管家popwindow
 */
public class CreateAutoTaskPop extends PopupWindow implements View.OnClickListener {
    private Context context;
    private final View view;
    private LinearLayout llSceneTask;
    private LinearLayout llTimerTask;

    public CreateAutoTaskPop(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popwindow_create_auto_task, null);
        initView();
        initPopWindow();
    }

    private void initView() {
        llSceneTask = view.findViewById(R.id.ll_auto_task_scene);
        llTimerTask = view.findViewById(R.id.ll_auto_task_timer);
        llSceneTask.setOnClickListener(this);
        llTimerTask.setOnClickListener(this);

    }


    private void initPopWindow() {
        this.setContentView(view);
        // 设置弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击()
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_animation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) context, 0.5f);//0.0-1.0
    }

    /**
     * 设置添加屏幕的背景透明度(值越大,透明度越高)
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_auto_task_scene:
                context.startActivity(new Intent(context, HouseKeeperActivity.class)
                        .putExtra("url", "circumstances.html"));
                dismiss();
                break;

            case R.id.ll_auto_task_timer:
                context.startActivity(new Intent(context, HouseKeeperActivity.class)
                        .putExtra("url", "timeTask1.html"));
                dismiss();
                break;
        }

    }
}
