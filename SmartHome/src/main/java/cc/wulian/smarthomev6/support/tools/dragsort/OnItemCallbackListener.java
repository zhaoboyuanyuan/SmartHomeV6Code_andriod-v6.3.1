package cc.wulian.smarthomev6.support.tools.dragsort;

/**
 * Created by Veev on 2017/5/18
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public interface OnItemCallbackListener {
    /**
     * @param fromPosition 起始位置
     * @param toPosition 移动的位置
     */
    void onMove(int fromPosition, int toPosition);
    void onSwipe(int position);
}
