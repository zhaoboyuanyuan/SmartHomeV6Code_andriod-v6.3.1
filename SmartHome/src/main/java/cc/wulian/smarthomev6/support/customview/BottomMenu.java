package cc.wulian.smarthomev6.support.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.customview.wheel.WheelViewNew;

/**
 * 作者：Administrator on 2017/11/7 15:43
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class BottomMenu{

    private PopupWindow popupWindow;
    private Context mContext;
    private WheelViewNew mWheelView;
    private TextView mTextTitle;
    private MenuClickListener mMenuClickListener;

    public BottomMenu(Context context, MenuClickListener menuClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mContext = context;
        mMenuClickListener = menuClickListener;
        View mMenuView = inflater.inflate(R.layout.popwindow_list, null);
        mWheelView = (WheelViewNew) mMenuView.findViewById(R.id.wheelView);
        mTextTitle = (TextView) mMenuView.findViewById(R.id.title);
        mWheelView.setCyclic(false);
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

    public void setData(ArrayList data){
        mWheelView.refreshData(data);
    }

    public void setCurrent(int index){
        mWheelView.setDefault(index);
    }

    public int getCurrent(){
        return mWheelView.getSelected();
    }

    public String getCurrentItem(){
        return mWheelView.getSelectedText();
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
