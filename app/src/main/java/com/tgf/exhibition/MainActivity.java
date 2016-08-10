package com.tgf.exhibition;

import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.handler.UserCenterRequestHandler;
import com.tgf.exhibition.http.json.AttachedUserInfo;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.json.Scene;
import com.tgf.exhibition.http.json.XCScene;
import com.tgf.exhibition.http.msg.XCSceneMessage;
import com.tgf.exhibition.util.UserPreferences;
import com.tgf.exhibition.widget.LoadingDialog;
import com.tgf.exhibition.widget.MGridView;

public class MainActivity extends TgfActivity {
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private ListPopupWindow mListPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginData loginData = getTgfDelegate().getLoginData();
        if(loginData == null) {
            getTgfDelegate().gotoLoginActivity();
            onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mXCSceneLoader.setAutoSelfLoadData(false);
    }

    @Override
    protected void onDataRefresh() {
        mXCSceneLoader.setAutoSelfLoadData(true);
        XCScene xcScene = getTgfDelegate().getXcScene();
        if(xcScene != null) {
            refreshViews(xcScene);
            loadSceneData(false);
        } else {
            loadSceneData(true);
        }
    }

    private void loadSceneData(boolean showLoading) {
        synchronized (mXCSceneLoader) {
            if(!mXCSceneLoader.isLoadingDialogShown()) {
                LoginData loginData = getTgfDelegate().getLoginData();
                UserCenterRequestHandler iHttpHandler = getTgfDelegate().getUserCenterHandler()
                        .setUserToken(loginData.userToken)
                        .setIRequestUrl(UserCenterRequestHandler.UserCenterURL.GET_XC_SCENE_INFO);
                if(!TextUtils.isEmpty(loginData.loginSceneId)) {
                    iHttpHandler.setSceneId(loginData.loginSceneId);
                }
                mXCSceneLoader.setShowLoading(showLoading);
                iHttpHandler.doHttpRequest(mXCSceneLoader);
            }
        }
    }

    private void refreshViews(XCScene xcScene) {
        initUserView(xcScene.userInfo);
        initScenesView(xcScene);
        initModuleView(xcScene);
    }
    private MGridView mGridview;
    private MainGridAdapter mMainGridAdapter = new MainGridAdapter(this);
    private void initModuleView(XCScene xcScene) {
        if(mGridview == null) {
            mGridview = (MGridView) findViewById(R.id.gridview);
            mGridview.setAdapter(mMainGridAdapter);
            mGridview.setOnItemClickListener(mMainGridAdapter);
        }
        mMainGridAdapter.setAttachedModules(xcScene.attachedModules);
    }

    private ScenesListAdapter mScenesListAdapter = new ScenesListAdapter();
    private void initScenesView(XCScene xcScene) {
        ImageView sceneLogo = (ImageView) findViewById(R.id.iv_scene_logo);
        if(!TextUtils.isEmpty(xcScene.sceneIconUrl)) {
            mImageLoader.displayImage(xcScene.sceneIconUrl, sceneLogo);
        }

        TextView tvSceneName = (TextView)findViewById(R.id.tv_scenes);
        if(tvSceneName != null) {
            tvSceneName.setText(xcScene.sceneTitle);
        }

        View selectScenes = findViewById(R.id.iv_list_scenes);
        if(xcScene.scenes != null && xcScene.scenes.length > 1) {
            if (selectScenes != null) {
                if (mListPopupWindow == null) {
                    mListPopupWindow = new ListPopupWindow(this);
                    mListPopupWindow.setAdapter(mScenesListAdapter);

                    // Register a listener to be notified when an item in our popup window has
                    // been clicked.
                    mListPopupWindow.setOnItemClickListener(mScenesListAdapter);
                    // Set popup window modality based on the current checkbox state.
                    mListPopupWindow.setModal(true);

                    View scenePanel = findViewById(R.id.scene_panel);
                    mListPopupWindow.setAnchorView(scenePanel);
                    scenePanel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListPopupWindow.show();
                        }
                    });
                }
                mScenesListAdapter.setScenes(xcScene.scenes);
            }
        } else {
            selectScenes.setVisibility(View.GONE);
        }
    }

    private void initUserView(AttachedUserInfo userInfo) {
        if(!TextUtils.isEmpty(userInfo.headPictureUrl)) {
            ImageLoader.getInstance().displayImage(userInfo.headPictureUrl, (ImageView)findViewById(R.id.iv_user_photo));
        }

        TextView v = (TextView)findViewById(R.id.tv_realname);
        v.setText(userInfo.realname);
        v = (TextView)findViewById(R.id.tv_phone);
        v.setText(userInfo.phoneNumber);

        findViewById(R.id.layout_user_panel).setOnClickListener(mGotoRegisterListener);
    }

    private View.OnClickListener mGotoRegisterListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            getTgfDelegate().gotoRegisterActivity();
        }
    };

    private void recordUserLoginScene(XCScene xcScene) {
        UserPreferences userPreferences = UserPreferences.createUserPreferences(MainActivity.this, xcScene.userInfo.id);
        userPreferences.recordUserInfo(xcScene.userInfo);
        userPreferences.recordUserLoginScene(xcScene.sceneId);
        getTgfDelegate().putLoginData(userPreferences.retreiveLoginData());
        getTgfDelegate().putXcScene(xcScene);
        getTgfDelegate().putUserPreferences(userPreferences);
    }

    private XCSceneLoader mXCSceneLoader = new XCSceneLoader();
    private class XCSceneLoader extends JsonResponseHandler<XCSceneMessage> implements Runnable {
        private boolean mShowLoading;

        public XCSceneLoader() {
            super(XCSceneMessage.class);
        }

        @Override
        public void onStart() {
            if(mShowLoading) {
                setLoadingDialog(new LoadingDialog(MainActivity.this));
            } else {
                setLoadingDialog(null);
            }
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(XCSceneMessage xcSceneMessage, String rawJsonResponse) {
            if(xcSceneMessage.statusCode == 0) {
                XCScene xcScene = xcSceneMessage.data;
                getTgfDelegate().putXcScene(xcScene);
                refreshNavigationView();

                TextView message = (TextView) findViewById(R.id.tv_message);
                if ("no_scene".equals(xcScene.status)) {
                    findViewById(R.id.scene_panel).setVisibility(View.GONE);
                    findViewById(R.id.modules_panel).setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(xcScene.message);
                } else {
                    findViewById(R.id.scene_panel).setVisibility(View.VISIBLE);
                    findViewById(R.id.modules_panel).setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);

                    recordUserLoginScene(xcScene);

                    findViewById(R.id.modules_panel).postDelayed(this, 30 * DateUtils.SECOND_IN_MILLIS);
                }

                refreshViews(xcScene);
            } else if(xcSceneMessage.statusCode == 1001) {
                getTgfDelegate().logoutUser();
                getTgfDelegate().gotoLoginActivity();
            } else {
                getTgfDelegate().postTipsMassage(xcSceneMessage.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_data_load_faild);
        }

        public void setShowLoading(boolean showLoading) {
            mShowLoading = showLoading;
        }

        @Override
        public void run() {
            if(autoSelfLoading) {
                loadSceneData(false);
            }
        }

        private boolean autoSelfLoading = true;
        public void setAutoSelfLoadData(boolean enable) {
            autoSelfLoading = enable;
        }
    }

    private class ScenesListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
        private Scene[] mScenes;

        public ScenesListAdapter() {
        }

        public void setScenes(Scene[] scenes) {
            mScenes = scenes;
            if(mScenes == null) {
                return;
            }
            for(int i = 0; i< mScenes.length; i++) {
                if("Y".equals(mScenes[i].isSelected)) {
                    mListPopupWindow.setSelection(i);
                    break;
                }
            }
            notifyDataSetChanged();
        }

        class ViewHolder {
            private ImageView icon;
            private TextView title;
        }

        @Override
        public int getCount() {
            return mScenes==null? 0 : mScenes.length;
        }

        @Override
        public Scene getItem(int position) {
            return mScenes==null? null : mScenes[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                       R.layout.list_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(android.R.id.text1);
                viewHolder.icon = (ImageView) convertView.findViewById(android.R.id.icon);
                convertView.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.title.setText(mScenes[position].sceneTitle);
            mImageLoader.displayImage(mScenes[position].sceneIconUrl, viewHolder.icon);
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Scene selectedScene = getItem(position);

            TextView viewById = (TextView)findViewById(R.id.tv_scenes);
            if(viewById != null) {
                viewById.setText(selectedScene.sceneTitle);
            }
            mListPopupWindow.dismiss();

            LoginData loginData = getTgfDelegate().getLoginData();
            loginData.loginSceneId = selectedScene.sceneId;
            UserPreferences userPreferences = UserPreferences.createUserPreferences(MainActivity.this, loginData.userId);
            userPreferences.recordUserLoginScene(selectedScene.sceneId);
            loadSceneData(true);
        }
    }
}
