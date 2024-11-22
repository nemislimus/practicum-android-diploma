package ru.practicum.android.diploma.data.impl

import ru.practicum.android.diploma.data.db.converters.AreaRoomToAreaMapper
import ru.practicum.android.diploma.data.db.dao.AreasDao
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.AreaType
import ru.practicum.android.diploma.domain.repository.AreasRepository

class AreasRepositoryImpl(private val dao: AreasDao) : AreasRepository {
    override suspend fun getAllCountries(search: String?): List<Area> {
        return AreaRoomToAreaMapper.map(
            if (search.isNullOrBlank()) {
                dao.areasByType(
                    AreaType.COUNTRY.type
                )
            } else {
                dao.areasByTypeAndName(
                    AreaType.COUNTRY.type,
                    search.trim()
                )
            }
        )
    }

    override suspend fun getAllRegions(search: String?): List<Area> {
        return AreaRoomToAreaMapper.map(
            if (search.isNullOrBlank()) {
                dao.areasByType(
                    AreaType.REGION.type
                )
            } else {
                dao.areasByTypeAndName(
                    AreaType.REGION.type,
                    search.trim()
                )
            }
        )
    }

    override suspend fun getAllCities(search: String?): List<Area> {
        return AreaRoomToAreaMapper.map(
            if (search.isNullOrBlank()) {
                dao.areasByType(
                    AreaType.CITY.type
                )
            } else {
                dao.areasByTypeAndName(
                    AreaType.CITY.type,
                    search.trim()
                )
            }
        )
    }

    override suspend fun getRegionsInCountry(countryId: String, search: String?): List<Area> {
        return AreaRoomToAreaMapper.map(
            if (search.isNullOrBlank()) {
                dao.areasByParent(
                    parentId = countryId.toInt()
                )
            } else {
                dao.areasByParentAndName(
                    parentId = countryId.toInt(),
                    search = search.trim()
                )
            }
        )
    }

    override suspend fun getCitiesInCountry(countryId: String, search: String?): List<Area> {
        return AreaRoomToAreaMapper.map(
            if (search.isNullOrBlank()) {
                dao.citiesInCountry(
                    countryId = countryId.toInt()
                )
            } else {
                dao.citiesInCountryByName(
                    countryId = countryId.toInt(),
                    search = search.trim()
                )
            }
        )
    }

    override suspend fun getCitiesInRegion(regionId: String, search: String?): List<Area> {
        return AreaRoomToAreaMapper.map(
            if (search.isNullOrBlank()) {
                dao.areasByParent(
                    parentId = regionId.toInt()
                )
            } else {
                dao.areasByParentAndName(
                    parentId = regionId.toInt(),
                    search = search.trim()
                )
            }
        )
    }

    override suspend fun countCitiesInCountry(countryId: String): Int {
        return dao.countCitiesInCountry(countryId.toInt())
    }

    override suspend fun countCitiesInRegion(regionId: String): Int {
        return dao.countAreasInParent(regionId.toInt())
    }

    override suspend fun countRegionsInCountry(countryId: String): Int {
        return dao.countAreasInParent(countryId.toInt())
    }
}
