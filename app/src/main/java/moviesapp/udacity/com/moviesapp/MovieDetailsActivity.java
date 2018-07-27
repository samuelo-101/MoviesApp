package moviesapp.udacity.com.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import moviesapp.udacity.com.moviesapp.model.Movie;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String ARG_MOVIE_PARCEL = "MovieDetailsActivity_ARG_MOVIE_PARCEL";
    private Movie movie;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imageView_backdrop)
    ImageView mImageViewBackdrop;

    @BindView(R.id.imageView_poster)
    ImageView mImageViewPoster;

    @BindString(R.string.api_movie_image_base_uri)
    String imageBaseUrl;

    @BindString(R.string.api_default_image_size)
    String defaultImageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        validateAndGetMovieIdArgument();

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(movie.getTitle());


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
    }

    private void validateAndGetMovieIdArgument() {
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

}
