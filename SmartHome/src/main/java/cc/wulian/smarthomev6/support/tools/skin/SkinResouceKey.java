package cc.wulian.smarthomev6.support.tools.skin;

/**
 * Created by zbl on 2017/10/13.
 */

public interface SkinResouceKey {

    //****************图片******************/

    ///通用
    String BITMAP_MAIN_BACKGROUND = "login_background@2x.png"; // 主背景图
    String BITMAP_TITLE_BACKGROUND = "nav_BackgroundImage@2x.png"; // 标题栏背景图
    String BITMAP_NAV_BOTTOM_BACKGROUND = "nav_bottom_background@2x.png"; // 设备列表界面标题栏扩展背景图
    String BITMAP_BUTTON_BG_S = "default_btn_background_available@2x.png";//按钮可用
    String BITMAP_BUTTON_BG_N = "default_btn_background@2x.png";//按钮不可用

    //首页底部导航栏图标
    String BITMAP_BOTTOM_NAVIGATION_HOME_S = "home_Icon_selected@2x.png";
    String BITMAP_BOTTOM_NAVIGATION_HOME_N = "home_Icon@2x.png";
    String BITMAP_BOTTOM_NAVIGATION_DEVICE_S = "device_Icon_selected@2x.png";
    String BITMAP_BOTTOM_NAVIGATION_DEVICE_N = "device_Icon@2x.png";
    String BITMAP_BOTTOM_NAVIGATION_FIND_S = "find_Icon_selected@2x.png";
    String BITMAP_BOTTOM_NAVIGATION_DIND_N = "find_Icon@2x.png";
    String BITMAP_BOTTOM_NAVIGATION_MINE_S = "mine_Icon_selected@2x.png";
    String BITMAP_BOTTOM_NAVIGATION_MINE_N = "mine_Icon@2x.png";
    String BITMAP_BOTTOM_NAVIGATION_VOICE_N = "voice_Icon@2x.png";//语音按钮
    String BITMAP_BOTTOM_NAVIGATION_VOICE_S = "voice_Icon_selected@2x.png";//语音按钮
    //登录
    String BITMAP_BUTTON_EXIT = "login_back_icon@2x.png";//退出按钮(×)
    String BITMAP_BUTTON_EDIT_DELETE = "icon_edittext_delete.png";//编辑框删除按钮
    String BITMAP_LOGIN_LOGO = "login_logo_icon@2x.png";//logo
    String BITMAP_BUTTON_THIRD_WECHAT = "login_wx_icon@2x.png";//第三方登录按钮，微信
    String BITMAP_BUTTON_THIRD_QQ = "login_qq_icon@2x.png";//第三方登录按钮，QQ
    String BITMAP_BUTTON_THIRD_WEIBO = "login_wb_icon@2x.png";//第三方登录按钮，微博
    //红外转发器
    String BITMAP_AIRCOND_UNDER_TITLE_BG = "special_nav_background@2x.png";//红外转发器标题栏下方渐变色图片
    String BITMAP_AIRCOND_BUTTON_ROUND_DOWNLOAD_BG = "small_btn_background_available@2x.png";//红外转发器下载按钮


    //****************颜色******************/

    String COLOR_NAV = "nav_color";//主题颜色
    //首页
    String COLOR_BOTTOM_NAVIGATION_TEXT_N = "tab_text_color";//首页底部导航栏选中项目的文字颜色
    String COLOR_BOTTOM_NAVIGATION_TEXT_S = "tab_select_text_color";//首页底部导航栏选中项目的文字颜色
    //登录
    String COLOR_BUTTON_TEXT = "default_btn_text_color";//按钮文字颜色(不可用时)
    String COLOR_BUTTON_TEXT_ACTIVE = "default_btn_text_available_color";//按钮文字颜色(按钮可用时)
    String COLOR_HINT_TEXT = "login_text_color";//登录界面hint文字颜色
    String COLOR_LOGIN_HIGHLIGHT_TEXT = "login_text_available_color";//登录界面高亮文字颜色
    String COLOR_LOGIN_OTHER_TEXT = "login_other_color";//登录界面其它文字颜色
    String COLOR_HINT_OTHER_TEXT = "login_other_alpha_color";//登录界面特殊hint文字颜色
}
