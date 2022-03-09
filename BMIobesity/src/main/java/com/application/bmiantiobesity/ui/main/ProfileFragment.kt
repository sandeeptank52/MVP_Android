package com.application.bmiantiobesity.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.ui.settings.SettingsActivity

import com.application.bmiantiobesity.utilits.OnHorizontalSwipeListener
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile_fragment.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_fragment, container, false)


        MainViewModel.updateProfile.observe(this.requireActivity(), Observer {
            if (it.last_name.isNotEmpty()) view.profile_main_name.text = "${it.first_name} ${it.last_name}"
            else view.profile_main_name.text = it.first_name

            // Здесь загрузка картинки
            if (it.image != null)
                Glide.with(this.requireContext())
                    .load(it.image)
                    .placeholder(R.drawable.ic_avatar_profile)
                    .circleCrop()
                    .into(view.profile_main_imageView)
        })

        MainViewModel.singleDashBoard?.let {
            if (!it.birth_date.isNullOrEmpty()) view.text_date.text = it.birth_date
        }


        //Personal Card
        val personalCardListener = View.OnClickListener {
            startSettingsActivity<SettingsActivity>(
                this.requireActivity(),
                false,
                "ProfileDetailFragment"
            )
        }
        //view.constraint_personal.setOnClickListener(personalCardListener)
        view.frame_profile.setOnClickListener(personalCardListener)



        //MedCard
        view.constraint_medcard.setOnClickListener {
            findNavController().navigate(R.id.dataFragment)
            //Toast.makeText(this.requireContext(), getText(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }

        //Doctor
        view.constraint_doctor.setOnClickListener {
            if (BuildConfig.DEBUG) findNavController().navigate(R.id.doctorFragment) else
                Toast.makeText(this.requireContext(), getText(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }


        // Device data
        //val deviceListener = View.OnClickListener { Toast.makeText(this.requireContext(), getText(R.string.coming_soon), Toast.LENGTH_SHORT).show() }


        //Settings
        view.constraint_settings.setOnClickListener {
            startSettingsActivity<SettingsActivity>(this.requireActivity(), false, "SettingsFragment")
            //Toast.makeText(this.requireContext(), getText(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }


        //Connected devices
        view.constraint_devices.setOnClickListener {
            //startSettingsActivity<SettingsActivity>(this.requireActivity(), false, "ConnectToFragment")
            Toast.makeText(this.requireContext(), getText(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }


        //Help & Support
        view.constraint_help.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.support_mail),null))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, MainViewModel.singleProfile?.email ?: "")
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
        }


        // Go to Site
        view.profile_site_text.setOnClickListener {
            val uriString = "https://intime.digital"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uriString)))
        }

        // Добавления контролёра свайпа
        //listenHorizontalSwipe(view)

        return view
    }

    //Обработчик свайпа
    private fun listenHorizontalSwipe(view:View){
        view.setOnTouchListener(object : OnHorizontalSwipeListener(this@ProfileFragment.requireContext()){
            override fun onRightSwipe() {
                //Work good
                /*try {
                    val mainActivity = this@ProfileFragment.activity as ChangeFragment
                    mainActivity.changeFragment(MainActivity.menu.findItem(R.id.main_menu_main))
                }catch (ex: Exception){
                    Log.d("Swipe-MF", ex.message ?: "")
                }*/
                Log.d("Swipe-PF", "Swipe RIGHT!")
            }

            override fun onLeftSwipe() {
                //Work good
                Log.d("Swipe-PF", "Swipe LEFT!")
            }
        })
    }

}
