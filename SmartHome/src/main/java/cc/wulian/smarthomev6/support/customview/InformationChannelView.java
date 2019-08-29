package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 信道表格图
 * by zbl
 */
public class InformationChannelView extends View {

    private Context mContext;
//    private Runnable refreshWifiListTask = new Runnable() {
//        @Override
//        public void run() {
//            //fixme 测试数据
//            WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            setWifiList(wifiManager.getScanResults());
////            postDelayed(this,1000);//1秒刷新一次
//        }
//    };
    private List<ScanResult> wifiInfoList = new ArrayList<>();
    private List<Integer> zigbeeInfoList = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint curvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint zigbeeRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path coordinatePath = new Path();
    private Path curvePath = new Path();

    //view内部的留空
    private int padding_left, padding_right, padding_top, padding_bottom;
    //view尺寸
    private int viewWidth, viewHeight;
    //下方zigbee信道x轴的区域
    private int zigbeeArea_width, zigbeeArea_height;
    //轴坐标值字体大小
    private int value_text_size;
    //坐标轴线粗细
    private int coordinateStrokeWidth;
    //信道曲线粗细
    private int curveStrokeWidth;


    public InformationChannelView(Context context) {
        super(context);
        init(context);
    }

    public InformationChannelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InformationChannelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        //初始化绘制参数
        padding_left = DisplayUtil.dip2Pix(context, 12);
        padding_right = DisplayUtil.dip2Pix(context, 12);
        padding_top = DisplayUtil.dip2Pix(context, 50);
        padding_bottom = DisplayUtil.dip2Pix(context, 20);
        value_text_size = DisplayUtil.dip2Pix(context, 10);
        coordinateStrokeWidth = DisplayUtil.dip2Pix(context, 2);
        curveStrokeWidth = DisplayUtil.dip2Pix(context, 1);

        //初始化画笔
        paint.setStyle(Paint.Style.FILL);
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setStrokeWidth(curveStrokeWidth);
        zigbeeRectPaint.setStyle(Paint.Style.FILL);
        zigbeeRectPaint.setColor(0x4def07a0);

//        post(refreshWifiListTask);
//        setZigbeeInfoList(Arrays.asList(21));
    }

    public void setWifiList(List<ScanResult> list) {
        wifiInfoList.clear();
        if (list != null) {
            wifiInfoList.addAll(list);
        }
        postInvalidate();
    }

    public void setZigbeeInfoList(List<Integer> list) {
        zigbeeInfoList.clear();
        if (list != null) {
            zigbeeInfoList.addAll(list);
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        viewWidth = getWidth();
        viewHeight = getHeight();
        zigbeeArea_width = viewWidth - padding_left - padding_right;
        zigbeeArea_height = (int) ((viewHeight - padding_top - padding_bottom) * 0.16f);

        canvas.drawColor(0xff0f1331);//背景颜色
        //绘制wifi坐标轴
        int anchor_x = padding_left + value_text_size * 3;//坐标轴原点x
        int anchor_y = viewHeight - padding_bottom - zigbeeArea_height - value_text_size;//坐标轴原点y
        int diagram_right = viewWidth - padding_right - value_text_size * 4;//坐标轴右边界
        int diagram_top = padding_top;//坐标轴上边界
        coordinatePath.reset();
        coordinatePath.moveTo(anchor_x, diagram_top - value_text_size);
        coordinatePath.lineTo(anchor_x, anchor_y);
        coordinatePath.lineTo(diagram_right, anchor_y);
        paint.setShader(null);
        paint.setColor(0xccffffff);
        paint.setStrokeWidth(coordinateStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(coordinatePath, paint);
        //绘制zigbee坐标轴
        int zigbee_anchor_y = viewHeight - padding_bottom - value_text_size * 2;
        canvas.drawLine(anchor_x, zigbee_anchor_y, diagram_right, zigbee_anchor_y, paint);
        //绘制wifi信道图坐标轴上的坐标文字
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(value_text_size);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("dBm", anchor_x + 3, diagram_top - value_text_size / 2, paint);//dBm字样
        canvas.drawText("WiFi", diagram_right + 3, anchor_y + value_text_size / 2, paint);//WiFi字样
        canvas.drawText("Zigbee", diagram_right + 3, zigbee_anchor_y + value_text_size / 2, paint);//Zigbee字样
        paint.setTextAlign(Paint.Align.RIGHT);
        int y_step = (anchor_y - diagram_top) / 6;//纵坐标文字和横线
        for (int i = 0; i <= 6; i++) {
            int y = anchor_y - i * y_step;
            paint.setColor(0xccffffff);
            canvas.drawText("" + (i * 10 - 100), anchor_x - 3, y + value_text_size / 2, paint);
            paint.setColor(0x4dffffff);
            canvas.drawLine(anchor_x, y, diagram_right, y, paint);
        }
        paint.setTextAlign(Paint.Align.CENTER);
        int x_step = (diagram_right - anchor_x) / 17;//横坐标文字和刻度
        paint.setColor(0xccffffff);
        for (int i = 0; i < 16; i++) {
            int x = anchor_x + i * x_step;
            //wifi信道图坐标
            if (i >= 2) {
                canvas.drawText("" + (i - 1), x, anchor_y + value_text_size + 2, paint);
                canvas.drawLine(x, anchor_y, x, anchor_y - coordinateStrokeWidth * 3, paint);
            }
            //zigbee信道图坐标
            canvas.drawText("" + (i + 11), x, zigbee_anchor_y + value_text_size + 2, paint);
            canvas.drawLine(x, zigbee_anchor_y, x, zigbee_anchor_y - coordinateStrokeWidth * 3, paint);
        }
        //绘制wifi信号信道/强度的贝塞尔曲线
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xcc3eb9ff);
        paint.setStrokeWidth(1);
        curvePath.setFillType(Path.FillType.WINDING);
        for (ScanResult wifiInfo : wifiInfoList) {
            int dBm = wifiInfo.level;
            int channel = getChannelByFrequency(wifiInfo.frequency);
            if (channel != -1) {//合法2.4G频段信道范围
                curvePath.reset();
                int x = anchor_x + (channel + 1) * x_step;
                int curveHeight = -((-100 - dBm) * y_step / 10);
                int y = anchor_y - 2 * curveHeight;
                curvePath.moveTo(x - 2 * x_step, anchor_y);
                curvePath.quadTo(x, y, x + 2 * x_step, anchor_y);
                LinearGradient curveGradient = new LinearGradient(0, anchor_y - curveHeight, 0, anchor_y, new int[]{0xff10baf7, 0xff24ecd9}, null, Shader.TileMode.CLAMP);
                curvePaint.setShader(curveGradient);
                canvas.drawPath(curvePath, curvePaint);
                canvas.drawText(wifiInfo.SSID, x, anchor_y - curveHeight - value_text_size / 2, paint);
            }
        }
        //绘制zigbee信号信道的直方图
        for (int zigbeeChannel : zigbeeInfoList) {
            if (zigbeeChannel >= 11 && zigbeeChannel <= 26) {
                int x = anchor_x + (zigbeeChannel - 11) * x_step;
                canvas.drawRect(x - x_step, diagram_top - value_text_size, x + x_step, zigbee_anchor_y - coordinateStrokeWidth * 4, zigbeeRectPaint);
            }
        }
    }

    /**
     * 根据频率获得信道
     *
     * @param frequency 频率值，从wifi信息中获取
     * @return 信道值，2.4G频段范围是1到14，-1表示超出范围
     */
    public int getChannelByFrequency(int frequency) {
        int channel;
        if (frequency < 5000) {//2.4G频段
            channel = (frequency - 2407) / 5;
            if (channel < 1 || channel > 14) {
                channel = -1;
            }
        } else {//5G频段
//            channel = (frequency - 5000) / 5;
            channel = -1;
        }
        return channel;
    }

}
