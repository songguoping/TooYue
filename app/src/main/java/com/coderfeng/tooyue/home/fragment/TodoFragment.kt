package com.coderfeng.tooyue.home.fragment

import androidx.navigation.Navigation
import com.coderfeng.tooyue.R
import com.coderfeng.tooyue.base.fragment.BaseDataBindVMFragment
import com.coderfeng.tooyue.base.viewmodel.BaseViewModel
import com.coderfeng.tooyue.databinding.FragmentTodoBinding

import com.coderfeng.tooyue.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_todo.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class TodoFragment : BaseDataBindVMFragment<FragmentTodoBinding>() {

    private val mViewModel: UserViewModel by viewModel()

    override fun getLayoutRes(): Int = R.layout.fragment_todo


    override fun getViewModel(): BaseViewModel  = mViewModel

    override fun initView() {
        btn.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }
    }
}
