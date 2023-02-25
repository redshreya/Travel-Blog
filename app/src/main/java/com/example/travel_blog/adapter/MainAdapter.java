package com.example.travel_blog.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.travel_blog.R;
import com.example.travel_blog.http.Blog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainAdapter extends ListAdapter<Blog, MainAdapter.MainViewHolder > {
    public interface OnItemClickListener {
        void onItemClicked(Blog blog);
    }

    private OnItemClickListener clickListener;
    public MainAdapter(OnItemClickListener clickListener){
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    // Implementing Filter Logic
    private List<Blog> originalList = new ArrayList<>();

    public void setData(@Nullable List<Blog> list){
        originalList = list;
        super.submitList(list);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter(String query){
        List<Blog> filteredList = new ArrayList<>();
        for(Blog blog : originalList){
            if(blog.getTitle().toLowerCase().contains(query.toLowerCase())){
                filteredList.add(blog);
            }
        }
        submitList(filteredList);
    }

    // Sorting Methods Implementation:
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByTitle(){
        List<Blog> currentList = new ArrayList<>(getCurrentList());
        Collections.sort(currentList, ((o1, o2) -> o1.getTitle().compareTo(o2.getTitle())));
        submitList(currentList);
    }

    // Implementing Sort by Date Function
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByDate(){
        List<Blog> currentList = new ArrayList<>(getCurrentList());
        Collections.sort(currentList, ((o1, o2) -> o2.getDateMillis().compareTo(o1.getDateMillis())));
        submitList(currentList);
    }


    private static final DiffUtil.ItemCallback<Blog> DIFF_CALLBACK = new DiffUtil.ItemCallback<Blog>() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean areItemsTheSame(@NonNull Blog oldItem, @NonNull Blog newItem) {
                return oldItem.getId().equals(newItem.getId());
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Blog oldItem, @NonNull Blog newItem) {
            return oldItem.equals(newItem);

        }
    };
    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_main,parent, false);
        return new MainViewHolder(view, clickListener);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(MainViewHolder holder, int position ){
        holder.bindTo(getItem(position));
    }
    static class MainViewHolder extends RecyclerView.ViewHolder{

        private TextView textTitle;
        private TextView textDate;
        private ImageView imageAvatar;
        private Blog blog;

        MainViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(v -> listener.onItemClicked(blog));
            textTitle= itemView.findViewById(R.id.textTitle);
            textDate = itemView.findViewById(R.id.textDate);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);


        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        void bindTo(Blog blog) {
            this.blog = blog;
            textTitle.setText(blog.getTitle());
            textDate.setText(blog.getDate());

            Glide.with(itemView)
                    .load(blog.getAuthor().getAvatarURL())
                    .transform(new CircleCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageAvatar);
        }
    }

}
