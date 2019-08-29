package cc.wulian.smarthomev6.main.home.scene;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.home.adapter.SceneAllAdapter;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.LoginEvent;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.event.SortSceneEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class SceneListDialogActivity extends BaseActivity {

    private HomeSceneAdapter mHomeSceneAdapter;
    private View btn_exit;

    private SceneManager sceneManager;
    private Preference preference = Preference.getPreferences();
    private List<SceneInfo> sceneInfos;
    private SceneInfo operatedScene;
    private GridLayoutManager mSceneGridLayoutManager;
    private Context context;

    private Handler handler;
    private long lastTime = 0;
    private int clickPosition = -1;//记录点击位置，防止同时点击
    private Handler mHandler;
    private ValueAnimator mLoadingAnimator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler(Looper.getMainLooper());
        boolean emptyGateway = TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayID());
        boolean offLine = TextUtils.equals(Preference.getPreferences().getCurrentGatewayState(), "0");
        if (emptyGateway || offLine) {
            ToastUtil.show(R.string.Home_Scene_NoScene2);
            finish();
            return;
        }
        sceneManager = new SceneManager(context);
        sceneInfos = sceneManager.acquireScene();
        if (sceneInfos == null || sceneInfos.size() == 0) {
            ToastUtil.show(R.string.Home_Scene_NoScene2);
            finish();
            return;
        }
        setContentView(R.layout.activity_dialog_scene_list);
        EventBus.getDefault().register(this);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initView() {
        btn_exit = findViewById(R.id.btn_exit);
        mHomeSceneAdapter = new HomeSceneAdapter(context, null);
        RecyclerView mSceneRecyclerView = (RecyclerView) findViewById(R.id.rv_scene);
        mSceneGridLayoutManager = new GridLayoutManager(context, 4);
        mSceneRecyclerView.setLayoutManager(mSceneGridLayoutManager);
        mSceneRecyclerView.setAdapter(mHomeSceneAdapter);
        mSceneRecyclerView.setHasFixedSize(true);
        mSceneRecyclerView.setNestedScrollingEnabled(false);

        mHomeSceneAdapter.setOnClickListener(new SceneAllAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (preference.isLogin()) {
                    openSceneByPosition(position);
                    final View itemView = mSceneGridLayoutManager.findViewByPosition(position);
                    WLog.i("luzx","click");
                    lastTime = System.currentTimeMillis();//记录item点击时间
                    final View loadingView = itemView.findViewById(R.id.loading_layout);
                    final ImageView loadingIcon = (ImageView) itemView.findViewById(R.id.loading_icon);
                    loadingView.setVisibility(View.VISIBLE);
                    if(mLoadingAnimator == null){
                        mLoadingAnimator = ValueAnimator.ofFloat(0, 360*16);
                        mLoadingAnimator.setInterpolator(new LinearInterpolator());
                        mLoadingAnimator.setDuration(10000);
                    }
                    mLoadingAnimator.removeAllUpdateListeners();
                    mLoadingAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if(System.currentTimeMillis() - lastTime > 1000){
                                loadingView.setVisibility(View.GONE);
                                clickPosition = -1;
                            }
                        }
                    });
                    mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            loadingIcon.setRotation((float)animation.getAnimatedValue());
                        }
                    });
                    mLoadingAnimator.start();
                } else {
                    context.startActivity(new Intent(context, SigninActivity.class));
                }
            }
        });
    }

    private void initData() {
        reloadSceneData();
    }

    private void initListeners() {
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void reloadSceneData() {
        if (mHomeSceneAdapter != null) {
            boolean emptyGateway = TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayID());
            boolean offLine = TextUtils.equals(Preference.getPreferences().getCurrentGatewayState(), "0");
            if (emptyGateway || offLine) {
                sceneInfos = new ArrayList<>();
            } else {
                sceneInfos = sceneManager.acquireScene();
            }
            mHomeSceneAdapter.update(sceneInfos);
        }
    }

    private void openSceneByPosition(int position) {
        if (preference.getCurrentGatewayState().equals("0")) {
            ToastUtil.show(R.string.Gateway_Offline);
            return;
        }

        // 切换场景
        SceneInfo sceneInfo = sceneInfos.get(position);
        operatedScene = sceneInfo;
        sceneManager.toggleScene(sceneInfo);
    }

    class HomeSceneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater layoutInflater;
        private List<SceneInfo> mList;
        private SceneAllAdapter.OnItemClickListener onClickListener;

        public HomeSceneAdapter(Context context, ArrayList<SceneInfo> list) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            mList = list;
        }

        public void setOnClickListener(SceneAllAdapter.OnItemClickListener listener) {
            this.onClickListener = listener;
        }

        public void update(List<SceneInfo> list) {
            if(list!=null&&list.size()>0){
                mList = list;
                notifyDataSetChanged();
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemView viewholder = new ItemView(layoutInflater.inflate(R.layout.home_scene_item, parent, false));

            return viewholder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SceneInfo info = mList.get(position);
            if (TextUtils.equals(info.getStatus(), "2")) {
                ((ItemView) holder).name.setText(info.getName() + context.getString(R.string.Home_Scene_IsOpen));
                ((ItemView) holder).name.setTextColor(context.getResources().getColor(R.color.v6_text_green));
            } else {
                ((ItemView) holder).name.setText(info.getName());
                ((ItemView) holder).name.setTextColor(context.getResources().getColor(R.color.white));
            }
            ((ItemView) holder).imageView.setImageDrawable(SceneManager.getSceneIconQuick(context, info.getIcon()));
            ((ItemView) holder).itemView.setTag(position);
            ((ItemView) holder).loadingView.setBackgroundResource(R.drawable.scene_icon_coverlayer);

        }

        @Override
        public int getItemCount() {
            if (mList == null) {
                return 0;
            }
            return mList.size();
        }

        class ItemView extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private View loadingView;
            private TextView name;

            public ItemView(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.scene_icon);
                loadingView = itemView.findViewById(R.id.loading_layout);
                name = (TextView) itemView.findViewById(R.id.scene_name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = (int) v.getTag();
//                        onClickListener.onItemClick(p);
                        if(clickPosition != -1){
                            return;
                        }
                        clickPosition = p;
                        onClickListener.onItemClick(p);
                    }
                });
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneUpdated(SceneInfoEvent event) {
        if (event.sceneBean != null && operatedScene != null && TextUtils.equals(event.sceneBean.sceneID, operatedScene.getSceneID())) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },800);
        }
        long intval = System.currentTimeMillis() - lastTime;
        if(intval > 1000){
            clickPosition = -1;
            reloadSceneData();
        }else{
            if(mHandler == null){
                mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        if(msg.what == 1){
                            Log.i("luzx", "setVisibility:" + (System.currentTimeMillis() - lastTime));
                            clickPosition = -1;
                            reloadSceneData();
                        }
                    }
                };
            }
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, 1000 - intval);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneReport(GetSceneListEvent event) {
        reloadSceneData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSortSceneEvent(SortSceneEvent event) {
        reloadSceneData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        reloadSceneData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChangedEvent(GatewayStateChangedEvent event) {
        reloadSceneData();
    }

}
