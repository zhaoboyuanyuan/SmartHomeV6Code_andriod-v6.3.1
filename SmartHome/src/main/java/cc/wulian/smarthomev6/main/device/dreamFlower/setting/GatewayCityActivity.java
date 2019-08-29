package cc.wulian.smarthomev6.main.device.dreamFlower.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.main.device.dreamFlower.bean.CityBean;
import cc.wulian.smarthomev6.main.device.dreamFlower.bean.ProvinceBean;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2017/12/18.
 * func： 梦想之花网关城市设置
 * email: hxc242313@qq.com
 */

public class GatewayCityActivity extends BaseTitleActivity {
    private ListView lvCity;
    private List<CityBean.CityListBean> cityList;
    private CityAdapter adapter;
    private String defaultCity;
    private String province;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_city, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle("城市");
    }

    @Override
    protected void initView() {
        super.initView();
        lvCity = (ListView) findViewById(R.id.lv_city);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        lvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectItem(position);
                adapter.notifyDataSetInvalidated();
                setResult(RESULT_OK, new Intent()
                        .putExtra("city", cityList.get(position).cityName)
                        .putExtra("cityCode", cityList.get(position).cityCode));
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        defaultCity = getIntent().getStringExtra("city");
        province = getIntent().getStringExtra("province");
        DataApiUnit dataApiUnit = new DataApiUnit(this);
        dataApiUnit.doGetGatewayCity("CN", ApiConstant.getUserID(), province, new DataApiUnit.DataApiCommonListener<CityBean>() {
            @Override
            public void onSuccess(CityBean bean) {
                cityList = bean.cityList;
                adapter = new CityAdapter(GatewayCityActivity.this, cityList);
                lvCity.setAdapter(adapter);
                if (!TextUtils.isEmpty(defaultCity)) {
                    setDefaultCity(defaultCity);
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void setDefaultCity(String city) {
        for (int i = 0; i < cityList.size(); i++) {
            if (TextUtils.equals(city, cityList.get(i).cityName)) {
                adapter.setSelectItem(i);
                adapter.notifyDataSetInvalidated();
            }
        }
    }

    public class CityAdapter extends WLBaseAdapter<CityBean.CityListBean> {
        private List<CityBean.CityListBean> mData;

        public CityAdapter(Context context, List<CityBean.CityListBean> data) {
            super(context, data);
            this.mData = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_broadcast_voice, null);
                holder.ivCheck = (ImageView) convertView.findViewById(R.id.iv_checked);
                holder.tvCity = (TextView) convertView.findViewById(R.id.tv_broadcast_language);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvCity.setText(mData.get(position).cityName);
            if (position == selectItem) {
                holder.ivCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivCheck.setVisibility(View.GONE);
            }
            return convertView;
        }

        public void setSelectItem(int selectItem) {
            this.selectItem = selectItem;
        }

        private int selectItem = -1;

        public final class ViewHolder {
            public ImageView ivCheck;
            public TextView tvCity;
        }
    }

}
