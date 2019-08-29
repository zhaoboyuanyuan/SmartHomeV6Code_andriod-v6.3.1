package cc.wulian.smarthomev6.support.tools;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Veev on 2017/6/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    RecycleView上拉加载更多
 *              仅用于  LinearLayoutManager的 RecyclerView
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0;
    private boolean loading = true;
    private int lastCompletelyVisiableItemPosition, visibleItemCount, totalItemCount;
    private int currentPage = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    private int getTotalItemCount() {
        return mLinearLayoutManager.getItemCount();
    }

    private int getLastCompletelyVisiableItemPosition() {
        return  mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = getTotalItemCount();

        lastCompletelyVisiableItemPosition = getLastCompletelyVisiableItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading
                && (visibleItemCount > 0)
                && (lastCompletelyVisiableItemPosition >= totalItemCount - 1)) {
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);

    public void resetLoadMoreConfig() {
        previousTotal = 0;
        loading = true;
        lastCompletelyVisiableItemPosition = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        currentPage = 1;
    }
}
