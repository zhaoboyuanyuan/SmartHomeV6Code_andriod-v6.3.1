package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;

import cc.wulian.smarthomev6.R;

/**
 * Created by Veev on 2017/5/9
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public class HumitureText extends View {

    private Context mContext;

    private String mText;
    @ColorInt
    private int mTextColor;
    @ColorInt
    private int mStrokeColor;
    private float humStrokeWidth;
    private float mTextSize;

    private Paint mStrokePaint, mTextPaint;

    public HumitureText(Context context) {
        super(context);

        mContext = context;
        init();
    }

    public HumitureText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HumitureText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HumitureText, 0, 0);

        try {
            mText = ta.getString(R.styleable.HumitureText_humText);
            mTextColor = ta.getColor(R.styleable.HumitureText_humTextColor, context.getResources().getColor(R.color.colorPrimary));
            mStrokeColor = ta.getColor(R.styleable.HumitureText_humStrokeColor, context.getResources().getColor(R.color.colorPrimary));
            humStrokeWidth = ta.getDimension(R.styleable.HumitureText_humStrokeWidth, 3);
            mTextSize = ta.getDimension(R.styleable.HumitureText_humTextSize, 50);
        } finally {
            ta.recycle();
        }

        init();
    }

    private void init() {
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(humStrokeWidth);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2.5f, mStrokePaint);

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        Rect r = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), r);
        int height = r.bottom - r.top;
        float textWidth = mTextPaint.measureText(mText);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.bottom - fm.top;
        float offsetY = height / 2 - (fm.bottom - (textHeight - height) / 2);
        canvas.drawText(mText, (getWidth() - textWidth) / 2, getHeight() / 2 + offsetY, mTextPaint);
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public void setText(@StringRes int resID) {
        setText(mContext.getResources().getString(resID));
    }

    public void setAllColor(@ColorInt int color) {
        mTextColor = color;
        mStrokeColor = color;

        mStrokePaint.setColor(mStrokeColor);
        mTextPaint.setColor(mTextColor);

        invalidate();
    }
}
