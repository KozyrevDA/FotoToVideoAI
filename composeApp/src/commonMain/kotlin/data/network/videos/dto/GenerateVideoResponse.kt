package data.network.videos.dto

sealed class GenerateVideoResponse {
    class Success(
        val chatMessageDto: ChatMessageDto? = null,
        val video: ByteArray? = null,
        val allIds: List<String>? = null,
    ) : GenerateVideoResponse()

    object NotEnoughCoins : GenerateVideoResponse()
    object Error : GenerateVideoResponse()
    object UserPromtIsNull : GenerateVideoResponse()
    object UnknownError : GenerateVideoResponse()
    data class InternalServerError(val message: String? = null) : GenerateVideoResponse()
}