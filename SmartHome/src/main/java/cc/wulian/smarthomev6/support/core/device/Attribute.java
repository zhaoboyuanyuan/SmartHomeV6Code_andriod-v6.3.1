package cc.wulian.smarthomev6.support.core.device;

/**
 * Created by zbl on 2017/7/25.
 */

public class Attribute {
    public int attributeId;
    public String attributeValue;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Attribute{");
        sb.append("attributeId=").append(attributeId);
        sb.append(", attributeValue='").append(attributeValue).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
