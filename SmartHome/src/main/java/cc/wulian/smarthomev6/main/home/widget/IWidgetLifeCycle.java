package cc.wulian.smarthomev6.main.home.widget;

/**
 * Created by Veev on 2017/5/25
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 首页widget 设备widget 接口
 */

public interface IWidgetLifeCycle {

    void onBindViewHolder(HomeItemBean bean);
    void onViewRecycled();

}
