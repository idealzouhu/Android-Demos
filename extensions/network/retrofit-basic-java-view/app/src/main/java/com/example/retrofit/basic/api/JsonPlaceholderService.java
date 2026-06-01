package com.example.retrofit.basic.api;

import com.example.retrofit.basic.model.Post;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit API 接口：用注解描述请求方法与路径，返回 Call 由 Retrofit 执行。
 * 示例使用 JSONPlaceholder 公开接口。
 */
public interface JsonPlaceholderService {

    /**
     * GET /posts — 获取帖子列表。
     *
     * @return 由 Gson 反序列化为 List&lt;Post&gt;
     */
    @GET("posts")
    Call<List<Post>> getPosts();

    /**
     * GET /posts?userId=1 — 按用户 ID 筛选（演示 @Query 用法）。
     */
    @GET("posts")
    Call<List<Post>> getPostsByUserId(@Query("userId") int userId);

    /**
     * POST /posts — 演示 @Body 请求体与 ResponseBody 响应（不解析、拿原始响应体）。
     */
    @POST("posts")
    Call<ResponseBody> createPost(@Body Post post);
}
