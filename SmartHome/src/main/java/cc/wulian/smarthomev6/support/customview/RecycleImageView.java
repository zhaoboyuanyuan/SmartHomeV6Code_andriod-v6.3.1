package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by 上海滩小马哥 on 2018/02/09.
 * recycleview item中带动画时用
 */

public class RecycleImageView extends ImageView {
    private Animation columnAnim;

    public RecycleImageView(Context context) {
        super(context);
    }

    public RecycleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (null == getAnimation()){
            setAnimation(columnAnim);
        }
    }

    @Override
    public void setAnimation(Animation animation) {
        super.setAnimation(animation);
        columnAnim = animation;
    }

    public void viewRecycle(){
        clearAnimation();
        columnAnim = null;
    }
}
