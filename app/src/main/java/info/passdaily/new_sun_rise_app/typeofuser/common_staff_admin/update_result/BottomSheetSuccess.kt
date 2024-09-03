package info.passdaily.new_sun_rise_app.typeofuser.common_staff_admin.update_result

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.new_sun_rise_app.R
import info.passdaily.new_sun_rise_app.databinding.BottomSheetSuccessBinding

class BottomSheetSuccess : BottomSheetDialogFragment {

    private var _binding: BottomSheetSuccessBinding? = null
    private val binding get() = _binding!!

    var message = ""
    constructor()
    lateinit var updateResultListener: UpdateResultListener

    constructor(updateResultListener: UpdateResultListener,message: String){
        this.updateResultListener = updateResultListener
        this.message = message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)


        _binding = BottomSheetSuccessBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code
            updateResultListener.onFinishListener("")
        }, 3000)


    }

    companion object {
        var TAG = "BottomSheetSuccess"
    }
}