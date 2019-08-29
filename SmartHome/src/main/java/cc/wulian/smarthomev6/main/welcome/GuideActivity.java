package cc.wulian.smarthomev6.main.welcome;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.customview.viewpagerindicator.CirclePageIndicator;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.wrecord.utils.WLog;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 作者: mamengchao
 * 时间: 2017/4/18 0018
 * 描述: 应到页面
 * 联系方式: 805901025@qq.com
 */

public class GuideActivity extends AppCompatActivity {
    private final List<View> guideViews = new ArrayList<>();

    private ViewPager guideViewPager;
    private CirclePageIndicator guideIndecator;
    private GifImageView[] gifImageView = new GifImageView[4];
    private GifDrawable[] gifDrawable = new GifDrawable[4];
    private int[] guide_animation = new int[]{
            R.drawable.guide_animation1,
            R.drawable.guide_animation2,
            R.drawable.guide_animation3,
            R.drawable.guide_animation4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initGuideViews();
        guideViewPager = (ViewPager) this
                .findViewById(R.id.home_viewPager);
        guideIndecator = (CirclePageIndicator) findViewById(R.id.home_indicator);
        guideViewPager.setAdapter(new GuidePagerAdapter(guideViews));
        guideIndecator.setViewPager(guideViewPager);
        guideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position > 0 && position < gifDrawable.length) {
                    try {
                        if (gifDrawable[position] == null) {
                            gifDrawable[position] = new GifDrawable(getResources(), guide_animation[position]);
//                            gifDrawable[position].addAnimationListener(new AnimationListener() {
//                                @Override
//                                public void onAnimationCompleted(int loopNumber) {
//                                    gifDrawable[position].stop();
//                                }
//                            });
                            gifImageView[position].setImageDrawable(gifDrawable[position]);
                        } else {
                            gifDrawable[position].start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initGuideViews() {
        LayoutInflater inflater = getLayoutInflater();

        ViewGroup guideOne = (ViewGroup) inflater.inflate(R.layout.guide_viewpage_one, null);
        gifImageView[0] = (GifImageView) guideOne.findViewById(R.id.image);
        TextView textView1 = (TextView) guideOne.findViewById(R.id.text);
        ImageView iv_indicator1 = guideOne.findViewById(R.id.iv_indicator);

        ViewGroup guideTwo = (ViewGroup) inflater.inflate(R.layout.guide_viewpage_one, null);
        gifImageView[1] = (GifImageView) guideTwo.findViewById(R.id.image);
        TextView textView2 = (TextView) guideTwo.findViewById(R.id.text);
        ImageView iv_indicator2 = guideTwo.findViewById(R.id.iv_indicator);

        ViewGroup guideThree = (ViewGroup) inflater.inflate(R.layout.guide_viewpage_one, null);
        gifImageView[2] = (GifImageView) guideThree.findViewById(R.id.image);
        TextView textView3 = (TextView) guideThree.findViewById(R.id.text);
        ImageView iv_indicator3 = guideThree.findViewById(R.id.iv_indicator);

        ViewGroup guideFour = (ViewGroup) inflater.inflate(R.layout.guide_viewpage_three, null);
        gifImageView[3] = (GifImageView) guideFour.findViewById(R.id.image);
        TextView textView4 = (TextView) guideFour.findViewById(R.id.text);
        ImageView iv_indicator4 = guideFour.findViewById(R.id.iv_indicator);

        gifImageView[0].setImageResource(R.drawable.guide_animation1);
//        gifImageView[1].setImageResource(R.drawable.guide_animation4);
//        gifImageView[2].setImageResource(R.drawable.guide_animation2);
//        gifImageView[3].setImageResource(R.drawable.guide_animation3);

        textView1.setText(R.string.Bootpage_610_Page1);
        textView2.setText(R.string.Bootpage_610_Page2);
        textView3.setText(R.string.Bootpage_610_Page3);
        textView4.setText(R.string.Bootpage_610_Page4);

        iv_indicator1.setImageResource(R.drawable.guide_indicator1);
        iv_indicator2.setImageResource(R.drawable.guide_indicator2);
        iv_indicator3.setImageResource(R.drawable.guide_indicator3);
        iv_indicator4.setImageResource(R.drawable.guide_indicator4);

        ImageView skip = (ImageView) guideFour.findViewById(R.id.guide_tv);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (LanguageUtil.isChina()) {
            skip.setBackgroundResource(R.drawable.icon_guide_btn_ch);
        } else {
            skip.setBackgroundResource(R.drawable.icon_guide_btn_en);
        }
        //其他语言图片待添加
        guideViews.add(guideOne);
        guideViews.add(guideTwo);
        guideViews.add(guideThree);
        guideViews.add(guideFour);
    }

    private class GuidePagerAdapter extends PagerAdapter {

        private final List<View> viewList;

        public GuidePagerAdapter(List<View> list) {
            this.viewList = list;
        }

        @Override
        public int getCount() {

            return viewList != null ? viewList.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position), 0);
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

    }
}
