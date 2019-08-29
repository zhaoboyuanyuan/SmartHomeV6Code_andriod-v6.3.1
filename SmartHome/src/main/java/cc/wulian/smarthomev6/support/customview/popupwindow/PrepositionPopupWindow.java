package cc.wulian.smarthomev6.support.customview.popupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.device.adapter.SimpleAdapter;
import cc.wulian.smarthomev6.main.device.cylincam.utils.IotUtil;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CylincamPositionBean;
import cc.wulian.smarthomev6.support.tools.ImageLoaderTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by 上海滩小马哥 on 2017/9/20.
 * 小物预置位弹窗
 */

public class PrepositionPopupWindow {
    private Context context;
    private LinearLayout rootView;
    private LayoutInflater inflater;
    private PopupWindow popupWindow;
    private GridView gridView;
    private PrepositionAdapter prepositionAdapter;
    private String devId;
    private DataApiUnit dataApiUnit;
    private boolean hasLocalPosition;

    public static interface PrePositionListener {
        void roateToPosition(int position);

        void addPosition(int position);

        void removePosition(int position, String name);
    }

    public PrepositionPopupWindow(Context context, String devId) {
        this.context = context;
        this.devId = devId;
        inflater = LayoutInflater.from(context);
        rootView = (LinearLayout) inflater.inflate(
                R.layout.popup_pre_position, null);
        gridView = (GridView) rootView.findViewById(R.id.gridView);

        init();
    }

    private void init() {
        dataApiUnit = new DataApiUnit(context);
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

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private void initData() {
        List<CylincamPositionBean.PositionBean> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            CylincamPositionBean.PositionBean prepositionModel = new CylincamPositionBean.PositionBean();
            prepositionModel.name = null;
            prepositionModel.image = null;
            list.add(prepositionModel);
        }
        prepositionAdapter.clear();
        prepositionAdapter.add(list);
        getLocalData(list);
    }

    private void getLocalData(List<CylincamPositionBean.PositionBean> posList) {
        File[] files = null;
        files = IotUtil.getFiles(FileUtil.getPrePositionPath() + "/" + devId);
        for (int i = 1; i <= 4; i++) {
            String name = Preference.getPreferences().getCylincamPrePosition(devId, i);
            if (!TextUtils.isEmpty(name)) {
                hasLocalPosition = true;
//                if (files == null || files.length == 0) {
//                    PrepositionModel prepositionModel = new PrepositionModel();
//                    prepositionModel.name = name;
//                    prepositionModel.pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_device_cn_icon);
//                    list.remove(i - 1);
//                    list.add(i - 1, prepositionModel);
//                } else {
//                    PrepositionModel prepositionModel = new PrepositionModel();
//                    prepositionModel.name = name;
//                    prepositionModel.pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_device_cn_icon);
//                    for (int j = 0; j < files.length; j++) {
//                        File pic = files[j];
//                        if (pic.getName().substring(0, pic.getName().length() - 4).equals(name)) {
//                            if (name.endsWith(".jpg")) {
//                                prepositionModel.name = name.substring(0, name.length() - 4);
//                            } else {
//                                prepositionModel.name = name;
//                            }
//                            prepositionModel.pic = BitmapFactory.decodeFile(pic.getAbsolutePath());
//                            break;
//                        }
//                    }
//                    list.remove(i - 1);
//                    list.add(i - 1, prepositionModel);
//                }
            }
        }
        if (hasLocalPosition) {
            if (!Preference.getPreferences().getLocalPositionTip()) {
                ToastUtil.show(context.getResources().getString(R.string.Camera_Preset_Position));
                Preference.getPreferences().setLocalPositionTip(true);
            }
        }
        getCylincamPositionData(posList);
//        prepositionAdapter.swapData(list);
    }


    private void getCylincamPositionData(final  List posList) {
        dataApiUnit.doGetCylincamPosition(devId, new DataApiUnit.DataApiCommonListener<List<CylincamPositionBean.PositionBean>>() {
            @Override
            public void onSuccess(List<CylincamPositionBean.PositionBean> list) {
                if (list != null) {
                    for(int i = 0;i<list.size();i++){
                        posList.remove(Integer.parseInt(list.get(i).id)-1);
                        posList.add(Integer.parseInt(list.get(i).id)-1,list.get(i));
                    }
                    prepositionAdapter.swapData(posList);
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    PrePositionListener listener;

    public void setOnClickListener(PrePositionListener listener) {
        this.listener = listener;
    }

    public void refresh() {
        initData();
    }

    class PrepositionAdapter extends SimpleAdapter<CylincamPositionBean.PositionBean> {

        public PrepositionAdapter(Context context, List<CylincamPositionBean.PositionBean> info) {
            super(context, info);
        }

        @Override
        public View view(int position, View convertView, ViewGroup parent) {
            CylincamPositionBean.PositionBean pModel = eList.get(position);
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

            if (!TextUtils.isEmpty(pModel.name)) {
                viewHolder.perName.setText(pModel.name);
                viewHolder.perName.setVisibility(View.VISIBLE);
            } else {
                viewHolder.perName.setText(null);
                viewHolder.perName.setVisibility(View.GONE);
            }
            if (pModel.image != null) {
//                viewHolder.perImage.setImageBitmap(pModel.pic);
                ImageLoader.getInstance().displayImage(pModel.image, viewHolder.perImage, ImageLoaderTool.getAdOptions());
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
                        listener.removePosition(pos + 1, eList.get(pos).name);
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
