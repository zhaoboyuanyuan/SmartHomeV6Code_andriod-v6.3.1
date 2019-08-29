package cc.wulian.smarthomev6.support.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

/**
 * Created by 王伟 on 2017/3/16
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: Bitmap 工具类
 */
public class BitmapUtil {

    /**
     * 改变 bitmap 的颜色
     * 图片空白区域必须为透明
     * 改变后的 bitmap 是纯色
     *
     * @param src   源图片
     * @param color 需要改变的颜色
     * @return 现图片
     */
    public static Bitmap changeBitmapColor(Bitmap src, @ColorInt int color) {
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRect(0, 0, src.getWidth(), src.getHeight(), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

    public static Bitmap getA(Bitmap src, @ColorInt int color) {
        int w = src.getWidth();
        int h = src.getHeight();
        int side = w <= h ? w : h;
        Bitmap bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        canvas.drawCircle(side / 2, side / 2, side / 2, paint);
        Rect rectSrc = new Rect(w < h ? 0 : (h - w) / 2,
                h < w ? 0 : (w - h / 2),
                w < h ? w : ((h - w) / 2 + w),
                h < w ? h : ((w - h)) / 2 + h);
        RectF rectDst = new RectF(side / 6, h / 6, side / 6 * 5, h / 6 * 5);
        canvas.drawBitmap(src, rectSrc, rectDst, paint);
        return bitmap;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitMap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        if (needRecycle)
            bitMap.recycle();
        return newBitMap;
    }

    public static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null)
            return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static boolean saveBitmap(Bitmap bitmap, int quality, File file) {
        if (bitmap == null)
            return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        int h = options.outHeight;
        int w = options.outWidth;
        int inSampleSize;
        if (reqWidth >= 1f) {
            reqWidth = 0.5f;
        }
        float ratioW = w * reqWidth;
        float ratioH = h * reqWidth;
        inSampleSize = (int) Math.min(ratioH, ratioW);
        inSampleSize = Math.max(1, inSampleSize);
        return inSampleSize;
    }

    /**
     * @param filePath  源文件
     * @param reqWidth  百分比
     * @param reqHeight 百分比
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, float reqWidth, float reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    public static SoftReference<Bitmap> getBitmap(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                ConfigUtil.SP_SNAPSHOT, Activity.MODE_PRIVATE);
        String base64str = sp.getString(key + "_snapshot_or_avatar", "");
        if (!TextUtils.isEmpty(base64str)) {
            byte[] data = Base64.decode(base64str, Base64.DEFAULT);
            BitmapFactory.Options opts=new BitmapFactory.Options();
            //为位图设置100K的缓存
            opts.inTempStorage = new byte[100 * 1024];
            //设置可回收
            opts.inPurgeable = true;
            return new SoftReference<Bitmap> (BitmapFactory.decodeByteArray(data, 0, data.length, opts));
        }
        return new SoftReference<Bitmap>(null);
    }
}
