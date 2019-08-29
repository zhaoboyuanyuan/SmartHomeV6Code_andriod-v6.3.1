package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.entity.VoiceControlResultBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 语音控制接口
 */
public class VoicecontrolApiUnit {

    private Context context;

    public VoicecontrolApiUnit(Context context) {
        this.context = context;
    }

    public interface CommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * 控制设备
     */
    public void doPostControlDevice(String action, Device device, String input_text, boolean is_new, String sequence, String session_id, final CommonListener listener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sequence", sequence);
            jsonObject.put("versionid", "1.0");
            jsonObject.put("status", "INTENT");
            jsonObject.put("timestamp", System.currentTimeMillis());

            {
                JSONObject slots = new JSONObject();
                slots.put("bizname", "smarthome_android");
                slots.put("action", action);
                slots.put("deviceName", device.name);
                slots.put("deviceType", device.type);
                slots.put("cmdFlag", "controlDevCmd");
                jsonObject.put("slots", slots);
            }

            jsonObject.put("input_text", input_text);

            {
                JSONObject session = new JSONObject();
                session.put("is_new", is_new);
                session.put("session_id", session_id);
                {
                    JSONObject attributes = new JSONObject();
                    attributes.put("bizname", "smarthome_android");
                    attributes.put("action", action);
                    attributes.put("deviceName", device.name);
                    attributes.put("cmdFlag", "controlDevCmd");
                    session.put("attributes", attributes);
                }
                jsonObject.put("session", session);
            }

            {
                JSONObject application_info = new JSONObject();
                application_info.put("application_name", "smartHome");
                application_info.put("application_id", "p95n336s");
                application_info.put("application_version", "10000");
                jsonObject.put("application_info", application_info);
            }

            {
                JSONObject user = new JSONObject();
                user.put("user_id", ApiConstant.getUserID());
                jsonObject.put("user", user);
            }


            doPostControlCmd(jsonObject, listener);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail(-1, context.getString(R.string.Service_Error));
        }
    }

    /**
     * 控制场景
     */
    public void doPostControlScene(String action, SceneInfo sceneInfo, String input_text, boolean is_new, String sequence, String session_id, final CommonListener listener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sequence", sequence);
            jsonObject.put("versionid", "1.0");
            jsonObject.put("status", "INTENT");
            jsonObject.put("timestamp", System.currentTimeMillis());

            {
                JSONObject slots = new JSONObject();
                slots.put("bizname", "smarthome_android");
                slots.put("action", action);
                slots.put("deviceName", sceneInfo.getName());
//                slots.put("deviceType", device.type);
                slots.put("cmdFlag", "controlSceneCmd");
                jsonObject.put("slots", slots);
            }

            jsonObject.put("input_text", input_text);

            {
                JSONObject session = new JSONObject();
                session.put("is_new", is_new);
                session.put("session_id", session_id);
                {
                    JSONObject attributes = new JSONObject();
                    attributes.put("bizname", "smarthome_android");
                    attributes.put("action", action);
                    attributes.put("deviceName", sceneInfo.getName());
                    attributes.put("cmdFlag", "controlSceneCmd");
                    session.put("attributes", attributes);
                }
                jsonObject.put("session", session);
            }

            {
                JSONObject application_info = new JSONObject();
                application_info.put("application_name", "smartHome");
                application_info.put("application_id", "p95n336s");
                application_info.put("application_version", "10000");
                jsonObject.put("application_info", application_info);
            }

            {
                JSONObject user = new JSONObject();
                user.put("user_id", ApiConstant.getUserID());
                jsonObject.put("user", user);
            }


            doPostControlCmd(jsonObject, listener);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail(-1, context.getString(R.string.Service_Error));
        }
    }

    /**
     * 二次会话
     */
    public void doPostTheSecond(String deviceName, String input_text, boolean is_new, String sequence, String session_id, final CommonListener listener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sequence", sequence);
            jsonObject.put("versionid", "1.0");
            jsonObject.put("status", "INTENT");
            jsonObject.put("timestamp", System.currentTimeMillis());

            {
                JSONObject slots = new JSONObject();
                slots.put("bizname", "smarthome_android");
                slots.put("deviceName", deviceName);
                slots.put("cmdFlag", "controlDevCmd");
                jsonObject.put("slots", slots);
            }

            jsonObject.put("input_text", input_text);

            {
                JSONObject session = new JSONObject();
                session.put("is_new", is_new);
                session.put("session_id", session_id);
                {
                    JSONObject attributes = new JSONObject();
                    attributes.put("bizname", "smarthome_android");
                    attributes.put("deviceName", deviceName);
                    attributes.put("cmdFlag", "controlDevCmd");
                    session.put("attributes", attributes);
                }
                jsonObject.put("session", session);
            }

            {
                JSONObject application_info = new JSONObject();
                application_info.put("application_name", "smartHome");
                application_info.put("application_id", "p95n336s");
                application_info.put("application_version", "10000");
                jsonObject.put("application_info", application_info);
            }

            {
                JSONObject user = new JSONObject();
                user.put("user_id", ApiConstant.getUserID());
                jsonObject.put("user", user);
            }


            doPostControlCmd(jsonObject, listener);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail(-1, context.getString(R.string.Service_Error));
        }
    }

    /**
     * 向云发送语音控制命令
     */
    private void doPostControlCmd(JSONObject jsonObject, final CommonListener listener) {

        String url = ApiConstant.URL_VOICE_CONTROL + "/" + ApiConstant.getAppToken();
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        VoiceControlResultBean bean = JSON.parseObject(result, VoiceControlResultBean.class);
                        if (!TextUtils.isEmpty(bean.sequence)) {
                            listener.onSuccess(bean);
                        } else {
                            String msg = context.getString(R.string.Service_Error);
                            listener.onFail(-1, msg);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }
}
