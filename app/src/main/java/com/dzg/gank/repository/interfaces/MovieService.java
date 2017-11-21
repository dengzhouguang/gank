package com.dzg.gank.repository.interfaces;


import com.dzg.gank.mvp.model.Movie;
import com.dzg.gank.mvp.model.MovieDetail;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;



public interface MovieService {
    @GET("v2/movie/top250")
    Observable<Movie> getTopMovie(@Query("start") int start, @Query("count") int count);
    @GET("v2/movie/subject/{id}")
    Observable<MovieDetail> getMovieDetail(@Path("id") String id);
}
