package com.fotoable.gifkeyboard.net;

import com.fotoable.gifkeyboard.model.GankResults;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wanglei on 2016/12/31.
 */

public interface GankService {

    @GET("data/{type}/{number}/{page}")
    Observable<GankResults> getGankData(@Path("type") String type,
                                        @Path("number") int pageSize,
                                        @Path("page") int pageNum);
}
