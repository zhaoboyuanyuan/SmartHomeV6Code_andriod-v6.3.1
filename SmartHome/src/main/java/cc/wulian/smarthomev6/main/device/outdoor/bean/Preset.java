package cc.wulian.smarthomev6.main.device.outdoor.bean;


import android.graphics.Bitmap;

public class Preset {
    private long id;
    private String device_id;
    private int preset;
    private String desc;
    private String picture="";
    private Bitmap bitmap;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getPreset() {

        return preset;
    }

    public void setPreset(int preset) {
        this.preset = preset;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "Preset{" +
                "id=" + id +
                ", device_id='" + device_id + '\'' +
                ", preset=" + preset +
                ", desc='" + desc + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}
