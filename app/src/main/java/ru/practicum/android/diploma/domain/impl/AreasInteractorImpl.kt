package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.repository.AreasInteractor
import ru.practicum.android.diploma.domain.repository.AreasRepository

class AreasInteractorImpl(private val repository: AreasRepository) : AreasInteractor {
    override suspend fun getAllCountries(search: String?): List<Area> {
        return repository.getAllCountries(search)
    }

    override suspend fun getAllRegions(search: String?): List<Area> {
        return repository.getAllRegions(search)
    }

    override suspend fun getAllCities(search: String?): List<Area> {
        return repository.getAllCities(search)
    }

    override suspend fun getRegionsInCountry(countryId: String, search: String?): List<Area> {
        return repository.getCitiesInCountry(countryId, search)
    }

    override suspend fun getCitiesInCountry(countryId: String, search: String?): List<Area> {
        return repository.getCitiesInCountry(countryId, search)
    }

    override suspend fun getCitiesInRegion(regionId: String, search: String?): List<Area> {
        return repository.getCitiesInRegion(regionId, search)
    }
}