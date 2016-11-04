package ru.merkulyevsasha.movies.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.merkulyevsasha.movies.DetailsActivity;
import ru.merkulyevsasha.movies.R;
import ru.merkulyevsasha.movies.models.Movie;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>{

    private Activity mActivity;
    public List<Movie> Items;

    public RecyclerViewAdapter(Activity activity, List<Movie> items){
        Items = items;
        mActivity = activity;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view, new OnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent detailsIntent = new Intent(mActivity, DetailsActivity.class);
                Movie item = Items.get(position);
                detailsIntent.putExtra("movieId", item.id);
                mActivity.startActivity(detailsIntent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        String caption = Items.get(position).originalTitle.trim();
        String description = Items.get(position).overview;
        double vote = Items.get(position).voteAverage;
        Date date = Items.get(position).releaseDate;

        holder.mTextCaption.setText(caption == null ? "" : caption);

        if (description != null && description.length() > 50){
            description = description.substring(0, 50);
        }

        holder.mTextDescription.setText(description == null ? "" : description);

        if (date != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                holder.mTextYear.setText(String.valueOf(year));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        DecimalFormat format = new DecimalFormat("#.#");
        holder.mtextVote.setText(format.format(vote));
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        private TextView mtextVote;
        private TextView mTextCaption;
        private TextView mTextDescription;
        private TextView mTextYear;

        public ItemViewHolder(View itemView, final OnClickListener clickListener) {
            super(itemView);
            mtextVote = (TextView)itemView.findViewById(R.id.textVote);
            mTextCaption = (TextView)itemView.findViewById(R.id.textCaption);
            mTextDescription = (TextView)itemView.findViewById(R.id.textDescription);
            mTextYear = (TextView)itemView.findViewById(R.id.textYear);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v, getPosition());
                }
            });
        }
    }

    public interface OnClickListener {
        void onItemClick(View v, int position);
    }

}
