package cc.wulian.smarthomev6.main.device.camera_lc.setting;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.adapter.LcCustomTimeAdapter;
import cc.wulian.smarthomev6.main.device.camera_lc.adapter.LcDayTimeAdapter;
import cc.wulian.smarthomev6.main.device.camera_lc.adapter.LcWeekAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcCustomTimeBean;
import cc.wulian.smarthomev6.support.customview.wheel.NumericWheelAdapter;
import cc.wulian.smarthomev6.support.customview.wheel.WheelView;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class LcCustomProtectTimeActivity extends BaseTitleActivity {
    private RecyclerView weekRecycleView;
    private RecyclerView dayTimeRecycleView;
    private RecyclerView allDaysRecycleView;
    private ImageView ivAddTime;
    private LinearLayout llDayTime;
    private FrameLayout flAllTime;

    private LcWeekAdapter lcWeekAdapter;
    private LcDayTimeAdapter lcDayTimeAdapter;
    private LcCustomTimeAdapter lcCustomTimeAdapter;

    private int selectWeekPosition;
    private String ruleType;
    private int[] weeks = {R.string.MessageCenter_Calendar_Sun, R.string.MessageCenter_Calendar_Mon, R.string.MessageCenter_Calendar_Tue,
            R.string.MessageCenter_Calendar_Wen, R.string.MessageCenter_Calendar_Thu, R.string.MessageCenter_Calendar_Fri, R.string.MessageCenter_Calendar_Sta};
    private String[] week = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private List<LcCustomTimeBean> dayList;//每天的
    private HashMap<String, List<LcCustomTimeBean>> dateMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc_custom_time, true);
    }


    @Override
    protected void initView() {
        super.initView();
        weekRecycleView = (RecyclerView) findViewById(R.id.rv_week);
        dayTimeRecycleView = (RecyclerView) findViewById(R.id.rv_day);
        allDaysRecycleView = (RecyclerView) findViewById(R.id.iv_all_days);
        ivAddTime = (ImageView) findViewById(R.id.iv_add_time);
        llDayTime = (LinearLayout) findViewById(R.id.ll_day_time);
        flAllTime = (FrameLayout) findViewById(R.id.fl_all_time);
    }

    @Override
    protected void initData() {
        super.initData();
        selectWeekPosition = 0;
        ruleType = getIntent().getStringExtra("ruleType");
        dateMap = (HashMap<String, List<LcCustomTimeBean>>) getIntent().getSerializableExtra("dateMap");
        lcWeekAdapter = new LcWeekAdapter(this);
        lcDayTimeAdapter = new LcDayTimeAdapter(this);
        lcCustomTimeAdapter = new LcCustomTimeAdapter(this, null);
        initWeekRecycleView();
        initDayTimeRecycleView();
        initCustomTimeRecycleView();
        if (TextUtils.equals(ruleType, "3")) {
            flAllTime.setVisibility(View.VISIBLE);
            llDayTime.setVisibility(View.GONE);
            setToolBarTitleAndRightImg(R.string.Infraredrelay_List_Custom, R.drawable.area_edit);
        } else {
            flAllTime.setVisibility(View.GONE);
            llDayTime.setVisibility(View.VISIBLE);
            setToolBarTitleAndRightBtn(R.string.Infraredrelay_List_Custom, R.string.Save);
        }

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        ivAddTime.setOnClickListener(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

    }

    private void initWeekRecycleView() {
        weekRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        weekRecycleView.setItemAnimator(new DefaultItemAnimator());
        weekRecycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        weekRecycleView.setAdapter(lcWeekAdapter);
        lcWeekAdapter.setOnItemClickListener(new LcWeekAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectWeekPosition = position;
                showSelectDayTime();
            }
        });
        ArrayList<String> datas = new ArrayList<>();
        for (int i = 0; i < weeks.length; i++) {
            datas.add(i, getString(weeks[i]));
        }
        lcWeekAdapter.update(datas);
        lcWeekAdapter.setSelectPosition(0);
    }

    private void initDayTimeRecycleView() {
        dayTimeRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        dayTimeRecycleView.setItemAnimator(new DefaultItemAnimator());
        dayTimeRecycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        lcDayTimeAdapter.setOnDeleteListener(new LcDayTimeAdapter.onDeleteClickListener() {
            @Override
            public void onclick(int position) {
                showSelectDayTime();
            }
        });
        dayTimeRecycleView.setAdapter(lcDayTimeAdapter);
    }

    private void initCustomTimeRecycleView() {
        allDaysRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        allDaysRecycleView.setItemAnimator(new DefaultItemAnimator());
        allDaysRecycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        allDaysRecycleView.setAdapter(lcCustomTimeAdapter);
        if (dateMap == null) {
            dateMap = new HashMap<>();
        }
        lcCustomTimeAdapter.setList(dateMap);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_add_time:
                showPopWindow("00,00,23,59");
                break;
            case R.id.img_right:
                setToolBarTitleAndRightBtn(R.string.Infraredrelay_List_Custom, R.string.Save);
                showSelectDayTime();
                llDayTime.setVisibility(View.VISIBLE);
                flAllTime.setVisibility(View.GONE);
                break;
            case R.id.btn_right:
                setResult(RESULT_OK, new Intent().putExtra("dataMap", dateMap));
                finish();
                break;
        }
    }


    /**
     * 显示popupWindow
     */
    private void showPopWindow(String defaultTime) {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.custom_common_timeperiod_alertdialog, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        TextView btnCancel = (TextView) contentView.findViewById(R.id.tvCancel);
        TextView btnSure = (TextView) contentView.findViewById(R.id.tv_Sure);
        final WheelView start_time_hour = (WheelView) contentView
                .findViewById(R.id.start_time_hour);
        final WheelView start_time_min = (WheelView) contentView
                .findViewById(R.id.start_time_min);
        final WheelView end_time_hour = (WheelView) contentView
                .findViewById(R.id.end_time_hour);
        final WheelView end_time_min = (WheelView) contentView
                .findViewById(R.id.end_time_min);

        start_time_hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        start_time_min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        end_time_hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        end_time_min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        Calendar calender = Calendar.getInstance(Locale.getDefault());
        start_time_hour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
        start_time_min.setCurrentItem(calender.get(Calendar.MINUTE));
        end_time_hour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
        end_time_min.setCurrentItem(calender.get(Calendar.MINUTE));
        start_time_hour.setCyclic(true);
        start_time_min.setCyclic(true);
        end_time_hour.setCyclic(true);
        end_time_min.setCyclic(true);

        if (!TextUtils.isEmpty(defaultTime)) {
            String timeNum[] = defaultTime.split(",");
            if (timeNum.length == 4) {
                start_time_hour.setCurrentItem(Integer.parseInt(timeNum[0]),
                        false);
                start_time_min.setCurrentItem(Integer.parseInt(timeNum[1]),
                        false);
                end_time_hour.setCurrentItem(Integer.parseInt(timeNum[2]),
                        false);
                end_time_min
                        .setCurrentItem(Integer.parseInt(timeNum[3]), false);
            }
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startTimeHour = start_time_hour.getCurrentItem();
                int startTimeMin = start_time_min.getCurrentItem();
                int endTimeHour = end_time_hour.getCurrentItem();
                int endTimeMin = end_time_min.getCurrentItem();
                if (startTimeHour * 60 + startTimeMin >= endTimeHour * 60 + endTimeMin) {
                    ToastUtil.show(R.string.add_deviceLC_time_set);
                    return;
                }
                String data = startTimeHour + "," + startTimeMin + "," + endTimeHour + "," + endTimeMin;
                String time = formatSelectTime(data);
                saveTime(time);
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(contentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    private String formatSelectTime(String data) {
        String times[] = data.split(",");
        String result = null;
        for (int i = 0; i < times.length; i++) {
            if (times[i].length() == 1) {
                times[i] = "0" + times[i];
            }
        }
        result = times[0] + ":" + times[1] + ":00" + "--" + times[2] + ":" + times[3] + ":00";
        return result;
    }

    private void saveTime(String result) {
        String key = week[selectWeekPosition];
        dayList = dateMap.get(key);
        if (dayList == null) {
            dayList = new ArrayList<>();
            dayList.add(new LcCustomTimeBean(result, false));
            dateMap.put(key, dayList);
        } else {
//            if (dayList.size() >= 6) {
//                ToastUtil.show("单天最多设置6段防护时间");
//            } else {
            dayList.add(new LcCustomTimeBean(result, false));
            dateMap.put(key, dayList);
//            }
        }
//        lcDayTimeAdapter.update(dayList);
        showSelectDayTime();
    }

    private void showSelectDayTime() {
        String key = week[selectWeekPosition];
        dayList = dateMap.get(key);
        if (dayList != null && dayList.size() == 6) {
            ivAddTime.setVisibility(View.GONE);
        } else {
            ivAddTime.setVisibility(View.VISIBLE);
        }
        lcDayTimeAdapter.update(dayList);
    }

}
