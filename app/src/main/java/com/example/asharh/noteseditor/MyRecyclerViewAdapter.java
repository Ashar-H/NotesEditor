package com.example.asharh.noteseditor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> implements CompoundButton.OnCheckedChangeListener{

    public static SparseBooleanArray mCheckBoxState;
    private static ArrayList<Note> mList;
    // private static List<Note> userSelection = new ArrayList<>();
    private Context mContext;

    public MyRecyclerViewAdapter(Context context,ArrayList<Note> list){

        this.mContext = context;
        this.mList = list;
        this.mCheckBoxState = new SparseBooleanArray();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_layout,parent,false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

        holder.mTitleTextView.setText(mList.get(position).getTitle());
        holder.mContentTextView.setText(mList.get(position).getContent());
        holder.mListItemDateTextView.setText(mList.get(position).getDateTimeFormattedAsString(mContext,"MMM dd,yyyy"));

        holder.mCheckbox.setTag(position);

        if (MainActivity.deleteActionFlag){

            //  Log.d("POSITION","getView position: "+position);

            holder.mCheckbox.setVisibility(View.VISIBLE);

            //To prevent calling onCheckedChangedListener on scrolling
            holder.mCheckbox.setOnCheckedChangeListener(null);

            if (mCheckBoxState.get(position))
                holder.mCheckbox.setChecked(true);
            else
                holder.mCheckbox.setChecked(false);

        }
        else{
            holder.mCheckbox.setVisibility(View.GONE);
        }

        holder.mCheckbox.setOnCheckedChangeListener(this);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {


        int position = (int) buttonView.getTag();




        mCheckBoxState.put(position,isChecked);
        Log.d("TAG","Value: "+isChecked+" at TAG: "+position);


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




    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleTextView;
        TextView mContentTextView;
        TextView mListItemDateTextView;
        CheckBox mCheckbox;


        public MyViewHolder(View itemView){

            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.mTitleTextView);
            mContentTextView = itemView.findViewById(R.id.mContentTextView);
            mListItemDateTextView = itemView.findViewById(R.id.mListItemDateTextView);
            mCheckbox = itemView.findViewById(R.id.mCheckbox);

        }


    }


}
