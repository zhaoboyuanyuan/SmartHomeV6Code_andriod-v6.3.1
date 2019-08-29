package cc.wulian.smarthomev6.main.mine.gatewaycenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenu;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuLayout;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuView;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * 作者: mamengchao
 * 时间: 2017/4/7 0007
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class AccountListAdapter extends SwipeMenuAdapter<UserBean> {

    private LayoutInflater mInflater;

    public AccountListAdapter(Context context, List<UserBean> mData) {
        super(context, mData);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null){
            holder=new ViewHolder();
            View view = mInflater.inflate(R.layout.item_account_list, null);
            holder.tvAccountName = (TextView) view.findViewById(R.id.tv_account_name);
            holder.tvAccountId = (TextView) view.findViewById(R.id.tv_account_id);
            convertView = createMenuView(position, parent, view);
            convertView.setTag(holder);
        }else {
            updateMenuView(position, (SwipeMenuLayout)convertView);
            holder = (ViewHolder)convertView.getTag();
        }

        if (StringUtil.isNullOrEmpty(mData.get(position).nick)){
            holder.tvAccountName.setText(mData.get(position).phone);
        }else {
            holder.tvAccountName.setText(mData.get(position).nick);
        }

        if (!TextUtils.isEmpty(mData.get(position).phone)){
            holder.tvAccountId.setText(mData.get(position).phone);
        }else {
            holder.tvAccountId.setText(mData.get(position).email);
        }
        return convertView;
    }

    public final class ViewHolder{
        public TextView tvAccountName;
        public TextView tvAccountId;
    }
}
