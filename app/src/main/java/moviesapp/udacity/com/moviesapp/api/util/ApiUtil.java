package moviesapp.udacity.com.moviesapp.api.util;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import moviesapp.udacity.com.moviesapp.api.model.response.ErrorResponse;
import retrofit2.Response;

public class ApiUtil {

    public static ErrorResponse getApiErrorFromResponse(Response response) {
        try {
            if(response.errorBody() != null) {
                String responseString = response.errorBody().string();
                ErrorResponse errorResponse = new Gson().fromJson(responseString, ErrorResponse.class);
                return errorResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("api_err", e.getLocalizedMessage());
        }
        return null;
    }
}
