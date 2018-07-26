package moviesapp.udacity.com.moviesapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.net.ConnectException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import moviesapp.udacity.com.moviesapp.adapter.MoviesGridRecyclerViewAdapter;
import moviesapp.udacity.com.moviesapp.api.model.response.ErrorResponse;
import moviesapp.udacity.com.moviesapp.api.model.response.FetchMoviesResponse;
import moviesapp.udacity.com.moviesapp.api.service.MoviesApiServiceHelper;
import moviesapp.udacity.com.moviesapp.api.util.ApiUtil;
import moviesapp.udacity.com.moviesapp.model.Movie;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.recyclerView_movies)
    RecyclerView mRecyclerViewMovies;

    private AlertDialog mAlertDialog;

    private MoviesGridRecyclerViewAdapter adapter;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mRecyclerViewMovies.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new MoviesGridRecyclerViewAdapter(getApplicationContext(), new ArrayList<Movie>());
        mRecyclerViewMovies.setAdapter(adapter);

        showLoadingIndicator(true);
        disposable.add(
                getFetchPopularMoviesObservable()
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    private void showLoadingIndicator(boolean showLoading) {
        mProgressBar.setVisibility(showLoading ? View.VISIBLE : View.GONE);
        mRecyclerViewMovies.setVisibility(showLoading ? View.GONE : View.VISIBLE);
    }

    @NonNull
    private DisposableSingleObserver<Response<FetchMoviesResponse>> getFetchPopularMoviesObservable() {
        return MoviesApiServiceHelper.getInstance(getApplicationContext())
                .fetchPopularMovies(getString(R.string.api_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<FetchMoviesResponse>>() {
                    @Override
                    public void onSuccess(Response<FetchMoviesResponse> response) {
                        showLoadingIndicator(false);
                        handleFetchMoviesResponse(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showLoadingIndicator(false);
                        if(e instanceof ConnectException) {
                            showConnectionFailedErrorMessage();
                        } else {
                            showGenericErrorMessage();
                        }

                    }
                });
    }

    private void handleFetchMoviesResponse(Response<FetchMoviesResponse> response) {
        int responseCode = response.code();
        switch (responseCode) {
            case 200:
                FetchMoviesResponse fetchMoviesResponse = response.body();
                if(fetchMoviesResponse != null) {
                    adapter.setMovies(fetchMoviesResponse.getResults());
                }
                break;
            case 401:
                showUnauthorizedErrorMessage();
                break;
            case 400:
            case 404:
                ErrorResponse errorResponse = ApiUtil.getApiErrorFromResponse(response);
                showApiErrorFromErrorResponse(errorResponse);
                break;
            default:
                showGenericErrorMessage();
                break;
        }
    }

    private void showConnectionFailedErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.api_connection_error_title));
        builder.setMessage(getString(R.string.api_connection_error_message));
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void showUnauthorizedErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.api_generic_error_title));
        builder.setMessage(getString(R.string.api_unauthorized_error_message));
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void showGenericErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.api_generic_error_title));
        builder.setMessage(this.getString(R.string.api_generic_error_message));
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void showApiErrorFromErrorResponse(ErrorResponse errorResponse) {
        if(errorResponse == null) {
            showGenericErrorMessage();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.api_generic_error_title));
            builder.setMessage(new StringBuilder().append(errorResponse.getStatus_message())
                    .append(" (Code: ")
                    .append(errorResponse.getStatus_code())
                    .append(")").toString());
            if (mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
            mAlertDialog = builder.create();
            mAlertDialog.show();
        }
    }

}
