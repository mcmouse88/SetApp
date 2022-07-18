package com.mcmouse88.user_list.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.mcmouse88.user_list.R
import com.mcmouse88.user_list.databinding.FragmentUserDetailsBinding
import com.mcmouse88.user_list.screens.tasks.SuccessResult
import com.mcmouse88.user_list.viewmodel.UserDetailViewModel

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding: FragmentUserDetailsBinding
        get() = _binding ?: throw NullPointerException("FragmentUserDetailsBinding is null")

    private val userDetailViewModel by viewModelCreator<UserDetailViewModel> {
        UserDetailViewModel(it.userService, requireArguments().getLong(ARG_USER_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        userDetailViewModel.actionGoBack.observe(viewLifecycleOwner) {
            it.getValue()?.let { navigator().goBack() }
        }

        userDetailViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> navigator().toast(messageRes) }
        }

        userDetailViewModel.state.observe(viewLifecycleOwner) {
            binding.contentContainer.visibility = if (it.showContent) {
                val userDetail = (it.userDetailResult as SuccessResult).data
                binding.tvUserNameDetail.text = userDetail.user.name
                if (userDetail.user.photo.isNotBlank()) {
                    Glide.with(this)
                        .load(userDetail.user.photo)
                        .circleCrop()
                        .into(binding.ivAvatarDetail)
                } else {
                    Glide.with(this)
                        .load(R.drawable.ic_person_24)
                        .into(binding.ivAvatarDetail)
                }
                binding.tvUserDescriptionDetail.text = userDetail.detail
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.progressBarDetail.visibility = if (it.showProgress) View.VISIBLE else View.GONE
            binding.buttonDelete.isEnabled = it.enableDeleteButton
        }

        binding.buttonDelete.setOnClickListener {
            userDetailViewModel.deleteUser()
        }

        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {

        private const val ARG_USER_ID = "arg_user_id"

        fun newInstance(userId: Long): UserDetailsFragment {
            val fragment = UserDetailsFragment()
            fragment.arguments = bundleOf(ARG_USER_ID to userId)
            return fragment
        }
    }
}