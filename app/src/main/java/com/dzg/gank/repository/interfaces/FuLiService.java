package com.dzg.gank.repository.interfaces;


import com.dzg.gank.mvp.model.FuLiBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2017/4/6.
 */

public interface FuLiService {
    /**
     * 分类数据: http://gank.io/api/data/数据类型/请求个数/第几页
     * 数据类型： 福利 | Android | iOS | 休息视频 | 拓展资源 | 前端 | all
     * 请求个数： 数字，大于0
     * 第几页：数字，大于0
     * eg: http://gank.io/api/data/Android/10/1
     */
    @GET("data/福利/20/{page}")
    Observable<FuLiBean> getFuLiData(@Path("page") int page);
}
