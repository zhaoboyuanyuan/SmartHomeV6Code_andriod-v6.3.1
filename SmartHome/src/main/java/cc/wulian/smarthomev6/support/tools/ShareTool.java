package cc.wulian.smarthomev6.support.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/12/6.
 */

public class ShareTool {

    private Activity mActivity;

    public ShareTool(Activity context) {
        this.mActivity = context;
    }

    public void shareImage(File file, SHARE_MEDIA share_media) {
        UMImage image = new UMImage(mActivity, file);
        new ShareAction(mActivity)
                .setPlatform(share_media)//传入平台
                .withText("hello")
                .withMedia(image)
                .setCallback(shareListener)
                .share();
    }

    public void shareImage(String url, SHARE_MEDIA share_media) {
        UMImage image = new UMImage(mActivity, url);
        new ShareAction(mActivity)
                .setPlatform(share_media)//传入平台
                .withText("hello")
                .withMedia(image)
                .setCallback(shareListener)
                .share();
    }

    public void shareUrl(String url, String picUrl, String title, String msg, final boolean isVideo, final SHARE_MEDIA share_media) {
        final UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        web.setDescription(msg);//描述
        if (picUrl != null) {//缩略图
            ImageLoader.getInstance().loadImage(picUrl, new ImageSize(100, 100), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Bitmap bitmap = loadedImage.copy(Bitmap.Config.RGB_565, true);
                    Bitmap playBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.home_view_cateye_play_online);
                    UMImage thumb = null;
                    if (isVideo) {
                        Canvas canvas = new Canvas(bitmap);
                        int size = 50;
                        int x = (bitmap.getWidth() - size) / 2;
                        int y = (bitmap.getHeight() - size) / 2;
                        canvas.drawBitmap(playBitmap, null, new Rect(x, y, x + size, y + size), null);
                    }
                    thumb = new UMImage(mActivity, bitmap);
                    web.setThumb(thumb);
                    new ShareAction(mActivity)
                            .setPlatform(share_media)//传入平台
                            .withText("")
                            .withMedia(web)
                            .setCallback(shareListener)
                            .share();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
            new ShareAction(mActivity)
                    .setPlatform(share_media)//传入平台
                    .withText("")
                    .withMedia(web)
                    .setCallback(shareListener)
                    .share();
        }
    }

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtil.show(R.string.Share_Success);
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            String message = t.getLocalizedMessage();
            if (message != null && message.contains("2008")) {
                ToastUtil.show(R.string.Multi_Platform_No_Aplay);
            } else {
                ToastUtil.show(R.string.Share_Fail);
            }
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.show(R.string.Cancel_Share);

        }
    };

}
