package code.name.monkey.retromusic.feature.settings.servers.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.databinding.FragmentServersBinding
import code.name.monkey.retromusic.feature.settings.server.presentation.ServerSettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServersFragment : Fragment(R.layout.fragment_servers) {

    private val viewModel by viewModel<ServersViewModel>()
    private var adapter: ServersAdapter = ServersAdapter { item, menuItem ->
        when(menuItem.itemId) {
            R.id.action_edit -> {
                launchServerSettings(item.id)
                return@ServersAdapter true
            }
            R.id.action_set_active -> {
                viewModel.setActive(item.id)
                return@ServersAdapter true
            }
            R.id.action_delete -> {
                viewModel.delete(item.id)
                return@ServersAdapter true
            }
        }
        return@ServersAdapter false
    }

    private var _binding: FragmentServersBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentServersBinding.bind(view)
        setupRecyclerview()

        lifecycleScope.launchWhenStarted {
            viewModel.servers.collect {
                adapter.swapDataset(it)
            }
        }
        binding.create.setOnClickListener {
            launchServerSettings()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerview() {
        binding.backupRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ServersFragment.adapter
        }
    }

    private fun launchServerSettings(id: Long? = null) {
        val intent = Intent(requireActivity(), ServerSettingsActivity::class.java).apply {
            putExtra(ServerSettingsActivity.SERVER_SETTINGS_ID, id)
        }
        requireActivity().startActivity(intent)
    }
}