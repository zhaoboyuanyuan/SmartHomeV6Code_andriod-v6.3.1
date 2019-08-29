package cc.wulian.smarthomev6.support.tools;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import cc.wulian.smarthomev6.R;

/**
 * Created by zbl on 2017/4/10.
 */

public class ImageLoaderTool {

    private static DisplayImageOptions userAvatarOptions;
    private static DisplayImageOptions adOptions;
    private static DisplayImageOptions snapshotOptions;

    public static DisplayImageOptions getUserAvatarOptions() {
        if (userAvatarOptions == null) {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            userAvatarOptions = builder
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageForEmptyUri(R.drawable.icon_head)
                    .showImageOnLoading(R.drawable.icon_head)
                    .showImageOnFail(R.drawable.icon_head)
                    .build();
        }
        return userAvatarOptions;
    }

    public static DisplayImageOptions getAdOptions() {
        if (adOptions == null) {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            adOptions = builder
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageForEmptyUri(R.drawable.icon_stub)
                    .showImageOnLoading(R.drawable.icon_stub)
                    .showImageOnFail(R.drawable.icon_error)
                    .build();
        }
        return adOptions;
    }


    public static DisplayImageOptions getSnapshotOptions() {
        if (snapshotOptions == null) {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            snapshotOptions = builder
                    .cacheInMemory(false)
                    .cacheOnDisk(false)
                    .build();
        }
        return snapshotOptions;
    }
}
