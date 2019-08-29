package cc.wulian.smarthomev6.main.home.scene;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.RecommendSceneInfo;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2018/12/5.
 * func： 推荐场景
 * email: hxc242313@qq.com
 */

public class RecommendSceneFragment extends WLFragment {
    private SceneManager sceneManager;
    private List<SceneInfo> sceneList;

    private RecyclerView recyclerView;
    private RecommendAdapter recommendAdapter;
    private int[] descArray = {
            R.string.recommendScene_01, R.string.recommendScene_02, R.string.recommendScene_03, R.string.recommendScene_04,
            R.string.recommendScene_05, R.string.recommendScene_06, R.string.recommendScene_07, R.string.recommendScene_08, R.string.recommendScene_09};

    private int[] nameArray = {
            R.string.Home_Scene_HJ, R.string.Home_Scene_LJ, R.string.recommendScene_11, R.string.recommendScene_12,
            R.string.recommendScene_13, R.string.recommendScene_14, R.string.Home_Scene_QC, R.string.Home_Scene_SJ, R.string.Home_Scene_QY};

    private int[] resIdArray = {
            R.drawable.bg_recommend_go_home, R.drawable.bg_recommend_leave_home, R.drawable.bg_recommend_all_on, R.drawable.bg_recommend_all_off,
            R.drawable.bg_recommend_defense, R.drawable.bg_recommend_undefense, R.drawable.bg_recommend_getup, R.drawable.bg_recommend_sleep, R.drawable.bg_recommend_midnight};

    @Override
    public int layoutResID() {
        return R.layout.fragment_recommend_scene;
    }

    @Override
    public void initView(View view) {
        recyclerView = view.findViewById(R.id.rl_recommend_scene);

    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        header.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        sceneManager = new SceneManager(getActivity());
        recommendAdapter = new RecommendAdapter(getActivity(), null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recommendAdapter);
        recommendAdapter.update(getRecommendScene());
        recommendAdapter.setOnClickListener(new RecommendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if ("1".equals(preference.getCurrentGatewayState())) {
                    EditRecSceneActivity.start(getActivity(), getRecommendSceneName(position), SceneManager.getRecommendSceneId(position));
                } else {
                    ToastUtil.show(getString(R.string.Gateway_Offline));
                }

//                ToastUtil.show(getRecommendSceneName(position) + SceneManager.getRecommendSceneId(position));
            }
        });
    }


    private List<RecommendSceneInfo> getRecommendScene() {
        List<RecommendSceneInfo> mData = new ArrayList<>();
        for (int i = 0; i < resIdArray.length; i++) {
            RecommendSceneInfo recommendSceneInfo = new RecommendSceneInfo();
            recommendSceneInfo.setSceneName(getString(nameArray[i]));
            recommendSceneInfo.setDescription(getString(descArray[i]));
            recommendSceneInfo.setResId(resIdArray[i]);
            mData.add(recommendSceneInfo);
        }
        return mData;
    }

    private String getRecommendSceneName(int position) {
        String sceneName = getString(nameArray[position]);
        String tmpName = getString(nameArray[position]);
        int index = 1;
        sceneList = sceneManager.acquireScene();
        for (SceneInfo sceneInfo :
                sceneList) {
            if (TextUtils.equals(sceneInfo.getName(), tmpName)) {
                ++index;
                tmpName = sceneName + index;
                repairSceneName(tmpName, sceneName, index);
            }
        }
        return tmpName;
    }


    /**
     * 当场景已存在时场景名称递增重新遍历直到场景名称不同(如回家1-->回家2)
     *
     * @param tmpName
     * @param sceneName
     * @param index
     * @return
     */
    private String repairSceneName(String tmpName, String sceneName, int index) {
        for (SceneInfo sceneInfo :
                sceneList) {
            if (TextUtils.equals(sceneInfo.getName(), tmpName)) {
                ++index;
                tmpName = sceneName + index;
                repairSceneName(tmpName, sceneName, index);
            }
        }
        return tmpName;
    }

    static class RecommendAdapter extends RecyclerView.Adapter {

        private Context context;
        private List<RecommendSceneInfo> mList;
        private RecommendAdapter.OnItemClickListener onClickListener;

        public RecommendAdapter(Context context, List<RecommendSceneInfo> list) {
            this.context = context;
            mList = list;
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnClickListener(RecommendAdapter.OnItemClickListener listener) {
            this.onClickListener = listener;
        }

        public void update(List<RecommendSceneInfo> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            RecommendAdapter.ItemView viewholder = new RecommendAdapter.ItemView(layoutInflater.inflate(R.layout.item_recommend_scene_list, parent, false));

            return viewholder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            RecommendSceneInfo info = mList.get(position);
            ((ItemView) holder).tvSceneName.setText(info.getSceneName());
            ((ItemView) holder).tvSceneDesc.setText(info.getDescription());
            ((ItemView) holder).ivSceneBg.setImageResource(info.getResId());
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            if (mList == null) {
                return 0;
            }
            return mList.size();
        }

        class ItemView extends RecyclerView.ViewHolder {
            private ImageView ivSceneBg;
            private TextView tvSceneName;
            private TextView tvSceneDesc;

            public ItemView(View itemView) {
                super(itemView);
                ivSceneBg = itemView.findViewById(R.id.iv_scene_bg);
                tvSceneName = itemView.findViewById(R.id.tv_scene_name);
                tvSceneDesc = itemView.findViewById(R.id.tv_scene_desc);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = (int) v.getTag();
                        onClickListener.onItemClick(p);
                    }
                });
            }
        }
    }
}
