package ru.merkulyevsasha.movies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.merkulyevsasha.movies.helpers.DisplayHelper;
import ru.merkulyevsasha.movies.http.ImageService;
import ru.merkulyevsasha.movies.models.Cast;
import ru.merkulyevsasha.movies.models.Credits;
import ru.merkulyevsasha.movies.models.Crew;
import ru.merkulyevsasha.movies.models.Dict;
import ru.merkulyevsasha.movies.models.Titles;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import ru.merkulyevsasha.movies.http.MovieService;
import ru.merkulyevsasha.movies.models.Details;

public class DetailsActivity extends AppCompatActivity {

    @Bind(R.id.details_content)
    public View mRootView;

    @Bind(R.id.vote)
    public TextView mVote;

    @Bind(R.id.tagline)
    public TextView mTagline;

    @Bind(R.id.description)
    public TextView mDescription;

    @Bind(R.id.genres)
    public TextView mGenres;

    @Bind(R.id.countries)
    public TextView mCountries;

    @Bind(R.id.casts)
    public TextView mCasts;

    @Bind(R.id.crew)
    public TextView mCrew;

    @Bind(R.id.container_casts)
    public LinearLayout mContainerCasts;

    @Bind(R.id.container_crew)
    public LinearLayout mContainerCrew;

    @Bind(R.id.container_genres)
    public LinearLayout mContainerGenres;

    @Bind(R.id.container_countries)
    public LinearLayout mContainerCountries;

    @Bind(R.id.fab)
    public FloatingActionButton mFab;

    private Subscription mSubscription;
    private String mLocale;

    private File mImageFolder;

    private String mOriginalTitle;
    private String mTaglineText;
    private String mOverview;
    private String mVoteAverage;
    private String mGenresText;
    private String mProductionCountries;
    private String mBackdropPath;
    private String mHomePage;

    private String mImageWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_new);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mLocale = Locale.getDefault().getLanguage();

        mImageWidth = DisplayHelper.getDetailsActivityImageWidth(this);
        File imageFolder = new File(this.getFilesDir(), ImageService.MOVIES_IMAGES_FOLDER);
        mImageFolder= new File(imageFolder, mImageWidth);
        mImageFolder.mkdirs();

        Intent intent = getIntent();
        final int movieId = intent.getIntExtra("movieId", 0);

        if (savedInstanceState == null) {
            MovieService service = MovieService.getInstance();
            MovieService.unsubscribe(mSubscription);
            mSubscription = service.details(movieId, mLocale)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getSubscriber());

            service.credits(movieId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getCreditSubscriber());

            service.titles(movieId, mLocale)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getTitlesSubscriber());

        } else {
            mHomePage = savedInstanceState.getString("mHomePage");
            mOriginalTitle = savedInstanceState.getString("mOriginalTitle");
            mTaglineText = savedInstanceState.getString("mTaglineText");
            mOverview = savedInstanceState.getString("mOverview");
            mVoteAverage = savedInstanceState.getString("mVoteAverage");
            mGenresText = savedInstanceState.getString("mGenresText");
            mProductionCountries = savedInstanceState.getString("mProductionCountries");
            mBackdropPath = savedInstanceState.getString("mBackdropPath");

            setTitle(mOriginalTitle);
            mTagline.setText(mTaglineText);
            mDescription.setText(mOverview);

            mVote.setText(mVoteAverage);

            mGenres.setText(mGenresText);
            mCountries.setText(mProductionCountries);

            bindOnClickHomeButtonIfHomePageExists(mHomePage);

            final File imageFile = new File(mImageFolder, mBackdropPath);
            final ImageView image = (ImageView) findViewById(R.id.picture);
            if (imageFile.exists()) {
                setImageBitmap(imageFile, image);
            }

        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBackdropPath != null && !mBackdropPath.isEmpty()) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("image/png");

                    final File imageFile = new File(mImageFolder, mBackdropPath);
                    final Bitmap mBitmap = BitmapFactory.decodeFile(imageFile.getPath());
                    final String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, mOriginalTitle, mOriginalTitle);
                    final Uri uri = Uri.parse(path);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    sendIntent.putExtra(Intent.EXTRA_TEXT, mOverview);
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_using)));
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("mHomeButonVisibility", mHomePage);
        savedInstanceState.putString("mOriginalTitle", mOriginalTitle);
        savedInstanceState.putString("mTaglineText", mTaglineText);
        savedInstanceState.putString("mOverview", mOverview);
        savedInstanceState.putString("mVoteAverage", mVoteAverage);
        savedInstanceState.putString("mGenresText", mGenresText);
        savedInstanceState.putString("mProductionCountries", mProductionCountries);
        savedInstanceState.putString("mBackdropPath", mBackdropPath);
    }

    private Subscriber<Details> getSubscriber() {
        return new Subscriber<Details>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                FirebaseCrash.report(e);
                Snackbar.make(mRootView.findViewById(R.id.content_main), R.string.details_error_message, Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onNext(final Details details) {
                if (details != null) {

                    if (details.title == null || details.title.isEmpty()) {
                        mOriginalTitle = details.originalTitle;
                    } else {
                        mOriginalTitle = details.title;
                    }

                    setTitle(mOriginalTitle);

                    mTaglineText = details.tagline;
                    mOverview = details.overview;
                    mGenresText = joinDicts(details.genres);
                    mProductionCountries = joinDicts(details.productionCountries);

                    mTagline.setText(details.tagline);
                    mDescription.setText(details.overview);

                    if (details.voteAverage != null && !details.voteAverage.isEmpty()) {
                        try {
                            double vode = Double.parseDouble(details.voteAverage);
                            DecimalFormat format = new DecimalFormat("#.#");
                            mVoteAverage = format.format(vode);
                            mVote.setText(mVoteAverage);
                        }
                        catch(Exception e){
                            FirebaseCrash.report(e);
                        }
                    }

                    String genres = joinDicts(details.genres);
                    if (genres.isEmpty()){
                        mContainerGenres.setVisibility(View.GONE);
                    } else {
                        mContainerGenres.setVisibility(View.VISIBLE);
                        mGenres.setText(genres);
                    }
                    String countries = joinDicts(details.productionCountries);
                    if (countries.isEmpty()){
                        mContainerCountries.setVisibility(View.GONE);
                    } else {
                        mContainerCountries.setVisibility(View.VISIBLE);
                        mCountries.setText(countries);
                    }

                    mHomePage = details.homepage;
                    bindOnClickHomeButtonIfHomePageExists(mHomePage);

                    mBackdropPath = details.backdropPath;
                    if (mBackdropPath != null && !mBackdropPath.isEmpty()) {
                        mBackdropPath = mBackdropPath.substring(1); // remove slash
                        final File imageFile = new File(mImageFolder, mBackdropPath);
                        final ImageView image = (ImageView) findViewById(R.id.picture);

                        if (imageFile.exists()) {
                            setImageBitmap(imageFile, image);
                        } else {
                            ImageService service = ImageService.getInstance();
                            service.getImage(mImageWidth, mBackdropPath, mLocale)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()) {
                                                if (ImageService.DownloadImage(imageFile, response.body())) {
                                                    setImageBitmap(imageFile, image);
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
                } else {
                    Snackbar.make(mRootView.findViewById(R.id.content_main), R.string.details_error_message, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        };

    }

    private Subscriber<Credits> getCreditSubscriber() {
        return new Subscriber<Credits>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                FirebaseCrash.report(e);
            }

            @Override
            public void onNext(final Credits credits) {
                if (credits != null) {
                    String casts = joinCast(credits.casts);
                    if (casts.isEmpty()){
                        mContainerCasts.setVisibility(View.GONE);
                    } else {
                        mContainerCasts.setVisibility(View.VISIBLE);
                        mCasts.setText(casts);
                    }
                    String crew = joinCrew(credits.crew);
                    if (crew.isEmpty()){
                        mContainerCrew.setVisibility(View.GONE);
                    } else {
                        mContainerCrew.setVisibility(View.VISIBLE);
                        mCrew.setText(crew);
                    }
                }
            }
        };

    }

    private Subscriber<Titles> getTitlesSubscriber() {
        return new Subscriber<Titles>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                FirebaseCrash.report(e);
            }

            @Override
            public void onNext(final Titles titles) {
                if (titles != null) {
                }
            }
        };

    }


    private void bindOnClickHomeButtonIfHomePageExists(final String homePage){
//        if (homePage == null || homePage.isEmpty()){
//            mButtonHome.setVisibility(View.GONE);
//        } else{
//
//            mButtonHome.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(homePage));
//                    if (intent.resolveActivity(getPackageManager()) != null)
//                    {
//                        startActivity(intent);
//                    }
//                }
//            });
//        }
    }

    private void setImageBitmap(File imageFile, ImageView imageView){
        Bitmap bMap = BitmapFactory.decodeFile(imageFile.getPath());
        imageView.setImageBitmap(bMap);
    }

    private String joinDicts(List<Dict> list){
        StringBuilder sb = new StringBuilder();

        for (Dict item : list) {
            if (sb.length() > 0){
                sb.append("\n");
            }
            sb.append(item.name);
        }

        return sb.toString();
    }

    private String joinCast(List<Cast> list){
        StringBuilder sb = new StringBuilder();
        int i = 0;

        for (Cast item : list) {
            if (sb.length() > 0){
                sb.append(", ");
            }
            sb.append(item.name);

            if (item.character != null && !item.character.isEmpty()){
                sb.append(" (");
                sb.append(item.character);
                sb.append(")");
            }

            if (i> 9)
                break;
            i++;
        }

        return sb.toString();
    }

    private String joinCrew(List<Crew> list){
        StringBuilder sb = new StringBuilder();
        int i = 0;

        for (Crew item : list) {
            if (sb.length() > 0){
                sb.append(", ");
            }
            sb.append(item.name);

            if (item.department != null && !item.department.isEmpty()){
                sb.append(" (");
                sb.append(item.department);
                sb.append(")");
            }

            if (i> 5)
                break;
            i++;
        }

        return sb.toString();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        MovieService.unsubscribe(mSubscription);
    }

}
