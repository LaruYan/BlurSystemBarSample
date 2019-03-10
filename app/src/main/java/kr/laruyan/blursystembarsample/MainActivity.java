package kr.laruyan.blursystembarsample;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import io.alterac.blurkit.BlurLayout;

public class MainActivity extends AppCompatActivity {

    private byte fullScreenFlags; // 전체화면 플래그를 기억할 변수
    private BlurLayout blDummyStatusBar;
    private BlurLayout blDummyToolBar;
    private BlurLayout blDummyNavBarPort;
    private BlurLayout blDummyNavBarLandLeft;
    private BlurLayout blDummyNavBarLandRight;
    private View vwDummyStatusBar;
    private View vwDummyToolBar;
    private View vwDummyNavBarPort;
    private View vwDummyNavBarLandLeft;
    private View vwDummyNavBarLandRight;


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

        vwStatusBarAffecteds = new View[] {toolbar,contentView};
        vwNavBarAffecteds =  new View[] {contentView};

        // 전체화면 활용 처리
        toolbar.setOnSystemUiVisibilityChangeListener(visibility -> setFullScreenMode((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) );
        setFullScreenMode(true);

        toolbar.post(()->VisibleEstatesUtil.setScrollableTransparentPaddings(MainActivity.this,safeArea,new View[]{contentView}, toolbar));
    }

    @Override
    protected void onStart() {
        super.onStart();

        blDummyStatusBar.startBlur();
        blDummyToolBar.startBlur();
        blDummyNavBarPort.startBlur();
        blDummyNavBarLandLeft.startBlur();
        blDummyNavBarLandRight.startBlur();
    }

    @Override
    protected void onStop() {
        super.onStop();

        blDummyStatusBar.pauseBlur();
        blDummyToolBar.pauseBlur();
        blDummyNavBarPort.pauseBlur();
        blDummyNavBarLandLeft.pauseBlur();
        blDummyNavBarLandRight.pauseBlur();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreenMode(fullScreenFlags >= 0); //trigger last known systemuivisibility
    }

    private void setFullScreenMode(boolean isStatusBarVisible) {
        toolbar.post(() -> fullScreenFlags = VisibleEstatesUtil.setFullScreenMode(
                MainActivity.this,
                isStatusBarVisible,
                drawer,
                toolbar.getHeight(),
                vwDummyToolBar,
                null,
                vwDummyStatusBar,
                vwDummyNavBarPort,
                vwDummyNavBarLandRight,
                true,
                null,
                null));
    }

}
