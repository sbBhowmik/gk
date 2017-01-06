package com.rupik.a5000gkquestionsanswers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by macmin5 on 14/11/16.
 */
public class QuizListAdapter extends BaseAdapter {
    Context context;
    ArrayList<GKItem> dataSet;
    private static LayoutInflater inflater=null;

    public QuizListAdapter(Context context, ArrayList<GKItem> dataSet)
    {
        this.context = context;
        this.dataSet = dataSet;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView questionsTV;
        TextView answerTV;
        ImageButton favBtn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.gk_list_item_cell, null);

        holder.questionsTV = (TextView) rowView.findViewById(R.id.gkQuestionTV);
        holder.answerTV = (TextView) rowView.findViewById(R.id.gkAnswerTV);
        holder.favBtn = (ImageButton) rowView.findViewById(R.id.gkFavIB);

        final GKItem item = (GKItem) getItem(position);
        holder.questionsTV.setText(item.getQuestion());
        if(item.isCurrentAffairsType)
        {
            holder.answerTV.setVisibility(View.GONE);
        }
        else {
            holder.answerTV.setVisibility(View.VISIBLE);
            holder.answerTV.setText(item.getAnswer());
        }

        if(item.isFavourite())
        {
            holder.favBtn.setImageResource(R.drawable.fav_icon_selected);
        }
        else {
            holder.favBtn.setImageResource(R.drawable.fav_icon);
        }

        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAdded = false;
                //
                SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                boolean isProfileUpdated = sp.getBoolean("isProfileUpdated", false);
                if(!isProfileUpdated)
                {
                    Intent i = new Intent(context, UserProfileActivity.class);
                    context.startActivity(i);
                    return;
                }
                //
                if(item.isFavourite())
                {
                    item.setFavourite(false);
                    holder.favBtn.setImageResource(R.drawable.fav_icon);
                    isAdded = false;
                }
                else {
                    item.setFavourite(true);
                    holder.favBtn.setImageResource(R.drawable.fav_icon_selected);
                    isAdded = true;
                }
                updateFavouritesInSharedPrefs(isAdded, item.getIdentifier(), item.isCurrentAffairsType, item.getDateType());
            }
        });

        return rowView;
    }

    void updateFavouritesInSharedPrefs(boolean isAdded, String identifier, boolean isCurrentAffairsType, int dateType)
    {
        SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String favSize;
        String favName;
        if(isCurrentAffairsType)
        {
            if(dateType==1) {
                favSize = "fav_size_CA_DateType";
                favName = "favourite_CA_DateType";
            }
            else {
                favSize = "fav_size_CA";
                favName = "favourite_CA_";
            }
        }
        else {
            favSize = "fav_size";
            favName = "favourite_";
        }
        int fav_size = sp.getInt(favSize,0);
        if(fav_size==0 && !isAdded)
        {
            return; //something is fishy
        }
        if(isAdded)
        {
            fav_size+=1;
            editor.putInt(favSize,fav_size);
            editor.putString(favName+Integer.toString(fav_size), identifier);
        }
        else {
            for(int i=1;i<=fav_size;i++)
            {
                String storedId = sp.getString(favName+Integer.toString(i),"");
                if(storedId.contains(identifier))
                {
                    int counter = i;
                    for(int j=i+1;j<=fav_size;j++)
                    {
                        storedId = sp.getString(favName+Integer.toString(j),"");
                        editor.putString(favName+Integer.toString(counter), storedId);
                        counter+=1;
                    }
                    editor.remove(favName+Integer.toString(fav_size));
                    fav_size-=1;
                    editor.putInt(favSize,fav_size);
                    break;
                }
            }
        }
        editor.commit();
    }
}