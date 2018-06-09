package zoomapps.mobileoko.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import mvp.model.ModelSetting
import zoomapps.mobileoko.R
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.list_item_checkbox.view.*
import kotlinx.android.synthetic.main.list_item_hr.view.*
import kotlinx.android.synthetic.main.list_item_switch.view.*
import com.suke.widget.SwitchButton
import android.R.id.toggle





class SettingAdapter(var dataSetting: List<ModelSetting>) : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {
    var ITEM_CHECBOX = 0
    var ITEM_HR = 1
    var ITEM_SWITCH = 2
    var ITEM_SWITCH_NO = 3


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingAdapter.ViewHolder {
        var layoutRes = 0
        when (viewType) {
            0 -> layoutRes = R.layout.list_item_checkbox
            1 -> layoutRes = R.layout.list_item_hr
            2 -> layoutRes = R.layout.list_item_switch
            3 -> layoutRes = R.layout.list_item_checkbox_no
        }
        Log.e("viewType", "${layoutRes}")
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = dataSetting.size


    override fun onBindViewHolder(holder: SettingAdapter.ViewHolder, position: Int) {

        if (holder.checkbox_subtitle != null) {
            holder.checkbox_subtitle.text = dataSetting[position].checkbox_subtitle
            holder.name_chechbox.text = dataSetting[position]?.name_chechbox!!
        } else if (holder.hr_title != null) {
            holder.hr_title.text = dataSetting[position]?.name_chechbox!!
        } else if (holder.swith_title != null) {
            holder.switchButton.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->

            })
        }



    }

    override fun getItemViewType(position: Int): Int {
        when (dataSetting[position].position) {
            0 -> return ITEM_CHECBOX
            1 -> return ITEM_HR
            2 -> return ITEM_SWITCH
            3 -> return ITEM_SWITCH_NO
        }
        return ITEM_CHECBOX
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_chechbox = itemView.name_chechbox
        val checkbox_subtitle = itemView.checkbox_subtitle
        val checkbox = itemView.checkbox
        val hr_title = itemView.hr_title
        val swith_title = itemView.swith_title
        val switchButton = itemView.switch_button
    }


}