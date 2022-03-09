package com.application.bmiantiobesity.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.ui.main.MainViewModel
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.koushikdutta.ion.Ion
import java.io.File


class SettingsActivity : AppCompatActivity() {

    //Подключение viewModel
    //private lateinit var fitnessViewModel: GoogleFitApiModel

    companion object{
        var firstLoad = false
        var nameOfFragment = ""
    }

    //Подключение viewModel
    //private lateinit var fitnessViewModel: GoogleFitApiModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        //Подключение модели для отправки туда информации их Google Fit
        //fitnessViewModel = ViewModelProvider(this).get(GoogleFitApiModel::class.java)

        // Получение данных из другой деятельности
        firstLoad = intent.extras?.getBoolean(LoginViewModel.USER_FIRST_LOGIN, false) ?: false
        nameOfFragment = intent.extras?.getString(SettingsViewModel.NAME_OF_SETTINGS_FRAGMENT,"") ?: ""

        val navController = findNavController(R.id.nav_settings_host_fragment)
        navController.popBackStack() // Для сброса стартового фрагмента

        when (nameOfFragment){
            "ProfileDetailFragment" -> navController.navigate(R.id.profileDetailFragment)
            "ConnectToFragment" -> navController.navigate(R.id.connectToFragment)
            "SettingsFragment" -> navController.navigate(R.id.settingsFragment)
            else -> navController.navigate(R.id.profileDetailFragment)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Окончательная проверка всех разрешений при регистрации
        when (requestCode){
            ConnectToFragment.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                    MainViewModel.accessToGoogleFitApi.value = true
                    //fitnessViewModel.accessGoogleFitHistory(this)
                    //fitnessViewModel.accessGoogleFitSensors(this)
                    // Сохранение access to api
                    //safePreferences(true)
            }
            SettingsViewModel.FILE_IMAGE_REQUEST_CODE -> {
                val files: ArrayList<MediaFile>? = data?.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)
                files?.forEach { Toast.makeText(this.applicationContext, it.path, Toast.LENGTH_SHORT).show() }

                // Load  Avatar File to Server for file?.get[0].path
                files?.let {  if (it.size > 0) uploadPhoto("https://intime.digital/api/v1/profile/", it[0].path) }
            }
        }

            // Сохранение access to api
            //safePreferences(false)

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadPhoto(url: String, file: String){
        Ion.getDefault(this).configure().setLogging("MyLogs", Log.DEBUG);

        Ion.with(this)
            .load("POST", url) //url de query
            //.setHeader("Cache-Control", "No-Cache") //desabilitando cache denovo porque essa parada é bug
            .setHeader("Authorization", "Bearer ${MainViewModel.userToken.access}") //token de acesso
            //.noCache() //desabilitando cache
            .setLogging("LOG_LOAD_FILES", Log.VERBOSE)//para debug
            //.uploadProgressBar(uploadProgressBar)
            .setMultipartParameter("application/json", MainViewModel.singleProfile.toString() )
            .setMultipartFile("image", "multipart/form-data", File(file))//jpg
            .asJsonObject() //array recebida
            .setCallback{ e, result -> // do stuff with the result or error
                Log.v("R Foto: ", "" + result)
                if (e != null) {
                    Toast.makeText(this, "Error Query: $e", Toast.LENGTH_LONG).show() //cria balao de texto na view indicada
                    Log.v("Query Error: ", "" + e.message) //DEBUG
                } else
                    Toast.makeText(this, "Cliente de cara nova ;) !", Toast.LENGTH_LONG).show()

            }
    }

    /*private fun uploadFile(file:String){
        var conn: HttpURLConnection? = null
        var dos: DataOutputStream? = null
        var inStream: DataInputStream? = null
        val lineEnd = "\r\n"
        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val buffer: ByteArray
        val maxBufferSize = 1 * 1024 * 1024
        val urlString = "http://" // server ip

        try {
            //------------------ CLIENT REQUEST
            val fileInputStream = FileInputStream(File(file))
            // open a URL connection to the Servlet
            val url = URL(urlString)
            // Open a HTTP connection to the URL
            conn = url.openConnection() as HttpURLConnection
            // Allow Inputs
            conn.setDoInput(true)
            // Allow Outputs
            conn.setDoOutput(true)
            // Don't use a cached copy.
            conn.setUseCaches(false)
            // Use a post method.
            conn.setRequestMethod("POST")
            conn.setRequestProperty("Connection", "Keep-Alive")
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=    ")
            conn.setRequestProperty("Authorization", )
            dos = DataOutputStream(conn.getOutputStream())
            dos.writeBytes(lineEnd)
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"$file\"$lineEnd")
            dos.writeBytes(lineEnd)

            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            buffer = ByteArray(bufferSize)

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize)
                bytesAvailable = fileInputStream.available()
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd)
            dos.writeBytes(lineEnd)

            // close streams
            Log.e("Debug", "File is written")
            fileInputStream.close()
            dos.flush()
            dos.close()
        } catch (ex: MalformedURLException) {
            Log.e("Debug", "error: " + ex.message, ex)
        } catch (ioe: IOException) {
            Log.e("Debug", "error: " + ioe.message, ioe)
        }

        //------------------ read the SERVER RESPONSE

        //------------------ read the SERVER RESPONSE
        try {
            inStream = DataInputStream(conn?.getInputStream())
            var str: String
            while (inStream.readLine().also { str = it } != null) {
                Log.e("Debug", "Server Response $str")
            }
            inStream.close()
        } catch (ioex: IOException) {
            Log.e("Debug", "error: " + ioex.message, ioex)
        }
    }*/
}
