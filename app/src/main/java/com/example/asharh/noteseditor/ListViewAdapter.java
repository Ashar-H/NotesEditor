package com.example.asharh.noteseditor;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Note> implements CompoundButton.OnCheckedChangeListener{

    public SparseBooleanArray mCheckBoxState;
    private List<Note> mList;
   // private static List<Note> userSelection = new ArrayList<>();
    private Context mContext;
   // private static ActionMode actionMode = null;


    public ListViewAdapter(Context context,List<Note> list){


        super(context,R.layout.listitem_layout,list);
        this.mContext = context;
        this.mList = list;
        this.mCheckBoxState = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View row = LayoutInflater.from(mContext).inflate(R.layout.listitem_layout,parent,false);


        TextView mTitleTextView = row.findViewById(R.id.mTitleTextView);
        TextView mContentTextView = row.findViewById(R.id.mContentTextView);
        TextView mListItemDateTextView = row.findViewById(R.id.mListItemDateTextView);
        CheckBox checkBox = row.findViewById(R.id.mCheckbox);


        mTitleTextView.setText(mList.get(position).getTitle());
        mContentTextView.setText(mList.get(position).getContent());
        mListItemDateTextView.setText(mList.get(position).getDateTimeFormattedAsString(getContext(),"MMM dd,yyyy"));

        checkBox.setTag(position);

        if (MainActivity.deleteActionFlag){

          //  Log.d("POSITION","getView position: "+position);

            checkBox.setVisibility(View.VISIBLE);

        }
        else{
            checkBox.setVisibility(View.GONE);
        }

        //listView is reloaded on scrolling
        //resetting checkbox last state when the view is refreshed.
        checkBox.setChecked(mCheckBoxState.get(position,false));
        checkBox.setOnCheckedChangeListener(this);

        return row;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {

       // Log.d("TAG","Value: "+isChecked+"at TAG: "+buttonView.getTag());

        int position = (int) buttonView.getTag();

        mCheckBoxState.put(position, isChecked);

        if (isChecked){
            MainActivity.userSelection.add(mList.get(position));

//            ViewGroup parent = (ViewGroup) buttonView.getParent();
//            parent.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccentLight));

        }

        else{
            MainActivity.userSelection.remove(mList.get(position));

//            ViewGroup parent = (ViewGroup) buttonView.getParent();
//            parent.setBackgroundColor(getContext().getResources().getColor(R.color.Transparent));

        }

        MainActivity.actionMode.setTitle(MainActivity.userSelection.size()+" items selected");


    }


}
