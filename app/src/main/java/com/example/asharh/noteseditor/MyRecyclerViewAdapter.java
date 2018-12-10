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

       Log.e("MyRecyclerViewAdapter","constructor");

        this.mContext = context;
        this.mList = list;
        this.mCheckBoxState = new SparseBooleanArray();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {

       Log.e("MyRecyclerViewAdapter","onCreateViewHolder()");

        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_layout,parent,false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

       Log.e("MyRecyclerViewAdapter","onBindViewHolder()-setting textViews");

        holder.mTitleTextView.setText(mList.get(position).getTitle());
        holder.mContentTextView.setText(mList.get(position).getContent());
        holder.mListItemDateTextView.setText(mList.get(position).getDateTimeFormattedAsString(mContext,"MMM dd,yyyy"));

        holder.mCheckbox.setTag(position);

       Log.e("MyRecyclerViewAdapter","onBindViewHolder()-Checking deleteActionFlag from MainActivity");

        if (MainActivity.deleteActionFlag){

           Log.e("MyRecyclerViewAdapter","onBindViewHolder()-DeleteActionFlag is true-Show checkboxes");

            //  Log.d("POSITION","getView position: "+position);

            holder.mCheckbox.setVisibility(View.VISIBLE);

           Log.e("MyRecyclerViewAdapter","onBindViewHolder()-Disable onCheckedChangedListener to prevent calling the method on scrolling ");
            //To prevent calling onCheckedChangedListener on scrolling
            holder.mCheckbox.setOnCheckedChangeListener(null);

           Log.e("MyRecyclerViewAdapter","onBindViewHolder()-Setting checkbox state from mCheckBoxState ArrayList");
            if (mCheckBoxState.get(position))
                holder.mCheckbox.setChecked(true);
            else
                holder.mCheckbox.setChecked(false);

        }
        else{
           Log.e("MyRecyclerViewAdapter","onBindViewHolder()-DeleteActionFlag is false-Hide checkboxes");
            holder.mCheckbox.setVisibility(View.GONE);
        }

       Log.e("MyRecyclerViewAdapter","onBindViewHolder()-Set onCheckedChangeListener()");
        holder.mCheckbox.setOnCheckedChangeListener(this);

    }

    @Override
    public int getItemCount() {

       Log.e("MyRecyclerViewAdapter","getItemCount()-returns list's size");
        return mList.size();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {

       Log.e("MyRecyclerViewAdapter","onCheckedChange()");

        int position = (int) buttonView.getTag();


        mCheckBoxState.put(position,isChecked);
       Log.e("MyRecyclerViewAdapter","onCheckedChange()-Checkbox at position "+position+" is set to "+isChecked);


        if (isChecked){
           Log.e("MyRecyclerViewAdapter","onCheckedChange()-Added to userSelection array in MainActivity");
            MainActivity.userSelection.add(mList.get(position));
//            ViewGroup parent = (ViewGroup) buttonView.getParent();
//            parent.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccentLight));

        }

        else{
           Log.e("MyRecyclerViewAdapter","onCheckedChange()-Removed from userSelection array in MainActivity");
            MainActivity.userSelection.remove(mList.get(position));
//            ViewGroup parent = (ViewGroup) buttonView.getParent();
//            parent.setBackgroundColor(getContext().getResources().getColor(R.color.Transparent));

        }

       Log.e("MyRecyclerViewAdapter","onCheckedChange()-actionMode title set to the number of items selected");
        MainActivity.actionMode.setTitle(MainActivity.userSelection.size()+" items selected");


    }




    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleTextView;
        TextView mContentTextView;
        TextView mListItemDateTextView;
        CheckBox mCheckbox;


        public MyViewHolder(View itemView){

            super(itemView);

           Log.e("MyViewHolder Class","constructor");

            mTitleTextView = itemView.findViewById(R.id.mTitleTextView);
            mContentTextView = itemView.findViewById(R.id.mContentTextView);
            mListItemDateTextView = itemView.findViewById(R.id.mListItemDateTextView);
            mCheckbox = itemView.findViewById(R.id.mCheckbox);

        }


    }


}
