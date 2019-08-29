package cc.wulian.smarthomev6.main.home.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.support.utils.DisplayUtil;

/**
 * Created by zbl on 2017/9/25.
 * 会自动滚动的字符串列表
 */

public class AutoScrollTextView extends View implements Choreographer.FrameCallback {

    private Context context;
    private Choreographer choreographer;
    private Handler handler = new Handler();
    private Paint paint;

    private int viewHeight, viewWidth;

    private int textColor = 0xffffffff;
    private int textSize = 30;
    private int animateDuration = 500;
    private int moveTimeGap = 3000;

    private int maxLines = 6;
    private int itemHeight;

    private boolean isRunning = false;
    private int index = 0;
    private long savedStartTime;
    private float process;

    private List<String> textList = new ArrayList<>();

    public AutoScrollTextView(Context context) {
        super(context);
        init(context);
    }

    public AutoScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        choreographer = Choreographer.getInstance();
        itemHeight = DisplayUtil.dip2Pix(context, 40);
        textSize = DisplayUtil.dip2Pix(context, 20);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DisplayUtil.dip2Pix(context, 2));
        paint.setTextSize(textSize);
        paint.setColor(textColor);
    }

    public void setTextColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public void setTextSizeInDip(int dp) {
        textSize = DisplayUtil.dip2Pix(context, dp);
        paint.setTextSize(textSize);
        invalidate();
    }

    public void setText(List<String> list) {
        textList.clear();
        if (list != null) {
            textList.addAll(list);
        }
        index = 0;
        invalidate();
    }

    public void start() {
        stopMove();
        handler.removeCallbacks(moveTask);
        handler.postDelayed(moveTask, moveTimeGap);
    }

    public void stop() {
        handler.removeCallbacks(moveTask);
        stopMove();
    }

    private Runnable moveTask = new Runnable() {
        @Override
        public void run() {
            nextLine();
            handler.postDelayed(this, moveTimeGap);
        }
    };

    private void nextLine() {
        if (!this.isRunning) {
            choreographer.removeFrameCallback(this);
            choreographer.postFrameCallback(this);
            isRunning = true;
            savedStartTime = System.currentTimeMillis();
        }
    }

    private void stopMove() {
        isRunning = false;
        choreographer.removeFrameCallback(this);
        process = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        viewHeight = getHeight();
        viewWidth = getWidth();
        paint.setTextAlign(Paint.Align.CENTER);
        float x = viewWidth / 2;
        float y = (itemHeight - textSize) / 2 + itemHeight;
        y -= process * itemHeight;
        for (int i = 0; i < maxLines + 1 && i < textList.size(); i++) {
            if (i == 0) {
                paint.setAlpha((int) ((1 - process) * 0xff));
            } else if (i == maxLines) {
                paint.setAlpha((int) (process * 0xff));
            } else {
                paint.setAlpha(0xff);
            }
            canvas.drawText(textList.get((i + index) % textList.size()), x, y, paint);
            y += itemHeight;
        }
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (isRunning) {
            long offsetTime = System.currentTimeMillis() - savedStartTime;
            process = (float) offsetTime / (float) animateDuration;
            invalidate();
            if (offsetTime >= animateDuration) {
                index += 1;
                if (index >= textList.size()) {
                    index = 0;
                }
                stopMove();
            } else {
                choreographer.postFrameCallback(this);
            }

        }
    }
}
