package com.coderfeng.tooyue.home.fragment

import android.os.Handler
import android.os.Looper
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.coderfeng.tooyue.R
import com.coderfeng.tooyue.base.fragment.BaseDataBindVMFragment
import com.coderfeng.tooyue.base.viewmodel.BaseViewModel
import com.coderfeng.tooyue.common.timer.OnTimerListener
import com.coderfeng.tooyue.common.timer.TimerHelper
import com.coderfeng.tooyue.databinding.FragmentMainPage2Binding
import com.coderfeng.tooyue.ext.DAY
import com.coderfeng.tooyue.ext.HOUR
import com.coderfeng.tooyue.ext.MIN
import com.coderfeng.tooyue.home.GridDividerItemDecoration
import com.coderfeng.tooyue.home.UserBirthAdapter
import com.coderfeng.tooyue.home.model.TimeItem
import com.coderfeng.tooyue.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_main_page2.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

class MainPage2Fragment : BaseDataBindVMFragment<FragmentMainPage2Binding>(), OnTimerListener {

    private val mViewModel: UserViewModel by viewModel()
    private lateinit var mTimerHelper: TimerHelper
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mProgressTime: BigDecimal?= null
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mWeekly: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private val mAdapter: UserBirthAdapter by lazy { UserBirthAdapter() }
    private val mDelayTime:BigDecimal = BigDecimal(0.00000003)
    override fun getLayoutRes(): Int = R.layout.fragment_main_page2

    override fun getViewModel(): BaseViewModel = mViewModel

    override fun initView() {
        initRecycleView()
        arguments?.apply {
            var birth = getString("date")
            getAge(Date(birth))
            setData()
            mTimerHelper = TimerHelper(this@MainPage2Fragment)
            mTimerHelper.setDelay(3)
            mTimerHelper.startTimer()
        }

        mBtnDead.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

    }

    private fun setData() {
        val list = mutableListOf<TimeItem>()
        list.add(TimeItem(mYear, "年"))
        list.add(TimeItem(mMonth, "月"))
        list.add(TimeItem(mWeekly, "周"))
        list.add(TimeItem(mDay, "天"))
        list.add(TimeItem(mHour, "小时"))
        list.add(TimeItem(mMinute, "分钟"))
        mAdapter.setNewInstance(list)

    }

    private fun initRecycleView() {
        mRvUserData.apply {
            layoutManager = GridLayoutManager(mActivity, 3)
            val itemDecoration = GridDividerItemDecoration(mActivity);
            addItemDecoration(itemDecoration)
            adapter = mAdapter
        }
    }

    override fun onTimerUpdate(time: Long, delay: Long) {
        mHandler.post {
            mProgressTime = mProgressTime?.add(mDelayTime)?.setScale(8,BigDecimal.ROUND_HALF_UP)
            mTvAgo.setCurrValue(mProgressTime.toString())
            mTvAgo.startAnimation()
        }
    }

    private fun getAge(birth: Date) {
        val calendar = Calendar.getInstance()
        if (calendar.before(birth)) return
        val currTime = calendar.timeInMillis
        val yearNow = calendar.get(Calendar.YEAR)
        val monthNow = calendar.get(Calendar.MONTH)
        val dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.time = birth
        val birthTime = calendar.timeInMillis
        val yearBirth = calendar.get(Calendar.YEAR)
        val monthBirth = calendar.get(Calendar.MONTH)
        val dayOfMonthBirth = calendar.get(Calendar.DAY_OF_MONTH)
        mYear = yearNow - yearBirth
        val totalTime = currTime - birthTime
        calendar.set(Calendar.MONTH, monthBirth)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthBirth)
        if ((monthNow == monthBirth && dayOfMonthNow < dayOfMonthBirth) || monthNow < monthBirth) {
            //今年生日还没到
            mYear--
            calendar.set(Calendar.YEAR, yearNow)
        } else {
            calendar.set(Calendar.YEAR, yearNow + 1)
        }
        val nextBirthTime = calendar.timeInMillis
        //上次生日时间
        if ((monthNow == monthBirth && dayOfMonthNow < dayOfMonthBirth) || monthNow < monthBirth) {
            calendar.set(Calendar.YEAR, yearNow - 1)
        } else {
            calendar.set(Calendar.YEAR, yearNow)
        }
        mProgressTime = BigDecimal((currTime - calendar.timeInMillis).toDouble() / (nextBirthTime - calendar.timeInMillis).toDouble()).setScale(8,BigDecimal.ROUND_DOWN)
        mProgressTime?.add(BigDecimal(mYear))
        mMonth = mYear * 12 + monthNow - monthBirth
        mWeekly = (totalTime / DAY / 7).toInt()
        mDay = (totalTime / DAY).toInt()
        mHour = (totalTime / HOUR).toInt()
        mMinute = (totalTime / MIN).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimerHelper.stopTimer()
    }
}