package com.tgf.exhibition.declaration;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.json.AreaWorkObject;
import com.tgf.exhibition.http.json.WorkObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 2016/5/17.
 */
class SubmitOrderStepOne implements SubmitOrderActivity.Step<WorkObject> {
    private final ListView mListView;
    private StepOneAdapter mStepOneAdapter;

    protected SubmitOrderStepOne(ListView listView, AreaWorkObject[] data) {
        mListView = listView;
        mStepOneAdapter = new StepOneAdapter(listView.getContext(), data);
    }

    @Override
    public void goStep(final SubmitOrderActivity.OnStepFinishedListener<WorkObject> listener) {
        mListView.setAdapter(mStepOneAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = mStepOneAdapter.getItem(position);
                if(listener != null && item instanceof WorkObject) {
                    listener.OnStepFinished((WorkObject) item);
                }
            }
        });
    }
}

class StepOneAdapter extends AbsSubmitOrderAdpater {
    ImageLoader imageLoader = ImageLoader.getInstance();
    List<Object> mGLDianList = new ArrayList<Object>();
    private int[] mTextColors = new int[2];

    StepOneAdapter(Context context, AreaWorkObject[] data) {
        super(context);
        Resources resources = mContext.getResources();
        mTextColors[0] = resources.getColor(R.color.colorPrimaryDark);
        mTextColors[1] = resources.getColor(android.R.color.primary_text_light);

        for(AreaWorkObject obj : data) {
            mGLDianList.add(obj);
            for(WorkObject wobj : obj.workObjects) {
                mGLDianList.add(wobj);
            }
        }
    }

    @Override
    public int getCount() {
        return mGLDianList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGLDianList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Object item = getItem(position);
        if(item instanceof WorkObject) {
            return Long.valueOf(((WorkObject) item).objectId);
        }
        return -1L;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position) instanceof WorkObject;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object item = getItem(position);
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item, null);
        }

        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        if(layoutParams==null) {
            layoutParams = new ListView.LayoutParams(
                    ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
        }

        ImageView iv = (ImageView) convertView.findViewById(android.R.id.icon);
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);;
        if(item instanceof WorkObject) {
            iv.setVisibility(View.VISIBLE);
            WorkObject wobj = (WorkObject)item;
            imageLoader.displayImage(wobj.owner_icon, iv);
            convertView.setBackgroundResource(R.drawable.grid_item_inner_bg);
            layoutParams.height = dp2px(50);
            tv.setTextColor(mTextColors[1]);
            tv.setText(wobj.title);
        } else {
            iv.setVisibility(View.GONE);
            layoutParams.height = dp2px(35);
            convertView.setBackgroundResource(R.color.colorAccent);
            tv.setTextColor(mTextColors[0]);
            tv.setText(((AreaWorkObject)item).areaTitle);
        }
        convertView.setLayoutParams(layoutParams);


        return convertView;
    }
}
