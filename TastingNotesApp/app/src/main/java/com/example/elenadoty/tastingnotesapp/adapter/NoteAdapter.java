package com.example.elenadoty.tastingnotesapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.elenadoty.tastingnotesapp.R;
import com.example.elenadoty.tastingnotesapp.activities.DetailsActivity;
import com.example.elenadoty.tastingnotesapp.activities.MainScreen;
import com.example.elenadoty.tastingnotesapp.activities.MyPostsActivity;
import com.example.elenadoty.tastingnotesapp.data.BaseEntry;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elenadoty on 11/23/17.
 */


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private Context context;
    private List<BaseEntry> entryList;
    private List<String> entryKeys;
    public static final int MY_POSTS = 0;
    public static final int ALL_POSTS = 1;
    private int postsType;
    private DatabaseReference entryRef;

    public NoteAdapter(Context context, int postsType) {
        this.context = context;
        this.postsType = postsType;

        entryList = new ArrayList<>();
        entryKeys = new ArrayList<>();

        entryRef = FirebaseDatabase.getInstance().getReference();
    }

    public void addNote(BaseEntry newNote, String key){
        entryList.add(newNote);
        entryKeys.add(key);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View newRow = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_row, parent, false);
        return new ViewHolder(newRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final BaseEntry baseEntry = entryList.get(position);
        holder.entryName.setText(baseEntry.getNoteName());

        final String date = baseEntry.getNoteDate().toString();
        holder.entryDate.setText(date.toString());

        holder.entryLocation.setText(baseEntry.getPlaceName());

        if(baseEntry.getImageURL() != null){
            Glide.with(context).load(baseEntry.getImageURL()).into(holder.rowImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("entry_id", baseEntry.getDatabaseID());
                intent.putExtra("image_url", baseEntry.getImageURL());

                ((MainScreen)context).startActivity(intent);
            }
        });

        holder.entryLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display map centered on pin
                ((MainScreen)context).findViewById(R.id.navigation_dashboard).callOnClick();

            }
        });

        switch (postsType){
            case 0:
                holder.tvUserName.setVisibility(View.GONE);
                break;
            case 1:
                holder.tvUserName.setText(baseEntry.getUserName());
                holder.tvUserName.setVisibility(View.VISIBLE);
                break;
            default: break;
        }

    }

    public void removePost(int index) {
        entryRef.child(entryKeys.get(index)).removeValue();
        entryList.remove(index);
        entryKeys.remove(index);
        notifyItemRemoved(index);
    }

    public void removePostByKey(String key) {
        int index = entryKeys.indexOf(key);
        if (index != -1) {
            entryList.remove(index);
            entryKeys.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void removeAll(){
        entryList = new ArrayList<>();
        entryKeys = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView rowImage;
        public TextView entryName;
        public TextView entryDate;
        public TextView entryLocation;
        public TextView tvUserName;

        public ViewHolder(View itemView) {
            super(itemView);
            rowImage = itemView.findViewById(R.id.row_image);
            entryDate = itemView.findViewById(R.id.tvEntryDate);
            entryName = itemView.findViewById(R.id.tvEntryName);
            entryLocation = itemView.findViewById(R.id.tvEntryLocation);
            tvUserName = itemView.findViewById(R.id.tvUserNameForum);
        }
    }

    public void filterByCategory(String category){
        if (postsType == ALL_POSTS) {
            for (int i = 0; i < entryList.size(); i++) {
                if (!((entryList.get(i).getNoteCategory()).equals(category))){
                    
                }
            }
        }
    }

    public void updateBackground(View view){
        if(entryList.isEmpty()) {
            if (postsType == MY_POSTS) {
                view.findViewById(R.id.imageEmptyMyPosts)
                        .setVisibility(View.VISIBLE);
            }
            if (postsType == ALL_POSTS) {
                view.findViewById(R.id.imageEmptyForum)
                        .setVisibility(View.VISIBLE);
            }
        }
        else {
            if(postsType == MY_POSTS) {
                view.findViewById(R.id.imageEmptyMyPosts)
                        .setVisibility(View.GONE);
            }
            if(postsType == ALL_POSTS){
                view.findViewById(R.id.imageEmptyForum)
                        .setVisibility(View.GONE);
            }
        }
    }
}
