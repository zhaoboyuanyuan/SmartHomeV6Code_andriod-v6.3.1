package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;

/**
 * Created by syf on 2017/2/14.
 */
public class DeviceListAdapter extends WLBaseAdapter<Device> {
    private LayoutInflater mInflater;

    public DeviceListAdapter(Context context, List<Device> data) {
        super(context, data);
        this.mInflater = LayoutInflater.from(context);
    }

    public void sort(List<Device> data) {
        if (data == null) {
            return;
        }
        Collections.sort(data, new Comparator<Device>() {
            @Override
            public int compare(Device o1, Device o2) {
//                int result = Trans2PinYin
//                        .trans2PinYin(o1.getName().trim())
//                        .toLowerCase()
//                        .compareTo(
//                                Trans2PinYin.trans2PinYin(o2.getName().trim())
//                                        .toLowerCase());
                int result = o1.sortStr.compareTo(o2.sortStr);
                return result;
            }
        });
    }

    public boolean isSwith(String type) {
        boolean flag = false;
        if (TextUtils.equals(type, "Am")) {
            flag = true;
        } else if (TextUtils.equals(type, "An")) {
            flag = true;
        } else if (TextUtils.equals(type, "Ao")) {
            flag = true;
        } else if (TextUtils.equals(type, "61")) {
            flag = true;
        } else if (TextUtils.equals(type, "62")) {
            flag = true;
        } else if (TextUtils.equals(type, "63")) {
            flag = true;
        } else if (TextUtils.equals(type, "Aj")) {
            flag = true;
        } else if (TextUtils.equals(type, "At")) {
            flag = true;
        } else if (TextUtils.equals(type, "50")) {
            flag = true;
        } else if (TextUtils.equals(type, "77")) {
            flag = true;
        } else if (TextUtils.equals(type, "16")) {
            flag = true;
        } else if (TextUtils.equals(type, "Bt")) {
            flag = true;
        } else if (TextUtils.equals(type, "Bu")) {
            flag = true;
        } else if (TextUtils.equals(type, "Bv")) {
            flag = true;
        } else if (TextUtils.equals(type, "Bw")) {
            flag = true;
        } else if (TextUtils.equals(type, "Ai")) {
            flag = true;
        } else if (TextUtils.equals(type, "12")) {
            flag = true;
        } else if (TextUtils.equals(type, "13")) {
            flag = true;
        } else if (TextUtils.equals(type, "Cl")) {
            flag = true;
        } else if (TextUtils.equals(type, "Cm")) {
            flag = true;
        } else if (TextUtils.equals(type, "Cn")) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public ConcurrentHashMap<Integer, String> getSwithStatus(final Device device) {
        final ConcurrentHashMap<Integer, String> swithStatus = new ConcurrentHashMap<Integer, String>();

        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {

                int endpointNumber = endpoint.endpointNumber;
                int endpointType = endpoint.endpointType;
                String attributeValue = attribute.attributeValue;
                int attributeId = attribute.attributeId;

                if (TextUtils.isEmpty(attributeValue)) {
                    attributeValue = "empty";
                }

                if (TextUtils.equals(device.type, "Am") || TextUtils.equals(device.type, "An")
                        || TextUtils.equals(device.type, "Ao") || TextUtils.equals(device.type, "Bu")
                        || TextUtils.equals(device.type, "Bv") || TextUtils.equals(device.type, "Bw")
                        || TextUtils.equals(device.type, "Cl") || TextUtils.equals(device.type, "Cm")
                        || TextUtils.equals(device.type, "Cn")) {
                    if (endpointType == 2 && cluster.clusterId == 6) {
                        String status = cluster.clusterId + "," + attributeValue;
                        if (attributeId == 0) {
                            swithStatus.put(endpointNumber, status);
                        }
                    } else {
                        if (!swithStatus.containsKey(endpointNumber)) {
                            if (endpointType != 2) {
                                String status = "0," + attributeValue;
                                swithStatus.put(endpointNumber, status);
                            }
                        }
                    }
                } else {
                    String status = cluster.clusterId + "," + attributeValue;
                    if ((attributeId == 0 && cluster.clusterId == 6) || (attributeId == 17 && cluster.clusterId == 8)) {
                        swithStatus.put(endpointNumber, status);
                    }
                }

            }
        });

        return swithStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_device_list, null);
            holder.ivDeviceIcon = (ImageView) convertView.findViewById(R.id.iv_device_icon);
            holder.tvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            holder.tvDeviceType = (TextView) convertView.findViewById(R.id.tv_device_type);
            holder.tvDeviceArea = (TextView) convertView.findViewById(R.id.tv_device_area);
            holder.llStatus = (LinearLayout) convertView.findViewById(R.id.status_layout);
            holder.ivSwithStatu1 = (ImageView) convertView.findViewById(R.id.swith_statu1);
            holder.ivSwithStatu2 = (ImageView) convertView.findViewById(R.id.swith_statu2);
            holder.ivSwithStatu3 = (ImageView) convertView.findViewById(R.id.swith_statu3);
            holder.tv_device_desc = (TextView) convertView.findViewById(R.id.tv_device_desc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Device device = mData.get(position);
        holder.ivDeviceIcon.setImageResource(DeviceInfoDictionary.getIconByType(device.type));
        holder.tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        holder.llStatus.setVisibility(View.GONE);
        if (!device.isZigbee()) {
            holder.tvDeviceArea.setVisibility(View.GONE);
            holder.llStatus.setVisibility(View.GONE);
        } else {
            String areaName = ((MainApplication) mContext.getApplicationContext()).getRoomCache().getRoomName(device.roomID);
            if (TextUtils.isEmpty(areaName)) {
                areaName = device.roomName;
            }
            holder.tvDeviceArea.setVisibility(View.VISIBLE);
            holder.tvDeviceArea.setText("[" + areaName + "]");

            try {
                //指定类型的开关设备添加设备状态
                if (device.isOnLine()) {
                    if (isSwith(device.type)) {

                        ConcurrentHashMap<Integer, String> swithStatus = null;

                        swithStatus = getSwithStatus(device);

                        holder.llStatus.setVisibility(View.VISIBLE);
                        for (HashMap.Entry<Integer, String> entry : swithStatus.entrySet()) {
                            int endpointNumber = entry.getKey();
                            String cluId = entry.getValue().split(",")[0];
                            String statu = entry.getValue().split(",")[1];

                            if (endpointNumber == 1) {
                                if (TextUtils.equals(cluId, "6")) {
                                    if (TextUtils.equals(statu, "1")) {
                                        holder.ivSwithStatu1.setImageResource(R.drawable.status_on);
                                    } else if (TextUtils.equals(statu, "0")) {
                                        holder.ivSwithStatu1.setImageResource(R.drawable.status_off);
                                    } else {
                                        holder.ivSwithStatu1.setImageResource(R.drawable.status_off);
                                    }
                                } else if (TextUtils.equals(cluId, "8")) {
                                    if (Integer.valueOf(statu) == 0) {
                                        holder.ivSwithStatu1.setImageResource(R.drawable.status_off);
                                    } else {
                                        holder.ivSwithStatu1.setImageResource(R.drawable.status_on);
                                    }
                                } else {
                                    holder.ivSwithStatu1.setImageResource(R.drawable.status_other);
                                }

                            } else if (endpointNumber == 2) {
                                if (TextUtils.equals(cluId, "6")) {
                                    if (TextUtils.equals(statu, "1")) {
                                        holder.ivSwithStatu2.setImageResource(R.drawable.status_on);
                                    } else if (TextUtils.equals(statu, "0")) {
                                        holder.ivSwithStatu2.setImageResource(R.drawable.status_off);
                                    } else {
                                        holder.ivSwithStatu2.setImageResource(R.drawable.status_off);
                                    }
                                } else if (TextUtils.equals(cluId, "8")) {
                                    if (Integer.valueOf(statu) == 0) {
                                        holder.ivSwithStatu2.setImageResource(R.drawable.status_off);
                                    } else {
                                        holder.ivSwithStatu2.setImageResource(R.drawable.status_on);
                                    }
                                } else {
                                    holder.ivSwithStatu2.setImageResource(R.drawable.status_other);
                                }

                            } else if (endpointNumber == 3) {
                                if (TextUtils.equals(cluId, "6")) {
                                    if (TextUtils.equals(statu, "1")) {
                                        holder.ivSwithStatu3.setImageResource(R.drawable.status_on);
                                    } else if (TextUtils.equals(statu, "0")) {
                                        holder.ivSwithStatu3.setImageResource(R.drawable.status_off);
                                    } else {
                                        holder.ivSwithStatu3.setImageResource(R.drawable.status_off);
                                    }
                                } else {
                                    holder.ivSwithStatu3.setImageResource(R.drawable.status_other);
                                }

                            }
                        }

                        //设置需要显示的开关
                        if (TextUtils.equals(device.type, "An") || TextUtils.equals(device.type, "62")
                                || TextUtils.equals(device.type, "At") || TextUtils.equals(device.type, "Bv")
                                || TextUtils.equals(device.type, "13")|| TextUtils.equals(device.type, "Cm")) {
                            holder.ivSwithStatu1.setVisibility(View.VISIBLE);
                            holder.ivSwithStatu2.setVisibility(View.VISIBLE);
                            holder.ivSwithStatu3.setVisibility(View.GONE);
                        } else if (TextUtils.equals(device.type, "Ao") || TextUtils.equals(device.type, "63")
                                || TextUtils.equals(device.type, "Bw")|| TextUtils.equals(device.type, "Cn")) {
                            holder.ivSwithStatu1.setVisibility(View.VISIBLE);
                            holder.ivSwithStatu2.setVisibility(View.VISIBLE);
                            holder.ivSwithStatu3.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivSwithStatu1.setVisibility(View.VISIBLE);
                            holder.ivSwithStatu2.setVisibility(View.GONE);
                            holder.ivSwithStatu3.setVisibility(View.GONE);
                        }
                    } else {
                        holder.llStatus.setVisibility(View.GONE);
                    }
                } else {
                    holder.llStatus.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                holder.llStatus.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }

        // 第一次上线
        if (device.isOnLine()) {
            holder.tvDeviceType.setTextColor(mContext.getResources().getColor(R.color.v6_text_green_light));
            if (TextUtils.equals(device.type, "CMICA4")) {//小物摄像机需隐藏状态
                holder.tvDeviceType.setText("");
            } else {
                holder.tvDeviceType.setText(mContext.getString(R.string.Device_Online));
            }
            holder.tvDeviceName.setTextColor(Color.BLACK);
        } else {
            holder.tvDeviceType.setTextColor(mContext.getResources().getColor(R.color.v6_text_gray_light));
            holder.tvDeviceType.setText(mContext.getString(R.string.Device_Offline));
            holder.tvDeviceName.setTextColor(mContext.getResources().getColor(R.color.v6_text_gray_light));
        }

        if (device.isShared && !device.isZigbee()) {
            holder.tv_device_desc.setVisibility(View.VISIBLE);
            holder.tv_device_desc.setText(String.format(MainApplication.getApplication().getString(R.string.Share_Source), device.shareSource));
        } else {
            holder.tv_device_desc.setVisibility(View.GONE);
        }

        return convertView;
    }

    public final class ViewHolder {
        public ImageView ivDeviceIcon;
        public TextView tvDeviceName;
        public TextView tvDeviceType;
        public TextView tvDeviceArea;
        public LinearLayout llStatus;
        public ImageView ivSwithStatu1;
        public ImageView ivSwithStatu2;
        public ImageView ivSwithStatu3;
        public TextView tv_device_desc;
    }
}
