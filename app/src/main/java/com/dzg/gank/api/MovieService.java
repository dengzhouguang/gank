package com.dzg.gank.api;


import com.dzg.gank.module.Movie;
import com.dzg.gank.module.MovieDetail;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;



public interface MovieService {
    @GET("v2/movie/top250")
    Call<Movie> getTopMovie(@Query("start") int start, @Query("count") int count);
    @GET("v2/movie/top250")
    Observable<Movie> getTopMovieByRetrofit(@Query("start") int start, @Query("count") int count);
    @GET("v2/movie/subject/{id}")
    Observable<MovieDetail> getMovieDetail(@Path("id") String id);
}
