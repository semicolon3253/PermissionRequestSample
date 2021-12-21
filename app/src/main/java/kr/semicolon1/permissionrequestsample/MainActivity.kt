package kr.semicolon1.permissionrequestsample

/**
 * 작성자: Semicolon
 *
 * 이 프로젝트는 안드로이드 M부터 적용되는 새로운 위험 권한 요청 방식을 처리하는
 * 예시를 보여준다.
 * 어디까지나 예시이므로 참고하여 자신에 맞게 이용하면 된다.
 */


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kr.semicolon1.permissionrequestsample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val permission_array = arrayListOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.ADD_VOICEMAIL,
        Manifest.permission.USE_SIP,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECEIVE_MMS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val data_list = ArrayList<ItemData>()
    private val PERMISSION_REQUEST_CODE = 333

    // 권한 목록 커스텀 리스트뷰 생성
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        for (name in permission_array){
            val obj = ItemData(false, name)
            data_list.add(obj)
        }

        val adapter = CustomAdapter(this, data_list)
        binding.permissionListView.adapter = adapter

        // 요청 버튼 클릭 리스너
        binding.btnRequest.setOnClickListener {
            permissionRequest()
        }
    }

    private fun permissionRequest() {
        val selectedList = ArrayList<String>()

        // 선택된 권한들만 추려낸다.
        for ((_, v) in data_list.withIndex()){
            if(v.isChecked){
                selectedList.add(v.name)
            }
        }

        // 선택된 권한 중 거부된 권한만 추려낸다.
        val deniedList = getNotGrantedPermissions(applicationContext, selectedList)
        if (deniedList.size > 0){  // 거부된 권한이 하나라도 있다면 모두 요청한다.
            val denied_permissions: Array<String?> = arrayOfNulls(deniedList.size)
            ActivityCompat.requestPermissions(this, deniedList.toArray(denied_permissions), PERMISSION_REQUEST_CODE)
        }

    }


    // 거부된 권한들만 추려내는 함수
    private fun getNotGrantedPermissions(ctx: Context, permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions){
            if(ActivityCompat.checkSelfPermission(ctx, permission) != PackageManager.PERMISSION_GRANTED){
                result.add(permission)
                Log.d("TEST", "$permission is Denied")
            }else{
                Log.d("TEST", "$permission is Granted")
            }
        }
        return result
    }


    // 사용자가 권한을 설정한 후 실행되는 콜백
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (requestCode == PERMISSION_REQUEST_CODE){
            val never_ask_again_permissions = ArrayList<String>()

            for ((i, grant) in grantResults.withIndex()){  // 처리된 권한들의 허용 여부를 인덱스와 함께 가져온다.
                val permissionName = permissions[i]  // 현재 인덱스의 권한 이름을 가져온다
                val isGrant = (grant == PackageManager.PERMISSION_GRANTED)
                val rationale = shouldShowRequestPermissionRationale(permissionName)
                //  Log.d("test", "$permissionName:  rationale=$rationale, isGrant=$isGrant")

                // !rationale && isGrant  // 권한 허용됨. 처리 불필요
                // rationale && !isGrant  // 사용자가 거절함
                // !rationale && !isGrant  // 다시 안보기 거절
                // rationale && isGrant  // 불가능

                if (!rationale && isGrant){
                    Log.d("TEST", "$permissionName is Granted.")
                }else if(rationale && !isGrant){
                    Log.d("TEST", "$permissionName is Denied.")
                }else if (!rationale && !isGrant){
                    Log.d("TEST", "$permissionName is Denied(Never-Ask-Again).")
                    never_ask_again_permissions.add(permissionName)
                }else{
                    Log.d("TEST", "IMPOSSIBLE!")
                }
            }

            if (never_ask_again_permissions.size > 0){
                // 다시안보기 거절된 권한들을 가져와서 적당히 설명하는 다이얼로그 띄우기
                // 이 if 문 내에서 변수 never_ask_again_permissions 에 들어있는 권한들을 반복문으로 하나씩 꺼내면서
                // 각 권한들에 맞는 설명을 이어붙인 후
                // 다이얼로그 메시지를 띄우도록 하면 좋을 것 같다.
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("권한 승인 필요")
                dialog.setMessage("(대충 이 권한(들)이 필요한 이유)")
                dialog.setPositiveButton("허용하기"){ _, _ ->
                    openAppSetting()  // 사용자가 직접 권한을 지정하기 위해 이 앱의 설정창을 열어준다.
                }
                dialog.setNegativeButton("취소"){ _, _ ->

                }
                dialog.show()
            }
        }
    }


    // 이 앱의 설정을 연다.
    private fun openAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}