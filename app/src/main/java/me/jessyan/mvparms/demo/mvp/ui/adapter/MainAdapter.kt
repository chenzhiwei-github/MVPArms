package me.jessyan.mvparms.demo.mvp.ui.adapter

import com.jess.arms.base.BaseHolder
import com.jess.arms.base.DefaultAdapter
import me.jessyan.mvparms.demo.R
import me.jessyan.mvparms.demo.mvp.model.entity.IndexMenuEntity

/**
 * Created by chan on 2018/3/27.
 */
class MainAdapter(list: MutableList<IndexMenuEntity.MoudleListBean>) : DefaultAdapter<IndexMenuEntity.MoudleListBean>(list) {

    override fun bindData(holder: BaseHolder<*>?, position: Int, item: IndexMenuEntity.MoudleListBean?) {
        holder?.run {
            item?.run {
                setText(R.id.tv_name, moduleName).setImageByUrl(R.id.iv_avatar, imgUrl)
            }
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.recycle_list
    }

}
