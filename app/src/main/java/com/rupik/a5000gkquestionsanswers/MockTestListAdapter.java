package com.rupik.a5000gkquestionsanswers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by macmin5 on 21/02/17.
 */

public class MockTestListAdapter extends BaseAdapter {

    Context context;
    ArrayList<MockTestListItem> mockTestLists;
    private static LayoutInflater inflater=null;

    public MockTestListAdapter(Context context, ArrayList<MockTestListItem> mockTestLists)
    {
        this.context = context;
        this.mockTestLists = mockTestLists;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mockTestLists.size();
    }

    @Override
    public Object getItem(int i) {
        return mockTestLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {
        TextView titleTV;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.mock_test_lists_cell, null);
        holder.titleTV = (TextView) rowView.findViewById(R.id.MockTestTitleTV);
        MockTestListItem item = (MockTestListItem)getItem(i);
        holder.titleTV.setText(item.getTitle());
        return rowView;
    }
}
