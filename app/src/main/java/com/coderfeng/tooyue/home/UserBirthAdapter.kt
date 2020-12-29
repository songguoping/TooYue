package com.coderfeng.tooyue.home

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.coderfeng.tooyue.R
import com.coderfeng.tooyue.databinding.ItemUserTimeBinding
import com.coderfeng.tooyue.home.model.TimeItem


class UserBirthAdapter() :
    BaseQuickAdapter<TimeItem, BaseDataBindingHolder<ItemUserTimeBinding>>(R.layout.item_user_time) {


    override fun convert(holder: BaseDataBindingHolder<ItemUserTimeBinding>, item: TimeItem) {
        val binding = holder.dataBinding
        binding?.item = item
    }

}