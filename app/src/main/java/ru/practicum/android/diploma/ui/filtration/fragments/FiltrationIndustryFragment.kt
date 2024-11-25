package ru.practicum.android.diploma.ui.filtration.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentFiltrationIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.ui.filtration.adapters.IndustryAdapter
import ru.practicum.android.diploma.ui.filtration.viewmodels.FiltrationIndustryData
import ru.practicum.android.diploma.ui.filtration.viewmodels.FiltrationIndustryViewModel
import ru.practicum.android.diploma.ui.utils.BindingFragment
import ru.practicum.android.diploma.util.EMPTY_STRING

class FiltrationIndustryFragment : BindingFragment<FragmentFiltrationIndustryBinding>() {
    private val listAdapter = IndustryAdapter { clickOnIndustry(it) }
    private val vModel: FiltrationIndustryViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFiltrationIndustryBinding {
        return FragmentFiltrationIndustryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tbIndustryToolBar.setOnClickListener { goBack() }
            rvIndustryist.adapter = listAdapter
            llSearchIndustryField.ivClearIcon.setOnClickListener {
                clearQuery()
            }

            llSearchIndustryField.etSearchQueryText.addTextChangedListener { s ->
                setSearchIcon(s.isNullOrBlank())
                vModel.getIndustries(s.toString())
            }

            btnSelectIndustry.setOnClickListener {
                vModel.saveIndustryFilter()
            }
        }

        vModel.getLiveData().observe(viewLifecycleOwner) {
            when (it) {
                FiltrationIndustryData.GoBack -> goBack()
                is FiltrationIndustryData.Industries -> {
                    listAdapter.industries.clear()
                    listAdapter.industries.addAll(it.industries)
                    listAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setSearchIcon(queryIsEmpty: Boolean) {
        with(binding.llSearchIndustryField) {
            ivSearchIcon.isVisible = queryIsEmpty
            ivClearIcon.isVisible = !queryIsEmpty
        }
    }

    private fun clearQuery() {
        with(binding) {
            llSearchIndustryField.etSearchQueryText.setText(EMPTY_STRING)
        }
        closeKeyboard()
    }

    private fun closeKeyboard() {
        activity?.let {
            it.currentFocus?.let { view ->
                val manager = requireActivity().baseContext.getSystemService(
                    INPUT_METHOD_SERVICE
                ) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    view.windowToken,
                    0
                )
            }
        }
    }

    private fun clickOnIndustry(industry: Industry) {
        binding.btnSelectIndustry.isVisible = true
        // сохраняем значение в фильтр
        vModel.setIndustry(industry)
    }

    private fun goBack() {
        findNavController().navigateUp()
    }
}
