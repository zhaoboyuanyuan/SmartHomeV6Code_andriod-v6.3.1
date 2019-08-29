package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MessageNewInfo;
import cc.wulian.smarthomev6.entity.VisitRecordEntity;
import cc.wulian.smarthomev6.main.device.cateye.CateyePicActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.tools.ImageLoaderTool;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/5/10
 * 描述: 访客记录adapter
 * 联系方式: 805901025@qq.com
 */

public class CateyeVisitorAdapter extends RecyclerView.Adapter<CateyeVisitorAdapter.ItemHolder> {

    private Context context;
    private String deviceId;
    private String cameraId;
    private List<VisitRecordEntity> mData;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private DisplayImageOptions mOptions;

    public CateyeVisitorAdapter(Context context, String deviceId,String cameraId) {
        this.deviceId = deviceId;
        this.cameraId = cameraId;
        this.context = context;
        iCamCloudApiUnit = new ICamCloudApiUnit(context);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.common_loading_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public void addMore(List<VisitRecordEntity> data) {
        if (mData == null || mData.size() == 0){
            mData = data;
            notifyDataSetChanged();
        }else {
            int lastIndex = mData.size() - 1;
            mData.addAll(data);
            notifyItemRangeInserted(lastIndex, data.size());
            notifyItemRangeChanged(lastIndex, mData.size());
        }
    }

    public List<VisitRecordEntity> getData() {
        return mData;
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_cateye_visitor, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        String itemUrl = mData.get(holder.getAdapterPosition()).url;
        String itemMsg = mData.get(holder.getAdapterPosition()).msg;
        String itemData = mData.get(holder.getAdapterPosition()).date;
        long itemTime = mData.get(holder.getAdapterPosition()).time;

        String bucket = ApiConstant.BUCKET;
        String region = ApiConstant.REGION;
        if (mData.get(holder.getAdapterPosition()).extData1 != null){
            bucket = mData.get(holder.getAdapterPosition()).extData1.bucket;
            region = mData.get(holder.getAdapterPosition()).extData1.region;
        }

        holder.mTextMsg.setText(itemMsg);
        holder.mTextDate.setText(itemData);
        holder.mImage.setImageResource(R.drawable.bg_button_fullscreen);

        if (TextUtils.isEmpty(itemUrl)){
            holder.mImage.setVisibility(View.GONE);
        }else {
            holder.mImage.setVisibility(View.VISIBLE);
            iCamCloudApiUnit.doGetPic(deviceId, itemTime, itemUrl, bucket, region, new ICamCloudApiUnit.IcamApiCommonListener<File>() {
                @Override
                public void onSuccess(File file) {
                    WLog.d("CateyeVisitorAdapter","imageUrl:"+file.getAbsolutePath().trim());
                    ImageLoader.getInstance().displayImage("file://"+ file.getAbsolutePath().trim(), holder.mImage, mOptions);
                }

                @Override
                public void onFail(int code, String msg) {
                    WLog.d("CateyeVisitorAdapter","加载图片失败 code="+code+" msg="+msg);
                    holder.mImage.setVisibility(View.GONE);
                }
            });
            final long finalTime = itemTime;
            final String finalUrl = itemUrl;
            final String finalMsg = itemMsg;
            final String finalBucket = bucket;
            final String finalRegion = region;
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CateyePicActivity.start(context, deviceId, cameraId, finalMsg, finalTime, finalUrl, finalBucket, finalRegion);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mTextMsg, mTextDate;
        private ImageView mImage;

        public ItemHolder(View itemView) {
            super(itemView);

            mTextMsg = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_msg);
            mTextDate = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_date);
            mImage = (ImageView) itemView.findViewById(R.id.item_alarm_detail_image);
        }
    }
}
