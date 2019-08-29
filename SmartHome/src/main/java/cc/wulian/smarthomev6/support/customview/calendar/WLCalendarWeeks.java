package cc.wulian.smarthomev6.support.customview.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cc.wulian.smarthomev6.R;

/**
 * Created by Veev on 2017/5/5
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public class WLCalendarWeeks extends View {

    private static String weeks[];

    // 画笔
    private Paint mTextPaint;

    private int mTextSize;

    private int padding = 60;

    public WLCalendarWeeks(Context context) {
        super(context);

        initView(context);
    }

    public WLCalendarWeeks(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public WLCalendarWeeks(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        weeks = new String[]{
                context.getResources().getString(R.string.MessageCenter_Calendar_Sun),
                context.getResources().getString(R.string.MessageCenter_Calendar_Mon),
                context.getResources().getString(R.string.MessageCenter_Calendar_Tue),
                context.getResources().getString(R.string.MessageCenter_Calendar_Wen),
                context.getResources().getString(R.string.MessageCenter_Calendar_Thu),
                context.getResources().getString(R.string.MessageCenter_Calendar_Fri),
                context.getResources().getString(R.string.MessageCenter_Calendar_Sta),
        };

        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.message_center_detail_dateBar_text_function_size);
        padding = context.getResources().getDimensionPixelSize(R.dimen.message_center_detail_dateBar_Padding);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(context.getResources().getColor(R.color.v6_text_dark));
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        adjustSize(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 调整大小
     * 处理wrap_content情况
     */
    private void adjustSize(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSize;
        int height = heightSize;

        if (heightMode == MeasureSpec.AT_MOST) {
            // 高度为 文字高度 + 上下边距
            height = mTextSize;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeeks(canvas);
    }

    /**
     * 绘制星期
     */
    private void drawWeeks(Canvas canvas) {
        float unitWidth = (getWidth() - padding * 2) / 7f;

        int startY = mTextSize;
        int startX = padding;
        for (int i = 0; i < 7; i++) {
            // 注意 “星期几” 和 “上个月”“下个月”的对齐方式
            canvas.drawText(weeks[i], startX + (unitWidth + (unitWidth - mTextSize) / 7) * i,
                    startY,
                    mTextPaint);
        }
    }

}
