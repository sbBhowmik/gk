package com.rupik.a5000gkquestionsanswers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by macmin5 on 20/02/17.
 */


public class MockTestAllAnswersAdapter extends BaseAdapter {

    Context context;
    ArrayList<MCQItem> mcqDataList;
    private static LayoutInflater inflater=null;

    public MockTestAllAnswersAdapter(Context context, ArrayList<MCQItem> dataSet)
    {
        this.context = context;
        this.mcqDataList = dataSet;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mcqDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mcqDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {
        TextView questionsTV;
        TextView answerTV;
        TextView answerExplainedTV;
        TextView questionsTitleTV;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.mock_test_all_answers_cell, null);

        holder.questionsTV = (TextView) rowView.findViewById(R.id.mockTestAllAnswersCellQTextView);
        holder.answerTV = (TextView) rowView.findViewById(R.id.mockTestAllAnswersCellAnsTextView);
        holder.answerExplainedTV = (TextView) rowView.findViewById(R.id.mockTestAllAnswersCellAnsExpTextView);
        holder.questionsTitleTV = (TextView) rowView.findViewById(R.id.qTitle);

        MCQItem item = (MCQItem)getItem(i);

        holder.questionsTitleTV.setText("Question "+ Integer.toString(i+1));
        holder.questionsTV.setText(item.getMcqQuestion());
        holder.answerTV.setText("Correct Answer: " + item.getAnswer());
        holder.answerExplainedTV.setText(item.getDetailedExplanation());

        return rowView;
    }
}
