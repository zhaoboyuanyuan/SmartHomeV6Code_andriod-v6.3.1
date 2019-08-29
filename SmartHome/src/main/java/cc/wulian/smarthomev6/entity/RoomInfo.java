package cc.wulian.smarthomev6.entity;

import android.text.TextUtils;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import java.io.Serializable;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;

@Entity
public class RoomInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;

	@NotNull
	private String roomID;
	@NotNull
	private String name;
	private String gwID;
	private String count;
	private int sort;
	@Generated(hash = 1680803103)
	public RoomInfo(Long id, @NotNull String roomID, @NotNull String name,
			String gwID, String count, int sort) {
		this.id = id;
		this.roomID = roomID;
		this.name = name;
		this.gwID = gwID;
		this.count = count;
		this.sort = sort;
	}
	@Generated(hash = 1870725637)
	public RoomInfo() {
	}

	public RoomInfo(RoomBean bean) {
		gwID = bean.gwID;
		roomID = bean.roomID;
		name = bean.name;
		count = bean.name;
	}

	/**
	 * 更新this
	 * 并且返回是否需要更新
	 *
	 * @param bean that bean
	 * @return			true	需要更新
	 * 					false	不需要更新
	 */
	public boolean update(RoomBean bean) {
		boolean isNeedUpdated = false;

		if (!TextUtils.isEmpty(bean.name) && !TextUtils.equals(bean.name, name)) {
			name = bean.name;
			isNeedUpdated = true;
		}
		if (!TextUtils.isEmpty(bean.count) && !TextUtils.equals(bean.count, count)) {
			count = bean.count;
			isNeedUpdated = true;
		}
		return isNeedUpdated;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public Long getId() {
					return this.id;
	}
	public void setId(Long id) {
					this.id = id;
	}
	public String getRoomID() {
					return this.roomID;
	}
	public void setRoomID(String roomID) {
					this.roomID = roomID;
	}
	public String getName() {
					return this.name;
	}
	public void setName(String name) {
					this.name = name;
	}
	public String getGwID() {
					return this.gwID;
	}
	public void setGwID(String gwID) {
					this.gwID = gwID;
	}
	public String getCount() {
					return this.count;
	}
	public void setCount(String count) {
					this.count = count;
	}
}
