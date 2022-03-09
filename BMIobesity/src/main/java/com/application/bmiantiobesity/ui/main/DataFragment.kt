package com.application.bmiantiobesity.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.models.DataTypeInTime
import com.application.bmiantiobesity.models.SetNewDataValue
import com.application.bmiantiobesity.models.converterDashBoardToLoadData
import com.application.bmiantiobesity.retrofit.LoadData
import com.application.bmiantiobesity.retrofit.showErrorIfNeed
import com.application.bmiantiobesity.retrofit.updateTokenIfItNeed
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.data_list_content_recycler.view.*
import kotlinx.android.synthetic.main.data_separate_content_recycler.view.*


class DataFragment : Fragment() {

    /*companion object {
        fun newInstance() = DataFragment()
    }*/

    private var disposableSetDashBoard:Disposable? = null
    private var disposableAutoSetDashBoard:Disposable? = null
    private var disposableGetDashBoard:Disposable? = null
    //private var testSubscriber: Disposable? = null

    //private lateinit var viewModel:MainViewModel
    private val viewModel by viewModels<MainViewModel>()
    //private var mDashBoard:DashBoard? = null

    private lateinit var mainView: View
    private lateinit var myDataAdapter:DataSetAdapter
    private lateinit var recyclerView:RecyclerView

    private val data = mutableListOf<LoadData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //viewModel = ViewModelProvider(this.requireActivity()).get(MainViewModel::class.java)

        mainView = inflater.inflate(R.layout.data_fragment, container, false)
        // Настройка Toolbar
        //setToolbarTitle<MainActivity>(R.id.main_toolbar, getString(R.string.data))

        createRecyclerView()

        // Подписка на обновление DashBoard (не инициализируется вначале)
        /*disposableGetDashBoard = MainViewModel.publishNewValueFromFit
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
            {next ->
                //Обновление (здесь косячит разобраться нужно говорит, что одинаковые )
                //if (mDashBoard != next) {
                    MainViewModel.singleDashBoard?.let {
                        it.updateField(next)
                        setDataAdapter(converterDashBoardToLoadData(context!!, it))
                    }
                //}
            },
            {error ->  Log.d("Error DF-", error.message ?: "")
                Toast.makeText(context, parserError(error).toString(), Toast.LENGTH_SHORT).show()})*/

        // Подписка на обновление dashboard
        disposableSetDashBoard = viewModel.getChangeDashBoard()
            .subscribe(
                { next -> //if (BuildConfig.DEBUG) Toast.makeText(context, next.toString(), Toast.LENGTH_SHORT).show()
                    setDataAdapter(
                        converterDashBoardToLoadData(
                            requireContext(),
                            next
                        )
                    )
                },
                { error ->
                    updateTokenIfItNeed(
                        this.requireContext(),
                        viewModel,
                        error
                    )
                    showErrorIfNeed(
                        this.requireContext(),
                        error
                    )
                    //finishActivityIfTokenNotValid(this.requireActivity(), error)
                })


        return mainView
    }

    private fun createRecyclerView() {

        // Загрузка данных при обновлении и Подключение RecycleView
        recyclerView = mainView.findViewById(R.id.main_recycler_data)
        //recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Test or Work
        if (MainViewModel.singleDashBoard != null) {
            setDataAdapter(
                converterDashBoardToLoadData(
                    requireContext(),
                    MainViewModel.singleDashBoard!!
                )
            )
        } else if (BuildConfig.DEBUG) {
            // Test for Debug
            data.add(
                LoadData(
                    DataTypeInTime.WEIGHT,
                    "WEIGHT",
                    "100",
                    "funt",
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.HEIGHT,
                    "HEIGHT",
                    "196",
                    "in",
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.NECK,
                    "Neck",
                    "19",
                    "in",
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.HEARTS_RATE_ALONE,
                    "P2",
                    "",
                    "",
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.GENDER,
                    "Parametr #1",
                    "25",
                    "",
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.BLOOD_PRESSURE,
                    "Parametr #2",
                    "125",
                    "Parametr #3",
                    "76"
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.GLUCOSE,
                    "Glucose",
                    "",
                    "",
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.BODY_WEIGHT,
                    "Parametr #4",
                    "250",
                    "",
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.DAILY_ACTIVITY_LEVEL,
                    getString(R.string.activity_level),
                    "1.375",
                    "",
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.SMOKER,
                    "",
                    "False",
                    "",
                    ""
                )
            )
            setDataAdapter(data)
        }


        setListeners()

        viewModel.intData.observe(this.requireActivity(), Observer {
            //Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            data.forEach { item ->
                if (item.type == DataTypeInTime.GENDER) {
                    item.contentFirst = it.toString()
                    myDataAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun setDataAdapter(data: MutableList<LoadData>) {

        // Сортировка и вставка разделителей
        val sortedData = data.sortedByDescending { it.contentFirst }.toMutableList()
        val index = sortedData.indexOf(sortedData.find { it.contentFirst == "" })
        if (index != -1) sortedData.add(index,
            LoadData(
                DataTypeInTime.SEPARATOR,
                getString(R.string.unavailable_data),
                "",
                "",
                ""
            )
        )
        sortedData.add(0,
            LoadData(
                DataTypeInTime.SEPARATOR,
                getString(R.string.available_data),
                "",
                "",
                ""
            )
        )

        myDataAdapter = DataSetAdapter(sortedData, object : DataSetAdapter.Callback {
            override fun onItemClicked(item: LoadData) {
                //Сюда придёт элемент, по которому кликнули. Можно дальше с ним работать

                //MainViewModel.singleDashBoard = converterLoadDataToDashBoard(data) // Здесь ошибка и генерировалась!!!

                // Вызов диалогового окна установки значений
                activity.let { it1 ->
                    if (item.type == DataTypeInTime.BLOOD_PRESSURE) {
                        val first = item.contentFirst.substringBefore(" /") // почему-то дописывается значение второго поля
                        val dialogInt = DialogsClasses(viewModel, DialogType.DIALOG_DOUBLE, item.type, first.toFloatOrNull(), item.contentSecond.toIntOrNull())
                        dialogInt.show(it1!!.supportFragmentManager, "Dialog_DOUBLE")
                    } else {
                        val dialogInt = DialogsClasses(viewModel, DialogType.DIALOG_PICKER, item.type, item.contentFirst.toFloatOrNull(), null)
                        dialogInt.show(it1!!.supportFragmentManager, "Dialog_PICKER")
                    }
                }
                //MainViewModel.singleDashBoard?.let { viewModel.setDashBoardToInternet(it) }

                Log.d("UPDATE DASHBOARD -", "DF")
                //Snackbar.make(mainView, item.contentFirst, Snackbar.LENGTH_LONG).show()

                //dbAction.delete(item)
            }
        }, this.requireContext())

        recyclerView.adapter = myDataAdapter
        // Для сохранение отредактированных измененний.
        recyclerView.setItemViewCacheSize(data.size)

        //Test Для обновления по приходу данных
        myDataAdapter.notifyDataSetChanged()

    }

    override fun onDestroy() {
        disposableGetDashBoard?.let { if (!it.isDisposed) it.dispose()}
        disposableSetDashBoard?.let { if (!it.isDisposed) it.dispose()}
        disposableAutoSetDashBoard?.let { if (!it.isDisposed) it.dispose()}
        super.onDestroy()
    }

    private fun setListeners(){

        /*GoogleFitApiModel.bloodPressureDiastolic.observe(this, Observer <Int> {
            mDashBoard?.let { dash ->
                dash.blood_pressure_dia = it
                setDataAdapter(converterDashBoardToLoadData(context!!, dash))
            }
        })

        GoogleFitApiModel.bloodPressureSystolic.observe(this, Observer <Int> {
            mDashBoard?.let { dash ->
                dash.blood_pressure_sys = it
                setDataAdapter(converterDashBoardToLoadData(context!!, dash))
            }
        })*/

       MainViewModel.steps.observe(this.requireActivity(), Observer <Int> {
           if (BuildConfig.DEBUG)  Toast.makeText(context, "Шагов всего $it", Toast.LENGTH_SHORT).show()
        })
    }
}

// Адаптер для данных
class DataSetAdapter(var items: List<LoadData>, val callback: Callback, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //Определение типа сообщения
    override fun getItemViewType(position: Int): Int {
        return when (items[position].type){
            DataTypeInTime.BLOOD_PRESSURE -> 0
            DataTypeInTime.SMOKER -> 2
            DataTypeInTime.SEPARATOR -> 3
            DataTypeInTime.DAILY_ACTIVITY_LEVEL -> 4
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = when (viewType)
    {
        1 -> HolderBloodPressure(LayoutInflater.from(parent.context).inflate(R.layout.data_blood_pressure_content_recycler, parent, false))
        2 -> HolderBoolean(LayoutInflater.from(parent.context).inflate(R.layout.data_boolean_content_recycler, parent, false))
        3 -> HolderSeparator(LayoutInflater.from(parent.context).inflate(R.layout.data_separate_content_recycler, parent, false))
        4 -> HolderList(LayoutInflater.from(parent.context).inflate(R.layout.data_list_content_recycler, parent, false))
        else -> HolderMain(LayoutInflater.from(parent.context).inflate(R.layout.data_common_content_recycler, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is HolderMain -> holder.bind(items[position])
            is HolderBoolean -> holder.bind(items[position])
            is HolderBloodPressure -> holder.bind(items[position])
            is HolderSeparator -> holder.bind(items[position])
            is HolderList -> holder.bind(items[position])
            else -> throw IllegalArgumentException("Unknown Holder")
        }
    }

    inner class HolderMain(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameText:TextView = itemView.findViewById(R.id.data_content_input)
        private val valueText:TextView = itemView.findViewById(R.id.data_content_textView)
        private val dimensionText:TextView = itemView.findViewById(R.id.data_content_dimension)
        //private val strelkaText:TextView = itemView.findViewById(R.id.data_content_strelka)

        fun bind(item: LoadData) {
            nameText.text = item.firstName
            if (item.contentFirst == ""){
                valueText.isVisible = false
                dimensionText.isVisible = false
            } else {
                if (item.type != DataTypeInTime.BLOOD_PRESSURE) valueText.text = item.contentFirst
                else {
                    val str = "${item.contentFirst} / ${item.contentSecond}"
                    valueText.text = str
                }
                dimensionText.text = item.secondName
            }

            val onClickListener = View.OnClickListener {
                item.contentFirst = valueText.text.toString()
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            itemView.setOnClickListener(onClickListener)
        }
    }

    inner class HolderList(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Initialize
        private val activityLevels = itemView.resources.getStringArray(R.array.activity_levels).toList()
        val adapter = ArrayAdapter(this@DataSetAdapter.context, android.R.layout.simple_spinner_item, activityLevels)
        var countSelection:Int = 0

        private fun toIndex(value:Float?) = when (value){
            1.2f -> 0
            1.375f -> 1
            1.55f -> 2
            1.775f -> 3
            1.9f -> 4
            else -> 0
        }
        private fun toValue(index:Int) = when (index){
            0 -> 1.2f
            1 -> 1.375f
            2 -> 1.55f
            3 -> 1.775f
            4 -> 1.9f
            else -> 1.2f
        }

        fun bind(item: LoadData) {
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            itemView.data_list_textView.text = item.firstName
            //Test Position
            /*itemView.data_list_textView.setOnClickListener {
                Toast.makeText(this@DataSetAdapter.context, "Position - $adapterPosition", Toast.LENGTH_LONG).show()
            }*/

            itemView.data_list_spinner.adapter = adapter
            itemView.data_list_spinner.setSelection(toIndex(item.contentFirst.toFloatOrNull()))
            itemView.data_list_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val result = toValue(position)

                    Log.d("SelectedItem", "Count - $countSelection")

                    // Update Dashboard if it need
                    MainViewModel.singleDashBoard?.let {
                        val newValue = SetNewDataValue(DataTypeInTime.DAILY_ACTIVITY_LEVEL, result.toString(), System.currentTimeMillis())
                        if (countSelection > 0) MainViewModel.publishNewValueFromEvent.onNext(newValue)

                        //notifyItemChanged(adapterPosition)
                    }

                    countSelection++ // для того чтобы не отправлять в первый раз при установки начального значения
                }

            }
        }
    }

    inner class HolderSeparator(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: LoadData) {
            itemView.data_separator_textView.text = item.firstName
        }
    }

    inner class HolderBloodPressure(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val maxTextInputLayout:TextInputLayout = itemView.findViewById(R.id.data_content_input_max)
        private val maxEditText: EditText = itemView.findViewById(R.id.data_content_textView_max)
        private val minTextInputLayout:TextInputLayout = itemView.findViewById(R.id.data_content_input_min)
        private val minEditText: EditText = itemView.findViewById(R.id.data_content_textView_min)
        private val button:Button = itemView.findViewById(R.id.data_content_button)

        fun bind(item: LoadData) {
            maxTextInputLayout.hint = item.firstName
            maxEditText.text.clear()
            maxEditText.text.insert(0, item.contentFirst)
            minTextInputLayout.hint = item.secondName
            minEditText.text.clear()
            minEditText.text.insert(0, item.contentSecond)

            val onClickListener = View.OnClickListener {
                item.contentFirst = maxEditText.text.toString()
                item.contentSecond = minEditText.text.toString()
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            button.setOnClickListener (onClickListener)
            //itemView.setOnClickListener (onClickListener)
        }
    }

    inner class HolderBoolean(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textData:TextView = itemView.findViewById(R.id.data_boolean_content_input)
        private val btSwitch:SwitchMaterial = itemView.findViewById(R.id.data_boolean_content_switcher)


        fun bind(item: LoadData) {
            btSwitch.isChecked = item.contentFirst.toBoolean()
            if (btSwitch.isChecked) textData.setText(R.string.yes) else textData.setText(R.string.no)

            btSwitch.setOnCheckedChangeListener { _, isChecked ->
                item.contentFirst = isChecked.toString()
                if (isChecked) textData.setText(R.string.yes) else textData.setText(R.string.no)

                // Не отправляет в MainActivity
                //GoogleFitApiModel.liveDashBoard.value?.smoker = isChecked
                MainViewModel.singleDashBoard?.let {
                    it.smoker = isChecked
                    val newValue =
                        SetNewDataValue(DataTypeInTime.SMOKER, isChecked.toString(), System.currentTimeMillis())
                    MainViewModel.publishNewValueFromEvent.onNext(newValue)
                }
                //notifyDataSetChanged()
            }

            /*val onClickListener = View.OnClickListener {
                item.contentFirst = maxEditText.text.toString()
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }*/

            //button.setOnClickListener (onClickListener)
            //itemView.setOnClickListener (onClickListener)
        }
    }

    interface Callback {
        fun onItemClicked(item: LoadData)
    }
}