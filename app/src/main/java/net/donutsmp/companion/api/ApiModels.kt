package net.donutsmp.companion.api

data class AuctionSearchRequest(
    val search: String? = null,
    val sort: String? = null
)

data class AuctionItem(
    val count: Int,
    val display_name: String,
    val id: String,
    val lore: List<String>
)

data class Seller(
    val name: String,
    val uuid: String
)

data class AuctionEntry(
    val item: AuctionItem,
    val price: Double,
    val seller: Seller,
    val time_left: Long
)

data class AuctionListResponse(
    val result: List<AuctionEntry>,
    val status: Int
)
