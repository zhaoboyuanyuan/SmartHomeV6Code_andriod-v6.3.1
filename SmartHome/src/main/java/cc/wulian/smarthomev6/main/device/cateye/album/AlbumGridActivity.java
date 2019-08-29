package cc.wulian.smarthomev6.main.device.cateye.album;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;

/**
 * 作者: chao
 * 时间: 2017/5/5
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class AlbumGridActivity extends BaseTitleActivity {
    private static final String TAG = "AlbumGridActivity";
    private AlbumGridFragment albumGridFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_start_config, true);
        albumGridFragment = new AlbumGridFragment();
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, albumGridFragment, TAG);
            ft.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlbumGridFragment fragment = (AlbumGridFragment) getSupportFragmentManager().findFragmentByTag(TAG);
            boolean exit = true;

            if (fragment != null) {

                exit = fragment.onKeyBack();

            }

            if (exit) {

                finish();
            }

            return true;

        }

        return super.onKeyDown(keyCode, event);
    }
}