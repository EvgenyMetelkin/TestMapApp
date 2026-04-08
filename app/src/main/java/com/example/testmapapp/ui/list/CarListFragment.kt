package com.example.testmapapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testmapapp.databinding.FragmentCarListBinding
import com.example.testmapapp.ui.list.adapter.CarListAdapter
import com.example.testmapapp.data.CarRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CarListFragment : Fragment() {

    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarListViewModel by viewModels {
        CarListViewModelFactory(
            requireActivity().application,
            CarRepository()
        )
    }

    private val adapter = CarListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.items.collectLatest { items ->
                adapter.submitList(items)
                binding.emptyView.isVisible = items.isNullOrEmpty()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}