package code.name.monkey.retromusic.feature.settings.server.presentation

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import code.name.monkey.retromusic.activities.base.AbsThemeActivity
import code.name.monkey.retromusic.databinding.ActivityServerSettingsBinding
import code.name.monkey.retromusic.feature.main.presentation.MainActivity
import com.afollestad.vvalidator.form
import kotlinx.coroutines.flow.filter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ServerSettingsActivity : AbsThemeActivity() {

    private val id: Long? by lazy { intent.getLongExtra(SERVER_SETTINGS_ID, -1).takeIf { it != -1L } }
    private val isFirstServerCreate: Boolean by lazy {
        intent.getBooleanExtra(FIRST_SERVER_CREATE, false)
    }

    private val viewModel: ServerSettingsViewModel by viewModel {
        parametersOf(id)
    }

    private lateinit var binding: ActivityServerSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = " "
        initValidation()
        subscribeUi()
    }

    private fun subscribeUi() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .address
                .filter { it != binding.serverAddress.text?.toString() }
                .collect {
                    binding.serverAddress.setText(it)
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .password
                .filter { it != binding.password.text?.toString() }
                .collect {
                    binding.password.setText(it)
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .title
                .filter { it != binding.serverName.text?.toString() }
                .collect {
                    binding.serverName.setText(it)
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .username
                .filter { it != binding.username.text?.toString() }
                .collect {
                    binding.username.setText(it)
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .isActive
                .filter { it != binding.activeSwitch.isChecked }
                .collect {
                    binding.activeSwitch.isChecked = it
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .isFirstServer
                .collect {
                    binding.activeSwitch.isEnabled = !it
                }
        }
    }

    private fun initValidation() = form {
        input(binding.serverName) {
            isNotEmpty()
            onErrors { _, _ ->
                binding.serverNameInputLayout.error = "Server name can't be empty"
            }
        }
        input(binding.serverAddress) {
            isNotEmpty()
            isUrl()
            onErrors { _, _ ->
                binding.serverAddressInputLayout.error = "Server address is not valid url"
            }
        }
        input(binding.username) {
            isNotEmpty()
            onErrors { _, _ ->
                binding.usernameInputLayout.error = "Username can't be empty"
            }
        }
        input(binding.password) {
            isNotEmpty()
            onErrors { _, _ ->
                binding.passwordInputLayout.error = "Password can't be empty"
            }
        }
        submitWith(binding.saveButton) {
            viewModel.saveServer(getServerSettings())
            if (isFirstServerCreate) {
                startActivity(Intent(this@ServerSettingsActivity, MainActivity::class.java))
            }
            finish()
        }
    }

    private fun getServerSettings(): ServerSettingsUi {
        return ServerSettingsUi(
            title = requireNotNull(binding.serverName.text?.toString()),
            address = requireNotNull(binding.serverAddress.text?.toString()),
            username = requireNotNull(binding.username.text?.toString()),
            password = requireNotNull(binding.password.text?.toString()),
            isActive = binding.activeSwitch.isChecked
        )
    }

    companion object {
        const val SERVER_SETTINGS_ID = "server_settings_id"
        const val FIRST_SERVER_CREATE = "first_server_create"
    }
}