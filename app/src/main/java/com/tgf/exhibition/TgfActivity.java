package com.tgf.exhibition;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.http.json.AttachedUserInfo;
import com.tgf.exhibition.http.json.XCScene;

import cn.jpush.android.api.JPushInterface;

public abstract class TgfActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTitleView;
    private NavigationView mNavigationView;
    private TgfDelegate mTgfDelegate;

    public TgfDelegate getTgfDelegate() {
        if (mTgfDelegate == null) {
            mTgfDelegate = TgfDelegate.create(this);
        }
        return mTgfDelegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_tgf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        mTitleView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        setTitle(getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if(!(this instanceof  MainActivity)) {
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
//                Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//                upArrow.setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
//                getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        refreshNavigationView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTgfDelegate != null) {
            mTgfDelegate.getDeclarationRequestHandler().cancelAllHttpRequests(true);
            mTgfDelegate.getUserCenterHandler().cancelAllHttpRequests(true);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(this instanceof  MainActivity) {
            refreshNavigationView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this instanceof  MainActivity) {
            refreshNavigationView();
        }
        checkNewVersion();
        onDataRefresh();
        JPushInterface.onResume(this);
    }

    protected abstract void onDataRefresh();

    protected void refreshNavigationView() {
        XCScene xcScene = getTgfDelegate().getXcScene();
        if(xcScene == null) {
            return;
        }
        View headerView = mNavigationView.getHeaderView(0);
        AttachedUserInfo userInfo = xcScene.userInfo;
        ImageView imageView = (ImageView) headerView.findViewById(R.id.iv_user_photo);
        if(!TextUtils.isEmpty(userInfo.headPictureUrl) && imageView != null) {
            ImageLoader.getInstance().displayImage(userInfo.headPictureUrl, imageView);
        }
        getTgfDelegate().setTextViewText(
                (TextView) headerView.findViewById(R.id.tv_user_name),
                userInfo.nickName + "(" + userInfo.phoneNumber + ")");
        String sceneTitle = xcScene.sceneTitle;
        if(TextUtils.isEmpty(xcScene.status) || "no_scene".equals(xcScene.status)) {
            sceneTitle = userInfo.currentCity != null ? userInfo.currentCity : "-";
        }
        getTgfDelegate().setTextViewText((TextView) headerView.findViewById(R.id.tv_scene_name), sceneTitle);

//        if(getTgfDelegate().hasNewVersion(this)) {
//            BadgeView badge = (BadgeView) versionCheckerView.getTag();
//            if(badge == null) {
//                badge = new BadgeView(this, versionCheckerView);
//                badge.setBadgeMargin(0);
//                versionCheckerView.setTag(badge);
//                badge.setText("新");
//            }
//            badge.show();
//        } else {
//            BadgeView badge = (BadgeView) versionCheckerView.getTag();
//            if(badge != null) {
//                badge.hide();
//            }
//        }
    }

    private void checkNewVersion() {
        Menu menu = mNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.nav_version_checker);
        FrameLayout actionView = (FrameLayout) item.getActionView();
        if (getTgfDelegate().hasNewVersion(this)) {
            actionView.setVisibility(View.VISIBLE);
            IconicsDrawable color = new IconicsDrawable(this)
                    .icon(FontAwesome.Icon.faw_download)
                    .color(Color.BLACK).sizeDp(24);
            ActionItemBadge.update(this, item, color, ActionItemBadge.BadgeStyles.RED.getStyle(), "新");
        } else {
            actionView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        ViewGroup contentParent = (ViewGroup) findViewById(R.id.tgf_content);
        contentParent.removeAllViews();
        LayoutInflater.from(this).inflate(layoutResID, contentParent);
    }

    @Override
    public void setContentView(View view) {
        ViewGroup contentParent = (ViewGroup) findViewById(R.id.tgf_content);
        contentParent.removeAllViews();
        contentParent.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams lp) {
        ViewGroup contentParent = (ViewGroup) findViewById(R.id.tgf_content);
        contentParent.removeAllViews();
        contentParent.addView(view, lp);
    }

    @Override
    public void addContentView(View v, ViewGroup.LayoutParams lp) {
        ViewGroup contentParent = (ViewGroup) findViewById(R.id.tgf_content);
        contentParent.addView(v, lp);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tgf_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        getTgfDelegate().logoutUser();
        getTgfDelegate().gotoLoginActivity();
        onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_version_checker) {
            Intent intent = new Intent(this, TgfUpdateService.class);
            intent.putExtra(PackageUpdater.KEY_START_WHAT, PackageUpdater.START_WHAT_VERSION_CHECK);
            intent.putExtra(PackageUpdater.KEY_PROMPT_TYPE,PackageUpdater.PROMPT_TYPE_DIALOG);
            intent.putExtra(PackageUpdater.KEY_SHOW_LOADING, true);
            startService(intent);
        }/* else if (id == R.id.nav_gallery) {

        } */else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }/* else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected SpannableStringBuilder coloredString(String str, int start, int end, int colorResId) {
        if(TextUtils.isEmpty(str)) {
            return new SpannableStringBuilder("");
        }

        SpannableStringBuilder style=new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(colorResId)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
//        style.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
//        style.setSpan(new ForegroundColorSpan(Color.YELLOW), 2, 4,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
//        style.setSpan(new ForegroundColorSpan(Color.GREEN), 4, 6,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        return style;
    }
}
