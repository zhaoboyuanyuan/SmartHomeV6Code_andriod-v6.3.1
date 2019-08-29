package cc.wulian.smarthomev6.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Veev on 2017/4/20
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */
@Entity
public class H5Storage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;

    private String room;
    private String key;
    private String value;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("H5Storage{");
        sb.append("id=").append(id);
        sb.append(", room='").append(room).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public H5Storage() {
    }

    public H5Storage(String room, String key) {
        this.room = room;
        this.key = key;
    }

    public H5Storage(String room, String key, String value) {
        this.room = room;
        this.key = key;
        this.value = value;
    }

    @Generated(hash = 778266625)
    public H5Storage(Long id, String room, String key, String value) {
        this.id = id;
        this.room = room;
        this.key = key;
        this.value = value;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
