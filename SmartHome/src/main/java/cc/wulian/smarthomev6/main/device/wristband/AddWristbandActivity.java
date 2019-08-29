package cc.wulian.smarthomev6.main.device.wristband;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.UUID;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.device.wristband.utils.ScanRecorder;
import cc.wulian.smarthomev6.support.customview.AnimationRing;
import cc.wulian.smarthomev6.support.event.WristBandEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class AddWristbandActivity extends BaseActivity implements View.OnClickListener{

    public static UUID UUID_BEACON = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    private View root_view;
    private View v_open_btn;
    private View open_buletooth_layout;
    private View ring_layout;
    private ImageView ring_icon;
    private TextView top_state_tip_text;
    private TextView top_tip_text;
    private TextView no_vibrate_btn;
    private WLDialog mWLDialog;
    private AnimationRing connectingBar;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private ScanCallback mScanCallback;
    private boolean mScanning;
    private Handler mHandler;
    private BroadcastReceiver mReceiver;
    private BroadcastReceiver bluetoothConnectReceiver;
    private static final int REQUEST_ENABLE_BT = 1;
    // 60秒后停止查找搜索.
    private static final int SCAN_PERIOD = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wristband);
        EventBus.getDefault().register(this);
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            WLog.e("luzx", "不支持ble 蓝牙");
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }else{
                checkBleDevice();
            }
        }else{
            checkBleDevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[0])){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkBleDevice();
            } else {
                // Permission Denied
            }
        }
    }

    /**
     * 判断是否支持蓝牙，并打开蓝牙
     * 获取到BluetoothAdapter之后，还需要判断是否支持蓝牙，以及蓝牙是否打开。
     * 如果没打开，需要让用户打开蓝牙：
     */
    public void checkBleDevice() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                scanLeDevice(true);
            }
        } else {
            WLog.i("luzx", "手机不支持蓝牙");
        }
    }

    @Override
    protected void onDestroy() {
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if(bluetoothConnectReceiver != null){
            unregisterReceiver(bluetoothConnectReceiver);
            bluetoothConnectReceiver = null;
        }
        EventBus.getDefault().unregister(this);
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                scanLeDevice(false);
            }
        }
        super.onDestroy();
    }

    class BluetoothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == BluetoothAdapter.ACTION_STATE_CHANGED){
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch(blueState){
                    case BluetoothAdapter.STATE_TURNING_ON:
                        WLog.e("onReceive---------STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        WLog.e("onReceive---------STATE_ON");
                        scanLeDevice(true);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        WLog.e("onReceive---------STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        WLog.e("onReceive---------STATE_OFF");
                        break;
                }
            }
        }
    }
    
    public void initView(){
        root_view = findViewById(R.id.root_view);
        v_open_btn = findViewById(R.id.open_btn);
        ring_layout = findViewById(R.id.ring_layout);
        open_buletooth_layout = findViewById(R.id.open_buletooth_layout);
        top_state_tip_text = (TextView) findViewById(R.id.top_state_tip_text);
        top_tip_text = (TextView) findViewById(R.id.top_tip_text);
        ring_icon = (ImageView) findViewById(R.id.ring_icon);
        no_vibrate_btn = (TextView) findViewById(R.id.no_vibrate_btn);
        connectingBar = (AnimationRing) findViewById(R.id.connecting_bar);
        v_open_btn.setOnClickListener(this);
        no_vibrate_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open_btn:
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                if(mReceiver == null){
                    mReceiver = new BluetoothBroadcastReceiver();
                    registerReceiver(mReceiver, filter);
                }
                break;
            case R.id.no_vibrate_btn:
                mWLDialog = DialogUtil.showConfigOrBindDialog(this, getString(R.string.Band_Notvibrated_tips),
                        getString(R.string.Common_Retry), getString(R.string.Band_Bluetooth_Notbunding), new WLDialog.MessageListener(){
                            @Override
                            public void onClickPositive(View var1, String msg) {
                                scanLeDevice(true);
                            }

                            @Override
                            public void onClickNegative(View var1) {
                                mWLDialog.dismiss();
                                finish();
                            }
                        });
                mWLDialog.show();
                break;
        }
    }

    private void scanLeDevice(boolean enable) {
        if(enable){
            root_view.setBackgroundResource(R.color.colorPrimary);
            open_buletooth_layout.setVisibility(View.GONE);
            ring_layout.setVisibility(View.VISIBLE);
            top_state_tip_text.setTextColor(0xffffffff);
            top_state_tip_text.setText(R.string.Band_Bluetooth_Searching);
            top_tip_text.setVisibility(View.VISIBLE);
            top_tip_text.setText(R.string.Band_Phone_Distance);
            ring_icon.setImageResource(R.drawable.pic_wristband);
            connectingBar.setTimeout(SCAN_PERIOD);
            connectingBar.setState(AnimationRing.WAITING);
            connectingBar.setAnimatorListener(new AnimationRing.AnimatorListener() {
                @Override
                public void onTimeout() {
                    no_vibrate_btn.setVisibility(View.VISIBLE);
                    top_state_tip_text.setText(R.string.Band_Click);
                    ring_icon.setImageResource(R.drawable.pic_wristband_found);
                    top_tip_text.setText(R.string.Band_Click_tips);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mBluetoothLeScanner.stopScan(mScanCallback);
                    }else{
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    mScanning = false;
                }

                @Override
                public void onEnd() {

                }
            });
        }
        if(mHandler == null){
            mHandler = new Handler();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(mScanCallback == null) {
                mScanCallback = new ScanCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        if (result.getScanRecord() == null) {
                            return;
                        }
                        Log.i("luzx", "DeviceName:" + result.getScanRecord().getDeviceName());
                        List<ParcelUuid> parcelUuids = result.getScanRecord().getServiceUuids();
                        if (parcelUuids != null) {
                            for (ParcelUuid p : parcelUuids) {
                                Log.i("luzx", "Uuid:" + p.getUuid());
                                Log.i("luzx", "address:" + result.getDevice().getAddress());
                                if (p.getUuid().compareTo(UUID_BEACON) == 0) {
//                                    IntentFilter filter = new IntentFilter();
//                                    filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
//                                    if(bluetoothConnectReceiver == null){
//                                        bluetoothConnectReceiver = new BluetoothConnectActivityReceiver();
//                                        registerReceiver(bluetoothConnectReceiver, filter);
//                                    }
//                                    result.getDevice().setPin("1234".getBytes());
//                                    result.getDevice().createBond();
//                                    try {
//                                        ClsUtils.cancelPairingUserInput(result.getDevice().getClass(), result.getDevice());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
                                    Intent intent = new Intent(AddWristbandActivity.this, BluetoothLeService.class);
                                    intent.putExtra("address", result.getDevice().getAddress());
                                    startService(intent);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        mBluetoothLeScanner.stopScan(mScanCallback);
                                    }else{
                                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                    }
                                    mScanning = false;
                                    break;
                                }
                            }
                        }
                    }
                };
            }
            if(mBluetoothLeScanner == null){
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }
            if (enable) {
                mScanning = true;
                mBluetoothLeScanner.startScan(mScanCallback);
            } else {
                mScanning = false;
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }else{
            if(mLeScanCallback == null) {
                mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        // 解析广播数据
                        ScanRecorder scanRecorder = ScanRecorder.parseFromBytes(scanRecord);
                        Log.i("luzx", "DeviceName:" + scanRecorder.getDeviceName());
                        List<ParcelUuid> parcelUuids = scanRecorder.getServiceUuids();
                        if (parcelUuids != null) {
                            for (ParcelUuid p : parcelUuids) {
                                Log.i("luzx", "Uuid:" + p.getUuid());
                                Log.i("luzx", "address:" + device.getAddress());
                                if (p.getUuid().compareTo(UUID_BEACON) == 0) {
                                    Intent intent = new Intent(AddWristbandActivity.this, BluetoothLeService.class);
                                    intent.putExtra("address", device.getAddress());
                                    startService(intent);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        mBluetoothLeScanner.stopScan(mScanCallback);
                                    }else{
                                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                    }
                                    mScanning = false;
                                    break;
                                }
                            }
                        }
                    }
                };
            }
            if (enable) {
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInfoReport(WristBandEvent event) {
        startActivity(new Intent(this, WristbandDetailActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public class BluetoothConnectActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    //(三星)4.3版本测试手机还是会弹出用户交互页面(闪一下)，如果不注释掉下面这句页面不会取消但可以配对成功。(中兴，魅族4(Flyme 6))5.1版本手机两中情况下都正常
                    //ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    //3.调用setPin方法进行配对...
//                    boolean ret = ClsUtils.setPin(mBluetoothDevice.getClass(), mBluetoothDevice, "你需要设置的PIN码");
                    mBluetoothDevice.setPin("1234".getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
