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

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.main.device.dreamFlower.bean.ProvinceBean;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2017/12/18.
 * func： 梦想之花网关省份设置
 * email: hxc242313@qq.com
 */

public class GatewayProvinceActivity extends BaseTitleActivity {
    private ListView lvProvince;
    private List<String> provinceList;
    private ProvinceAdapter adapter;
    private String defaultProvince;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_province, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle("省份");
    }

    @Override
    protected void initView() {
        super.initView();
        lvProvince = (ListView) findViewById(R.id.lv_province);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        lvProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectItem(position);
                adapter.notifyDataSetInvalidated();
                setResult(RESULT_OK, new Intent().putExtra("province", provinceList.get(position)));
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        defaultProvince = getIntent().getStringExtra("province");
        DataApiUnit dataApiUnit = new DataApiUnit(this);
        dataApiUnit.doGetGatewayProvince("CN", ApiConstant.getUserID(), new DataApiUnit.DataApiCommonListener<ProvinceBean>() {
            @Override
            public void onSuccess(ProvinceBean bean) {
                provinceList = bean.provinceList;
                adapter = new ProvinceAdapter(GatewayProvinceActivity.this, provinceList);
                lvProvince.setAdapter(adapter);
                if (!TextUtils.isEmpty(defaultProvince)) {
                    setDefaultProvince(defaultProvince);
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void setDefaultProvince(String province) {
        for (int i = 0; i < provinceList.size(); i++) {
            if (TextUtils.equals(province, provinceList.get(i))) {
                adapter.setSelectItem(i);
                adapter.notifyDataSetInvalidated();
            }
        }
    }

    public class ProvinceAdapter extends WLBaseAdapter<String> {
        public List<String> mData;

        public ProvinceAdapter(Context context, List<String> data) {
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
                holder.tvProvince = (TextView) convertView.findViewById(R.id.tv_broadcast_language);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvProvince.setText(mData.get(position));
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
            public TextView tvProvince;
        }
    }


}
