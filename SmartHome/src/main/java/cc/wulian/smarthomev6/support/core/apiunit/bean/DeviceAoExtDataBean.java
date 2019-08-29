package cc.wulian.smarthomev6.support.core.apiunit.bean;

/**
 * Created by hxc on 2017/7/19.
 * Func:
 * Email:  hxc242313@qq.com
 */

public class DeviceAoExtDataBean {
    private DeviceAoExtDataBean(){

    }

    public String bindDevID;
    public String bindDevtype ;
    public int endpointNumber  ;
    public String epName ;
    public String mode;
    public String name;
    public String sceneID ;
    public String sceneIcon ;
    public String sceneName ;
    public int endPointType;/*按键的模式*/
    public String endpointName;
    public int attributesId ;
    public String attributesValue;

    public static DeviceAoExtDataBean createNewBtn(int epNum){
        DeviceAoExtDataBean bean=new DeviceAoExtDataBean();
        bean.endpointNumber=epNum;
        return bean;
    }

}
