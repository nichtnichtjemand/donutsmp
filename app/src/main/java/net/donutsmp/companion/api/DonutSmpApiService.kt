package net.donutsmp.companion.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface DonutSmpApiService {
    @POST("v1/auction/list/{page}")
    suspend fun getAuctionList(
        @Path("page") page: Int,
        @Body search: AuctionSearchRequest
    ): AuctionListResponse
}
