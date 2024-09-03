package info.passdaily.new_sun_rise_app.typeofuser.common_staff_admin.quick_voice.send_to_voice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.new_sun_rise_app.R
import info.passdaily.new_sun_rise_app.databinding.DialogSendToParentBinding
import info.passdaily.new_sun_rise_app.lib.ProgressBarDialog
import info.passdaily.new_sun_rise_app.model.*
import info.passdaily.new_sun_rise_app.services.Status
import info.passdaily.new_sun_rise_app.services.Utils
import info.passdaily.new_sun_rise_app.services.ViewModelFactory
import info.passdaily.new_sun_rise_app.services.client_manager.ApiClient
import info.passdaily.new_sun_rise_app.services.client_manager.NetworkLayerStaff
import info.passdaily.new_sun_rise_app.services.localDB.LocalDBHelper
import info.passdaily.new_sun_rise_app.typeofuser.common_staff_admin.quick_voice.QuickVoiceMessageViewModel
import info.passdaily.new_sun_rise_app.typeofuser.common_staff_admin.quick_voice.VoiceMessageTabClicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class SendToGroupDialog : DialogFragment,StudentSelectionListener {

    lateinit var voiceMessageTabClicker: VoiceMessageTabClicker

    companion object {
        var TAG = "SendToGroupDialog"
    }

    private lateinit var quickVoiceMessageViewModel: QuickVoiceMessageViewModel
    private var _binding: DialogSendToParentBinding? = null
    private val binding get() = _binding!!
    var aCCADEMICID = 0
    var gROUPID = 0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getGroupList = ArrayList<GroupListModel.Group>()


    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var  schoolId = 0

    var getGroupStudents = ArrayList<GroupStudentModel.GroupStudent>()

    var selectedValues = ArrayList<Int>()

    lateinit var mAdapter : GroupAdapter
    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null
    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null

    var toolbar : Toolbar? = null

    var checkSelectAll : CheckBox? = null

    lateinit var voiceMessageListModel: VoiceMessageListModel.Voice
    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(voiceMessageListModel: VoiceMessageListModel.Voice, voiceMessageTabClicker : VoiceMessageTabClicker, url : String) {
        this.voiceMessageListModel = voiceMessageListModel
        this.voiceMessageTabClicker = voiceMessageTabClicker
        this.url = url
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        quickVoiceMessageViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickVoiceMessageViewModel::class.java]

        _binding = DialogSendToParentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Send To Group"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass


        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_group)

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())

        checkSelectAll = binding.checkSelectAll
        checkSelectAll?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (isChecked) {
                    for (i in getGroupStudents.indices) {
                        getGroupStudents[i].isChecked = true
                    }
                    mAdapter.notifyDataSetChanged()
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setMessage("Do you want to select all?")
                        .setCancelable(true)
                        .setPositiveButton(
                            "yes"
                        ) { dialog, id ->
                            selectedValues = ArrayList<Int>()
                            for (i in getGroupStudents.indices) {
                                selectedValues.add(i)
                            }
                        }
                    //Creating dialog box
                    val alert = builder.create()
                    //Setting the title manually
                    alert.setTitle("Select all")
                    alert.show()
                    val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                    buttonbackground.setTextColor(Color.BLACK)
                    val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    buttonbackground1.setTextColor(Color.BLACK)
                } else {
                    for (i in getGroupStudents.indices){ getGroupStudents[i].isChecked = false}
                    mAdapter.notifyDataSetChanged()
                    for (i in getGroupStudents.indices) {
                        selectedValues.remove(i)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "no data for select", Toast.LENGTH_LONG).show()
            }
        })


        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                groupListFun(aCCADEMICID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                gROUPID = getGroupList[position].gROUPID
                getFinalList(aCCADEMICID,gROUPID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.fab.visibility = View.GONE
        binding.buttonSubmit.visibility = View.VISIBLE
        binding.buttonSubmit.setOnClickListener {
            Log.i(TAG,"selectedValues $selectedValues")
            if (selectedValues.size > 0) {
                progressStart();
                for (i in selectedValues.indices) {
                 //   var url = "Group/SendMessageToGroup"
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("MESSAGE_ID", voiceMessageListModel.vOICEMAILID)
                        jsonObject.put("GROUP_NAME", getGroupStudents[selectedValues[i]].gROUPNAME)
                        jsonObject.put("STUDENT_ID",  getGroupStudents[selectedValues[i]].sTUDENTID)
                        jsonObject.put("STUDENT_FNAME",  getGroupStudents[selectedValues[i]].sTUDENTFNAME)
                        jsonObject.put("STUDENT_GUARDIAN_NAME", getGroupStudents[selectedValues[i]].sTUDENTGUARDIANNAME)
                        jsonObject.put("STUDENT_GUARDIAN_NUMBER", getGroupStudents[selectedValues[i]].sTUDENTGUARDIANNUMBER)
                        jsonObject.put("ACCADEMIC_TIME",  getGroupStudents[selectedValues[i]].aCCADEMICTIME)
                        jsonObject.put("ADMIN_ID", adminId)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")
                    val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    submitJsonList(url,accademicRe,i)
                }
            } else {
                Utils.getSnackBar4K(requireActivity(),"Select atleast one student",binding.constraintLayoutContent)
            }
        }

        initFunction()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun submitJsonList(url: String, jsonObject: RequestBody, position: Int) {

        quickVoiceMessageViewModel.getCommonPostFun(url,jsonObject)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
//                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Message send successfully", constraintLayoutContent!!)
//                                    initFunction()
                                }
                                Utils.resultFun(response) == "FAILED" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Message sent failed", constraintLayoutContent!!)
                                }
                            }
                            if (position + 1 == selectedValues.size) {
                                Log.i(TAG,"selectedValues $position")
                                progressStop()
//                                cancelFrg()
//                                voiceMessageTabClicker.onCloseBottomSheet("Message send successfully")
                                selectedValues = ArrayList<Int>()
                                for (i in getGroupStudents.indices){ getGroupStudents[i].isChecked = false}
                                mAdapter.notifyDataSetChanged()
                            }
                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
//                            progressStop()
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
//                            progressStart()
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })


    }

    private fun initFunction() {
        quickVoiceMessageViewModel.getYearClassExam(adminId, schoolId )
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }



                            Log.i(TAG,"initFunction SUCCESS")

                        }
                        Status.ERROR -> {
                            shimmerViewContainer?.visibility = View.GONE
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            shimmerViewContainer?.visibility = View.VISIBLE
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }

    fun groupListFun(aCCADEMICID: Int) {
        // /////Public member
        //    //http://demostaff.passdaily.in/ElixirApi/PublicGroup/GroupListGet?AccademicId=0
        quickVoiceMessageViewModel.getGroupListForStudentDelete("Group/GroupListGet",aCCADEMICID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getGroupList = response.groupList as ArrayList<GroupListModel.Group>
                            var group = Array(getGroupList.size){""}
                            for (i in getGroupList.indices) {
                                group[i] = getGroupList[i].gROUPNAME!!
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    group
                                )
                                spinnerClass?.adapter = adapter
                            }

                            Log.i(TAG,"initFunction SUCCESS")

                        }
                        Status.ERROR -> {
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }

    fun getFinalList(aCCADEMICID: Int, gROUPID: Int){
        quickVoiceMessageViewModel.getGroupStudentList(aCCADEMICID,gROUPID,schoolId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getGroupStudents = response.groupStudents as ArrayList<GroupStudentModel.GroupStudent>
                            if(getGroupStudents.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                mAdapter = GroupAdapter(
                                    this,
                                    getGroupStudents,
                                    requireActivity(),
                                    TAG
                                )
                                recyclerViewVideo!!.adapter = mAdapter

                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_state_pta)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getGroupStudents = ArrayList<GroupStudentModel.GroupStudent>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_state_pta)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    class GroupAdapter(
        var studentSelectionListener: StudentSelectionListener,
        var getGroupStudents: ArrayList<GroupStudentModel.GroupStudent>,
        var context: Context, var TAG : String)
        : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

        var mylist = ArrayList<Int>()
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textAcademicYear: TextView = view.findViewById(R.id.textAcademicYear)
         //   var imageViewCheck : ImageView = view.findViewById(R.id.imageViewCheck)
            var checkbox : CheckBox = view.findViewById(R.id.checkbox)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.student_selected_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textStudentName.text = getGroupStudents[position].gROUPNAME

            holder.textAcademicYear.text = "Academic : ${getGroupStudents[position].aCCADEMICTIME}"

//            if (getStudentList[position].isChecked) {
//                // viewHolder.checkBox.setChecked(true);
//                holder.imageViewCheck.setImageResource(R.drawable.ic_checked_black)
//                mylist.add(position)
//
//            } else {
//                //viewHolder.checkBox.setChecked(false);
//                holder.imageViewCheck.setImageResource(R.drawable.ic_check_gray)
//                mylist.remove(position)
//                studentSelectionListener.onRemoveSelected(mylist)
//            }
//
//            studentSelectionListener.onShowSelected(mylist)
//
//            holder.itemView.setOnClickListener {
//                getStudentList[position].isChecked = !getStudentList[position].isChecked
//                notifyItemChanged(position)
//            }

            holder.checkbox.isChecked = getGroupStudents[position].isChecked
            holder.checkbox.setOnCheckedChangeListener { compoundButton, b ->
                if (compoundButton.isChecked) {
                    getGroupStudents[position].isChecked = true
                    compoundButton.isChecked = true
                    mylist.add(position)
//                    studentSelectionListener.onShowSelected(mylist)
                } else {
                    getGroupStudents[position].isChecked = false
                    studentSelectionListener.onRemoveSelected(mylist)
                    compoundButton.isChecked = false
                    mylist.remove(position)
//                    studentSelectionListener.onShowSelected(mylist)
                }
                studentSelectionListener.onShowSelected(mylist)
            }

        }

        override fun getItemCount(): Int {
            return getGroupStudents.size
        }
        override fun getItemViewType(position: Int): Int {
            return position
        }

    }

    override fun onShowSelected(selectedValue: ArrayList<Int>) {
        this.selectedValues = selectedValue
        Log.i(TAG, "selectedValues ${selectedValues.size}")
        Log.i(TAG, "getStudentDetails ${getGroupStudents.size}")
//        if(selectedValues.size == getStudentDetails.size){
//            checkSelectAll?.isChecked = true
//        }
    }

    override fun onRemoveSelected(selectedValue: ArrayList<Int>) {
       checkSelectAll?.isChecked = false
    }

    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }

    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }


}
