package com.application.bmiantiobesity.interceptor

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.application.bmiantiobesity.R
import kotlinx.android.synthetic.main.interceptor_activity.*


class InterceptorActivity : AppCompatActivity() {

    //private lateinit var model: RequestViewModel// by activityViewModels()
    private val viewModel by viewModels<RequestViewModel>()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interceptor_activity)
        //model = ViewModelProvider(this).get(RequestViewModel::class.java)
        setSupportActionBar(appBar)
        navController = findNavController(R.id.interceptor_nav_host)

        //findNavController()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.interceptor_menu, menu)

        menu?.findItem(R.id.clearAllAction)?.setOnMenuItemClickListener {
            if (navController.currentDestination?.id  == R.id.navDetailRequest){
                navController.popBackStack()
            }

            viewModel.deleteAll()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {

        /*if (navController.currentDestination?.id == R.id.navListRequest) {

            finish()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            super.onBackPressed()
        }*/


        //this.finishActivity(0)
        super.onBackPressed()
    }
}