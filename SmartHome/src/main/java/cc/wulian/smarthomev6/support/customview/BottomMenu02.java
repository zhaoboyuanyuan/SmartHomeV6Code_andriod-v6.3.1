package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.customview.wheel.WheelViewNew;

/**
 * Created by Wulian on 2018/9/7.
 */

public class BottomMenu02 {

    private PopupWindow popupWindow;
    private Context mContext;
    private WheelViewNew mLeftWheelView;
    private WheelViewNew mRightWheelView;
    private TextView mTextTitle;
    private BottomMenu.MenuClickListener mMenuClickListener;

    public BottomMenu02(Context context, BottomMenu.MenuClickListener menuClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mContext = context;
        mMenuClickListener = menuClickListener;
        View mMenuView = inflater.inflate(R.layout.popwindow_list_02, null);
        mLeftWheelView = (WheelViewNew) mMenuView.findViewById(R.id.left_wheelView);
        mRightWheelView = (WheelViewNew) mMenuView.findViewById(R.id.right_wheelView);
        mTextTitle = (TextView) mMenuView.findViewById(R.id.title);
        mLeftWheelView.setCyclic(false);
        mRightWheelView.setCyclic(false);
        View cancel_btn = mMenuView.findViewById(R.id.cancel_btn);
        View sure_btn = mMenuView.findViewById(R.id.sure_btn);
        int height = Math.round(mContext.getResources().getDisplayMetrics().heightPixels / 3f);
        popupWindow = new PopupWindow(mMenuView, ViewGroup.LayoutParams.MATCH_PARENT, height, true);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        sure_btn.setOnClickListener(mOnClickListener);
        cancel_btn.setOnClickListener(mOnClickListener);
    }

    public interface MenuClickListener{
        void onSure();
        void onCancel();
    }

    public void setTitle(String title) {
        if(!TextUtils.isEmpty(title)){
            mTextTitle.setText(title);
        }
    }

    public void setData(ArrayList leftData, ArrayList rightData){
        mLeftWheelView.refreshData(leftData);
        mRightWheelView.refreshData(rightData);
    }

    public void setCurrent(int leftIndex, int rightIndex){
        mLeftWheelView.setDefault(leftIndex);
        mRightWheelView.setDefault(rightIndex);
    }

    public void setLeftCurrent(int leftIndex){
        mLeftWheelView.setDefault(leftIndex);
    }

    public void setRightCurrent(int rightIndex){
        mRightWheelView.setDefault(rightIndex);
    }

    public int getLeftCurrent(){
        return mLeftWheelView.getSelected();
    }

    public int getRightCurrent(){
        return mRightWheelView.getSelected();
    }

    public String getLeftCurrentItem(){
        return mLeftWheelView.getSelectedText();
    }

    public String getRightCurrentItem(){
        return mRightWheelView.getSelectedText();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sure_btn:
                    if(mMenuClickListener != null){
                        mMenuClickListener.onSure();
                    }
                    popupWindow.dismiss();
                    break;
                case R.id.cancel_btn:
                    if(mMenuClickListener != null){
                        mMenuClickListener.onCancel();
                    }
                    popupWindow.dismiss();
                    break;
            }
        }
    };

    /**
     * 显示菜单
     */
    public void show(View rootView){
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
}