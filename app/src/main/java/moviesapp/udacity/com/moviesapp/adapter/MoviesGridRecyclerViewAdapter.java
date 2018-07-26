package moviesapp.udacity.com.moviesapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import moviesapp.udacity.com.moviesapp.model.Movie;

public class MoviesGridRecyclerViewAdapter extends RecyclerView.Adapter<MoviesGridRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Movie> mMovies;

    public MoviesGridRecyclerViewAdapter(Context context, List<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = this.mMovies.get(position);

    }

    @Override
    public int getItemCount() {
        return this.mMovies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
