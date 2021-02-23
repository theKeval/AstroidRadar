package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidViewBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.repository.AsteroidFilter

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private var asteroidAdapter: AsteroidsAdapter? = null
    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)
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


        //region not sure whether we need it or not, so keeping it
//        viewModel.pod.observe(viewLifecycleOwner, Observer {
//            binding.executePendingBindings()
//        })
        //endregion

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // adding content description dynamically to provide the picture of day title in it
        binding.activityMainImageOfTheDayLayout.contentDescription =
            resources.getString(R.string.nasa_picture_of_day_content_description, viewModel.pod.value?.title)

        // Observing the asteroids live data to reassign the list of asteroids when it changes
        viewModel.asteroids.observe(viewLifecycleOwner, Observer { asteroids ->
            asteroids?.apply {
                asteroidAdapter?.asteroids = this
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_week_asteroid_menu -> AsteroidFilter.SHOW_WEEK
                R.id.show_today_asteroid_menu -> AsteroidFilter.SHOW_TODAY
                R.id.show_all_saved_asteroid_menu -> AsteroidFilter.SHOW_ALL_SAVED
                else -> AsteroidFilter.SHOW_TODAY
            }
        )

        return true
    }
}


class AsteroidsAdapter(val callback: AsteroidClicked) : RecyclerView.Adapter<AsteroidViewHolder>() {

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
            false
        )

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

class AsteroidViewHolder(val binding: AsteroidViewBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.asteroid_view
    }
}

class AsteroidClicked(val block: (Asteroid) -> Unit) {
    fun onClicked(model: Asteroid) = block(model)
}
