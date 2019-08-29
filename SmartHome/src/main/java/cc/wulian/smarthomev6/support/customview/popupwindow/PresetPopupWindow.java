package cc.wulian.smarthomev6.support.customview.popupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.device.adapter.SimpleAdapter;
import cc.wulian.smarthomev6.main.device.cylincam.utils.IotUtil;
import cc.wulian.smarthomev6.main.device.outdoor.bean.Preset;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.FileUtil;

/**
 * Created by hxc on 2017/10/10.
 * 户外摄像机预置位弹框
 */

public class PresetPopupWindow extends PopupWindow {
    private Context context;
    private LinearLayout rootView;
    private LayoutInflater inflater;
    private PopupWindow popupWindow;
    private GridView gridView;
    private PrepositionAdapter prepositionAdapter;
    private String devId;
    private String deviceDomain;
    private List<Preset> defaultList;

    public static interface PrePositionListener {
        void roateToPosition(int position);

        void addPosition(int position);

        void removePosition(int position, String name);
    }

    public PresetPopupWindow(Context context, String devId, String deviceDomain) {
        this.context = context;
        this.devId = devId;
        this.deviceDomain = deviceDomain;
        inflater = LayoutInflater.from(context);
        rootView = (LinearLayout) inflater.inflate(
                R.layout.popup_pre_position, null);
        gridView = (GridView) rootView.findViewById(R.id.gridView);

        init();
    }

    private void init() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(context);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0000000000));
            // 指定popupWindow的宽和高
            popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(rootView);
            rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            prepositionAdapter = new PrepositionAdapter(context, null);
            gridView.setAdapter(prepositionAdapter);
        }
    }

    public void showParent(View view) {
        initData();
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }
    public void dismiss(){
        popupWindow.dismiss();
    }

    private void initData() {
        defaultList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Preset preset = new Preset();
            preset.setDesc(null);
            preset.setPicture(null);
            defaultList.add(preset);
        }
        prepositionAdapter.clear();
        prepositionAdapter.add(defaultList);
        IPCMsgController.MsgQueryPresetsList(devId, deviceDomain);
    }

    PrePositionListener listener;

    public void setOnClickListener(PrePositionListener listener) {
        this.listener = listener;
    }

    public void refresh() {
        initData();
    }

    public void updateData(List<Preset> list) {
        File[] files = null;
        files = IotUtil.getFiles(FileUtil.getPrePositionPath() + "/" + devId);
        for (int i = 0; i < list.size(); i++) {
            String name1 = list.get(i).getDesc();//服务器拉取的第n个预置位的名字
            String name2 = Preference.getPreferences().getCylincamPrePosition(devId, list.get(i).getPreset());//本地第n个预置位的名字
            if (!TextUtils.isEmpty(name1)) {
                if (files == null || files.length == 0||TextUtils.isEmpty(name2)) {
                    Preset preset = new Preset();
                    preset.setDesc(name1);
                    preset.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_outdoor_preset_default));
                    defaultList.remove(list.get(i).getPreset() - 1);
                    defaultList.add(list.get(i).getPreset() - 1, preset);
                } else {
                    Preset preset = new Preset();
                    preset.setDesc(name1);
                    preset.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_device_cn_icon));
                    for (int j = 0; j < files.length; j++) {
                        File pic = files[j];
                        if (pic.getName().substring(0, pic.getName().length() - 4).equals(name1)) {
                            if (name1.endsWith(".jpg")) {
                                preset.setDesc(name1.substring(0, name1.length() - 4));
                            } else {
                                preset.setDesc(name1);
                            }
                            preset.setBitmap(BitmapFactory.decodeFile(pic.getAbsolutePath()));
                            break;
                        }
                    }
                    defaultList.remove(list.get(i).getPreset() - 1);
                    defaultList.add(list.get(i).getPreset() - 1, preset);
                }
            }

        }
        prepositionAdapter.swapData(defaultList);
    }

    class PrepositionAdapter extends SimpleAdapter<Preset> {

        public PrepositionAdapter(Context context, List<Preset> info) {
            super(context, info);
        }

        @Override
        public View view(int position, View convertView, ViewGroup parent) {
            Preset preset = eList.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = this.layoutInflater.inflate(R.layout.pre_position_item, null);
                viewHolder.perName = (TextView) convertView.findViewById(R.id.tv_testpos);
                viewHolder.perImageDefault = (ImageView) convertView.findViewById(R.id.iv_preset_position_default);
                viewHolder.perImage = (ImageView) convertView.findViewById(R.id.iv_preset_position);
                viewHolder.exitImage = (ImageView) convertView.findViewById(R.id.iv_del_pos);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (!TextUtils.isEmpty(preset.getDesc())) {
                viewHolder.perName.setText(preset.getDesc());
                viewHolder.perName.setVisibility(View.VISIBLE);
            } else {
                viewHolder.perName.setText(null);
                viewHolder.perName.setVisibility(View.GONE);
            }
            if (preset.getPicture() != null) {
                viewHolder.perImage.setImageBitmap(preset.getBitmap());
                viewHolder.perImage.setVisibility(View.VISIBLE);
                viewHolder.exitImage.setVisibility(View.VISIBLE);
            } else {
                viewHolder.perImage.setImageBitmap(null);
                viewHolder.perImage.setVisibility(View.GONE);
                viewHolder.exitImage.setVisibility(View.GONE);
            }

            final int pos = position;
            viewHolder.perImageDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.addPosition(pos + 1);
                    }
                }
            });
            viewHolder.perImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.roateToPosition(pos + 1);
                    }
                }
            });
            viewHolder.exitImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.removePosition(pos + 1, eList.get(pos).getDesc());
                    }
                }
            });
            return convertView;
        }

        private final class ViewHolder {
            private TextView perName;
            private ImageView perImage, exitImage, perImageDefault;
        }
    }

}
