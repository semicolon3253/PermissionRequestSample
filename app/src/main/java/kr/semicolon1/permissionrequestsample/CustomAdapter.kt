package kr.semicolon1.permissionrequestsample

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

class CustomAdapter(private val activity: Activity, private val data: ArrayList<ItemData>) : BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(i: Int): Any {
        return data[i]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {
        var convertView: View? = view

        if (view == null) {
            convertView = activity.layoutInflater.inflate(R.layout.listview_layout, null)
        }

        val name = convertView?.findViewById<TextView>(R.id.name)
        name?.text = data[position].name
        val check = convertView?.findViewById<CheckBox>(R.id.checkbox)
        check?.setOnCheckedChangeListener{ _, isChecked ->
            data[position].isChecked = isChecked
        }
        val bg = convertView?.findViewById<LinearLayout>(R.id.item_bg)
        bg?.setOnClickListener{
            check?.toggle()
        }
        check?.isChecked = data[position].isChecked

        return convertView
    }
}