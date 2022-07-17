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
import com.mcmouse88.user_list.viewmodel.UserDetailViewModel

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding: FragmentUserDetailsBinding
        get() = _binding ?: throw NullPointerException("FragmentUserDetailsBinding is null")

    private val userDetailViewModel by viewModels<UserDetailViewModel> { factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDetailViewModel.loadUser(requireArguments().getLong(ARG_USER_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        userDetailViewModel.userDetails.observe(viewLifecycleOwner) { userDetail ->
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
            binding.buttonDelete.setOnClickListener {
                userDetailViewModel.deleteUser()
                navigator().toast(getString(R.string.user_has_been_delete, userDetail.user.name))
                navigator().goBack()
            }
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