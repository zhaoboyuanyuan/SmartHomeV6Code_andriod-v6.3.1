package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 赵永健 on 2017/7/13.
 */
public class DoorLockVOCModel extends BaseProcModel {

    private String name;
    private int area;
    private String password;
    private int listIndex;
    private String SearchText;
    private String buttonText;
    private String OrName;


    public String getOrName() {
        return OrName;
    }

    public void setOrName(String orName) {
        OrName = orName;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getSearchText() {
        return SearchText;
    }

    public void setSearchText(String searchText) {
        SearchText = searchText;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    /**
     * 构造器
     *
     * @param actions - 动作集{@link Arrays}
     */
    public DoorLockVOCModel(int[] actions) {
        super(actions);
    }
}
