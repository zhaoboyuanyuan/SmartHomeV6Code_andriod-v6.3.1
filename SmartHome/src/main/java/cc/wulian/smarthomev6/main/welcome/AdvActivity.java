package cc.wulian.smarthomev6.main.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.AdvInfoBean;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.h5.CommonH5Activity;

/**
 * Created by syf on 2017/2/21.
 */

public class AdvActivity extends BaseActivity implements View.OnClickListener {

    private static final String DATA_ADV = "DATA_ADV";
    private int delay_time;

    private ImageView iv_splash;
    private TextView btn_skip;
    private AdvInfoBean advInfoBean;
    private Handler handler = new Handler();
    private Runnable finishTask = new Runnable() {
        @Override
        public void run() {
            if (delay_time >= 0) {
                btn_skip.setText(getString(R.string.Skip) + "(" + delay_time + ")");
                delay_time -= 1;
                handler.postDelayed(this, 1000);
            } else {
                finish();
            }
        }
    };

    public static void start(Context context, AdvInfoBean advInfoBean) {
        Intent intent = new Intent(context, AdvActivity.class);
        intent.putExtra(DATA_ADV, advInfoBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = AdvActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        iv_splash = (ImageView) findViewById(R.id.iv_splash);
        iv_splash.setOnClickListener(this);
        btn_skip = (TextView) findViewById(R.id.btn_skip);
        btn_skip.setOnClickListener(this);

        delay_time = 5;
        showImage();
    }

    private void showImage() {
        try {
            advInfoBean = (AdvInfoBean) getIntent().getSerializableExtra(DATA_ADV);
            if (advInfoBean != null) {
                ImageLoader.getInstance().displayImage(advInfoBean.imageUrl, iv_splash);
            }
            handler.post(finishTask);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(finishTask);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_skip) {
            handler.removeCallbacks(finishTask);
            finish();
        } else if (v == iv_splash) {
            if (advInfoBean != null) {
                CommonH5Activity.start(this, advInfoBean.url, "");//advInfoBean.name);
                handler.removeCallbacks(finishTask);
                finish();
            }
        }
    }
}
