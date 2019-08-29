package cc.wulian.smarthomev6.main.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.home.scene.SceneSortActivity;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;


public class SceneSelectAdapter extends RecyclerView.Adapter<SceneSelectAdapter.MyViewHolder> {
    //这个是checkbox的Hashmap集合
    public final HashMap<Integer, Boolean> map;
    //这个是数据集合
    private List<SceneInfo> list;
    private Context context;
    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(HashMap<Integer, Boolean> map);

    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.onClickListener = listener;
    }

    public SceneSelectAdapter(Context context, List<SceneInfo> list) {
        this.context = context;
        this.list = list;
        map = new HashMap<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                map.put(i, false);
            }
        }
    }

    public void update(Context context, List<SceneInfo> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }


    /**
     * 全选
     */
    public HashMap<Integer,Boolean> selectAll() {
        Set<Map.Entry<Integer, Boolean>> entries = map.entrySet();
        boolean isCheck = false;
        for (Map.Entry<Integer, Boolean> entry : entries) {
            Boolean value = entry.getValue();
            if (!value) {
                isCheck = true;
                break;
            }
        }
        for (Map.Entry<Integer, Boolean> entry : entries) {
            entry.setValue(isCheck);
        }
        notifyDataSetChanged();
        return map;
    }

    /**
     * 反选
     */
    public HashMap<Integer,Boolean> selectNone() {
        Set<Map.Entry<Integer, Boolean>> entries = map.entrySet();
        for (Map.Entry<Integer, Boolean> entry : entries) {
            entry.setValue(!entry.getValue());
        }
        notifyDataSetChanged();
        return map;
    }

    /**
     * 取消选中的
     */
    public void selectClear() {
        Set<Map.Entry<Integer, Boolean>> entries = map.entrySet();
        for (Map.Entry<Integer, Boolean> entry : entries) {
            if(entry.getValue()){
                entry.setValue(!entry.getValue());
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 单选
     * <p>
     * //     * @param postion
     */
    public void selectOne(int position) {
        Set<Map.Entry<Integer, Boolean>> entries = map.entrySet();
        for (Map.Entry<Integer, Boolean> entry : entries) {
            entry.setValue(false);
        }
        map.put(position, true);
        notifyDataSetChanged();
    }

    //这里主要初始化布局控件
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //初始化布局文件
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scene_group_select, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //放入集合中的值
        holder.name.setText(list.get(position).getName());
        String groupName = null;
        groupName = MainApplication.getApplication().getSceneCache().getGroupName(list.get(position).getGroupID());
        if (TextUtils.isEmpty(groupName)) {
            holder.tvGroup.setText(groupName);
        } else {
            holder.tvGroup.setText("[" + groupName + "]");
        }
        Bitmap bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconNormal(context, list.get(position).getIcon()));
        holder.scene_icon.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, 0xff262626));
        holder.checkBox.setChecked(map.get(position));
        if (holder.checkBox.isChecked()) {
            holder.itemView.setBackgroundResource(R.color.v6_bg);
        } else {
            holder.itemView.setBackgroundResource(R.color.white);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                map.put(position, !map.get(position));
                onClickListener.onItemClick(map);
                //刷新适配器
                notifyDataSetChanged();
                //单选
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        private ImageView scene_icon;
        private TextView name;
        private TextView tvGroup;
        private CheckBox checkBox;

        //初始化控件
        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = (TextView) itemView.findViewById(R.id.item_scene_sort_title);
            scene_icon = (ImageView) itemView.findViewById(R.id.scene_icon);
            checkBox = (CheckBox) itemView.findViewById(R.id.iv_scene_select);
            tvGroup = (TextView) itemView.findViewById(R.id.tv_scene_group);
        }
    }
}