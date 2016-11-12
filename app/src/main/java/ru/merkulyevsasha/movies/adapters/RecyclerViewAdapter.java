package ru.merkulyevsasha.movies.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.merkulyevsasha.movies.DetailsActivity;
import ru.merkulyevsasha.movies.R;
import ru.merkulyevsasha.movies.helpers.DisplayHelper;
import ru.merkulyevsasha.movies.http.ImageService;
import ru.merkulyevsasha.movies.models.Movie;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>{


    private final AppCompatActivity mActivity;
    public List<Movie> Items;
    private final File mImageFolder;
    private final String mLocale;
    private final String mImageWidth;

    public RecyclerViewAdapter(AppCompatActivity activity, List<Movie> items){
        Items = items;
        mActivity = activity;
        File imageFolder = new File(activity.getFilesDir(), ImageService.MOVIES_IMAGES_FOLDER);

        mImageWidth = DisplayHelper.getMainActivityImageWidth(activity);
        mImageFolder= new File(imageFolder, mImageWidth);
        mImageFolder.mkdirs();
        mLocale = Locale.getDefault().getLanguage();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ItemViewHolder(view, new OnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent detailsIntent = new Intent(mActivity, DetailsActivity.class);
                Movie item = Items.get(position);
                detailsIntent.putExtra("movieId", item.id);

                if (Build.VERSION.SDK_INT > 15){
                    ImageView moviePoster = (ImageView)view.findViewById(R.id.imageView);
                    TextView movieVote = (TextView)view.findViewById(R.id.textVote);
                    TextView movieCaption = (TextView)view.findViewById(R.id.textCaption);
                    TextView movieDescription = (TextView)view.findViewById(R.id.textDescription);

                    Pair<View, String> p1 = Pair.create((View)moviePoster, "movie_poster");
                    Pair<View, String> p2 = Pair.create((View)movieVote, "movie_vote");
                    Pair<View, String> p3 = Pair.create((View)movieCaption, "movie_caption");
                    Pair<View, String> p4 = Pair.create((View)movieDescription, "movie_description");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(mActivity, p1, p2, p3, p4);

                    mActivity.startActivity(detailsIntent, options.toBundle());
                }
                else{
                    mActivity.startActivity(detailsIntent);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        String caption = Items.get(position).originalTitle.trim();
        String description = Items.get(position).overview;
        String stringVote = Items.get(position).voteAverage;
        String stringDate = Items.get(position).releaseDate;

        holder.mTextCaption.setText(caption);

        if (description != null && description.length() > 50){
            description = description.substring(0, 50);
        }

        holder.mTextDescription.setText(description == null ? "" : description);

        if (stringDate != null && !stringDate.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = format.parse(stringDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                holder.mTextYear.setText(String.valueOf(year));
            }
            catch(Exception e){
                FirebaseCrash.report(e);
                e.printStackTrace();
            }
        }

        if (stringVote != null && !stringVote.isEmpty()) {
            try {
                double vode = Double.parseDouble(stringVote);
                DecimalFormat format = new DecimalFormat("#.#");
                holder.mtextVote.setText(format.format(vode));
            }
            catch(Exception e){
                FirebaseCrash.report(e);
            }
        }
        holder.mImageView.setImageBitmap(null);

        final String backdropPath = Items.get(position).backdropPath;
        if (backdropPath != null && !backdropPath.isEmpty()) {
            final String imageFileName = backdropPath.substring(1);
            final File imageFile = new File(mImageFolder, imageFileName);

            if (imageFile.exists()) {
                Bitmap bMap = BitmapFactory.decodeFile(imageFile.getPath());
                holder.mImageView.setImageBitmap(bMap);
            } else {
                holder.mImageView.setTag(R.id.imageView, backdropPath);

                ImageService service = ImageService.getInstance();
                service.getImage(mImageWidth, imageFileName, mLocale)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    if (ImageService.DownloadImage(imageFile, response.body())) {
                                        Bitmap bMap = BitmapFactory.decodeFile(imageFile.getPath());
                                        if (holder.mImageView.getTag(R.id.imageView).equals(backdropPath)) {
                                            holder.mImageView.setImageBitmap(bMap);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                FirebaseCrash.report(t);
                            }
                        });

            }
        }

    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private final ImageView mImageView;
        private final TextView mtextVote;
        private final TextView mTextCaption;
        private final TextView mTextDescription;
        private final TextView mTextYear;

        public ItemViewHolder(View itemView, final OnClickListener clickListener) {
            super(itemView);
            mtextVote = (TextView)itemView.findViewById(R.id.textVote);
            mTextCaption = (TextView)itemView.findViewById(R.id.textCaption);
            mTextDescription = (TextView)itemView.findViewById(R.id.textDescription);
            mTextYear = (TextView)itemView.findViewById(R.id.textYear);
            mImageView = (ImageView)itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    public interface OnClickListener {
        void onItemClick(View v, int position);
    }

}
