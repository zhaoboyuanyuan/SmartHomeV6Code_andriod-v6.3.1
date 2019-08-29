package cc.wulian.smarthomev6.main.device.device_23.AirConditioning;

/**
 * Created by Veev on 2017/9/20
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    AirShow
 */

public class AirShow {
    /**
     * 电源
     *      0 关
     *      1 开
     */
    public AirItem power;
    /**
     * 模式
     *      0 制冷
     *      1 制热
     *      2 自动
     *      3 除湿
     *      4 送风
     *      head    5
     *      fan     3
     *      auto    0
     *      cool    1
     *      dry     2
     */
    public AirItem mode;
    /**
     * 温度
     */
    public AirItem temp;
    /**
     * 温度单位
     *      0 摄氏度
     *      1 华氏度
     */
    public AirItem unit;
    /**
     * 风速
     *      2 ~ 6
     */
    public AirItem speed;
    /**
     * 扫风 横向
     */
    public AirItem swing_v;
    /**
     * 扫风 纵向
     */
    public AirItem swing_h;

    private AirShow() {}

    public static AirShow showDefault () {
        AirShow show = new AirShow();
        show.power = AirItem.enabled();
        show.mode = AirItem.enabled();
        show.temp = AirItem.enabled();
        show.unit = AirItem.enabled();
        show.speed = AirItem.enabled();
        show.swing_v = AirItem.enabled();
        show.swing_h = AirItem.enabled();
        return show;
    }

    public static class AirItem {
        public int value;
        public boolean enable;

        private AirItem () {}

        private AirItem(int value, boolean enable) {
            this.value = value;
            this.enable = enable;
        }

        public static AirItem disabled() {
            return new AirItem(0, false);
        }

        public static AirItem enabled() {
            return new AirItem(0, true);
        }

        public static AirItem enabled(int value) {
            return new AirItem(value, true);
        }

        public void enable(int value) {
            this.value = value;
            this.enable = true;
        }

        @Override
        public String toString() {
            return "" + value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AirItem item = (AirItem) o;

            if (value != item.value) return false;
            return enable == item.enable;

        }

        @Override
        public int hashCode() {
            int result = value;
            result = 31 * result + (enable ? 1 : 0);
            return result;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AirShow{");
        sb.append("power=").append(power);
        sb.append(", mode=").append(mode);
        sb.append(", temp=").append(temp);
        sb.append(", unit=").append(unit);
        sb.append(", speed=").append(speed);
        sb.append(", swing_v=").append(swing_v);
        sb.append(", swing_h=").append(swing_h);
        sb.append('}');
        return sb.toString();
    }
}
