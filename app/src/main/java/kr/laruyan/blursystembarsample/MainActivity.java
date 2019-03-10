package kr.laruyan.blursystembarsample;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.live.LiveBlurWorker;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;


public class MainActivity extends AppCompatActivity {

    private byte fullScreenFlags; // 전체화면 플래그를 기억할 변수

    private View blDummyStatusBar;
    private View blDummyToolBar;
    private View blDummyNavBarPort;
    private View blDummyNavBarLandLeft;
    private View blDummyNavBarLandRight;

    private View vwDummyStatusBar;
    private View vwDummyToolBar;
    private View vwDummyNavBarPort;
    private View vwDummyNavBarLandLeft;
    private View vwDummyNavBarLandRight;


    private LiveBlurWorker blurWorkerUpper;
    private LiveBlurWorker blurWorkerDowner;

    private Thread threadWorker;
    private boolean isBlurRequired = false;
    private boolean isBlurDaemon = true;

    private Toolbar toolbar;
    private Drawer drawer;
    private ScrollView contentView;
    private View safeArea;

    private View[] vwDummyStatusBars;
    private View[] vwDummyToolBars;
    private View[] vwDummyNavBarPorts;
    private View[] vwDummyNavBarLandLefts;
    private View[] vwDummyNavBarLandRights;

    private View[] vwStatusBarAffecteds;
    private View[] vwNavBarAffecteds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        blDummyStatusBar = findViewById(R.id.bl_dummy_statusBar);
        blDummyToolBar = findViewById(R.id.bl_dummy_toolbar);
        blDummyNavBarPort = findViewById(R.id.bl_dummy_navBarPort);
        blDummyNavBarLandLeft = findViewById(R.id.bl_dummy_navBarLandLeft);
        blDummyNavBarLandRight = findViewById(R.id.bl_dummy_navBarLandRight);

        vwDummyStatusBar = findViewById(R.id.vw_dummy_statusBar);
        vwDummyToolBar = findViewById(R.id.vw_dummy_toolbar);
        vwDummyNavBarPort = findViewById(R.id.vw_dummy_navBarPort);
        vwDummyNavBarLandLeft = findViewById(R.id.vw_dummy_navBarLandLeft);
        vwDummyNavBarLandRight = findViewById(R.id.vw_dummy_navBarLandRight);

        contentView = (ScrollView) findViewById(R.id.content_view);
        safeArea = findViewById(R.id.safe_area);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true) // 드로어의 확장을 장려합니다.
                .withTranslucentNavigationBar(true) // 드로어의 확장을 장려합니다.
                .build();

        //드로어 항목
        for(int items = 0; items < 64; items++) {
            drawer.addItem(new DividerDrawerItem());
        }



        // 액션바 아이콘
        toolbar.setNavigationOnClickListener((v) -> this.drawer.openDrawer());
        toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        // 액션바 제목
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.white));

        vwDummyStatusBars = new View[] {blDummyStatusBar,vwDummyStatusBar};
        vwDummyToolBars = new View[] {blDummyToolBar,vwDummyToolBar};
        vwDummyNavBarPorts = new View[] {blDummyNavBarPort,vwDummyNavBarPort};
        vwDummyNavBarLandLefts = new View[] {blDummyNavBarLandLeft,vwDummyNavBarLandLeft};
        vwDummyNavBarLandRights = new View[] {blDummyNavBarLandRight,vwDummyNavBarLandRight};

        vwStatusBarAffecteds = new View[] {toolbar,contentView};
        vwNavBarAffecteds =  new View[] {contentView};

        // 흐림효과 처리
        //  width / height 0인 뷰는 강제종료를 유발
        blurWorkerUpper = Dali.create(MainActivity.this).liveBlur(contentView,blDummyStatusBar,blDummyToolBar).downScale(8).assemble(true);
        blurWorkerDowner = Dali.create(MainActivity.this).liveBlur(contentView,blDummyNavBarPort).downScale(8).assemble(true);

        contentView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                blurWorkerUpper.updateBlurView();
                blurWorkerDowner.updateBlurView();

            }
        });
//        threadWorker = new Thread(new Runnable(){
//            @Override
//            public void run() {
//                while(isBlurDaemon) {
//                    if(isBlurRequired) {
//                        runOnUiThread(()->{blurWorkerUpper.updateBlurView();});
//                    }
//                    android.os.SystemClock.sleep(16);
//                }
//            }
//        });
//        threadWorker.start();

        // 전체화면 활용 처리
        toolbar.setOnSystemUiVisibilityChangeListener(visibility -> setFullScreenMode((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) );
        setFullScreenMode(true);

        toolbar.post(()->VisibleEstatesUtil.setScrollableTransparentPaddings(MainActivity.this,safeArea,new View[]{contentView}, toolbar));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreenMode(fullScreenFlags >= 0); //trigger last known systemuivisibility
        isBlurRequired = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBlurRequired = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBlurDaemon = false;
    }

    private void setFullScreenMode(boolean isStatusBarVisible) {
        toolbar.post(() -> fullScreenFlags = VisibleEstatesUtil.setFullScreenMode(
                MainActivity.this,
                isStatusBarVisible,
                drawer,
                toolbar.getHeight(),
                vwDummyToolBars,
                null,
                vwDummyStatusBars,
                vwDummyNavBarPorts,
                vwDummyNavBarLandRights,
                true,
                null,
                null));
    }

}
