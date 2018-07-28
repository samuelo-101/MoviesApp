package moviesapp.udacity.com.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import moviesapp.udacity.com.moviesapp.api.model.Movie;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String ARG_MOVIE_PARCEL = "MovieDetailsActivity_ARG_MOVIE_PARCEL";
    private Movie movie;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imageView_backdrop)
    ImageView mImageViewBackdrop;

    @BindView(R.id.imageView_poster)
    ImageView mImageViewPoster;

    @BindView(R.id.textView_user_rating)
    TextView mTextViewUserRating;

    @BindView(R.id.textView_release_month)
    TextView mTextViewReleaseMonth;

    @BindView(R.id.textView_release_year)
    TextView mTextViewReleaseYear;

    @BindView(R.id.textView_original_title)
    TextView mTextViewOriginalTitle;

    @BindView(R.id.textView_overview)
    TextView mTextViewOverview;

    @BindString(R.string.api_movie_image_base_uri)
    String imageBaseUrl;

    @BindString(R.string.api_default_image_size)
    String defaultImageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        validateAndGetMovieArgument();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupUI();
    }

    private void setupUI() {
        StringBuilder imageUrlStringBuilder = new StringBuilder().append(this.imageBaseUrl).append(this.defaultImageSize);
        Picasso.with(this)
                .load(imageUrlStringBuilder.append(movie.getBackdrop_path()).toString())
                .error(R.drawable.ic_broken_image_grey)
                .placeholder(R.drawable.ic_image_grey)
                .into(mImageViewBackdrop);

        imageUrlStringBuilder = new StringBuilder().append(this.imageBaseUrl).append(this.defaultImageSize);
        Picasso.with(this)
                .load(imageUrlStringBuilder.append(movie.getPoster_path()).toString())
                .error(R.drawable.ic_broken_image_grey)
                .placeholder(R.drawable.ic_image_grey)
                .into(mImageViewPoster);

        mTextViewUserRating.setText(String.valueOf(movie.getVote_average()));
        setReleaseMonthAndYearFromDateString(movie.getRelease_date());
        mTextViewOriginalTitle.setText(movie.getOriginal_title());
        mTextViewOverview.setText(movie.getOverview());
    }

    private void validateAndGetMovieArgument() {
        Intent intent = getIntent();
        if(intent != null) {
            Bundle extras = intent.getExtras();
            if(extras != null && extras.containsKey(ARG_MOVIE_PARCEL)) {
                this.movie = extras.getParcelable(ARG_MOVIE_PARCEL);
            } else {
                Intent goToMainActivity = new Intent(this, MainActivity.class);
                startActivity(goToMainActivity);
                finish();
            }
        }
    }

    private void setReleaseMonthAndYearFromDateString(String releaseDate) {
        if(!TextUtils.isEmpty(releaseDate)) {
            SimpleDateFormat releaseDateFormat = new SimpleDateFormat(getString(R.string.api_date_format), Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat(getString(R.string.full_month_date_format), Locale.getDefault());
            SimpleDateFormat yearFormat = new SimpleDateFormat(getString(R.string.full_year_date_format), Locale.getDefault());

            try {
                Date parsedReleaseDate = releaseDateFormat.parse(releaseDate);
                mTextViewReleaseMonth.setText(monthFormat.format(parsedReleaseDate));
                mTextViewReleaseYear.setText(yearFormat.format(parsedReleaseDate));
            } catch (ParseException e) {
                e.printStackTrace();
                mTextViewReleaseYear.setText(getString(R.string.error_failed_to_parse_release_date));
            }
        } else {
            mTextViewReleaseYear.setText(getString(R.string.default_no_data_available_placeholder));
        }
    }

}
