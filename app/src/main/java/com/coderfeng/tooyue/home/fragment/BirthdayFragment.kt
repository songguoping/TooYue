package com.coderfeng.tooyue.home.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.coderfeng.tooyue.R
import com.coderfeng.tooyue.base.fragment.BaseDataBindVMFragment
import com.coderfeng.tooyue.base.viewmodel.BaseViewModel
import com.coderfeng.tooyue.common.timer.OnTimerListener
import com.coderfeng.tooyue.common.timer.TimerHelper
import com.coderfeng.tooyue.databinding.FragmentBirthdayBinding
import com.coderfeng.tooyue.ext.DAY
import com.coderfeng.tooyue.ext.HOUR
import com.coderfeng.tooyue.ext.MIN
import com.coderfeng.tooyue.home.DiffTimeItemCallback
import com.coderfeng.tooyue.home.GridDividerItemDecoration
import com.coderfeng.tooyue.home.UserBirthAdapter
import com.coderfeng.tooyue.home.model.TimeItem
import com.coderfeng.tooyue.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_birthday.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.util.*

class BirthdayFragment : BaseDataBindVMFragment<FragmentBirthdayBinding>() , OnTimerListener {
    private val mViewModel: UserViewModel by viewModel()
    private val mTimerHelper: TimerHelper by lazy { TimerHelper(this@BirthdayFragment) }
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mProgressTime: BigDecimal? = null
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mWeekly: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var mBirthTime: Long = 0
    private val mAdapter: UserBirthAdapter by lazy { UserBirthAdapter() }
    private val mDelayTime: BigDecimal = BigDecimal(0.00000003)
    private val birthDate: Date = Date("2018/12/30")
    override fun getLayoutRes(): Int = R.layout.fragment_birthday

    override fun getViewModel(): BaseViewModel = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG_SGP", "MainPage2Fragment onCreate")
    }

    override fun initData() {
        super.initData()
        Log.d("TAG_SGP", "MainPage2Fragment initData")
        initRecycleView()
        mTimerHelper.setDelay(3)
        mBtnDead.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }
    }

    override fun initView() {
        Log.d("TAG_SGP", "MainPage2Fragment initView")
        getAge(birthDate)
        setData(true)
        mTimerHelper.startTimer()
    }

    private fun setData(first: Boolean) {
        val timeList: ArrayList<TimeItem> = ArrayList(mAdapter.data)
        if (timeList.size == 0) {
            timeList.add(0, TimeItem(mYear, "年"))
            timeList.add(1, TimeItem(mMonth, "月"))
            timeList.add(2, TimeItem(mWeekly, "周"))
            timeList.add(3, TimeItem(mDay, "天"))
            timeList.add(4, TimeItem(mHour, "小时"))
            timeList.add(5, TimeItem(mMinute, "分钟"))
            mAdapter.setList(timeList)
        } else {
            if (timeList[0].num != mYear) {
                timeList[0] = TimeItem(mYear, "年")
            }
            if (timeList[1].num != mMonth) {
                timeList[1] = TimeItem(mMonth, "月")
            }
            if (timeList[2].num != mWeekly) {
                timeList[2] = TimeItem(mWeekly, "周")
            }
            if (timeList[3].num != mDay) {
                timeList[3] = TimeItem(mDay, "天")
            }
            if (timeList[4].num != mHour) {
                timeList[4] = TimeItem(mHour, "小时")
            }
            if (timeList[5].num != mMinute) {
                timeList[5] = TimeItem(mMinute, "分钟")
            }
            mAdapter.setDiffNewData(timeList)
        }
    }

    private fun initRecycleView() {
        mRvUserData.apply {
            layoutManager = GridLayoutManager(mActivity, 3)
            val itemDecoration = GridDividerItemDecoration(mActivity)
            addItemDecoration(itemDecoration)
            mAdapter.setDiffCallback(DiffTimeItemCallback())
            adapter = mAdapter
        }
    }

    override fun onTimerUpdate(time: Long, delay: Long) {
        mHandler.post {
            if(isAdded){
                mProgressTime = mProgressTime?.add(mDelayTime)?.setScale(8, BigDecimal.ROUND_HALF_UP)
                mTvAgo.setCurrValue(mProgressTime.toString())
                updateTimeView(birthDate)
                mTvAgo.startAnimation()
            }
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
        mBirthTime = calendar.timeInMillis
        val yearBirth = calendar.get(Calendar.YEAR)
        val monthBirth = calendar.get(Calendar.MONTH)
        val dayOfMonthBirth = calendar.get(Calendar.DAY_OF_MONTH)
        mYear = yearNow - yearBirth
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
        mProgressTime =
                BigDecimal((currTime - calendar.timeInMillis).toDouble() / (nextBirthTime - calendar.timeInMillis).toDouble()).setScale(
                        8,
                        BigDecimal.ROUND_DOWN
                )
        mProgressTime = mProgressTime?.add(BigDecimal(mYear))
    }

    private fun updateTimeView(birth: Date) {
        val calendar = Calendar.getInstance()
        val currTime = calendar.timeInMillis
        val monthNow = calendar.get(Calendar.MONTH)

        calendar.time = birth
        mMonth = mYear * 12 + monthNow - calendar.get(Calendar.MONTH)
        val totalTime = currTime - calendar.timeInMillis

        mWeekly = (totalTime / DAY / 7).toInt()
        mDay = (totalTime / DAY).toInt()
        mHour = (totalTime / HOUR).toInt()
        mMinute = (totalTime / MIN).toInt()
        setData(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimerHelper.stopTimer()
        Log.d("TAG_SGP", "MainPage2Fragment onDestroy")
    }

    override fun onDestroyView() {
        mTvAgo?.stopAnimation()
        mTimerHelper.cancelTimer()
        super.onDestroyView()
        Log.d("TAG_SGP", "MainPage2Fragment onDestroyView")
    }

    override fun onPause() {
        super.onPause()
        Log.d("TAG_SGP", "MainPage2Fragment onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG_SGP", "MainPage2Fragment onResume")
    }
    override fun onStop() {
        super.onStop()
        Log.d("TAG_SGP", "MainPage2Fragment onStop")
    }
}