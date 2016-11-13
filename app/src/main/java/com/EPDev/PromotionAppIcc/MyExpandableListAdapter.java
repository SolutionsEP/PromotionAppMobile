package com.EPDev.PromotionAppIcc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by soluciones on 12-08-2016.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter{

    private Context context;
    private ArrayList<ParentRow> parentRowList;
    private ArrayList<ParentRow> originalList;

    public MyExpandableListAdapter(Context context,
                                   ArrayList<ParentRow> originalList) {
        this.context = context;
        this.parentRowList = new ArrayList<>();
        this.parentRowList.addAll(originalList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(originalList);
    }

    @Override
    public int getGroupCount() {
        return parentRowList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return parentRowList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentRowList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentRowList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentRow parentRow = (ParentRow) getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_row, null);
        }

        TextView heading = (TextView) convertView.findViewById(R.id.parent_text);

        heading.setText(parentRow.getName().trim());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildRow childRow = (ChildRow) getChild(groupPosition, childPosition);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_row, null);
        }

        ImageView childIcon = (ImageView) convertView.findViewById(R.id.child_icon);
        Picasso.with(context).load(childRow.getIcon()).into(childIcon);

        final TextView childText = (TextView) convertView.findViewById(R.id.child_text);
        childText.setText(childRow.getText().trim());

        final TextView childDescription = (TextView) convertView.findViewById(R.id.child_description);
        childDescription.setText(childRow.getDescription().trim());

        final TextView childStoreId = (TextView) convertView.findViewById(R.id.store_id);
        childStoreId.setText(childRow.getStoreId().trim());

        //set onclicks
        final View finalConvertView = convertView;
        childText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OnclickRow(finalConvertView,childStoreId);
            }
        });

        childIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OnclickRow(finalConvertView,childStoreId);
            }
        });

        return convertView;
    }


    public void OnclickRow(final View finalConvertView, final TextView childStoreId){
        //Logic to the click store
        Log.e("START_STORES_DETAIL","Store Detail view");

        Intent intent = new Intent(context,StoreDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("store_selected_id",childStoreId.getText().toString());
        intent.putExtras(bundle);

        context.startActivity(intent);

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterData(String query){
        query = query.toLowerCase();
        parentRowList.clear();

        if(query.isEmpty()){
            parentRowList.addAll(originalList);
        } else {
            for (ParentRow parentRow: originalList){
                ArrayList<ChildRow> childList = parentRow.getChildList();
                ArrayList<ChildRow> newList = new ArrayList<ChildRow>();

                for (ChildRow childRow: childList){
                    if (childRow.getText().toLowerCase().contains(query)){
                        newList.add(childRow);
                    }
                }//end for (ChildRow childRow: childList)

                if (newList.size() > 0){
                    ParentRow nParentRow = new ParentRow(parentRow.getName(), newList);
                    parentRowList.add(nParentRow);
                }
            }//end  for (ParentRow parentRow: originalList)
        }//end else

        notifyDataSetChanged();
    }
}
