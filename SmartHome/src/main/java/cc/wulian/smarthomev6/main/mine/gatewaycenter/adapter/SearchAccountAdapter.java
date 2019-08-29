package cc.wulian.smarthomev6.main.mine.gatewaycenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.customview.CircleImageView;
import cc.wulian.smarthomev6.support.tools.ImageLoaderTool;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by mamengchao on 2017/3/6 0006.
 * Tips:账户搜索adapter
 */

public class SearchAccountAdapter extends WLBaseAdapter<UserBean> {

    public SearchAccountAdapter(Context context, List<UserBean> data) {
        super(context, data);
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater, ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.personal_center_plugin, null, false);
    }

    @Override
    protected void bindView(Context context, View view, int pos, UserBean item) {
        CircleImageView icon = (CircleImageView) view.findViewById(R.id.setting_manager_item_name_iv);
        TextView name = (TextView) view.findViewById(R.id.setting_manager_item_name_tv);

        if (StringUtil.isNullOrEmpty(item.avatar)) {
            icon.setImageResource(R.drawable.icon_head);
        } else {
            ImageLoader.getInstance().displayImage(item.avatar, icon, ImageLoaderTool.getUserAvatarOptions());
        }

        if (StringUtil.isNullOrEmpty(item.nick)) {
            name.setText(item.phone);
        } else {
            name.setText(item.nick);
        }
    }
}
