package cc.wulian.smarthomev6.support.tools.singlechoice;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Veev on 2017/8/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    单选帮助类
 */
public class SingleChoiceHelper {

    // 单选监听器
    private OnItemCheckedListener mOnItemCheckedListener;
    // 单选 item集合
    private List<SingleChoice> mSingleChoices;
    // 单选 item 对应的 选择监听的集合
    private Map<SingleChoice, CheckedListener> mCheckedListenerMap;
    // 单选 item 对应的 tag的集合
    private Map<SingleChoice, Object> mTagMap;
    // 当前选择的
    private SingleChoice mChoice;

    public SingleChoiceHelper() {
        mSingleChoices = new ArrayList<>();
        mCheckedListenerMap = new HashMap<>();
        mTagMap = new HashMap<>();
    }

    /**
     * 添加一个单选项
     * @param pair      单选项 First   SingleChoice
     *                          Second  TAG
     */
    public void addSingleChoice(Pair<SingleChoice, Object> pair) {
        mTagMap.put(pair.first, pair.second);
        addSingleChoice(pair.first);
    }

    /**
     * 添加一个单选项
     */
    public void addSingleChoice(final SingleChoice singleChoice) {
        mSingleChoices.add(singleChoice);
        CheckedListener c = new CheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                if (mChoice == null) {
                    mChoice = singleChoice;
                }
                if (isChecked) {
                    mChoice = singleChoice;
                    cancelOthers();
                    if (mOnItemCheckedListener != null) {
                        mOnItemCheckedListener.onChecked(mChoice);
                        mOnItemCheckedListener.onChecked(mTagMap.get(mChoice));
                    }
                } else if (mChoice.equals(singleChoice)){
                    mChoice.setChecked(true);
                }
            }
        };
        mCheckedListenerMap.put(singleChoice, c);
        singleChoice.addCheckedListener(c);
    }

    /**
     * 添加一组单选项
     */
    public void addSingleChoice(SingleChoice ... singleChoices) {
        for (SingleChoice s : singleChoices) {
            addSingleChoice(s);
        }
    }

    /**
     * 移除一个单选项
     * 移除选项时, 同时移除对选项的监听
     */
    public void removeSingleChoice(SingleChoice singleChoice) {
        CheckedListener c = mCheckedListenerMap.remove(singleChoice);
        singleChoice.removeCheckedListener(c);
        mSingleChoices.remove(singleChoice);
        mTagMap.remove(singleChoice);
    }

    /**
     * 设置单选监听
     * @param onItemCheckedListener
     */
    public void setOnItemCheckedListener(OnItemCheckedListener onItemCheckedListener) {
        mOnItemCheckedListener = onItemCheckedListener;
    }

    /**
     * 清除其他单选
     */
    private void cancelOthers() {
        for (SingleChoice s : mSingleChoices) {
            if (!s.equals(mChoice)) {
                s.setChecked(false);
            }
        }
    }
}
