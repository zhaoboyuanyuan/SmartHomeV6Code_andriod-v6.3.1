package cc.wulian.smarthomev6.support.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;

public class SceneGroupPop extends PopupWindow implements View.OnClickListener {
    private Context context;
    private final View view;
    private TextView tvCancel;
    private TextView tvNoGroupTip;
    private RecyclerView mRecycleView;
    private ImageView ivAddGroup;
    private SceneGroupAdapter adapter;
    private List<SceneGroupListBean.DataBean> groupList;
    private onPopClickListener listener;

    public interface onPopClickListener {
        void CreateGroup();


        void joinGroup(String groupID);
    }

    public SceneGroupPop(Context context, List<SceneGroupListBean.DataBean> list) {
        super(context);
        this.context = context;
        this.groupList = list;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popwindow_scene_group, null);
        initView();
        initData();
        initPopWindow();
    }

    public void setOnClickListener(onPopClickListener listener) {
        this.listener = listener;
    }


    private void initView() {
        tvCancel = view.findViewById(R.id.tv_cancel);
        mRecycleView = view.findViewById(R.id.rl_group);
        ivAddGroup = view.findViewById(R.id.iv_add_group);
        tvNoGroupTip = view.findViewById(R.id.tv_no_group_tip);
        tvCancel.setOnClickListener(this);
        ivAddGroup.setOnClickListener(this);


    }

    private void initData() {
        boolean hasGroup = (groupList != null && groupList.size() > 0);
        mRecycleView.setVisibility(hasGroup ? View.VISIBLE : View.GONE);
        tvNoGroupTip.setVisibility(!hasGroup ? View.VISIBLE : View.GONE);
        if (hasGroup) {
            SceneGroupListBean.DataBean dataBean = new SceneGroupListBean.DataBean();
            dataBean.setName(context.getString(R.string.Smart_out_group));
            dataBean.setGroupID("");
            groupList.add(dataBean);
        }
        adapter = new SceneGroupAdapter(context, groupList);
        mRecycleView.setAdapter(adapter);
        mRecycleView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        adapter.setOnClickListener(new SceneGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (listener != null) {
                    listener.joinGroup(groupList.get(position).getGroupID());
                }
            }
        });

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
        this.setAnimationStyle(R.style.mypopwindow_anim_style);
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
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.iv_add_group:
                if (listener != null) {
                    listener.CreateGroup();
                }
                break;
        }

    }
}
