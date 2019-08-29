package cc.wulian.smarthomev6.support.event;

/**
 * Created by zbl on 2017/4/13.
 * 获取自动任务列表
 */

public class GetAutoProgramListEvent {
    public String jsonData;

    public GetAutoProgramListEvent(String jsonData) {
        this.jsonData = jsonData;
    }
}
