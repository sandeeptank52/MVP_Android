package com.application.bmiantiobesity.utilits

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.edit
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.interceptor.InterceptorActivity
import com.application.bmiantiobesity.retrofit.Device
import com.application.bmiantiobesity.retrofit.Locale
import com.application.bmiantiobesity.ui.login.LoginActivity
import com.application.bmiantiobesity.ui.login.LoginViewModel
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


// Получение текущей локали
@Suppress("DEPRECATION")
fun getProgramLocale(context: Context) =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) context.resources.configuration.locale
    else  context.resources.configuration.locales.get(0)

// Получения нужных локалей
fun getCurrentLocale(context: Context) =
    Locale(
        getProgramLocale(context).toString().toLowerCase(java.util.Locale.getDefault())
            .replace('_', '-')
    )
fun getStringLocale(context: Context) = getProgramLocale(context).toString().toLowerCase(java.util.Locale.getDefault()).replace('_', '-')

// Получение UUID
fun getUUID(context: Context):String {
    val sharedPreferences = context.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
    var uuid = ""
    sharedPreferences?.let { uuid = it.getString(LoginViewModel.DEVICE_UUID, "") ?: "" }

    if (uuid.isEmpty()) {
        uuid = UUID.randomUUID().toString()
        sharedPreferences?.edit { putString(LoginViewModel.DEVICE_UUID, uuid) }
    }
    return uuid
}

// Получение Android ID
fun getAndroidID(context: Context) = Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

//Получение данных об устройстве
fun getDevice(context: Context) = Device(
    getUUID(context),
    LoginViewModel.OS_NAME,
    Build.VERSION.RELEASE,
    "${Build.BRAND} - ${Build.MODEL}",
    BuildConfig.VERSION_NAME
)

// Функция округления до установленного порядка после запятой порядка
fun Float?.roundTo(n:Int) =
    if (this == null) null
    else when {
        n >= 0 -> "%10.${n}f".format(this).replace(',', '.').toFloat()
        //n == 0 -> "%-10f".format(this).replace(',','.').toFloat() //this.roundToInt().toFloat()
        else -> "%${abs(n)}f".format(this).replace(',', '.').toFloat()
    }

// Получение текущей отформатированной даты и времени
fun getNowDateTime(context: Context): String {
    val dateFormat = SimpleDateFormat("dd.MM HH:mm", getProgramLocale(context))
    return dateFormat.format(Date())
}
// Получение текущей отформатированной даты
fun getNowDate(context: Context): String {
    val dateFormat = SimpleDateFormat("dd.MM", getProgramLocale(context))
    return dateFormat.format(Date())
}

// Запись лога в файл
fun writeLogToFile(context: Context, string: String) {
    try {
        val f = FileOutputStream("sdcard/AntiObesity-build-${BuildConfig.VERSION_CODE}-date-${getNowDate(context)}.txt", true)
        val sw = OutputStreamWriter(f)
        sw.append("\n\n${getNowDateTime(context)} - $string")
        sw.flush()
        sw.close()
        Log.d("DEBUG_TAG_R|W file", string)
    } catch (e: Exception) {
        Log.d("DEBUG_TAG_R|W file", "Error write file!$e")
    }
}

// Генерация сообщений
fun createInterceptorNotification(context: Context, message: String, id: Int) {
    // Настройки для часов
    // Конвертируем Drawable в Bitmap
    //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
    // Настройки для часов
    val wearableExtender: NotificationCompat.Extender = NotificationCompat.WearableExtender()
    //.setHintHideIcon(true)
    //.setBackground(bitmap);


    // Создание сообщения
    val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    var NOTIFICATION_CHANNEL_ID: String = id.toString()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "${context.getString(R.string.app_name)}-ID", importance)
        mNotificationManager.createNotificationChannel(notificationChannel)
        NOTIFICATION_CHANNEL_ID = notificationChannel.id
    }


    val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
    mBuilder //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),icon))
        .setSmallIcon(android.R.drawable.sym_action_chat)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText(message)
        .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
        .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
        .setAutoCancel(true) //автоматическое исчезновение после открытия
        .setDefaults(android.app.Notification.DEFAULT_ALL)
        .extend(wearableExtender) // Подключение специальных уведомлений для часов


    // Отображение большого сообщения
    val inboxStyle = NotificationCompat.InboxStyle()
    inboxStyle.setBigContentTitle(context.getString(R.string.app_name))
    val temp = message.split("\n")
    temp.forEach { if (it.compareTo("") != 0) inboxStyle.addLine(it) }


    mBuilder.setStyle(inboxStyle)

    // Creates an explicit intent for an Activity in your app
    val resultIntent = Intent(context, InterceptorActivity::class.java)
       .apply { flags =  Intent.FLAG_ACTIVITY_MULTIPLE_TASK } //Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
    //resultIntent.flags = ActivityFlags
    // The stack builder object will contain an artificial back stack for the started Activity.
    // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
    //val notifyPendingIntent = PendingIntent.getActivities(context, 0 , arrayOf(resultIntent), PendingIntent.FLAG_UPDATE_CURRENT)
    //mBuilder.setContentIntent(notifyPendingIntent)

    val stackBuilder = TaskStackBuilder.create(context).apply {
        // Adds the Intent that starts the Activity to the top of the stack
        addNextIntentWithParentStack(resultIntent) //addNextIntent(resultIntent)
    }

    val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    mBuilder.setContentIntent(resultPendingIntent)

    //val pendingIntent = PendingIntent.getActivity(context,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    //mBuilder.setContentIntent(pendingIntent)

    // mId allows you to update the notification later on.
    mNotificationManager.notify(id, mBuilder.build())
}

