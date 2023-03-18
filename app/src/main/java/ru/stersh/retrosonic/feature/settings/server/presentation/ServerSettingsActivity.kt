/*
 * Copyright (c) 2020 Retro Sonic contributors.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package ru.stersh.retrosonic.feature.settings.server.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.filter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.activities.base.AbsThemeActivity
import ru.stersh.retrosonic.databinding.ActivityServerSettingsBinding
import ru.stersh.retrosonic.extensions.showToast
import ru.stersh.retrosonic.feature.main.presentation.MainActivity

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
        setupToolbar()
        subscribeUi()
        setupChangesButtonReseter()
    }

    private fun setupChangesButtonReseter() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.resetButtonState()
            }
        }
        binding.serverAddress.addTextChangedListener(textWatcher)
        binding.username.addTextChangedListener(textWatcher)
        binding.password.addTextChangedListener(textWatcher)
        binding.legacyAuthSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.resetButtonState()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
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
        lifecycleScope.launchWhenStarted {
            viewModel
                .useLegacyAuth
                .collect {
                    binding.legacyAuthSwitch.isChecked = it
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .submitButtonState
                .collect { submitButtonState ->
                    when (submitButtonState) {
                        SubmitButtonStateUi.TEST -> setTestButton()
                        SubmitButtonStateUi.SAVE -> setSaveButton()
                        SubmitButtonStateUi.TESTING -> setTestingButton()
                    }
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .errors
                .collect { showToast(it) }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .messages
                .collect {
                    when (it) {
                        MessageUi.SUCCESS_TEST_SERVER -> showToast(getString(R.string.connection_test_success_message))
                    }
                }
        }
    }

    private fun setTestButton() = with(binding.submitButton) {
        text = getString(R.string.test_connection_title)
        isEnabled = true
        setOnClickListener {
            validateInput {
                viewModel.testServer(getServerSettings())
            }
        }
    }

    private fun setSaveButton() = with(binding.submitButton) {
        text = getString(R.string.save)
        isEnabled = true
        setOnClickListener {
            validateInput {
                viewModel.saveServer(getServerSettings())
                if (isFirstServerCreate) {
                    startActivity(Intent(this@ServerSettingsActivity, MainActivity::class.java))
                }
                finish()
            }
        }
    }

    private fun setTestingButton() = with(binding.submitButton) {
        text = getString(R.string.testing_connection_title)
        isEnabled = false
        setOnClickListener(null)
    }

    private fun validateInput(action: () -> Unit) {
        var hasErrors = false
        if (binding.serverName.text?.toString().isNullOrEmpty()) {
            binding.serverNameInputLayout.error = getString(R.string.server_name_empty_error)
            hasErrors = true
        } else {
            binding.serverNameInputLayout.error = null
        }

        if (!isValidAddress(binding.serverAddress.text?.toString())) {
            binding.serverAddressInputLayout.error = getString(R.string.server_address_invalid_error)
            hasErrors = true
        } else {
            binding.serverAddressInputLayout.error = null
        }

        if (binding.username.text?.toString().isNullOrEmpty()) {
            binding.usernameInputLayout.error = getString(R.string.server_username_empty_error)
            hasErrors = true
        } else {
            binding.usernameInputLayout.error = null
        }

        if (binding.password.text?.toString().isNullOrEmpty()) {
            binding.passwordInputLayout.error = getString(R.string.server_password_empty_error)
            hasErrors = true
        } else {
            binding.passwordInputLayout.error = null
        }
        if (!hasErrors) {
            action.invoke()
        }
    }

    private fun getServerSettings(): ServerSettingsUi {
        return ServerSettingsUi(
            title = requireNotNull(binding.serverName.text?.toString()),
            address = requireNotNull(binding.serverAddress.text?.toString()),
            username = requireNotNull(binding.username.text?.toString()),
            password = requireNotNull(binding.password.text?.toString()),
            isActive = binding.activeSwitch.isChecked,
            useLegacyAuth = binding.legacyAuthSwitch.isChecked,
        )
    }

    companion object {
        const val SERVER_SETTINGS_ID = "server_settings_id"
        const val FIRST_SERVER_CREATE = "first_server_create"
    }
}
