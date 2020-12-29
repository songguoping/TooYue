package com.coderfeng.tooyue.home.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.coderfeng.tooyue.R
import com.coderfeng.tooyue.base.fragment.BaseDataBindVMFragment
import com.coderfeng.tooyue.base.viewmodel.BaseViewModel
import com.coderfeng.tooyue.databinding.FragmentMainPage1Binding
import com.coderfeng.tooyue.user.dao.UserDao
import com.coderfeng.tooyue.user.model.db.User
import com.coderfeng.tooyue.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_main_page1.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainPage1Fragment : BaseDataBindVMFragment<FragmentMainPage1Binding>() {
    private val mViewModel: UserViewModel by viewModel()

    private val mUserDao:UserDao by inject()
    private lateinit var mUser: User

    override fun getLayoutRes(): Int = R.layout.fragment_main_page1

    override fun getViewModel(): BaseViewModel  = mViewModel

    override fun initView() {
        btn.setOnClickListener {

            // 日期选择器
            val ca = Calendar.getInstance()
            var mYear = ca[Calendar.YEAR]
            var mMonth = ca[Calendar.MONTH]
            var mDay = ca[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                mActivity,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    mYear = year
                    mMonth = month
                    mDay = dayOfMonth
                    val mDate = "${year}/${month + 1}/${dayOfMonth}"
                    // 将选择的日期赋值给TextView
                    mUser.birth = mDate
                    mViewModel.saveLocalUser(mUser)
                    Bundle().run {
                        putString("date", mDate)
                        Navigation.findNavController(it).navigate(R.id.action_page2,this);
                    }
                },
                mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }
    }

    override fun initData() {
        initUserInfo()
        mUser = User(1,"","","")
    }

    private fun initUserInfo() {
        lifecycleScope.launch {
            //Androidx的协程支持LifecycleScope、ViewModelScope
            //mUser = mUserDao.getAll()[0]
        }
    }
}