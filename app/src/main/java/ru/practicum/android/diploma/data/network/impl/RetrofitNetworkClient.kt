package ru.practicum.android.diploma.data.network.impl

import android.util.Log
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.android.diploma.data.network.ApiRequest
import ru.practicum.android.diploma.data.network.ApiResponse
import ru.practicum.android.diploma.data.network.ApiResponse.Companion.BAD_GATEWAY_ERROR_CODE
import ru.practicum.android.diploma.data.network.ApiResponse.Companion.CAPTCHA_REQUIRED_ERROR_CODE
import ru.practicum.android.diploma.data.network.ApiResponse.Companion.INCORRECT_PARAM_ERROR_CODE
import ru.practicum.android.diploma.data.network.ApiResponse.Companion.INTERNAL_SERV_ERROR_CODE
import ru.practicum.android.diploma.data.network.ApiResponse.Companion.NOT_FOUND_ERROR_CODE
import ru.practicum.android.diploma.data.network.ApiResponse.Companion.NO_CONNECTION_ERROR_CODE
import ru.practicum.android.diploma.data.network.ApiResponse.Companion.SUCCESSFUL_RESPONSE_CODE
import ru.practicum.android.diploma.data.network.HhSearchApiProvider
import ru.practicum.android.diploma.data.network.api.NetworkClient
import ru.practicum.android.diploma.data.network.mapper.NetworkMapper
import ru.practicum.android.diploma.domain.repository.NetworkConnectionCheckerRepository
import java.io.IOException

class RetrofitNetworkClient(
    private val apiProvider: HhSearchApiProvider,
    private val connectionChecker: NetworkConnectionCheckerRepository,
    private val mapper: NetworkMapper,
) : NetworkClient {
    override suspend fun doRequest(dto: ApiRequest): ApiResponse {
        var resultResponse: ApiResponse? = null

        when {
            !connectionChecker.isConnected() -> {
                netLog("[$NO_CONNECTION_ERROR_CODE] - no connection")
                resultResponse = ApiResponse.BadResponse().apply { resultCode = NO_CONNECTION_ERROR_CODE }
            }
        }

        return resultResponse ?: withContext(Dispatchers.IO) {
            try {
                val response = sendValidRequest(dto)
                response.apply { resultCode = SUCCESSFUL_RESPONSE_CODE }
            } catch (e: HttpException) {
                val response = initResponseByError(e.code(), e.message())
                response
            } catch (e: IOException) {
                netLog("[$e] - IOException")
                badResponse()
            } catch (e: JsonSyntaxException) {
                netLog("[$e] - IOException")
                badResponse()
            }
        }
    }

    private suspend fun sendValidRequest(dto: ApiRequest): ApiResponse {
        val hhSearchApi = apiProvider.getApi()

        return when (dto) {
            is ApiRequest.Area -> hhSearchApi.getAreasByCountry(dto.parentAreaId)
            is ApiRequest.AreasAll -> hhSearchApi.getAreas()
            is ApiRequest.Country -> ApiResponse.CountryResponse(hhSearchApi.getCountries())
            is ApiRequest.Industry -> ApiResponse.IndustryResponse(hhSearchApi.getIndustries())
            is ApiRequest.Vacancy -> hhSearchApi.searchVacancies(mapper.map(dto.searchOptions))
            is ApiRequest.VacancyDetail -> hhSearchApi.getVacancyDetails(dto.vacancyId)
        }
    }

    private fun initResponseByError(errorCode: Int, message: String): ApiResponse {
        return when (errorCode) {
            INCORRECT_PARAM_ERROR_CODE -> {
                netLog("[$INCORRECT_PARAM_ERROR_CODE] - incorrect params exception\n$message")
                incorrectParamResponse()
            }

            CAPTCHA_REQUIRED_ERROR_CODE -> {
                netLog("[$CAPTCHA_REQUIRED_ERROR_CODE] - captcha required error\n$message")
                ApiResponse.BadResponse().apply { resultCode = CAPTCHA_REQUIRED_ERROR_CODE }
            }

            NOT_FOUND_ERROR_CODE -> {
                netLog("[$NOT_FOUND_ERROR_CODE] - page not found\n$message")
                ApiResponse.BadResponse().apply { resultCode = NOT_FOUND_ERROR_CODE }
            }

            BAD_GATEWAY_ERROR_CODE -> {
                netLog("[$BAD_GATEWAY_ERROR_CODE] - bad gateway\n$message")
                ApiResponse.BadResponse().apply { resultCode = BAD_GATEWAY_ERROR_CODE }
            }

            else -> {
                netLog("[$errorCode] - bad response\n$message")
                badResponse()
            }
        }
    }

    private fun netLog(message: String) {
        Log.d(REQUEST_EXCEPTION_TAG, message)
    }

    private fun incorrectParamResponse() = ApiResponse.BadResponse().apply {
        resultCode = INCORRECT_PARAM_ERROR_CODE
    }

    private fun badResponse() = ApiResponse.BadResponse().apply {
        resultCode = INTERNAL_SERV_ERROR_CODE
    }

    companion object {
        private const val REQUEST_EXCEPTION_TAG = "RETROFIT_EXCEPTION"
    }
}
