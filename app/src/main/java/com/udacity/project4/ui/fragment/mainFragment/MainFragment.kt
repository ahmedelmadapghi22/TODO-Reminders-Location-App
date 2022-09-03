package com.udacity.project4.ui.fragment.mainFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentMainBinding
import com.udacity.project4.ui.fragment.mainFragment.login.LoginViewModel
import com.udacity.project4.ui.fragment.mainFragment.login.LoginViewModel.AuthenticationState.AUTHENTICATED
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class MainFragment : Fragment() {
    companion object {
        const val TAG = "MainFragment"
    }

    private lateinit var binding: FragmentMainBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var activityResultLaunch: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentMainBinding.inflate(layoutInflater, container, false)
        onActivityResult()
        initMenu()
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        observeAuthenticationState()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_fragment_main)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            launchSignInFlow()
        }


    }
    private fun initToolbar() {
        (activity as AppCompatActivity).supportActionBar?.hide()

    }


    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()

        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        activityResultLaunch.launch(intent)
    }

    private fun onActivityResult() {
        activityResultLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val response = IdpResponse.fromResultIntent(result.data)
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.i(
                        TAG,
                        "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                    )
                    findNavController().navigate(R.id.reminderFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Sign in unsuccessful ${response?.error?.errorCode}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
    }

    private fun observeAuthenticationState() {
        loginViewModel.authenticationState.observe(
            viewLifecycleOwner
        ) { authenticationState ->
            when (authenticationState) {
                AUTHENTICATED -> {
                    binding.loginBtn.text = getString(R.string.get_start_button_text)
                    binding.loginBtn.setOnClickListener {
                        findNavController().navigate(R.id.reminderFragment)
                    }


                }
                else -> {


                    binding.loginBtn.text = getString(R.string.login_button_text)
                    binding.loginBtn.setOnClickListener {
                        launchSignInFlow()
                    }

                }
            }

        }
    }

    private fun initMenu() {
        val host = requireActivity()
        host.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })
    }


}