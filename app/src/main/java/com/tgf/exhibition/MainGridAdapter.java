package com.tgf.exhibition;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.json.AttachedModule;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.json.XCScene;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.util.AnimateFirstDisplayListener;
import com.tgf.exhibition.util.ViewHolder;
import com.tgf.exhibition.widget.BadgeView;

import java.util.HashMap;
import java.util.Map;

public class MainGridAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
	private Context mContext;
	private AttachedModule[] mAttachedModules;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private AnimateFirstDisplayListener animateFirstDisplayListener = new AnimateFirstDisplayListener();

	private static final Map<String, Class<?>> sActions = new HashMap<String, Class<?>>();
    private static final Map<String,DisplayImageOptions> sDisplayOptions = new HashMap<String, DisplayImageOptions>();
    private static final String[] sNames = {
            "xunchang", "apply_staff", "apply_guest",
            "stat", "message",
            "xunchang_control", "log",
            "rectification", "permission", "prepay_ctrl" };
	private static final int[] sImages = {
            R.mipmap.btn_xc_normal, R.mipmap.btn_sb_normal, R.mipmap.btn_sb_normal,
			R.mipmap.btn_tj_normal, R.mipmap.btn_xx_normal,
			R.mipmap.btn_xcgl_normal, R.mipmap.btn_jgjl_normal,
			R.mipmap.btn_zggl_normal, R.mipmap.btn_cksq_normal, R.mipmap.btn_yfkgl_normal };

    static {
        for(int i=0; i<sNames.length; i++) {
            sDisplayOptions.put(sNames[i], new DisplayImageOptions.Builder()
                    .showStubImage(sImages[i])          // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(sImages[i])  // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(sImages[i])       // 设置图片加载或解码过程中发生错误显示的图片
                    .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中
                    .displayer(new RoundedBitmapDisplayer(20))  // 设置成圆角图片
                    .build());
        }
//		sActions.put("xunchang", null);
		sActions.put("apply_staff", com.tgf.exhibition.declaration2.DeclarationActivity.class);
		sActions.put("apply_guest", com.tgf.exhibition.declaration.DeclarationActivity.class);
//		sActions.put("stat", null);
//		sActions.put("message", null);
//		sActions.put("xunchang_control", null);
//		sActions.put("log", null);
//		sActions.put("rectification", null);
//		sActions.put("permission", null);
		sActions.put("prepay_ctrl", com.tgf.exhibition.declaration.PrepayLogActivity.class);
	}

	public MainGridAdapter(Context context) {
		super();
		mContext = context;
	}

	public void setAttachedModules(AttachedModule[] attachedModules) {
		mAttachedModules = attachedModules;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mAttachedModules==null ? 0 : mAttachedModules.length;
	}

	@Override
	public AttachedModule getItem(int position) {
		return mAttachedModules==null ? null : mAttachedModules[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grid_item, parent, false);
		}
		TextView tv = ViewHolder.get(convertView, R.id.tv_item);
		ImageView iv = ViewHolder.get(convertView, R.id.iv_item);

        AttachedModule item = getItem(position);
        //iv.setBackgroundResource(sImages[position]);
        tv.setText(item.title);
        /**
         * 显示图片
         * 参数1：图片url
         * 参数2：显示图片的控件
         * 参数3：显示图片的设置
         * 参数4：监听器
         */
        mImageLoader.displayImage(item.icon, iv, sDisplayOptions.get(item.name), animateFirstDisplayListener);
        showBadgeIfNeeded(iv, item.msgCount);

		return convertView;
	}

    private void showBadgeIfNeeded(View target, String stringCount) {
        BadgeView badge = (BadgeView)target.getTag();
        int count = Integer.parseInt(stringCount);
        if(count>0) {
            if(badge == null) {
                badge = new BadgeView(mContext, target);
                badge.setBadgeMargin(0);
                target.setTag(badge);
            }
            badge.setText(stringCount);
            badge.show();
        } else {
            badge = (BadgeView) target.getTag();
            if(badge != null) {
                badge.hide(true);
            }
        }
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AttachedModule item = getItem(position);
		Class<?> actionClass = sActions.get(item.name);
		if(actionClass != null) {
			if (Integer.parseInt(item.msgCount) > 0 && mContext instanceof MainActivity) {
				MainActivity mainActivity = (MainActivity) mContext;
                LoginData loginData = mainActivity.getTgfDelegate().getLoginData();
                XCScene xcScene = mainActivity.getTgfDelegate().getXcScene();
                mainActivity.getTgfDelegate().getDeclarationRequestHandler()
                        .setActionName(item.name)
                        .setUserToken(loginData.userToken)
                        .setSceneId(xcScene.sceneId)
						.setIRequestUrl(DeclarationRequestHandler.DeclarationURL.APPLY_CLEAR_MAN_PAGE_MSG)
						.doHttpRequest(mClearMassageHandler);
			}
            mContext.startActivity(new Intent(mContext, actionClass));
        } else {
            Toast.makeText(mContext, R.string.tips_feature_not_ready, Toast.LENGTH_SHORT).show();
        }
	}
    private ClearMassageHandler mClearMassageHandler = new ClearMassageHandler();
    private static class ClearMassageHandler extends JsonResponseHandler<StringMessage> {
        public ClearMassageHandler() {
            super(StringMessage.class);
        }

        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {

        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {

        }
    }
}
