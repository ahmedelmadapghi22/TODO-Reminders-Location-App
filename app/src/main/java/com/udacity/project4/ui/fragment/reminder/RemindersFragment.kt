package com.udacity.project4.ui.fragment.reminder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentReminderBinding
import com.udacity.project4.ui.adapter.ReminderAdapter
import com.udacity.project4.ui.fragment.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RemindersFragment : BaseFragment() {
    override val _baseViewModel: RemindersViewModel by viewModel()
    private lateinit var binding: FragmentReminderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBackPressedConfiguration()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentReminderBinding.inflate(layoutInflater, container, false)
        binding.viewModel = _baseViewModel
        binding.invalidateAll()
        _baseViewModel.eventGoToAddReminder.observe(requireActivity()) {
            if (it) {
                navigateToAddReminder()
                _baseViewModel.completeGoToAddReminder()
            }
        }
        binding.navigateBtn.setOnClickListener {
            _baseViewModel.goToAddReminder()
        }
        _baseViewModel.showNoData.observe(viewLifecycleOwner) {
            if (it) {
                binding.ivNoData.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvReminder.visibility = View.GONE
            } else {
                binding.ivNoData.visibility = View.GONE
                binding.tvNoData.visibility = View.GONE
                binding.rvReminder.visibility = View.VISIBLE
            }

        }
        initMenu()
        return binding.root
    }

    private fun initToolbar() {
        activity?.title = getString(
            R.string.title_fragment_reminder
        )

//        (activity as AppCompatActivity).supportActionBar?.title =
//            getString(
//                R.string.title_fragment_reminder
//            )
//        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true);
//        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)

    }

    private fun navigateToAddReminder() {
        findNavController().navigate(R.id.addReminderFragment)
    }


    override fun onResume() {
        super.onResume()
        getReminders()


    }


    private fun getReminders() {
        val adapter = ReminderAdapter(ReminderAdapter.OnClickListener {
            Log.d("dapgoo", "initRecycler:${it.id}")
        })
        binding.rvReminder.adapter = adapter
        _baseViewModel.reminders.observe(requireActivity()) {
            it?.let {
                adapter.submitList(it)

            }
        }
    }


    private fun setBackPressedConfiguration() {
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val i = Intent()
                    i.action = Intent.ACTION_MAIN
                    i.addCategory(Intent.CATEGORY_HOME)
                    startActivity(i)
                }
            })
    }

    private fun initMenu() {
        val host = requireActivity()
        host.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.app_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.item_menu_logout) {
                    AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            findNavController().navigate(R.id.mainFragment)
                        }
                    }


                }





                return true
            }
        })
    }


}