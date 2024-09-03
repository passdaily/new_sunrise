package info.passdaily.new_sun_rise_app.typeofuser.common_staff_admin.staff_punch_attendance

import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import info.passdaily.new_sun_rise_app.R;
import info.passdaily.new_sun_rise_app.databinding.ActivityStaffAttendanceBinding
import info.passdaily.new_sun_rise_app.services.Global
import info.passdaily.new_sun_rise_app.services.Utils
import kotlinx.coroutines.*
import java.util.*


@Suppress("DEPRECATION")
class StaffAttendanceActivity : AppCompatActivity(){


    var TAG = "StaffAttendanceActivity"
    private lateinit var binding: ActivityStaffAttendanceBinding

    var doubleBackToExitPressedOnce = false

    var toolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      ///  setContentView(R.layout.activity_staff_attendance)
        binding = ActivityStaffAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Staff Attendance"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        if (savedInstanceState == null) {
            var fragmentManager = supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_staff_host_fragment, StaffAttendanceFragment(0,""))
//            .addToBackStack("home").commit()
                .commit()
        }

        Utils.setStatusBarColor(this)
    }


    override fun onBackPressed() {
        // super.onBackPressed()
        when (Global.screenState) {
            "staffAttendance" -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_staff_host_fragment, StaffAttendanceFragment(0,"")).commit()
            }
            "landingpage" -> {
//                if (!doubleBackToExitPressedOnce) {
//                    doubleBackToExitPressedOnce = true
//                    Toast.makeText(this, "click back again to exit.", Toast.LENGTH_SHORT).show()
//                    Handler(Looper.getMainLooper()).postDelayed(
//                        { doubleBackToExitPressedOnce = false },
//                        2000
//                    )
//                } else {
//                    Global.currentPage = 1
                    super.onBackPressed()
//                }
            }

        }
    }

}