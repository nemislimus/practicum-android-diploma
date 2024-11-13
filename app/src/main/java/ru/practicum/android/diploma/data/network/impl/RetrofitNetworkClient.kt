package ru.practicum.android.diploma.data.network.impl

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.android.diploma.data.network.Response
import ru.practicum.android.diploma.data.network.Response.Companion.BAD_GATEWAY_CODE
import ru.practicum.android.diploma.data.network.Response.Companion.CAPTCHA_REQUIRED_ERROR
import ru.practicum.android.diploma.data.network.Response.Companion.INCORRECT_PARAM_ERROR_CODE
import ru.practicum.android.diploma.data.network.Response.Companion.INTERNAL_SERV_ERROR_CODE
import ru.practicum.android.diploma.data.network.Response.Companion.IO_EXCEPTION_CODE
import ru.practicum.android.diploma.data.network.Response.Companion.NOT_FOUND_CODE
import ru.practicum.android.diploma.data.network.Response.Companion.NO_CONNECTION_CODE
import ru.practicum.android.diploma.data.network.Response.Companion.SUCCESSFUL_RESPONSE_CODE
import ru.practicum.android.diploma.data.network.api.HhSearchApi
import ru.practicum.android.diploma.data.network.api.NetworkClient
import ru.practicum.android.diploma.data.network.api.NetworkConnectionChecker
import ru.practicum.android.diploma.data.network.mapper.NetworkMapper
import ru.practicum.android.diploma.data.search.dto.request.AreaRequest
import ru.practicum.android.diploma.data.search.dto.request.CountryRequest
import ru.practicum.android.diploma.data.search.dto.request.IndustryRequest
import ru.practicum.android.diploma.data.search.dto.request.VacancyDetailedRequest
import ru.practicum.android.diploma.data.search.dto.request.VacancyRequest
import ru.practicum.android.diploma.data.search.dto.response.CountryResponse
import ru.practicum.android.diploma.data.search.dto.response.IndustryResponse
import java.io.IOException

class RetrofitNetworkClient(
    private val hhSearchApi: HhSearchApi,
    private val connectionChecker: NetworkConnectionChecker,
    private val mapper: NetworkMapper,
) : NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        when {
            !connectionChecker.isConnected() -> {
                Log.d(REQUEST_EXCEPTION_TAG, "[$NO_CONNECTION_CODE] - no connection")
                return Response().apply { resultCode = NO_CONNECTION_CODE }
            }

            !isValidRequest(dto) -> {
                Log.d(
                    REQUEST_EXCEPTION_TAG,
                    "[$INCORRECT_PARAM_ERROR_CODE] - incorrect params exception"
                )
                return incorrectParamResponse()
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = when (dto) {
                    is CountryRequest -> CountryResponse(
                        result = hhSearchApi.getCountries()
                    )

                    is AreaRequest -> hhSearchApi.getAreasByCountry(dto.parrentAreaId)

                    is IndustryRequest -> IndustryResponse(
                        result = hhSearchApi.getIndustries()
                    )

                    is VacancyRequest -> hhSearchApi.searchVacancies(
                        mapper.map(dto.options)

                    )

                    else ->
                        hhSearchApi.getVacancyDetails(
                            (dto as VacancyDetailedRequest).vacancyId
                        )

                }
                response.apply { resultCode = SUCCESSFUL_RESPONSE_CODE }
            } catch (e: HttpException) {
                val message = e.message()
                val response = when (val errorCode = e.code()) {
                    INCORRECT_PARAM_ERROR_CODE -> {
                        Log.d(
                            REQUEST_EXCEPTION_TAG,
                            "[$INCORRECT_PARAM_ERROR_CODE] - incorrect params exception\n$message"
                        )
                        incorrectParamResponse()
                    }

                    CAPTCHA_REQUIRED_ERROR -> {
                        Log.d(
                            REQUEST_EXCEPTION_TAG,
                            "[$CAPTCHA_REQUIRED_ERROR] - captcha required error\n$message"
                        )
                        Response().apply { resultCode = CAPTCHA_REQUIRED_ERROR }
                    }

                    NOT_FOUND_CODE -> {
                        Log.d(
                            REQUEST_EXCEPTION_TAG,
                            "[$NOT_FOUND_CODE] - page not found\n$message"
                        )
                        Response().apply { resultCode = NOT_FOUND_CODE }
                    }

                    BAD_GATEWAY_CODE -> {
                        Log.d(
                            REQUEST_EXCEPTION_TAG,
                            "[$BAD_GATEWAY_CODE] - bad gateway\n$message"
                        )
                        Response().apply { resultCode = BAD_GATEWAY_CODE }
                    }

                    else -> {
                        Log.d(
                            REQUEST_EXCEPTION_TAG,
                            "[$errorCode] - bad response\n$message"
                        )
                        badResponse()
                    }
                }
                response
            } catch (e: IOException) {
                Log.d(
                    REQUEST_EXCEPTION_TAG,
                    e.message + " - bad response\n"
                )
                Response().apply { resultCode = IO_EXCEPTION_CODE }
            }
        }
    }

    private fun isValidRequest(dto: Any): Boolean {
        return dto is AreaRequest || dto is CountryRequest || dto is IndustryRequest
            || dto is VacancyDetailedRequest || dto is VacancyRequest
    }

    private fun incorrectParamResponse() = Response().apply {
        resultCode = INCORRECT_PARAM_ERROR_CODE
    }

    private fun badResponse() = Response().apply {
        resultCode = INTERNAL_SERV_ERROR_CODE
    }

    companion object {
        private const val REQUEST_EXCEPTION_TAG = "RETROFIT_EXCEPTION"
    }

}
