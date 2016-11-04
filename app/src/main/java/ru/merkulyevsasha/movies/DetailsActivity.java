package ru.merkulyevsasha.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import ru.merkulyevsasha.movies.models.Dict;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import ru.merkulyevsasha.movies.http.MovieService;
import ru.merkulyevsasha.movies.models.Details;

public class DetailsActivity extends AppCompatActivity {

    @Bind(R.id.back_button)
    public ImageButton mBackButton;

    @Bind(R.id.details_content)
    public View mRootView;

    @Bind(R.id.caption)
    public TextView mCaption;

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

    @Bind(R.id.button_home)
    public Button mButtonHome;

    private Subscription mSubscription;
    private String mLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);

        mLocale = Locale.getDefault().getLanguage();

        Intent intent = getIntent();
        final int movieId = intent.getIntExtra("movieId", 0);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        MovieService service = MovieService.getInstance();
        MovieService.unsubscribe(mSubscription);
        mSubscription = service.details(movieId, mLocale)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    private Subscriber<Details> getSubscriber() {
        return new Subscriber<Details>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Snackbar.make(mRootView.findViewById(R.id.content_main), R.string.details_error_message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }

            @Override
            public void onNext(final Details details) {
                if (details != null) {

                    mCaption.setText(details.originalTitle);
                    mTagline.setText(details.tagline);
                    mDescription.setText(details.overview);

                    DecimalFormat format = new DecimalFormat("#.#");
                    mVote.setText(format.format(details.voteAverage));

                    mGenres.setText(joinDicts(details.genres));
                    mCountries.setText(joinDicts(details.productionCountries));

                    if (details.homepage == null || details.homepage.isEmpty()){
                        mButtonHome.setVisibility(View.GONE);
                    } else{

                        mButtonHome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(details.homepage));
                                if (intent.resolveActivity(getPackageManager()) != null)
                                {
                                    startActivity(intent);
                                }
                            }
                        });
                    }


                } else {
                    Snackbar.make(mRootView.findViewById(R.id.content_main), R.string.details_error_message, Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }
            }
        };

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        MovieService.unsubscribe(mSubscription);
    }

}
