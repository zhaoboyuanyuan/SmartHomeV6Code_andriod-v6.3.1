package cc.wulian.smarthomev6.main.device.device_CG27;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
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
import cc.wulian.smarthomev6.support.utils.ToastUtil;


public class VideoRingCallFragment extends Fragment implements OnClickListener {

    private boolean usingFrontCamera = true;
    private ImageView mHangUp, mUnlock;
    public Handler mHandler = null;
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
        View view = inflater.inflate(R.layout.fragment_video_ring_call, null);
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        initView(view);
        initListener();
        initData();
        return view;
    }

    private void initView(View view) {
        mHangUp = (ImageView) view.findViewById(R.id.iv_hang_up);
        mUnlock = (ImageView) view.findViewById(R.id.iv_unlock);
        // Create UI controls.
        videoView = (LinearLayout) view.findViewById(R.id.llRemoteView);
        remoteRender = ViERenderer.CreateRenderer(getActivity(), true);
        LayoutParams layoutParams = new LayoutParams(dm.widthPixels, dm.widthPixels * 3 / 5);
        remoteRender.setLayoutParams(layoutParams);
        videoView.addView(remoteRender);
    }

    private void initListener() {
        mHangUp.setOnClickListener(this);
        mUnlock.setOnClickListener(this);
    }

    private void initData() {
        mHandler = new Handler();
        deviceApiUnit = new DeviceApiUnit(getActivity());
        communityId = getArguments().getString("communityId");
        uc = getArguments().getString("uc");
        mainUc = getArguments().getString("mainUc");
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


    private void remoteUnlock() {
        deviceApiUnit.entranceGuardUnlock(communityId, mainUc, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                ToastUtil.show(R.string.device_CG27_opening);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void remoteHangUp() {
        deviceApiUnit.remoteHangUp(communityId, mainUc, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVideoChannel();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hang_up:
                remoteHangUp();
                break;
            case R.id.iv_unlock:
                PjSipService.instance().unlock();
                remoteUnlock();
                mUnlock.setImageResource(R.drawable.icon_lock_open);
                mUnlock.setEnabled(false);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mUnlock.setImageResource(R.drawable.icon_lock_close);
                        mUnlock.setEnabled(true);
                    }
                }, 1500);
                break;
        }
    }
}
