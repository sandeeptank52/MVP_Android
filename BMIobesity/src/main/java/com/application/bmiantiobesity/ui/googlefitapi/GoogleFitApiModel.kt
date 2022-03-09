package com.application.bmiantiobesity.ui.googlefitapi

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.application.bmiantiobesity.models.DataTypeInTime
import com.application.bmiantiobesity.models.SetNewDataValue
import com.application.bmiantiobesity.ui.main.MainViewModel
import com.application.bmiantiobesity.ui.settings.ConnectToFragment
import com.application.bmiantiobesity.utilits.getProgramLocale
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.google.android.gms.fitness.result.DataReadResponse
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import io.reactivex.subjects.PublishSubject

class GoogleFitApiModel : ViewModel() {
    // TODO: Implement the ViewModel

    companion object {
        private const val TAG_SENSORS = "Fitness SENSORS"
        private const val TAG_HISTORY = "Fitness HISTORY"
        private const val TAG_BUCKETS = "Fitness BUCKETS"

        const val GOOGLE_API_SETTINGS = "Google_Api_Settings"
        const val ACCESS_TO_API = "Access_To_Api"

        var PERIOD_DAY = 1

        //private const val REQUIRE_PERMISSIONS_REQUEST_CODE = 1
        //private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 2
    }

    // Передаёт всё чётко
    var liveText:PublishSubject<String> = PublishSubject.create()
    //var liveDashBoard:PublishSubject<SetNewDataValue> = PublishSubject.create()

    private lateinit var fitnessOptions:FitnessOptions
    private lateinit var mListener:OnDataPointListener
    //private var isLogin = false

    private lateinit var context:Context

    // Запрос доступа к Google Fit Api
    fun startGoogleFitnessApi(activity: AppCompatActivity) {

        context = activity.applicationContext

        /*mListener = OnDataPointListener { dataPoint ->
            for (field in dataPoint.dataType.fields) {
                val value = dataPoint.getValue(field)

                val stringBuilder = StringBuilder()
                stringBuilder.append("\n${field.name} - $value")
                Log.d(TAG_SENSORS, stringBuilder.toString() )
                //textView.append(stringBuilder.toString())

                //Здесь добавляются слушатели
                updateDataListeners(field, dataPoint)
                liveText.onNext(stringBuilder.toString())
            }
        }*/

        // Запрос конкретных прав на доступ к данным в Google FIT API
        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
            .build()

        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)

        //Проверка предоставленных прав
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity, // your activity
                ConnectToFragment.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                account,
                fitnessOptions
            )
        } else {
            accessGoogleFitHistory(activity)
            accessGoogleFitSensors(activity)
        }
    }

    // Когда доступк Google Fit Api разрешён
    fun accessGoogleFitSensors(activity: AppCompatActivity){

        mListener = OnDataPointListener { dataPoint ->
            for (field in dataPoint.dataType.fields) {
                val value = dataPoint.getValue(field)

                val stringBuilder = StringBuilder()
                stringBuilder.append("\n${dataPoint.dataType.name} \n${field.name} - $value")
                Log.d(TAG_SENSORS, stringBuilder.toString() )
                //textView.append(stringBuilder.toString())

                //Здесь добавляются слушатели
                updateDataListeners(field, dataPoint)
                liveText.onNext(stringBuilder.toString())
            }
        }

        context = activity.applicationContext

        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)

        // Получение реальных данных от сенсоров текущих!!!
        getSensorsData(activity, account)
    }

    // Когда доступк Google Fit Api разрешён
    fun accessGoogleFitHistory(activity: AppCompatActivity){

        context = activity.applicationContext

        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)

        // Запрос накопленных данных
        getHistoryData(activity, account)
    }

    // Получение реальных данных от сенсоров текущих!!!
    private fun getSensorsData(activity: AppCompatActivity, account: GoogleSignInAccount) {

        Fitness.getSensorsClient(activity, account)
            .findDataSources(
                DataSourcesRequest.Builder()
                    .setDataTypes(
                        DataType.TYPE_LOCATION_SAMPLE,
                        DataType.TYPE_STEP_COUNT_DELTA,
                        DataType.TYPE_DISTANCE_DELTA,
                        DataType.TYPE_HEART_RATE_BPM
                    )
                    .setDataSourceTypes(DataSource.TYPE_RAW, DataSource.TYPE_DERIVED)
                    .build()
            )
            .addOnSuccessListener { dataSources ->
                for (dataSource in dataSources) {

                    Log.d(TAG_SENSORS, "Data source found: $dataSource")
                    Log.d(TAG_SENSORS, "Data Source type: " + dataSource.dataType.name)
                    // Let's register a listener to receive Activity data!
                    if (dataSource.dataType == DataType.TYPE_LOCATION_SAMPLE ||
                        dataSource.dataType == DataType.TYPE_STEP_COUNT_DELTA ||
                        dataSource.dataType == DataType.TYPE_DISTANCE_DELTA ||
                        dataSource.dataType == DataType.TYPE_HEART_RATE_BPM
                    ) {
                        // Регистрация слушателей событий сенсоров
                        Fitness.getSensorsClient(activity, account)
                            .add(
                                SensorRequest.Builder()
                                    .setDataSource(dataSource) // Optional but recommended for custom data sets.
                                    .setDataType(dataSource.dataType) // Can't be omitted.
                                    .setSamplingRate(10, TimeUnit.SECONDS)
                                    .build(),
                                mListener
                            )
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG_SENSORS, "Listener registered!")
                                } else {
                                    Log.d(TAG_SENSORS, "Listener not registered.", task.exception)
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { Log.e(TAG_SENSORS, "failed", it) }
    }

    // Запрос накопленных данных
    private fun getHistoryData(activity: AppCompatActivity, account: GoogleSignInAccount) {

        // Получение накопленных данных о давлении за период
        getTypeOfHistoryPeriodData(activity, account, HealthDataTypes.TYPE_BLOOD_PRESSURE, PERIOD_DAY)
        // Получение накопленных данных о весе за период день
        getTypeOfHistoryPeriodData(activity, account, DataType.TYPE_WEIGHT, PERIOD_DAY)

        // Получение накопленных данных о давлении за день
        //getTypeOfHistoryData(account, HealthDataTypes.TYPE_BLOOD_PRESSURE)

        // Получение накопленных данных о весе за день
        //getTypeOfHistoryDayData(activity, account, DataType.TYPE_WEIGHT)

        // Получение накопленных данных о ритме за день
        getTypeOfHistoryDayData(activity, account, DataType.TYPE_HEART_POINTS)

        // Получение накопленных данных о шагах за день
        getTypeOfHistoryDayData(activity, account, DataType.TYPE_STEP_COUNT_DELTA)

    }

    private fun getTypeOfHistoryPeriodData(activity: AppCompatActivity, account: GoogleSignInAccount, dataType: DataType, day:Int) {
        val cal: Calendar = Calendar.getInstance()
        cal.time = Date()
        val endTime: Long = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -day)
        val startTime: Long = cal.timeInMillis

        val readRequest = DataReadRequest.Builder()
            //.aggregate(HealthDataTypes.TYPE_BLOOD_PRESSURE, HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY) // Обобщённые данные
            .read(dataType)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(day, TimeUnit.DAYS)
            .build()

        Fitness.getHistoryClient(activity, account)
            .readData(readRequest)
            .addOnSuccessListener {
                // Use response data here
                Log.d(TAG_HISTORY, "OnSuccess()  $it")
                //val dataSet =  it.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
                //Log.d(TAG_HISTORY, dataSet.dataType.name)

                printData(it)

                //val countSet = it.buckets.get(0).getDataSet(DataType.TYPE_STEP_COUNT_DELTA)
                //val steps = countSet?.dataPoints?.get(0)?.getValue(Field.FIELD_STEPS)?.asInt()
                //val steps = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt()
                //Log.d(TAG_HISTORY, "$countSet")
                //Log.d(TAG_HISTORY, "${steps}")

            }
            .addOnFailureListener {
                Log.d(TAG_HISTORY, "OnFailure() BLOOD", it)
            }//*/
    }

    private fun getTypeOfHistoryDayData(activity: AppCompatActivity, account: GoogleSignInAccount, dataType: DataType) {
        Fitness.getHistoryClient(activity, account)
            //.readData(readRequest)
            .readDailyTotal(dataType)
            .addOnSuccessListener {
                // Use response data here
                Log.d(TAG_HISTORY, "OnSuccess()  $dataType")

                //Log.d(TAG_HISTORY, dataSet.dataType.name)
                dumpDataSet(it)
            }
            .addOnFailureListener {
                Log.d(TAG_HISTORY, "OnFailure() $dataType", it)
            }
    }

    private fun printData( dataReadResult: DataReadResponse) {
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        Log.d(TAG_BUCKETS, "Number of returned buckets of DataSets is: ${dataReadResult.buckets.size}")
        if (dataReadResult.buckets.size > 0) {

            for (bucket in dataReadResult.buckets) {
                val dataSets = bucket.getDataSets()
                Log.d(TAG_BUCKETS, "DataSets: $dataSets")

                for (dataSet in dataSets) {
                    dumpDataSet(dataSet)
                }
            }
        } else if (dataReadResult.dataSets.size > 0) {
            Log.d(TAG_BUCKETS,"Number of returned DataSets is: ${dataReadResult.dataSets.size}")
            for (dataSet in dataReadResult.dataSets) {
                dumpDataSet(dataSet)
            }
        }
    }

    // Анализ данных ДатаСета
    private fun dumpDataSet(dataSet: DataSet) {
        Log.d(TAG_HISTORY, "Data returned for Data type: " + dataSet.dataType.name)
        val dateFormat =  SimpleDateFormat("dd.MM HH:mm", getProgramLocale(context))//getTimeInstance()

        for (dp in dataSet.dataPoints) {
            Log.d(TAG_HISTORY, "Data point:")
            Log.d(TAG_HISTORY, "\tType: " + dp.dataType.name)
            Log.d(TAG_HISTORY, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
            Log.d(TAG_HISTORY, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))

            val str = StringBuilder().append("\n Type - ${dp.dataType.name}")
                .append("\nStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                .append("\t End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))

            for (field in dp.dataType.fields) {
                Log.d(TAG_HISTORY, "\tField: " + field.name + " Value: " + dp.getValue(field))
                str.append("\nField: " + field.name + " Value: " + dp.getValue(field))


                updateDataListeners(field, dp)
            }

            liveText.onNext(str.toString())
        }
    }

    // Обновление слушателей
    private fun updateDataListeners(field: Field, dataPoint: DataPoint) {
        // Так работает правильно, когда новый объект. Иначе изменяет тольео старый и сообщает об этом.
        // Нужно передовать объет тип и значение (String) а потом их пасить на стороне приёмника!!!
        when(dataPoint.dataType.name){
            "com.google.blood_pressure" -> {
                if (field.name == "blood_pressure_systolic") {
                    val newValue =
                        SetNewDataValue(DataTypeInTime.BLOOD_PRESSURE_SYS, dataPoint.getValue(field).toString(), dataPoint.getTimestamp(TimeUnit.MILLISECONDS))
                    MainViewModel.publishNewValueFromEvent.onNext(newValue)
                    //liveDashBoard.onNext(newValue)
                }else if (field.name ==  "blood_pressure_diastolic"){
                    val newValue =
                        SetNewDataValue(DataTypeInTime.BLOOD_PRESSURE_DIA, dataPoint.getValue(field).toString(), dataPoint.getTimestamp(TimeUnit.MILLISECONDS))
                    MainViewModel.publishNewValueFromEvent.onNext(newValue)
                    //liveDashBoard.onNext(newValue)
                }
            }
            "com.google.weight" -> {
                if (field.name == "weight"){
                    val newValue =
                        SetNewDataValue(DataTypeInTime.WEIGHT, dataPoint.getValue(field).toString(), dataPoint.getTimestamp(TimeUnit.MILLISECONDS))
                    MainViewModel.publishNewValueFromEvent.onNext(newValue)
                    //liveDashBoard.onNext(newValue)
                }
            }
            "com.google.heart_rate.bpm" -> {
                if (field.name == "bpm")
                    if (dataPoint.getValue(field).toString().toFloat() >= 0f) {
                        val newValue =
                            SetNewDataValue(DataTypeInTime.HEARTS_RATE_ALONE, dataPoint.getValue(field).toString(), dataPoint.getTimestamp(TimeUnit.MILLISECONDS))
                        MainViewModel.publishNewValueFromEvent.onNext(newValue)
                        //liveDashBoard.onNext(newValue)
                    }
            }
            "com.google.step_count.delta" -> {
                if (field.name == "steps") {
                    // Отключил из-за редкого краша приложения потому что отсутствует steps
                    //MainViewModel.steps.value = dataPoint.getValue(field).toString().toFloat().roundToInt()
                }
            }
        }
    }
}