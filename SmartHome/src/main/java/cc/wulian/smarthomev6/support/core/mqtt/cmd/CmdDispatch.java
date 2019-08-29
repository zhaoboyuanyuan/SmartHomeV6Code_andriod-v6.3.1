package cc.wulian.smarthomev6.support.core.mqtt.cmd;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 分发cmd
 */

public class CmdDispatch {

    public static CmdHandle getCmdHandle(String cmd) {
        switch (cmd) {
            case "15":
                return new Cmd_15();
            case "500":
                return new Cmd_500();
            case "502":
                return new Cmd_502();
            case "503":
                return new Cmd_503();
            case "504":
                return new Cmd_504();
            case "505":
                return new Cmd_505();
            case "506":
                return new Cmd_506();
            case "507":
                return new Cmd_507();
            case "508":
                return new Cmd_508();
            case "509":
                return new Cmd_509();
            case "511":
                return new Cmd_511();
            case "512":
                return new Cmd_512();
            case "521":
                return new Cmd_521();
            case "513":
                return new Cmd_513();
            case "514":
                return new Cmd_514();
            default:
                return new Cmd_Default();
        }
    }
}
