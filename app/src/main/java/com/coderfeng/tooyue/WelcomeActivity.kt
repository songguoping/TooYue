package com.coderfeng.tooyue

import androidx.lifecycle.lifecycleScope
import com.afollestad.assent.*
import com.coderfeng.tooyue.base.activity.BaseDataBindActivity
import com.coderfeng.tooyue.databinding.ActivityWelcomeBinding
import com.coderfeng.tooyue.ext.*
import com.coderfeng.tooyue.home.MainActivity
import com.jaredrummler.android.widget.AnimatedSvgView
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class WelcomeActivity : BaseDataBindActivity<ActivityWelcomeBinding>() {


    override fun getLayoutId(): Int = R.layout.activity_welcome

    override fun initView() {
        mAnimatedSvgView.setViewportSize(512f, 512f)
        mAnimatedSvgView.setOnStateChangeListener {
            (it == AnimatedSvgView.STATE_FINISHED).yes {
                askForPermission()
            }
        }
        mAnimatedSvgView.start()
    }

    override fun initData() {
        mDataBind.versionName = getVersionName()
    }

    private fun askForPermission() {
        lifecycleScope.launch {
            runWithPermissions(
                Permission.READ_PHONE_STATE, Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE
            ).yes {
                startActivity<MainActivity>()
            }.otherwise {
                finish()
            }
        }
    }
}