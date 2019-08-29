package cc.wulian.smarthomev6.support.event;

/**
 * Created by zbl on 2017/4/13.
 * 获取自动任务列表
 */

public class GetAutoProgramTaskEvent {
    public String jsonData;

    public GetAutoProgramTaskEvent(String jsonData) {
        this.jsonData = jsonData;
    }
}
