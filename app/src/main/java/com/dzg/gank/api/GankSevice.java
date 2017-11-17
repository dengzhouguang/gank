package com.dzg.gank.api;

import com.dzg.gank.module.GankBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by dengzhouguang on 2017/10/11.
 */

public interface GankSevice {
    /**
     *   http://gank.io/api/data/Android/10/1
     */
        @GET("api/data/Android/50/{page}")
        Observable<GankBean> getGankByPage(@Path("page") String page);

    /**
     * http://gank.io/api/search/query/listview/category/Android/count/10/page/1
     category 后面可接受参数 all | Android | iOS | 休息视频 | 福利 | 拓展资源 | 前端 | 瞎推荐 | App
     count 最大 50
     */
    @GET("api/search/query/{searchContent}/category/{type}/count/10/page/{page}")
    Observable<GankBean> search(@Path("type")String type,@Path("page")String page,@Path("searchContent")String searchContent);

    /**
     * http://gank.io/api/history/content/day/2016/05/11
     */
    @GET("api/history/content/day/{year}/{month}/{day}")
    Observable<GankBean> getGankByDay(@Path("year")String year, @Path("month")String month, @Path("day")String day);
}
