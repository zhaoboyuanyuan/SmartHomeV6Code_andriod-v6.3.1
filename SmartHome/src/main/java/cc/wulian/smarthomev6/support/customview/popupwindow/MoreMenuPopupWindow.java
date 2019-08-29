package cc.wulian.smarthomev6.support.customview.popupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FastBlur;


public class MoreMenuPopupWindow {

	private LinearLayout rootView;
	private LinearLayout contentView;
	private ScrollView scrollView;
	private LayoutInflater inflater;
	private Context context;
	private PopupWindow popupWindow;
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();

	public MoreMenuPopupWindow(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		rootView = (LinearLayout) inflater.inflate(
				R.layout.device_setting_more_content, null);
		initScorll();

	}

	public void initScorll(){
		contentView = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		contentView.setOrientation(LinearLayout.VERTICAL);
		contentView.setLayoutParams(params);
		contentView.removeAllViews();

		scrollView = new ScrollView(context);
		scrollView.setBackgroundColor(Color.TRANSPARENT);
//		scrollView.setVerticalScrollBarEnabled(false);
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public boolean isShown() {
		if (popupWindow == null)
			return false;
		return popupWindow.isShowing();
	}

	public void show(View view, int width) {
		rootView.removeAllViews();
		for (MenuItem item : this.menuItems) {
			rootView.addView(item.getView());
		}
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this.context);
			popupWindow.setBackgroundDrawable(this.context.getResources()
					.getDrawable(R.drawable.popwindow_bg));
			/**
			 * 指定popupwindow的宽和高
			 */
			popupWindow.setWidth(width);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setContentView(rootView);
		}
		popupWindow.showAsDropDown(view, -10, 2);
		popupWindow.setFocusable(true);
		popupWindow.update();
	}

	public void show(View view) {
		rootView.removeAllViews();
		for (MenuItem item : this.menuItems) {
			rootView.addView(item.getView());
		}
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this.context);
			popupWindow.setBackgroundDrawable(this.context.getResources()
					.getDrawable(R.drawable.popwindow_bg));
			/**
			 * 指定popupwindow的宽和高
			 */
			popupWindow.setWidth(DisplayUtil.dip2Pix(context, 150));
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setContentView(rootView);
		}
		popupWindow.showAsDropDown(view, -10, 2);
		popupWindow.setFocusable(true);
		popupWindow.update();
	}

	public void showParent(View view) {
		rootView.removeAllViews();
		for (MenuItem item : this.menuItems) {
			rootView.addView(item.getView());
		}
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this.context);
			popupWindow.setBackgroundDrawable(this.context.getResources()
					.getDrawable(R.drawable.popwindow_bg));
			/**
			 * 指定popupwindow的宽和高
			 */
			popupWindow.setWidth(LayoutParams.MATCH_PARENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setContentView(rootView);
		}
		popupWindow.showAsDropDown(view, -10, 2);
		popupWindow.setFocusable(true);
		popupWindow.update();
	}

	public void setBlurResId(int resId){
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),resId);//从资源文件中得到图片，并生成Bitmap图片
		rootView.setBackground(new BitmapDrawable(FastBlur.fastBlur(context,bmp,0.1f,5)));
	}

	public void showParentScroll(View view) {
		rootView.removeAllViews();
		contentView.removeAllViews();
		scrollView.removeAllViews();

		for (MenuItem item : this.menuItems) {
			contentView.addView(item.getView());
		}
		scrollView.addView(contentView);
		rootView.addView(scrollView);
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this.context);
			popupWindow.setBackgroundDrawable(this.context.getResources()
					.getDrawable(R.drawable.popwindow_bg));
			/**
			 * 指定popupwindow的宽和高
			 */
			popupWindow.setWidth(LayoutParams.MATCH_PARENT);
            if (menuItems != null && menuItems.size() <= 8){
                popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
            }else {
                popupWindow.setHeight(DisplayUtil.dip2Pix(context, 441));
            }
            popupWindow.setContentView(rootView);
		}
		popupWindow.showAsDropDown(view, -10, 2);
		popupWindow.setFocusable(true);
		popupWindow.update();
	}

	/**
	 * <h1>隐藏Pop</h1>
	 */
	public void dismiss() {
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}

	public void setOnDismissListener(OnDismissListener listener) {
		if (popupWindow != null)
			popupWindow.setOnDismissListener(listener);
	}

	public static abstract class MenuItem {
		protected int icon;
		protected String name;
		protected Context context;
		protected LayoutInflater inflater;
		protected LinearLayout lineLayout;
		protected ImageView titleImageView;
		protected TextView titleTextView;

		public MenuItem(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(this.context);
			lineLayout = (LinearLayout) inflater.inflate(
					R.layout.device_detail_setting_more_item, null);
			titleImageView = (ImageView) lineLayout
					.findViewById(R.id.device_setting_more_title_icon);
			titleTextView = (TextView) lineLayout
					.findViewById(R.id.device_setting_more_title_text);
			initSystemState(titleTextView);
			initSystemState(titleImageView, titleTextView);
			lineLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					doSomething();
				}
			});
		}

		public MenuItem(Context context, boolean Left) {
			this.context = context;
			inflater = LayoutInflater.from(this.context);
			lineLayout = (LinearLayout) inflater.inflate(
					R.layout.device_detail_setting_more_left_item, null);
			titleImageView = (ImageView) lineLayout
					.findViewById(R.id.device_setting_more_title_icon);
			titleTextView = (TextView) lineLayout
					.findViewById(R.id.device_setting_more_title_text);
			initSystemState(titleTextView);
			initSystemState(titleImageView, titleTextView);
			lineLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					doSomething();
				}
			});
		}

		public int getIcon() {
			return icon;
		}

		public void setIcon(int icon) {
			this.icon = icon;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public abstract void initSystemState(TextView titleTextView);

		public void initSystemState(ImageView titleImageView, TextView titleTextView){};

		public abstract void doSomething();

		public View getView() {
			return lineLayout;
		}
	}
}
