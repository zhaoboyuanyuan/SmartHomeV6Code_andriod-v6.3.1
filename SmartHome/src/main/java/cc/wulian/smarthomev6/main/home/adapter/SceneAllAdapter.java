package cc.wulian.smarthomev6.main.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;

/**
 * Created by 王伟 on 2017/3/15
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public class SceneAllAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SceneInfo> datas;
    private String style;

    private OnItemClickListener onClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public SceneAllAdapter(Context context, List<SceneInfo> datas, String style) {
        this.context = context;
        this.datas = datas;
        this.style = style;
        notifyDataSetChanged();
    }

    public void update(List<SceneInfo> datas) {
        if (datas != null) {
            this.datas = datas;
            notifyDataSetChanged();
        }

    }

    public void remove(int index) {
        datas.remove(index);
        notifyItemRemoved(index);
        // 再次刷新，避免错乱
        notifyItemRangeChanged(index, datas.size());
    }

    public boolean isEmpty() {
        return datas.isEmpty();
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.onClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.onItemLongClickListener = longClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = null;
        if (TextUtils.equals("sudoku_3", style)) {
            itemView = layoutInflater.inflate(R.layout.item_all_scene_gridview_3, parent, false);
        } else if (TextUtils.equals("sudoku_4", style)) {
            itemView = layoutInflater.inflate(R.layout.item_all_scene_gridview_4, parent, false);
        } else if (TextUtils.equals("list", style)) {
            itemView = layoutInflater.inflate(R.layout.item_all_scene_list, parent, false);
        }
        SceneHolder holder = new SceneHolder(itemView);
        if (onClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick((Integer) v.getTag());
                }
            });
        }

        if (onItemLongClickListener != null) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClick((Integer) v.getTag());
                    return true;
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SceneInfo info = datas.get(position);

        ((SceneHolder) holder).name.setText(info.getName());
        if (TextUtils.equals(info.getStatus(), "2")) {
            ((SceneHolder) holder).name.setTextColor(0xff8dd652);
            ((SceneHolder) holder).frameLayout.setBackgroundResource(R.drawable.shape_scene_bg_selected);
            Bitmap bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconNormal(context, info.getIcon()));
            ((SceneHolder) holder).imageView.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, 0xff8dd652));
        } else {
            ((SceneHolder) holder).name.setTextColor(Color.BLACK);
            ((SceneHolder) holder).frameLayout.setBackgroundResource(R.drawable.shape_scene_bg_default);
            Bitmap bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconNormal(context, info.getIcon()));
            ((SceneHolder) holder).imageView.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, 0xff262626));
        }

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class SceneHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView name;
        private FrameLayout frameLayout;

        SceneHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.scene_icon);
            name = (TextView) itemView.findViewById(R.id.scene_name);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_scene_content);
        }
    }
}
