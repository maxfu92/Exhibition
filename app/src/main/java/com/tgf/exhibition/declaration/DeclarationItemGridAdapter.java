package com.tgf.exhibition.declaration;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.R;
import com.tgf.exhibition.declaration.SubmitOrderActivity;
import com.tgf.exhibition.http.json.DeclarationType;
import com.tgf.exhibition.util.ViewHolder;

/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class DeclarationItemGridAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
	private Context mContext;
    private DeclarationType[] mDeclarationTypes;

    /*
	private static final String[] sImgText = {
            "特装施工", "加班", "物流", "用电",
            "网络", "给排水", "电话", "压缩空气",
            "吊点服务", "车辆证件","人员证件", "自定义",};
	private static final int[] sImgs = {
            R.mipmap.btn_tzsg_normal, R.mipmap.btn_jb_normal, R.mipmap.btn_jb_normal, R.mipmap.ic_yd,
			R.mipmap.btn_net_normal, R.mipmap.btn_gps_normal, R.mipmap.btn_tel_normal, R.mipmap.btn_yskq_normal,
			R.mipmap.btn_yskq_normal, R.mipmap.btn_clzj_normal, R.mipmap.btn_ryzj_normal, R.mipmap.btn_ryzj_normal};
    */

	public DeclarationItemGridAdapter(Context mContext, DeclarationType[] data) {
		super();
		this.mContext = mContext;
        mDeclarationTypes = data;
	}

    @Override
	public int getCount() {
		return mDeclarationTypes.length;
	}

	@Override
	public DeclarationType getItem(int position) {
		return mDeclarationTypes[position];
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
        DeclarationType item = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();

        tv.setText(item.displayTitle);
        //iv.setBackgroundResource(sImgs[position]);
        imageLoader.displayImage(item.iconUrl, iv);

		return convertView;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, SubmitOrderActivity.class);
        intent.putExtra("applyType", getItem(position));
        mContext.startActivity(intent);
    }
}
