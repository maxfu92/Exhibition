package com.tgf.exhibition.declaration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.widget.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class DeclarationOrderActivity extends TgfActivity implements BaseOrderFragment.OnFragmentInteractionListener{
    public static final String ORDER_CATALOG_KEY = "order_catalog";
    public static final int ORDER_CATALOG_DFK = 0;
    public static final int ORDER_CATALOG_DFW = 1;
    public static final int ORDER_CATALOG_DQR = 2;
    public static final int ORDER_CATALOG_DPJ = 3;

    private ViewPager mViewPager;
    private ViewPagerIndicator mViewPagerIndicator;

    private List<Fragment> mTabContents = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declaration_order);
        mViewPager = (ViewPager) findViewById(R.id.id_vp);
        mViewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.id_vp_indicator);

        int pageIndex = getIntent().getIntExtra(ORDER_CATALOG_KEY, ORDER_CATALOG_DFK);

        mViewPager.setAdapter(getPagerAdapter());
        mViewPagerIndicator.setViewPager(mViewPager, pageIndex);
    }

    @Override
    protected void onDataRefresh() {
        // TODO: fragements refresh
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private PagerAdapter getPagerAdapter() {
        LoginData loginData = getTgfDelegate().getLoginData();
        mTabContents.add(OrderToBePaidFragment.newInstance(loginData));
        mTabContents.add(OrderToBeServedFragment.newInstance(loginData));
        mTabContents.add(OrderToBeConfirmFragment.newInstance(loginData));
        mTabContents.add(OrderToBeFinishedFragment.newInstance(loginData));

        return new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO: come from BaseOrderFragment.onBackPressed
    }
}
