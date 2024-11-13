package ru.practicum.android.diploma.data.network

open class Response {
    var resultCode = 0

    companion object {
        const val IO_EXCEPTION_CODE = -1
        const val NO_CONNECTION_CODE = -1
        const val SUCCESSFUL_RESPONSE_CODE = 200
        const val INCORRECT_PARAM_ERROR_CODE = 400
        const val CAPTCHA_REQUIRED_ERROR = 403
        const val NOT_FOUND_CODE = 404
        const val INTERNAL_SERV_ERROR_CODE = 500
        const val BAD_GATEWAY_CODE = 502
    }
}
