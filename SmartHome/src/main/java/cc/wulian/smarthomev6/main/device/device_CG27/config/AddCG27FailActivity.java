package cc.wulian.smarthomev6.main.device.device_CG27.config;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.config.AddIF02DeviceActivity;
import cc.wulian.smarthomev6.main.device.device_if02.config.IF02InfoBean;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

public class AddCG27FailActivity extends BaseTitleActivity implements View.OnClickListener {
    private Button btnNextStep;
    //    private TextView tvMoreSolution;
    private TextView failTextView;
    private String type;
    private Dialog tipDialog;
    private String dialogTitle;
    private String dialogMessage;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cg27_add_fail, true);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    private void showMoreSolutionDialog() {
        tipDialog = DialogUtil.showCommonTipDialog(context, false, getString(R.string.More_Solution), dialogMessage,
                getResources().getString(R.string.Sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        switch (id) {
            case R.id.btn_retry:
                finish();
                break;
            case R.id.tv_more_solutions:
                showMoreSolutionDialog();
                break;
        }
    }
}
