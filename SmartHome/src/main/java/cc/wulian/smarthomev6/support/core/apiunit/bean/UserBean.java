package cc.wulian.smarthomev6.support.core.apiunit.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mamengchao on 2017/3/6 0006.
 * Tips:账户信息
 */

public class UserBean implements Parcelable {
    public String birthday;
    public String os;
    public String phone;
    public String phoneCountryCode;
    public String osVer;
    public String imei;
    public String partnerId;
    public String thirdId;
    public String avatar;
    //    public String password;
    public String mqs;
    public String registDate;
    public String height;
    public String nick;
    public String email;
    public String uId;
    public String gender;
    public String userName;
    public String registIP;
    public int passSet;
    public int regType;//用户的注册类型，目前5是工匠，其他的都是普通用户

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        birthday = in.readString();
        os = in.readString();
        phone = in.readString();
        phoneCountryCode = in.readString();
        osVer = in.readString();
        imei = in.readString();
        partnerId = in.readString();
        thirdId = in.readString();
        avatar = in.readString();
        mqs = in.readString();
        registDate = in.readString();
        height = in.readString();
        nick = in.readString();
        email = in.readString();
        uId = in.readString();
        gender = in.readString();
        userName = in.readString();
        registIP = in.readString();
        passSet = in.readInt();
        regType = in.readInt();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(birthday);
        dest.writeString(os);
        dest.writeString(phone);
        dest.writeString(phoneCountryCode);
        dest.writeString(osVer);
        dest.writeString(imei);
        dest.writeString(partnerId);
        dest.writeString(thirdId);
        dest.writeString(avatar);
        dest.writeString(mqs);
        dest.writeString(registDate);
        dest.writeString(height);
        dest.writeString(nick);
        dest.writeString(email);
        dest.writeString(uId);
        dest.writeString(gender);
        dest.writeString(userName);
        dest.writeString(registIP);
        dest.writeInt(passSet);
        dest.writeInt(regType);
    }
}
