package kr.laruyan.blursystembarsample;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * Created by LaruYan on 10/12/2015.
 */
public class VisibleEstatesUtil {

    public static final byte FLAG_RESTORE_NOTHING = -1;
    public static final byte FLAG_RESTORE_BASIC = 0b001;
    public static final byte FLAG_RESTORE_TRANSLUCENT_STATUSBAR = 0b010;
    public static final byte FLAG_RESTORE_TRANSLUCENT_NAVBAR = 0b100;

    private static final int IDENTIFIER_DRAWER_DUMMY = 144;
    private static final boolean IS_DEBUG = true;
    private static final String LOG_TAG = "VisibleEstatesUtil";

    private static void logDebug(String msg){
        logDebug(msg,null);
    }

    private static void logDebug(String msg, Throwable throwable){
        if(IS_DEBUG) {
            Log.d(LOG_TAG, msg, throwable);
        }
    }

    public static int getStatusBarSize(Activity atv, boolean isSystemUiVisible){
        Rect rect =  new Rect();
        atv.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return getStatusBarSize(rect,atv,isSystemUiVisible);
    }

    public static int getStatusBarSize(Rect decorViewDisplayFrame, Activity atv, boolean isSystemUiVisible){
        int statusBarHeight = decorViewDisplayFrame.top;
        if (statusBarHeight <= 0) {
            logDebug("getStatusBarSize() :: statusBarHeight <= 0, status bar height not retrieved.");
            if (isSystemUiVisible) {
                logDebug("getStatusBarSize() :: isSystemUiVisible: true, should retrieve status bar height.");
                int resourceId = atv.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId != 0) {
                    logDebug("getStatusBarSize() :: resourceId: 0x" + Integer.toHexString(resourceId) + " found as android.R.dimen.status_bar_height");
                    statusBarHeight = atv.getResources().getDimensionPixelSize(resourceId);
                }
            }
//                // 예전 방식 활용. Immersive모드에 강제적으로 진입한 경우 작동보장 안됨
//                if ((systemUiVisibility & View.SYSTEM_UI_FLAG_IMMERSIVE) > 0 ||
//                        (systemUiVisibility & View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) > 0) {
//                    //do nothing
//                } else {
//                    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
//                    if (resourceId != 0) {
//                        statusBarHeight = resources.getDimensionPixelSize(resourceId);
//                    }
//                }

            //https://developer.android.com/intl/ko/training/system-ui/visibility.html
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            //if ((systemUiVisibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0 && (systemUiVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0 && (systemUiVisibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
//                if ((systemUiVisibility & View.SYSTEM_UI_FLAG_LAYOUT_STABLE) != 0 && (systemUiVisibility & View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) != 0 && (systemUiVisibility & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) != 0) {
//                    // The system bars are visible. Make any desired
//                    // adjustments to your UI, such as showing the action bar or
//                    // other navigational controls.
//                    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
//                    if (resourceId != 0) {
//                        statusBarHeight = resources.getDimensionPixelSize(resourceId);
//                    }
//                } else {
//                    // The system bars are NOT visible. Make any desired
//                    // adjustments to your UI, such as hiding the action bar or
//                    // other navigational controls.
//                }
        }
        logDebug("getStatusBarSize() :: statusBarHeight: " + statusBarHeight);

        return statusBarHeight;
    }

    //http://stackoverflow.com/a/29609679
    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    //http://stackoverflow.com/a/29609679
    private static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    //http://stackoverflow.com/a/29609679
    private static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getRealSize(size,display);
        }

        return size;
    }


    public static byte setFullScreenMode(Activity atv, boolean isSystemUiVisible, Drawer drawer, int toolbarHeight, View vwMainToolBarDummy,  View[] vwStatusBarAffecteds, View[] vwNavBarAffecteds) {
        return setFullScreenMode(atv, isSystemUiVisible, drawer,toolbarHeight, vwMainToolBarDummy, null, null, 0x00FFFFFF, null, null, 0x00FFFFFF, false, vwStatusBarAffecteds, vwNavBarAffecteds);
    }

    public static byte setFullScreenMode(Activity atv, boolean isSystemUiVisible, Drawer drawer, int toolbarHeight, View vwMainToolBarDummy, boolean noLimits, View[] vwStatusBarAffecteds, View[] vwNavBarAffecteds) {
        return setFullScreenMode(atv, isSystemUiVisible, drawer,toolbarHeight, vwMainToolBarDummy, null, null, 0x00FFFFFF, null, null, 0x00FFFFFF, noLimits, vwStatusBarAffecteds, vwNavBarAffecteds);
    }

    /**
     *
     * @param atv 액티비티
     * @param isSystemUiVisible SYSTEMUI 표시여부
     * @param drawer 드로어 (선택사항)
     * @param toolbarHeight 액션바 크기 (선택사항, 없으면 안드로이드 리소스에서 찾습니다.)
     * @param vwToolbarDummy 툴바 더미 (선택사항)
     * @param vwMainToolbarBottomDummy 툴바 더미 (선택사항)
     * @param vwStatusBarDummy 알림막대 더미 (선택사항, 단 noLimits가 true일 때 필수)
     * @param statusBarColor 알림막대 색상 (선택사항, 단 noLimits가 true일 때 필수)
     * @param vwNavBarPortDummy 소프트키 세로 더미 (noLimits가 true일 때 필수)
     * @param vwNavBarLandDummy 소프트키 가로 더미 (noLimits가 true일 때 필수)
     * @param navBarColor 소프트키 색상 (noLimits가 true일 때 필수)
     * @param noLimits true이면 알림막대와 소프트키를 직접 그립니다.
     * @param vwStatusBarAffecteds 알림막대 여백만큼 이동시킬 View들 (선택사항)
     * @param vwNavBarAffecteds 소프트키 여백만큼 이동시킬 View들
     * @return 현재 SYSTEMUI 요소 표시에 대한 플래그 (일반, 알림막대, 소프트키, 알림막대+소프트키)
     */
    public static byte setFullScreenMode(Activity atv, boolean isSystemUiVisible, Drawer drawer, int toolbarHeight, View vwToolbarDummy, View vwMainToolbarBottomDummy, View vwStatusBarDummy, int statusBarColor, View vwNavBarPortDummy, View vwNavBarLandDummy, int navBarColor, boolean noLimits, View[] vwStatusBarAffecteds, View[] vwNavBarAffecteds) {
        byte rememberResetStatus = FLAG_RESTORE_NOTHING;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(atv == null){
                logDebug("setFullScreenMode() :: activity is null. cannot continue.");
                return FLAG_RESTORE_NOTHING;
            }

            WindowManager windowManager = atv.getWindowManager();
            Window window = atv.getWindow();
            Resources resources = atv.getResources();

            if(window == null || windowManager == null){
                logDebug("setFullScreenMode() :: Window or WindowManager is null. cannot continue.");
                return FLAG_RESTORE_NOTHING;
            }

            View decorView = window.getDecorView();

            Rect rect = new Rect();

            decorView.getWindowVisibleDisplayFrame(rect);
            Point ptRealSize = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                windowManager.getDefaultDisplay().getRealSize(ptRealSize);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                getRealSize(ptRealSize,windowManager.getDefaultDisplay());
            }

            logDebug("setFullScreenMode() :: rect ( top: " + rect.top + " / left: " + rect.left + " / right: " + rect.right + " / bottom: " + rect.bottom + " )");
            logDebug("setFullScreenMode() :: ptRealSize ( x: " + ptRealSize.x + " / y: " + ptRealSize.y + " )");
//            int systemUiVisibility = decorView.getSystemUiVisibility();
//            System.out.println("SYSTEM_UI_FLAG: " + systemUiVisibility + "(" + Integer.toHexString(systemUiVisibility) + ")");


            int statusBarHeight  = getStatusBarSize(rect, atv, isSystemUiVisible);

            // 멀티윈도우 지원을 생각한다면 액션바만큼의 최소크기를 기준으로 생각하면 되겠습니다
            // 액션바 크기보다 크면 측정은 무시합니다.
            if (toolbarHeight <= 0) {
                //toolbarHeight = -1;
                logDebug("setFullScreenMode() :: toolbarHeight <= 0, action bar height not retrieved.");

                //http://stackoverflow.com/a/13216807
                // 액션바 높이 측정
                TypedValue tv = new TypedValue();
                if (atv.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                    logDebug("setFullScreenMode() :: R.attr.actionBarSize resolved.");
                    toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, atv.getResources().getDisplayMetrics());
                }
            }
            logDebug("setFullScreenMode() :: toolbarHeight: " + toolbarHeight);

            boolean isStatusBarThinerThanToolbarHeight = statusBarHeight < toolbarHeight;
            boolean isNavBarThinerThanToolbarHeight = false;
            boolean isInMultiWindowMode = false;

            logDebug("setFullScreenMode() :: isStatusBarThinerThanToolbarHeight: " + isStatusBarThinerThanToolbarHeight);

            ViewGroup.MarginLayoutParams rlpTlbDummy = null;
            if (vwToolbarDummy != null) {
                rlpTlbDummy = (ViewGroup.MarginLayoutParams) vwToolbarDummy.getLayoutParams();
            }
            ViewGroup.MarginLayoutParams rlpVwBottomDummy = null;
            if (vwMainToolbarBottomDummy != null) {
                rlpVwBottomDummy = (ViewGroup.MarginLayoutParams) vwMainToolbarBottomDummy.getLayoutParams();
            }


            ViewGroup.MarginLayoutParams[] rlpNavBarAffecteds = new ViewGroup.MarginLayoutParams[(vwNavBarAffecteds != null) ? vwNavBarAffecteds.length : 0];
            for (int cur = 0; cur < rlpNavBarAffecteds.length; cur++) {
                rlpNavBarAffecteds[cur] = (ViewGroup.MarginLayoutParams) vwNavBarAffecteds[cur].getLayoutParams();
            }

            int navBarWidth = ptRealSize.x - rect.right;
            int navBarHeight = ptRealSize.y - rect.bottom;
            int navBarLeft = rect.left;

            logDebug("setFullScreenMode() :: navBarWidth: " + navBarWidth + " / navBarHeight: " + navBarHeight + " / navBarLeft: " + navBarLeft);

            //if (windowManager.getDefaultDisplay().getRotation() % 180 != 0) {
            if(navBarWidth > navBarHeight){ // 화면 옆으로 소프트키가 붙은 경우
                logDebug("setFullScreenMode() :: navBarWidth > navBarHeight");
                if(navBarWidth <= 0)
                {
                    logDebug("setFullScreenMode() :: navBarWidth <= 0, navigation bar width may not retrieved.");
                    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                    if (resourceId != 0) {
                        logDebug("setFullScreenMode() :: resourceId: 0x" + Integer.toHexString(resourceId) + " found as android.R.dimen.navigation_bar_height");
                        navBarWidth = resources.getDimensionPixelSize(resourceId);
                    }
                }
                logDebug("setFullScreenMode() :: navBarWidth: " + navBarWidth);

                for (ViewGroup.MarginLayoutParams rlpNavBarAffected : rlpNavBarAffecteds) {
                    rlpNavBarAffected.bottomMargin = 0;
                }
                isNavBarThinerThanToolbarHeight = navBarWidth < toolbarHeight && navBarLeft < toolbarHeight;

                isInMultiWindowMode = !(isStatusBarThinerThanToolbarHeight && isNavBarThinerThanToolbarHeight);
                logDebug("setFullScreenMode() :: isNavBarThinerThanToolbarHeight: " + isNavBarThinerThanToolbarHeight);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    isInMultiWindowMode = atv.isInMultiWindowMode();
                }

                if (isSystemUiVisible && !isInMultiWindowMode) {
                    logDebug("setFullScreenMode() :: true for isSystemUiVisible && !isInMultiWindowMode");
                    if (rlpTlbDummy != null) {
                        rlpTlbDummy.rightMargin = navBarWidth;
                    }
                    if (rlpVwBottomDummy != null) {
                        rlpVwBottomDummy.rightMargin = navBarWidth;
                    }
                    for (ViewGroup.MarginLayoutParams rlpNavBarAffected : rlpNavBarAffecteds) {
                        rlpNavBarAffected.rightMargin = navBarWidth;
                    }
                } else {
                    logDebug("setFullScreenMode() :: false for isSystemUiVisible && !isInMultiWindowMode");
                    if (rlpTlbDummy != null) {
                        rlpTlbDummy.rightMargin = 0;
                    }
                    if (rlpVwBottomDummy != null) {
                        rlpVwBottomDummy.rightMargin = 0;
                    }
                    for (ViewGroup.MarginLayoutParams rlpNavBarAffected : rlpNavBarAffecteds) {
                        rlpNavBarAffected.bottomMargin = 0;
                    }
                }
//                        System.out.println("set Full Screen Mode: " + statusBarHeight + ", " + navBarWidth);
                if (drawer != null) {
                    logDebug("setFullScreenMode() :: drawer not null, should remove dummy item");
                    drawer.removeItem(IDENTIFIER_DRAWER_DUMMY);
                }
            } else if(navBarWidth < navBarHeight){ //화면 아래로 소프트키가 붙은경우
                logDebug("setFullScreenMode() :: navBarWidth < navBarHeight");
                if(navBarHeight <= 0)
                {
                    logDebug("setFullScreenMode() :: navBarHeight <= 0, navigation bar height may not retrieved.");
                    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                    if (resourceId != 0) {
                        logDebug("setFullScreenMode() :: resourceId: 0x" + Integer.toHexString(resourceId) + " found as android.R.dimen.navigation_bar_height");
                        navBarHeight = resources.getDimensionPixelSize(resourceId);
                    }
                }
                logDebug("setFullScreenMode() :: navBarHeight: " + navBarHeight);

                if (rlpTlbDummy != null) {
                    rlpTlbDummy.rightMargin = 0;
                }
                if (rlpVwBottomDummy != null) {
                    rlpVwBottomDummy.rightMargin = 0;
                }
                for (ViewGroup.MarginLayoutParams rlpNavBarAffected : rlpNavBarAffecteds) {
                    rlpNavBarAffected.rightMargin = 0;
                }

                isNavBarThinerThanToolbarHeight = navBarHeight < toolbarHeight;
                logDebug("setFullScreenMode() :: isNavBarThinerThanToolbarHeight: " + isNavBarThinerThanToolbarHeight);

                isInMultiWindowMode = !(isStatusBarThinerThanToolbarHeight && isNavBarThinerThanToolbarHeight);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    isInMultiWindowMode = atv.isInMultiWindowMode();
                }

                if (isSystemUiVisible && !isInMultiWindowMode) {
                    logDebug("setFullScreenMode() :: true for isSystemUiVisible && !isInMultiWindowMode");
                    for (ViewGroup.MarginLayoutParams rlpNavBarAffected : rlpNavBarAffecteds) {
                        rlpNavBarAffected.bottomMargin = navBarHeight;
                    }
                    if (drawer != null) {
                        if (drawer.getDrawerItem(IDENTIFIER_DRAWER_DUMMY) == null) {
                            logDebug("setFullScreenMode() :: drawer not null, should insert dummy item");
                            RelativeLayout view = new RelativeLayout(atv);
                            view.setMinimumHeight(navBarHeight);
                            PrimaryDrawerItem dummy = new PrimaryDrawerItem();
                            dummy.generateView(atv, view);
                            dummy.withTextColor(0x00FFFFFF); // HARDCODED_RESOURCE
                            dummy.withIdentifier(IDENTIFIER_DRAWER_DUMMY);
                            dummy.withSelectable(false);
                            dummy.withEnabled(false);
                            drawer.addItem(dummy);
                        }
                    }
                } else {
                    logDebug("setFullScreenMode() :: false for isSystemUiVisible && !isInMultiWindowMode");
                    for (ViewGroup.MarginLayoutParams rlpNavBarAffected : rlpNavBarAffecteds) {
                        rlpNavBarAffected.bottomMargin = 0;
                    }
                    if (drawer != null) {
                        logDebug("setFullScreenMode() :: drawer not null, should remove dummy item");
                        drawer.removeItem(IDENTIFIER_DRAWER_DUMMY);
                    }
                }
//                        System.out.println("set Full Screen Mode: " + statusBarHeight + ", " + navBarHeight);
            }
            else
            {
                logDebug("setFullScreenMode() :: navBarWidth == navBarHeight");

                // 모두 0일 때
                if (rlpTlbDummy != null) {
                    rlpTlbDummy.rightMargin = 0;
                }
                if (rlpVwBottomDummy != null) {
                    rlpVwBottomDummy.rightMargin = 0;
                }
                for (ViewGroup.MarginLayoutParams rlpNavBarAffected : rlpNavBarAffecteds) {
                    rlpNavBarAffected.bottomMargin = 0;
                }
//                        System.out.println("set Full Screen Mode: " + statusBarHeight + ", " + navBarWidth);
                if (drawer != null) {
                    logDebug("setFullScreenMode() :: drawer not null, should remove dummy item");
                    drawer.removeItem(IDENTIFIER_DRAWER_DUMMY);
                }
            }

            for (int cur = 0; cur < rlpNavBarAffecteds.length; cur++) {
                vwNavBarAffecteds[cur].setLayoutParams(rlpNavBarAffecteds[cur]);
            }

            // now it's time for near status bar;
            ViewGroup.MarginLayoutParams[] rlpStatusBarAffecteds = new ViewGroup.MarginLayoutParams[(vwStatusBarAffecteds != null) ? vwStatusBarAffecteds.length : 0];
            for (int cur = 0; cur < rlpStatusBarAffecteds.length; cur++) {
                rlpStatusBarAffecteds[cur] = (ViewGroup.MarginLayoutParams) vwStatusBarAffecteds[cur].getLayoutParams();
            }

            if (isSystemUiVisible && !isInMultiWindowMode) {
                if (rlpTlbDummy != null) {
                    rlpTlbDummy.topMargin = statusBarHeight;
                }
                if (rlpVwBottomDummy != null) {
                    rlpVwBottomDummy.bottomMargin = statusBarHeight;
                }
                for (ViewGroup.MarginLayoutParams rlpStatusBarAffected : rlpStatusBarAffecteds) {
                    rlpStatusBarAffected.topMargin = statusBarHeight;
                }
            } else {
                if (rlpTlbDummy != null) {
                    rlpTlbDummy.topMargin = 0;
                }
                if (rlpVwBottomDummy != null) {
                    rlpVwBottomDummy.bottomMargin = 0;
                }

                for (ViewGroup.MarginLayoutParams rlpStatusBarAffected : rlpStatusBarAffecteds) {
                    rlpStatusBarAffected.topMargin = 0;
                }
            }

            if (vwToolbarDummy != null) {
                vwToolbarDummy.setLayoutParams(rlpTlbDummy);
            }
            if (vwMainToolbarBottomDummy != null) {
                vwMainToolbarBottomDummy.setLayoutParams(rlpVwBottomDummy);
            }

            for (int cur = 0; cur < rlpStatusBarAffecteds.length; cur++) {
                vwStatusBarAffecteds[cur].setLayoutParams(rlpStatusBarAffecteds[cur]);
            }

            if (isSystemUiVisible) {
                logDebug("setFullScreenMode() :: SystemUi have to be visible");
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                rememberResetStatus = FLAG_RESTORE_BASIC;

                if(vwStatusBarAffecteds != null) {
                    window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    rememberResetStatus += FLAG_RESTORE_TRANSLUCENT_STATUSBAR;
                }

                if(vwNavBarAffecteds != null) {
                    window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    rememberResetStatus += FLAG_RESTORE_TRANSLUCENT_NAVBAR;
                }

                if(noLimits)
                {
                    logDebug("setFullScreenMode() :: no limit is true");
                    // NO Limits 플래그 사용시 알림막대 소프트키까지 완전 투명이 됩니다.
                    //window.setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);


                    if(vwStatusBarDummy != null) {
                        logDebug("setFullScreenMode() :: vwStatusBarDummy is not null");
                        int statusbarVisibility = View.GONE;
                        if (!isInMultiWindowMode) {
                            //멀티윈도우에 놓이지 않은 경우
                            logDebug("setFullScreenMode() :: false for isInMultiWindowMode");
                            //알림막대부를 새로 그립니다.
                            vwStatusBarDummy.setBackgroundColor(statusBarColor);
                            if(statusBarHeight > 0) {
                                ViewGroup.MarginLayoutParams statusBarLP = (ViewGroup.MarginLayoutParams) vwStatusBarDummy.getLayoutParams();
                                statusBarLP.height = statusBarHeight;
                                vwStatusBarDummy.setLayoutParams(statusBarLP);
                            }
                            statusbarVisibility = View.VISIBLE;
                        }
                        vwStatusBarDummy.setVisibility(statusbarVisibility);
                    }

                    if(vwNavBarPortDummy != null && vwNavBarLandDummy != null) {
                        logDebug("setFullScreenMode() :: vwNavBarDummies are not null");
                        //소프트키부를 새로 그립니다. 단 처음 실행 시 정상적으로 읽어오므로 이 값을 이용해 visibility 만 변경합니다.
                        int navbarPortVisibility = View.GONE;
                        int navbarLandVisibility = View.GONE;

                        //if (windowManager.getDefaultDisplay().getRotation() % 180 != 0) {
                        if(navBarWidth > navBarHeight){
                            // 소프트 키가 옆으로 붙음
                            if (!isInMultiWindowMode) {
                                //멀티윈도우 모드에 놓이지 않은 경우
                                logDebug("setFullScreenMode() :: false for isInMultiWindowMode");


                                vwNavBarLandDummy.setBackgroundColor(navBarColor);
                                if(navBarWidth > 0)
                                {
                                    ViewGroup.MarginLayoutParams navBarLandLP = (ViewGroup.MarginLayoutParams) vwNavBarLandDummy.getLayoutParams();
                                    navBarLandLP.width = navBarWidth;
                                    vwNavBarLandDummy.setLayoutParams(navBarLandLP);
                                }

                                navbarLandVisibility = View.VISIBLE;
                            }
                        } else {
                            // 소프트키가 아래로 붙음
                            if (!isInMultiWindowMode) {
                                //멀티윈도우 모드에 놓이지 않은 경우
                                logDebug("setFullScreenMode() :: false for isInMultiWindowMode");


                                vwNavBarPortDummy.setBackgroundColor(navBarColor);
                                if(navBarHeight > 0)
                                {
                                    ViewGroup.MarginLayoutParams navBarPortLP = (ViewGroup.MarginLayoutParams) vwNavBarPortDummy.getLayoutParams();
                                    navBarPortLP.height = navBarHeight;
                                    vwNavBarPortDummy.setLayoutParams(navBarPortLP);
                                }

                                navbarPortVisibility = View.VISIBLE;
                            }
                        }
                        // navBarWidth와 navBarHeight가 모두 0인 경우에는 isNavBarThinerThanToolBarHeight가 false로 고정이되기 때문에 조건 처리 필요 없음

                        vwNavBarPortDummy.setVisibility(navbarPortVisibility);
                        vwNavBarLandDummy.setVisibility(navbarLandVisibility);
                    }

                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
            }
            else
            {
                if(noLimits) {
                    // 알림막대 부를 숨기게 합니다.
                    if (vwStatusBarDummy != null) {
                        vwStatusBarDummy.setVisibility(View.GONE);
                    }
                    //소프트키 부를 숨기게 합니다.
                    if (vwNavBarPortDummy != null) {
                        vwNavBarPortDummy.setVisibility(View.GONE);
                    }
                    if (vwNavBarLandDummy != null) {
                        vwNavBarLandDummy.setVisibility(View.GONE);
                    }
                }
            }
        }
        return rememberResetStatus;
    }

    public static void setScrollableTransparentPaddings(final Activity atv, View parentView, final View[] listViews, final boolean scrollDirectionIsVertical, final int actionBarHeight){
        parentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                boolean isNeedForRelayout = false;

                if (left != oldLeft) {
                    isNeedForRelayout = true;
                }
                if (right != oldRight) {
                    isNeedForRelayout = true;
                }
                if (top != oldTop) {
                    isNeedForRelayout = true;
                }
                if (bottom != oldBottom) {
                    isNeedForRelayout = true;
                }


                if (isNeedForRelayout) {
                    int statusBarSize = VisibleEstatesUtil.getStatusBarSize(atv,true);

                    Point navBarSize = VisibleEstatesUtil.getNavigationBarSize(atv);
                    boolean isLandscape = false;
                    if (atv.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90 ||
                            atv.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270 ) {
                        isLandscape = true;
                    }else {
                        isLandscape = false;
                    }

                    //clipToPadding은 비쳐보이는 효과를 위해 false여야합니다
                    for(View listView : listViews){
                        if(isLandscape) {
                            // 스크롤이 가로방향이면 패딩에서 잘려나가도록 한다
                            // scrollDirectionIsVertical
                            if (listView instanceof ListView) {
                                ((ListView) listView).setClipToPadding(true);
                            } else if (listView instanceof ScrollView) {
                                ((ScrollView) listView).setClipToPadding(true);
                            }else if (listView instanceof HorizontalScrollView) {
                                ((HorizontalScrollView) listView).setClipToPadding(false);
                            }else if (listView instanceof RecyclerView){
                                RecyclerView recyclerView = ((RecyclerView) listView);

                                //RecyclerView는 코드상에서 방향을 확인할 수 있다.
                                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                                if(lm != null){
                                    // 스크롤 방향은 가로/세로 한 방향으로만
                                    if(lm.canScrollVertically()){
                                        recyclerView.setClipToPadding(true);
                                    }else{
                                        recyclerView.setClipToPadding(false);
                                    }
                                }

                            }
                            //Webview의 padding 구현에 문제가 있어 다른 방법으로 넣어야합니다.
//                            else if (listView instanceof MarkdownView){
//                                ((MarkdownView) listView).setClipToPadding(scrollDirectionIsVertical);
//                            } else if (listView instanceof WebView) {
//                                ((WebView) listView).setClipToPadding(scrollDirectionIsVertical);
//                            }
                            listView.setPadding(0, statusBarSize+actionBarHeight, navBarSize.x, 0);
                        }else{
                            // !scrollDirectionIsVertical
                            // 스크롤이 세로방향이면 패딩에서 잘려나가도록 한다
                            if (listView instanceof ListView) {
                                ((ListView) listView).setClipToPadding(false);

                            } else if (listView instanceof ScrollView) {
                                ((ScrollView) listView).setClipToPadding(false);

                            } else if (listView instanceof HorizontalScrollView) {
                                ((HorizontalScrollView) listView).setClipToPadding(true);

                            } else if (listView instanceof RecyclerView) {
                                RecyclerView recyclerView = ((RecyclerView) listView);
                                //RecyclerView는 코드상에서 방향을 확인할 수 있다.
                                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                                if(lm != null){
                                    // 스크롤 방향은 가로/세로 한 방향으로만
                                    if(lm.canScrollVertically()){
                                        recyclerView.setClipToPadding(false);
                                    }else{
                                        recyclerView.setClipToPadding(true);
                                    }
                                }

                            }//Webview의 padding 구현에 문제가 있어 다른 방법으로 넣어야합니다.
//                            else if (listView instanceof MarkdownView){
//                                ((MarkdownView) listView).setClipToPadding(!scrollDirectionIsVertical);
//                            } else if (listView instanceof WebView) {
//                                ((WebView) listView).setClipToPadding(!scrollDirectionIsVertical);
//                            }
                            listView.setPadding(0, statusBarSize+actionBarHeight, 0, navBarSize.y);
                        }
                    }
                }
            }
        });

    }

    public static void getRealSize(Point size, Display display) {
        try {
            size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
            size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        }
    }
}
