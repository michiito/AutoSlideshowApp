package jp.techacademy.michi.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import java.util.*
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val PERMISSIONS_REQUEST_CODE = 100

    var tapcount = 1

    lateinit var cursor: Cursor

    // タイマー用の時間のための変数
    var mTimer: Timer? = null

    var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movebutton.setOnClickListener {

            nextbutton.isEnabled = false
            backbutton.isEnabled = false
            tapcount += 1
            if (tapcount % 2 == 0) {
                movebutton.text = "停止"
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            getContentsInfo1()
                        }
                    }
                }, 2000, 2000) //スピードにより、動作が代わる

            } else {
                movebutton.text = "再生"
                nextbutton.isEnabled = true
                backbutton.isEnabled = true
                mTimer!!.cancel()
            }
        }

        nextbutton.setOnClickListener {

            if (this.cursor.moveToNext()) {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
                Log.d("ANDROID", "URI : " + imageUri.toString())

            } else {
                cursor.moveToFirst()

                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }
        }

        backbutton.setOnClickListener {

            if (this.cursor.moveToPrevious()) {

                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
                Log.d("ANDROID", "URI : " + imageUri.toString())
            } else {
                cursor.moveToLast()
            }
        }

// Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }else{
                    getContentsInfo()
                }
        }
    }


    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (cursor.moveToFirst()) {

            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)

            Log.d("ANDROID", "URI : " + imageUri.toString())

        }
        onStop()
    }

    private fun getContentsInfo1() {

        if (this.cursor.moveToNext()) {
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
            Log.d("ANDROID", "URI : " + imageUri.toString())

        } else {
            cursor.moveToFirst()
        }

    }
}





