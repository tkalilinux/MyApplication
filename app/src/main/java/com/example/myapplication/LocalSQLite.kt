package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


sealed class LocalSQLite{
    companion object{
        private const val DATABASE_VERSION= 1
        private const val DATABASE_NAME= "CSDLSqlite2.db"


        //✅table name là tên của table
        private const val TABLE_NAME = "TableName"
        private const val COL_NAME = "NAME"
        private const val COL_VALUE = "VALUE"
        private const val COL_IMAGE = "IMAGE"
    }

    data class TableNameData(var name: String, var value: Int, var image: Bitmap)

    class LiveDataModel(application: Application) : AndroidViewModel(application) {

        private val db = LocalSQLite.DB(application.applicationContext)

        // LiveData để chứa danh sách dữ liệu từ SQLite
        private val _dataList = MutableLiveData<ArrayList<LocalSQLite.TableNameData>>()
        val dataList: LiveData<ArrayList<LocalSQLite.TableNameData>> get() = _dataList

        // Hàm load toàn bộ dữ liệu từ SQLite
        fun loadAllData() {
            viewModelScope.launch {
                val all = db.get()
                _dataList.postValue(all)
            }
        }

        // Hàm thêm dữ liệu
        fun insert(tableData: TableNameData) {
            viewModelScope.launch {
                db.save(TableNameData(tableData.name, tableData.value, tableData.image))
                loadAllData()
            }
        }

        // Hàm xóa theo tên
        fun delete(name: String?= null) {
            viewModelScope.launch {
                db.delete(name)
                loadAllData()
            }
        }

        // Hàm cập nhật
        fun update(tableData: TableNameData) {
            viewModelScope.launch {
                db.update(TableNameData(tableData.name, tableData.value, tableData.image))
                loadAllData()
            }
        }
    }

    class DB(applicationContext: Context): SQLiteOpenHelper(
        applicationContext, DATABASE_NAME ,null, DATABASE_VERSION
    ){

        override fun onCreate(p0: SQLiteDatabase?) {

            //✅Note: nếu PRIMARY KEY không phải INTEGER AUTOINCREMENT thì phải check
            //nếu trùng key sẽ lỗi
            val createTableIdString = """
                CREATE TABLE $TABLE_NAME (
                    $COL_NAME TEXT PRIMARY KEY,
                    $COL_VALUE INTEGER,
                    $COL_IMAGE BLOB
                )
            """.trimIndent()

            val createTable = """
                CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_NAME TEXT,
                    $COL_VALUE INTEGER,
                    $COL_IMAGE BLOB
                )
            """.trimIndent()

            //✅dùng p0 (SQLiteDatabase) để tạo table chứa dữ liệu theo mô tả ở data class
            p0?.execSQL(createTable)
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            //✅Nếu update app thì lệnh trong này sẽ chạy
            //✅bên dưới là lệnh xóa table tableName
            p0?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

            //✅TẠO LẠI SAU KHI UPDATE APP
            onCreate(p0)
        }


        private fun Bitmap.toByteArray(): ByteArray {
            //✅Note: SQITE không nhận Bitmap mà chỉ có thể lưu dạng ByteArray
            //✅Từ bitmap chuyển thành dạng byte array
            return ByteArrayOutputStream().apply {
                compress(Bitmap.CompressFormat.PNG, 100, this)
            }.toByteArray()
        }

        private fun ByteArray.toBitmap(): Bitmap {
            return BitmapFactory.decodeByteArray(this, 0, size)
        }

        /**✅✅✅✅✅Crud functions✅✅✅✅✅**/
        @SuppressLint("Recycle")
        fun get(name: String?= null): ArrayList<TableNameData>{
            //✅ĐÂY LÀ LIST KẾT QUẢ TRẢ VỀ
            val listRow: ArrayList<TableNameData> = ArrayList()

            //✅LẤY ĐỐI TƯỢNG SQLITE
            readableDatabase.use { db->
                //✅lấy dữ liệu thông qua đối tượng Cursor
                val data= if(name != null) {
                    db.rawQuery("select * from $TABLE_NAME where $COL_NAME= ?", arrayOf(name))
                } else {
                    db.rawQuery("select * from $TABLE_NAME", null)
                }

                data.use {
                    if (data.moveToFirst()) {
                        do{
                            //✅LẤY GIÁ TRỊ TỪNG Ô TRONG HÀNG NGANG
                            //Note: column id vị trí 0
                            //COL_NAME: vị trí 1
                            val columnName= data.getColumnIndexOrThrow(COL_NAME)
                            //COL_VALUE: vị trí 2
                            val columnValue= data.getColumnIndexOrThrow(COL_VALUE)
                            //COL_IMAGE: vị trí 3
                            val columnImage= data.getBlob(3)

                            //✅Dữ liệu cần lấy ở từng hàng
                            val dataOnRow= TableNameData(
                                data.getString(columnName),
                                data.getInt(columnValue),

                                //✅Note: PHẢI BIẾN HÌNH ẢNH DẠNG BYTE THÀNH BITMAP RỒI TRẢ VỀ
                                columnImage.toBitmap()
                            )

                            //✅List row thêm row mới nếu có
                            listRow.add(dataOnRow)
                        } while (
                            data.moveToNext()
                        )
                    }
                }

            }

            return listRow
        }

        suspend fun save(data: TableNameData): Boolean = withContext(Dispatchers.IO){
            val result = writableDatabase.use{ db ->
                /**đặt nội dung muốn lưu gồm tên, giá và hình ảnh vào contentValues**/
                val values = ContentValues().apply {
                    put(COL_NAME, data.name)
                    put(COL_VALUE, data.value)
                    put(COL_IMAGE, data.image.toByteArray())
                }

                //✅tiến hành lưu vào table
                db.insert(TABLE_NAME, null, values)
            }

            return@withContext result != -1L
        }

        fun update(data: TableNameData): Boolean {
            val result = writableDatabase.use{ db ->
                //✅đặt nội dung muốn lưu gồm tên, giá và hình ảnh vào contentValues
                val values = ContentValues().apply {
                    put(COL_VALUE, data.value)
                    put(COL_IMAGE, data.image.toByteArray())
                }

                //✅Note: update vào key nào không thể trùng
                /**SQLITE UPDATE TABLE..., CÁC NỘI DUNG CẦN THAY ĐỔI, TẠI where
                CỘT TÊN=?, ARRAYOF( GIÁ TRỊ CỦA DẤU ? )**/
                db.update(TABLE_NAME, values, "$COL_NAME = ?", arrayOf(data.name))

            }

            return result > 0
        }

        fun delete(name: String?= null): Boolean {
            val result = writableDatabase.use { db ->

                name?.let {
                    /**✅SQLITE XÓA TABLE..., CÁC NỘI DUNG CẦN THAY ĐỔI, TẠI where
                    CỘT TÊN=?, ARRAYOF( GIÁ TRỊ CỦA DẤU ? )**/
                    val rows = db.delete(TABLE_NAME, "$COL_NAME = ?", arrayOf(name))

                    rows > 0
                } ?: run {
                    //✅SQLITE THỰC HIỆN LỆNH XÓA TOÀN BỘ DỮ LIỆU TRONG TABLE
                    db.execSQL("DELETE FROM $TABLE_NAME")

                    true
                }

            }

            return result
        }

        //end line
    }


    //end line
}