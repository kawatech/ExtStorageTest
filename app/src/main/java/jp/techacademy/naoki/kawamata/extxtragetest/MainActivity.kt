package jp.techacademy.naoki.kawamata.extxtragetest

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.*
import java.lang.reflect.Array.get
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION = 1000
    private var textView: TextView? = null
    private var editText: EditText? = null
    private var file: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //    file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        //    MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
        //    fw = new FileWriter(file, true);
        //    fw.write("sample");
        //    fw.close();

// 「やさしい」p282

        //  file = Environment.getExternalStorageDirectory();
        //   File f = new File(file, "testfile.txt");
    //    file = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)  // プロジェクト名の下
    //    file = getExternalFilesDir(Environment.DIRECTORY_DCIM)

    //    val testfile = "testfile2.txt"
     //   file = File(file, testfile)


/*
        val mydirName = "ext" // 保存フォルダー
        val ExtFileName = "tmp.txt" // ファイル名

        // 現在の外部ストレージのログ・ファイル名(パス含め)
         val filePath: String
        get() {
            val myDir = Environment.getExternalStorageDirectory().getPath()
            return  myDir+"/"+ getString(ExtFileName)
        }
*/

    //    val filePath = Environment.getExternalStorageDirectory().getPath() + "/tmp.txt"


        // PC\Priori 5\内部共有ストレージ\Android\data\jp.techacademy.naoki.kawamata.extxtragetest\filesの下にできる
         file = File(getExternalFilesDir("/"),"tmp9.txt")






//PC-TE510JAW\内部共有ストレージ\Pictures の下にできる。
        //       String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/memo8.txt";

        //    String filePath = Environment.getExternalStorageDirectory() + "/memo.txt";

// PC\Priori 5\内部共有ストレージ の下にできる
        //    String filePath = Environment.getExternalStorageDirectory().getPath() + "/memo9.txt";

        //           file = new File(filePath);


        // Android 6, API 23以上でパーミッシンの確認
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission()
        } else {
            setUpReadWriteExternalStorage()
        }
    }

    private fun setUpReadWriteExternalStorage() {
        textView = findViewById(R.id.text_view)
        editText = findViewById(R.id.edit_text)
        val buttonSave = findViewById<Button>(R.id.button_save)
        buttonSave.setOnClickListener {
            // 現在ストレージが書き込みできるかチェック
            if (isExternalStorageWritable) {
                var text: String? = null
                val str = editText?.getText().toString()

                // PCから見えるようにするために必要な処理
                MediaScannerConnection.scanFile(applicationContext, arrayOf(file!!.absolutePath), null, null)
                try {
                    //      new FileOutputStream(file, true);
                    FileOutputStream(file, false).use { fileOutputStream ->
                        OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8).use { outputStreamWriter ->
                            BufferedWriter(outputStreamWriter).use { bw ->
                                bw.write(str)
                                bw.flush()
                                text = "saved"
                            }
                        }
                    }
                } catch (e: Exception) {
                    text = "error: FileOutputStream"
                    e.printStackTrace()
                }
                textView?.setText(text)
            }
        }
        val buttonRead = findViewById<Button>(R.id.button_read)
        buttonRead.setOnClickListener {
            var str: String? = null

            // 現在ストレージが読出しできるかチェック
            if (isExternalStorageReadable) {
                try {
                    FileInputStream(file).use { fileInputStream ->
                        InputStreamReader(fileInputStream, StandardCharsets.UTF_8).use { inputStreamReader ->
                            BufferedReader(inputStreamReader).use { reader ->
                                var lineBuffer: String?
                                while (reader.readLine().also { lineBuffer = it } != null) {
                                    str = lineBuffer
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    str = "error: FileInputStream"
                    e.printStackTrace()
                }
            }
            textView?.setText(str)
        }
    }

    /* Checks if external storage is available for read and write */
    val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    /* Checks if external storage is available to at least read */
    val isExternalStorageReadable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }

    // permissionの確認
    fun checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            setUpReadWriteExternalStorage()
        } else {
            requestLocationPermission()
        }
    }

    // 許可を求める
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            val toast = Toast.makeText(this, "アプリ実行に許可が必要です", Toast.LENGTH_SHORT)
            toast.show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION)
        }
    }

    // 結果の受け取り
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpReadWriteExternalStorage()
            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(this, "何もできません", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }
}