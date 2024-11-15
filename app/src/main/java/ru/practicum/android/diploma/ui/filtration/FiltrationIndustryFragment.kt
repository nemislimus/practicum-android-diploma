package ru.practicum.android.diploma.ui.filtration

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.practicum.android.diploma.databinding.FragmentFiltrationSelectBinding
import ru.practicum.android.diploma.ui.utils.BindingFragment

class FiltrationIndustryFragment : BindingFragment<FragmentFiltrationSelectBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFiltrationSelectBinding {
        return FragmentFiltrationSelectBinding.inflate(inflater, container, false)
    }
}
