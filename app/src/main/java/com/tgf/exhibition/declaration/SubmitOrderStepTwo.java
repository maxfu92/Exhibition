package com.tgf.exhibition.declaration;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tgf.exhibition.R;
import com.tgf.exhibition.http.json.DeclarationItem;

/**
 * Created by jeff on 2016/5/17.
 */
class SubmitOrderStepTwo implements SubmitOrderActivity.Step<DeclarationItem> {
    private final ListView mListView;
    private final StepTwoAdapter mStepTwoAdapter;

    protected SubmitOrderStepTwo(ListView listView, DeclarationItem[] declarationItems) {
        mListView = listView;
        mStepTwoAdapter = new StepTwoAdapter(listView.getContext(), declarationItems);
    }

    @Override
    public void goStep(final SubmitOrderActivity.OnStepFinishedListener<DeclarationItem> listener) {
        mListView.setAdapter(mStepTwoAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener != null) {
                    listener.OnStepFinished(mStepTwoAdapter.getItem(position));
                }
            }
        });
    }
}

class StepTwoAdapter extends AbsSubmitOrderAdpater {

    private DeclarationItem[] mDeclarationItems;

    public StepTwoAdapter(Context context, DeclarationItem[] declarationItems) {
        super(context);
        mDeclarationItems = declarationItems;
    }

    @Override
    public int getCount() {
        return mDeclarationItems.length;
    }

    @Override
    public DeclarationItem getItem(int position) {
        return mDeclarationItems[position];
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(getItem(position).itemId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item, null);
            convertView.findViewById(android.R.id.icon).setVisibility(View.GONE);

            ViewGroup.LayoutParams layoutParams = new ListView.LayoutParams(
                    ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
            layoutParams.height = dp2px(50);
            convertView.setLayoutParams(layoutParams);
        }

        TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
        tv.setText(mDeclarationItems[position].displayTitle);
        return convertView;
    }
}