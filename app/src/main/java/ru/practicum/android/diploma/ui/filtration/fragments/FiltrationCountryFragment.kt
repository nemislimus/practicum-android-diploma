package ru.practicum.android.diploma.ui.filtration.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentFiltrationCountriesBinding
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.ui.filtration.adapters.RegionAdapter
import ru.practicum.android.diploma.ui.filtration.viewmodels.FiltrationCountriesViewModel
import ru.practicum.android.diploma.ui.filtration.viewmodels.FiltrationCountryData
import ru.practicum.android.diploma.ui.utils.BindingFragment

class FiltrationCountryFragment : BindingFragment<FragmentFiltrationCountriesBinding>() {

    private val viewModel: FiltrationCountriesViewModel by viewModel()

    private val listAdapter = RegionAdapter { area ->
        viewModel.setArea(area)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFiltrationCountriesBinding {
        return FragmentFiltrationCountriesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCountryList.adapter = listAdapter

        binding.tbCountryWorkToolBar.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.liveData.observe(viewLifecycleOwner) { countries ->
            manageCountriesData(countries)
        }
    }

    private fun manageCountriesData(data: FiltrationCountryData) {
        when (data) {
            is FiltrationCountryData.Countries -> setCountries(data.countries)
            FiltrationCountryData.GoBack -> {
                findNavController().navigateUp()
            }
        }
    }

    private fun setCountries(countries: List<Area>) {
        binding.rvCountryList.isVisible = true
        listAdapter.areas.addAll(countries)
        listAdapter.notifyDataSetChanged()
    }
}
