package cc.wulian.smarthomev6.main.device.device_CG27;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.cloudrtc.service.PjSipService;
import com.cloudrtc.util.UIUtils;

import org.webrtc.videoengine.ViERenderer;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardAddressBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardHostInfo;
import cc.wulian.smarthomev6.support.utils.ToastUtil;


public class VideoTalkFragment extends Fragment implements OnClickListener {

    private boolean usingFrontCamera = true;
    private ImageView ivQr, mUnlock;
    private SurfaceView remoteRender;
    private LinearLayout videoView;
    private DeviceApiUnit deviceApiUnit;

    private DisplayMetrics dm;
    private String communityId;
    private String uc;
    private String mainUc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_screen, null);

        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        initView(view);
        initListener();
        initData();
        return view;
    }

    private void initView(View view) {
        ivQr = (ImageView) view.findViewById(R.id.iv_qr_unlock);
        mUnlock = (ImageView) view.findViewById(R.id.iv_unlock);
        // Create UI controls.
        videoView = (LinearLayout) view.findViewById(R.id.llRemoteView);
        remoteRender = ViERenderer.CreateRenderer(getActivity(), true);
        LayoutParams layoutParams = new LayoutParams(dm.widthPixels, dm.widthPixels * 3 / 5);
        remoteRender.setLayoutParams(layoutParams);
        videoView.addView(remoteRender);
    }

    private void initListener() {
        ivQr.setOnClickListener(this);
        mUnlock.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        startVideo();
    }

    private void initData() {
        deviceApiUnit = new DeviceApiUnit(getActivity());
        communityId = getArguments().getString("communityId");
        uc = getArguments().getString("uc");
        mainUc = getArguments().getString("mainUc");
    }

    private void startVideo() {
        int count = UIUtils.getNumberOfCameras();
        usingFrontCamera = UIUtils.checkCameraAndChoiceBetter();
        int cameraOrientation = PjSipService.instance().GetCameraOrientation(usingFrontCamera ? 1 : 0);
        PjSipService.instance().SetupVideoChannel(320, 240, 15, 256);
        if (count == 1) {
            PjSipService.instance().StartVideoChannel(0, getCameraOrientation(cameraOrientation), null, remoteRender);
        } else {
            PjSipService.instance().StartVideoChannel(usingFrontCamera ? 1 : 0, cameraOrientation, null, remoteRender);
        }
        PjSipService.instance().SetCameraOutputRotation(getCameraOrientation(cameraOrientation));
        PjSipService.instance().SetLoudspeakerStatus(false);
        PjSipService.instance().StartVoiceChannel();
    }

    public void stopVideoChannel() {
        PjSipService.instance().Hangup();
        PjSipService.instance().StopVideoChannel();
    }


    /**
     * 远程开锁
     */
    private void remoteUnlock() {
        deviceApiUnit.entranceGuardUnlock(communityId, mainUc, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                ToastUtil.show(R.string.device_CG27_opening);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    public int getCameraOrientation(int cameraOrientation) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int displatyRotation = display.getRotation();
        int degrees = 0;
        switch (displatyRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;
        if (cameraOrientation > 180) {
            result = (cameraOrientation + degrees) % 360;
        } else {
            result = (cameraOrientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVideoChannel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_qr_unlock:
                CG27QRCodeActivity.start(getActivity(), communityId, uc, mainUc);
                break;
            case R.id.iv_unlock:
                PjSipService.instance().unlock();
                remoteUnlock();
                break;
        }
    }
}
