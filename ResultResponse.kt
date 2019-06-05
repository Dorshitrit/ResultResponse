sealed class ResultResponse {
    data class ServerSuccess<T: Any>(val data: ResultResponseModel<T>) : ResultResponse()
    data class ServerFailure(val serverError: String) : ResultResponse()
}

data class ResultResponseModel<out T : Any>(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val data: T,
    @SerializedName("error")
    val error: String? = "",
    @Expose val resultResponseCode: ResultResponseCode
)

sealed class ResultResponseCode {
    object CodeSuccess : ResultResponseCode()
    object CodeFailure : ResultResponseCode()
}
