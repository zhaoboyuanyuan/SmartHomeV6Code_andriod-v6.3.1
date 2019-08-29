package cc.wulian.smarthomev6.main.message.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;

/**
 * Created by Veev on 2017/7/11
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    RecyclerStickyScrollListener
 */

public class RecyclerStickyScrollListener extends RecyclerView.OnScrollListener {
    private TextView mStickView = null;

    public RecyclerStickyScrollListener(TextView textStickyHeaderView) {
        this.mStickView = textStickyHeaderView;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // 找到RecyclerView的item中，和RecyclerView的getTop 向下相距5个像素的那个item
        // (尝试2、3个像素位置都找不到，所以干脆用了5个像素)，
        // 我们根据这个item，来更新吸顶布局的内容，
        // 因为我们的StickyLayout展示的信息肯定是最上面的那个item的信息.
        View stickyInfoView = recyclerView.findChildViewUnder(mStickView.getMeasuredWidth() / 2, 5);
        if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
            mStickView.setText(String.valueOf(stickyInfoView.getContentDescription()));
        }

        // 找到固定在屏幕上方那个FakeStickyLayout下面一个像素位置的RecyclerView的item，
        // 我们根据这个item来更新假的StickyLayout要translate多少距离.
        // 并且只处理HAS_STICKY_VIEW和NONE_STICKY_VIEW这两种tag，
        // 因为第一个item的StickyLayout虽然展示，但是一定不会引起FakeStickyLayout的滚动.
        View transInfoView = recyclerView.findChildViewUnder(
                mStickView.getMeasuredWidth() / 2, mStickView.getMeasuredHeight() + 1);

        if (transInfoView != null && transInfoView.getTag() != null) {
            int transViewStatus = (int) transInfoView.getTag();
            int dealtY = transInfoView.getTop() - mStickView.getMeasuredHeight();

            // 如果当前item需要展示StickyLayout，
            // 那么根据这个item的getTop和FakeStickyLayout的高度相差的距离来滚动FakeStickyLayout.
            // 这里有一处需要注意，如果这个item的getTop已经小于0，也就是滚动出了屏幕，
            // 那么我们就要把假的StickyLayout恢复原位，来覆盖住这个item对应的吸顶信息.
            if (transViewStatus == MessageAlarmAdapter.TYPE_FIRST) {
                if (transInfoView.getTop() > 0) {
                    mStickView.setTranslationY(dealtY);
                } else {
                    mStickView.setTranslationY(0);
                }
            } else {
                // 如果当前item不需要展示StickyLayout，那么就不会引起FakeStickyLayout的滚动.
                mStickView.setTranslationY(0);
            }
        }
    }
}
