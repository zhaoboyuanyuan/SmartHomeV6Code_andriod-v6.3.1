package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayListActivity;

/**
 * Created by zbl on 2017/4/14.
 */

public class TopStateView extends FrameLayout implements View.OnClickListener {

    private Context context;
    private TextView tv_content;

    public TopStateView(Context context) {
        super(context);
        init(context);
    }

    public TopStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_top_state, this);
        tv_content = (TextView) findViewById(R.id.tv_content);
        setOnClickListener(this);
    }

    public void setContent(String content) {
        tv_content.setText(content);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, GatewayListActivity.class);
        context.startActivity(intent);
    }
}
