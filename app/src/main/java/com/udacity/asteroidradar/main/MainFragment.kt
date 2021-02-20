package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidViewBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.models.Asteroid

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)
    }

    private var asteroidAdapter: AsteroidsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        asteroidAdapter = AsteroidsAdapter(AsteroidClicked {
            viewModel.showAsteroidDetail(it)
        })

        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer { asteroid ->
            if (null != asteroid) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.doneShowingAsteroidDetail()
            }
        })

        binding.asteroidRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = asteroidAdapter
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.asteroids.observe(viewLifecycleOwner, Observer { asteroids ->
            asteroids.apply {
                asteroidAdapter?.asteroids = this
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}


class AsteroidsAdapter(val callback: AsteroidClicked): RecyclerView.Adapter<AsteroidViewHolder>() {

    var asteroids: List<Asteroid> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: AsteroidViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            AsteroidViewHolder.LAYOUT,
            parent,
            false)

        return AsteroidViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.binding.also {
            it.asteroid = asteroids[position]
            it.asteroidCallback = callback
        }
    }

    override fun getItemCount(): Int {
        return asteroids.size
    }

}

class AsteroidViewHolder(val binding: AsteroidViewBinding): RecyclerView.ViewHolder(binding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.asteroid_view
    }
}

class AsteroidClicked(val block: (Asteroid) -> Unit) {
    fun onClicked(model: Asteroid) = block(model)
}
