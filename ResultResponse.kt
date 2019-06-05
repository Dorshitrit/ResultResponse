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



fun <T : Any> Call<ResultResponseModel<T>>.enqueue(
    serverSuccess: (response: ResultResponseModel<T>) -> Unit,
    serverFailure: (t: String) -> Unit
) {
    enqueue(object : Callback<ResultResponseModel<T>> {
        override fun onResponse(call: Call<ResultResponseModel<T>>, response: Response<ResultResponseModel<T>>) {
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    if (result.code == 1) { //server success - positive
                        serverSuccess(result.copy(resultResponseCode = ResultResponseCode.CodeSuccess))
                    } else { //server success - negative
                        serverSuccess(result.copy(resultResponseCode = ResultResponseCode.CodeFailure))
                    }
                }
            } else { //server failure
                serverFailure("Error Connection")
            }
        }

        override fun onFailure(call: Call<ResultResponseModel<T>>, t: Throwable) {
            serverFailure(t.message ?: "")  //server failure
            t.printStackTrace()
        }
    })
}
