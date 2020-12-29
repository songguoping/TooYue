package com.coderfeng.tooyue.user.api

import com.coderfeng.tooyue.config.Configs
import com.coderfeng.tooyue.home.model.ReleaseModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface UserApi {


    @GET("repos/{owner}/{repo}/releases")
    suspend fun getReleases(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int = Configs.PAGE_SIZE
    ): List<ReleaseModel>
}