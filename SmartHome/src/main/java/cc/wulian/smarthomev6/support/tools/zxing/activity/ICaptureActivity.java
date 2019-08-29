package cc.wulian.smarthomev6.support.tools.zxing.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;

import com.google.zxing.Result;

import cc.wulian.smarthomev6.support.tools.zxing.camera.CameraManager;

/**
 * Created by zbl on 2017/5/9.
 * 扫码Activity通用接口
 */

public interface ICaptureActivity {
    CameraManager getCameraManager();

    Rect getCropRect();

    Handler getHandler();

    void handleDecode(Result rawResult, Bundle bundle);

    void setResult(int resultCode, Intent data);

    void finish();
}
