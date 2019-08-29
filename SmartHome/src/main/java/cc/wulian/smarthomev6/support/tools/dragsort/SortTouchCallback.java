package cc.wulian.smarthomev6.support.tools.dragsort;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Veev on 2017/5/18
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public class SortTouchCallback extends ItemTouchHelper.Callback {

    private OnItemCallbackListener mListener;

    public SortTouchCallback(OnItemCallbackListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //首先回调的方法 返回int表示是否监听该方向
        int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;//拖拽
        int swipeFlags = ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;//侧滑删除
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        mListener.onMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
}
