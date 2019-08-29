package cc.wulian.smarthomev6.main.device.device_23.AirConditioning;


import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;
import android.util.SparseArray;

import com.uei.control.AirConFunction;
import com.uei.control.AirConState;
import com.uei.control.AirConWidgetStatus;
import com.uei.control.acstates.StateDataTypes;

import java.util.List;

import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/9/20
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    AirConHelper
 */

public class AirConHelper {

    public static ArraySet<Integer> convertKeysToIdSet(List<AirConFunction> aFunctions) {
        ArraySet<Integer> set = new ArraySet<>();

        for (AirConFunction function : aFunctions) {
            set.add(function.Id);
        }

        return set;
    }

    public static void convertStatusToShow(List<AirConState> states, List<AirConWidgetStatus> status, List<AirConFunction> aFunctions, AirShow show) {
        SparseArray<String> array = new SparseArray<>();
        for (AirConFunction function : aFunctions) {
            array.put(function.Id, function.Name);
        }

//        AirShow show = AirShow.showDefault();
        for (AirConState state : states) {
            if (!state.Enabled) {
                continue;
            }
            WLog.i("AirConditioningMainActivity", "状态: Id - " + state.Id +
                    ", \tStateIndex - " + state.StateIndex +
                    ", \tMaxState - " + state.MaxState +
                    ", \tValue - " + state.Value +
                    ", \tDisplay - " + state.Display +
                    ", \t\tname - " + array.get(state.Id)
            );
            switch (array.get(state.Id)) {
                case "Power":
                    show.power.enable(state.StateIndex);
                    break;
                case "Temp":
                    show.temp.enable(state.Value);
                    break;
                case "Mode":
                    show.mode.enable(state.Value);
                    break;
                case "Speed":
                    show.speed.enable(state.StateIndex);
                    break;
                case "Swing":
                case "Swing V":
                    show.swing_v.enable(state.Value);
                    break;
                case "Swing H":
                    show.swing_h.enable(state.Value);
                    break;
                default:
                    break;
            }

            if (state.StateDataType == StateDataTypes.TEMPERATURE_FAHRENHEIT) {
                show.unit.enable(1);
            }
        }

        WLog.i("AirConditioningMainActivity", "AirShow: " + show);
    }

    public static ArrayMap<String, Integer> convertKeysToIds(List<AirConFunction> aFunctions) {
        ArrayMap<String, Integer> map = new ArrayMap<>();

        for (AirConFunction function : aFunctions) {
            switch (function.Name) {
                case "Power":
                    map.put("4", function.Id);
                    break;
                case "Mode":
                    map.put("1", function.Id);
                    break;
                case "Temperature Up":
                    map.put("2", function.Id);
                    break;
                case "Temperature Down":
                    map.put("3", function.Id);
                    break;
                case "Speed":
                case "Fan Speed":
                    map.put("5", function.Id);
                    break;
                case "Swing":
                case "Swing V":
                    map.put("6", function.Id);
                    break;
                case "Swing Left/Right":
                case "Swing H":
                    map.put("7", function.Id);
                    break;
                default:
                    break;
            }
        }

        return map;
    }
}
