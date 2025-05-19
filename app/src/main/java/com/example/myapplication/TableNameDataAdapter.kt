package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class TableNameDataAdapter(var context: Context, var data:ArrayList<LocalSQLite.TableNameData>): BaseAdapter() {

    /**Thay đổi data xem cos dduoc khong**/
    fun submitList(newData: ArrayList<LocalSQLite.TableNameData>) {
        data = newData
        notifyDataSetChanged()
    }

    /**ÁNH XẠ CÁC ITEM ĐÃ TẠO TRONG FILE XML LAYOUT**/
    class Layout(view: View){
        var image: ImageView = view.findViewById(R.id.image)
        var name: TextView = view.findViewById(R.id.name)
        var value: TextView = view.findViewById(R.id.value)
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        /**view -> là 1 view con trong list view**/
        val view: View?

        /**item -> là 1 view item trong layout**/
        val item:Layout

        /**convertView tức p1: View?**/
        if(convertView==null){
            /**khai báo layout dùng cho list view**/
            view= LayoutInflater.from(context).inflate(R.layout.listview_items,null)

            /**kết nối từng item trên layout đã findViewById bên trên với view trong list view**/
            item= Layout(view)

            /**kết nối từng item trên layout với view trong list view**/
            view.tag = item

        } else {
            view=convertView
            item= convertView.tag as Layout
        }

        /**data là dữ liệu được truyền vào**/
        val data: LocalSQLite.TableNameData = getItem(position) as LocalSQLite.TableNameData

        /**set data cho item image view
        nếu data hình là bitmap thì:
        item.hinh.setImageBitmap(danhsach.hinh)**/
        item.image.setImageBitmap(data.image)

        /**set data cho item text view id ten**/
        item.name.text= data.name

        /**set data cho item text view id gia**/
        item.value.text= data.value.toString()


        return view as View
    }
    override fun getCount(): Int {
        return data.size
    }
    override fun getItem(position: Int): Any {
        return data[position]
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
}