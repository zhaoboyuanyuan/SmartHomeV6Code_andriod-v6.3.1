package cc.wulian.smarthomev6.support.tools.skin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;

/**
 * Created by zbl on 2017/10/13.
 * 皮肤资源管理
 */

public class SkinManager {

    private static SkinManager instance;
    private Context context;
    private String skinPath;
    private String savedSkinNameForColor;
    private HashMap<String, String> colorMap = new HashMap<>();

    public interface ZipCallback {
        void onFinish();

        void onFail();
    }


    private   SkinManager(Context context) {
        this.context = context;
        skinPath = FileUtil.getSkinPath();
    }

    public static SkinManager getInstance(Context context) {
        if (instance == null) {
            instance = new SkinManager(context);
        }
        return instance;
    }

    /**
     * 获取皮肤中的图片资源
     *
     * @param drawableName SkinResourceKey中的值
     * @return
     */
    public Bitmap getBitmap(String drawableName) {
        String skin = Preference.getPreferences().getCurrentSkin();
        File file = new File(skinPath + "/" + skin + "/" + drawableName);
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }

    /**
     * 获取皮肤中的图片资源
     *
     * @param drawableName SkinResourceKey中的值
     * @return
     */
    public Drawable getDrawable(String drawableName) {
        Bitmap bitmap = getBitmap(drawableName);
        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            return drawable;
        }
        return null;
    }

    public void setBackground(View view, String drawableName) {
        if (view != null) {
            Drawable drawable = getDrawable(drawableName);
            if (drawable != null) {
                view.setBackground(drawable);
            }
        }
    }

    public void setImageViewDrawable(ImageView view, String drawableName) {
        if (view != null) {
            Drawable drawable = getDrawable(drawableName);
            if (drawable != null) {
                view.setImageDrawable(drawable);
            }
        }
    }

    /**
     * 给TextView或者Button按指定的颜色设置空心样式
     */
    public void setTextButtonColorAndBackground(TextView view, String colorName) {
        if (view != null) {
            Integer color = getColor(colorName);
            if (color != null) {
                view.setTextColor(color);
                float radius = DisplayUtil.dip2Pix(view.getContext(), 5);
                float[] outerR = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
                RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
                ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
                shapeDrawable.getPaint().setColor(color);
                shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                shapeDrawable.getPaint().setStrokeWidth(DisplayUtil.dip2Pix(view.getContext(), 2));
                view.setBackground(shapeDrawable);
            }
        }
    }

    public void setTextColor(TextView view, String colorName) {
        if (view != null) {
            Integer color = getColor(colorName);
            if (color != null) {
                view.setTextColor(color);
            }
        }
    }


    /**
     * 获取皮肤中的颜色资源
     *
     * @param colorName SkinResourceKey中的值
     * @return 颜色int值
     */
    public Integer getColor(String colorName) {
        String skin = Preference.getPreferences().getCurrentSkin();
        if (!TextUtils.equals(skin, savedSkinNameForColor)) {
            reloadColor(skin);
            savedSkinNameForColor = skin;
        }
        String colorString = colorMap.get(colorName);
        if (colorString != null) {
            try {
                int color = Color.parseColor(colorString);
                return color;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取皮肤中的颜色资源
     *
     * @param colorName SkinResourceKey中的值
     * @return 颜色字符串，如"#2d80c1"
     */
    public String getColorString(String colorName) {
        String skin = Preference.getPreferences().getCurrentSkin();
        if (!TextUtils.equals(skin, savedSkinNameForColor)) {
            reloadColor(skin);
            savedSkinNameForColor = skin;
        }
        return colorMap.get(colorName);
    }

    private void reloadColor(String skin) {
        colorMap.clear();
        String jsonString = FileUtil.readFileToString(skinPath + "/" + skin + "/color.json");
        if (jsonString != null) {
            try {
                JSONObject jsonObject = JSON.parseObject(jsonString);
                for (String key : jsonObject.keySet()) {
                    String colorString = jsonObject.getString(key);
                    colorMap.put(key, colorString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断皮肤资源包是否存在
     *
     * @param skinId
     * @return
     */
    public static boolean isSkinPackageExists(String skinId) {
        String skinPackagePath = FileUtil.getSkinPath() + "/" + skinId + ".zip";
        File skinPackageFile = new File(skinPackagePath);
        return skinPackageFile.exists();
    }

    /**
     * 解压皮肤包中的资源到皮肤目录
     *
     * @param skinId
     * @param callback
     */
    public static void unzipSkinPackage(String skinId, final ZipCallback callback) {
        AsyncTask<String, String, String> unzipTask = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String skin = params[0];
                String skinPackagePath = FileUtil.getSkinPath() + "/" + skin + ".zip";
                File skinPackageFile = new File(skinPackagePath);
                if (skinPackageFile.exists()) {//解压H5资源
                    String skinFolderPath = FileUtil.getSkinPath() + "/" + skin;
                    FileUtil.upZipFile(skinPackagePath, skinFolderPath + "/");
                    File h5SkinFile = new File(skinFolderPath + "/" + "skinSource.zip");
                    if (h5SkinFile.exists()) {
                        FileUtil.upZipFile(h5SkinFile.getAbsolutePath(), FileUtil.getHtmlResourcesPath() + "/");
                    }
                    return "";
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    callback.onFail();
                } else {
                    callback.onFinish();
                }
            }
        };
        unzipTask.execute(skinId);
    }
}
