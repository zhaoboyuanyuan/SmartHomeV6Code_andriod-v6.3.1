package cc.wulian.smarthomev6.main.device.device_if02.airconditioner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.bean.AirCodeLibsBean;
import cc.wulian.smarthomev6.main.device.device_if02.match.DownLoadCodeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/6/27.
 * func：空调匹配结果列表界面
 * email: hxc242313@qq.com
 */

public class MatchResultActivity extends BaseTitleActivity {
    public static final String MODE_CONTROL_SUCCESS = "IR_SUCESS";
    public static final String MODE_CONTROL_FAIL = "DATA_ERROR";
    private Button btnSure;

    private String deviceId;
    private String type;
    private String brandId;
    private static String brandName;
    private String code;
    private Device device;

    private List<AirCodeLibsBean.codeLibsBean> codeLibList;
    private RecyclerView recyclerView;
    private MatchResultAdapter adapter;
    private static int selectedPosition;
    private DataApiUnit dataApiUnit;

    public static void start(Context context, String deviceID, String type, String brandId, String brandName, String code) {
        Intent intent = new Intent(context, MatchResultActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("type", type);
        intent.putExtra("brandId", brandId);
        intent.putExtra("brandName", brandName);
        intent.putExtra("code", code);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if01_air_match, true);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.IF_020));
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView = findView(R.id.match_recycle);
        btnSure = (Button) findViewById(R.id.btn_sure);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnSure.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        code = getIntent().getStringExtra("code");
        brandId = getIntent().getStringExtra("brandId");
        brandName = getIntent().getStringExtra("brandName");
        deviceId = getIntent().getStringExtra("deviceID");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        dataApiUnit = new DataApiUnit(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MatchResultAdapter(this, deviceId, null);
        recyclerView.setAdapter(adapter);
        getCodeLibList();
    }

    private void getCodeLibList() {
        dataApiUnit.doMatchAirController(deviceId, type, brandId, "on", code, new DataApiUnit.DataApiCommonListener<AirCodeLibsBean>() {
            @Override
            public void onSuccess(AirCodeLibsBean bean) {
                if (bean != null && bean.codeLibs != null) {
                    codeLibList = bean.codeLibs;
                    codeLibList.get(0).isSelected = true;
                    adapter.update(codeLibList);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(R.string.device_IF01_one_button);

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_sure) {
            if (codeLibList != null) {
                DownLoadCodeActivity.start(v.getContext(), deviceId, brandName, type, codeLibList.get(selectedPosition).codeLib);
                WLog.i(TAG, "selectedPosition = " + selectedPosition + "\nselectCodeLib: " + codeLibList.get(selectedPosition).codeLib);
                finish();
            } else {
                finish();
            }
        }
    }

    static class MatchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private String deviceId;
        private DataApiUnit dataApiUnit;
        private List<AirCodeLibsBean.codeLibsBean> data;
        private int mSelectedPos = 0;   //实现单选，保存当前选中的position


        public MatchResultAdapter(Context context, String deviceId, List<AirCodeLibsBean.codeLibsBean> data) {
            this.context = context;
            this.data = data;
            this.deviceId = deviceId;
            dataApiUnit = new DataApiUnit(context);
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isSelected) {
                        mSelectedPos = i;
                    }
                }
            }
        }

        public void update(List<AirCodeLibsBean.codeLibsBean> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        public void addData() {
            notifyItemInserted(data.size());
        }

        public void removeData(int position) {
            notifyItemRemoved(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.item_air_match_result, parent, false);
            return new MatchResultAdapter.ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof MatchResultAdapter.ItemHolder) {
                final int pos = viewHolder.getAdapterPosition();
                final AirCodeLibsBean.codeLibsBean bean = data.get(viewHolder.getAdapterPosition());
                ((ItemHolder) viewHolder).tvModelName.setText(brandName + bean.model);
                if (bean.isSelected) {
                    ((ItemHolder) viewHolder).ivChoose.setImageResource(R.drawable.choose_pressed);
                } else {
                    ((ItemHolder) viewHolder).ivChoose.setImageResource(R.drawable.choose_normal);
                }
                ((ItemHolder) viewHolder).btnOpen.setVisibility(View.INVISIBLE);
                ((ItemHolder) viewHolder).btnClose.setVisibility(View.INVISIBLE);
                ((ItemHolder) viewHolder).btnCloseSwing.setVisibility(View.INVISIBLE);
                ((ItemHolder) viewHolder).btnOpenSwing.setVisibility(View.INVISIBLE);

                for (int i = 0; i < bean.rcCommand.size(); i++) {
                    if (bean.rcCommand.get(i).kn.contains("on")) {
                        ((ItemHolder) viewHolder).btnOpen.setVisibility(View.VISIBLE);
                        ((ItemHolder) viewHolder).btnOpen.setTag(bean.rcCommand.get(i).src);
                    }
                    if (bean.rcCommand.get(i).kn.contains("off")) {
                        ((ItemHolder) viewHolder).btnClose.setVisibility(View.VISIBLE);
                        ((ItemHolder) viewHolder).btnClose.setTag(bean.rcCommand.get(i).src);
                    }
                    if (bean.rcCommand.get(i).kn.contains("u0_l0")) {
                        ((ItemHolder) viewHolder).btnCloseSwing.setVisibility(View.VISIBLE);
                        ((ItemHolder) viewHolder).btnCloseSwing.setTag(bean.rcCommand.get(i).src);
                    }
                    if (bean.rcCommand.get(i).kn.contains("u1_l1")) {
                        ((ItemHolder) viewHolder).btnOpenSwing.setVisibility(View.VISIBLE);
                        ((ItemHolder) viewHolder).btnOpenSwing.setTag(bean.rcCommand.get(i).src);
                    }
                }

                ((ItemHolder) viewHolder).btnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateSelectedView(pos);
                        sendControlCmd(deviceId, ((ItemHolder) viewHolder).btnOpen.getTag().toString());

                    }
                });
                ((ItemHolder) viewHolder).btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateSelectedView(pos);
                        sendControlCmd(deviceId, ((ItemHolder) viewHolder).btnClose.getTag().toString());
                    }
                });
                ((ItemHolder) viewHolder).btnOpenSwing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateSelectedView(pos);
                        sendControlCmd(deviceId, ((ItemHolder) viewHolder).btnOpenSwing.getTag().toString());
                    }
                });
                ((ItemHolder) viewHolder).btnCloseSwing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateSelectedView(pos);
                        sendControlCmd(deviceId, ((ItemHolder) viewHolder).btnCloseSwing.getTag().toString());
                    }
                });

                ((ItemHolder) viewHolder).ivChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateSelectedView(pos);
                    }
                });

            }

        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        private class ItemHolder extends RecyclerView.ViewHolder {

            public Button btnClose;
            public Button btnOpen;
            public Button btnOpenSwing;
            public Button btnCloseSwing;
            public ImageView ivChoose;
            public TextView tvModelName;

            public ItemHolder(View itemView) {
                super(itemView);
                btnOpen = (Button) itemView.findViewById(R.id.btn_open);
                btnClose = (Button) itemView.findViewById(R.id.btn_close);
                ivChoose = (ImageView) itemView.findViewById(R.id.iv_choose);
                tvModelName = (TextView) itemView.findViewById(R.id.tv_model_name);
                btnOpenSwing = (Button) itemView.findViewById(R.id.btn_open_swing);
                btnCloseSwing = (Button) itemView.findViewById(R.id.btn_close_swing);
            }
        }

        private void updateSelectedView(int pos) {
            selectedPosition = pos;
            data.get(mSelectedPos).isSelected = false;
            //设置新的Item勾选状态
            mSelectedPos = pos;
            data.get(mSelectedPos).isSelected = true;
            notifyDataSetChanged();
        }

        private void sendControlCmd(String deviceId, String code) {
            WLog.i("MatchResultActivity", "Src:" + code);
            dataApiUnit.doControlWifiIR(deviceId, "2", null, null, code, new DataApiUnit.DataApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    WLog.i("MatchResultActivity", "controlSuccess: ");
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute
                            attribute) {
                        if (cluster.clusterId == 0x0F01) {
                            switch (attribute.attributeId) {
                                case 0x8002:
                                    parserControlData(attribute.attributeValue);
                                    break;
                            }
                        }
                    }
                });
            }
        }
    }

    private void parserControlData(String attribute) {
        switch (attribute) {
            case MODE_CONTROL_SUCCESS:
                WLog.i(TAG, "IR_SUCCESS");
                break;
            case MODE_CONTROL_FAIL:
                WLog.i(TAG, "DATA_ERROR");
                break;
        }
    }
}
