package ru.practicum.android.diploma.data.search.dto.model

import com.google.gson.annotations.SerializedName

data class AreaDto(
    val id: String,
    val name: String,
    @SerializedName("parent_id")
    val parentId: String? = null,
    val areas: List<AreaDto>? = null,
)
