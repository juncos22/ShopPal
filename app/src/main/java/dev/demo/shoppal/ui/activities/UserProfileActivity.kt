package dev.demo.shoppal.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.demo.shoppal.R
import dev.demo.shoppal.firestore.FirestoreClass
import dev.demo.shoppal.models.User
import dev.demo.shoppal.utils.Constants
import dev.demo.shoppal.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.lang.Exception

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var userDetails: User
    private var selectedImageUri: Uri? = null
    private var userProfileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        if (userDetails.profileCompleted == 0) {
            tv_profile_title.text = resources.getString(R.string.profile_title)
            ti_firstName.isEnabled = false
            ti_lastName.isEnabled = false
        }else {
            setupActionBar()
            tv_profile_title.text = resources.getString(R.string.edit_profile_title)
            GlideLoader(this@UserProfileActivity).loadUserPicture(userDetails.image, user_img)

            if (userDetails.mobile != 0L) {
                ti_phone.setText(userDetails.mobile.toString())
            }
            if (userDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            }else {
                rb_female.isChecked = true
            }
        }
        ti_email.isEnabled = false
        ti_firstName.setText(userDetails.firstName)
        ti_lastName.setText(userDetails.lastName)
        ti_email.setText(userDetails.email)

        user_img.setOnClickListener(this@UserProfileActivity)
        btn_save_profile.setOnClickListener(this@UserProfileActivity)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.user_img -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {

                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_save_profile -> {
                    if (validateUserProfileDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (selectedImageUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(this, selectedImageUri)
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val firstName = ti_firstName.text.toString().trim { it <= ' ' }
        if (firstName != userDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        val lastName = ti_lastName.text.toString().trim { it <= ' ' }
        if (lastName != userDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = ti_phone.text.toString().trim { it <= ' ' }
        if (mobileNumber.isNotEmpty() && mobileNumber != userDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }
        if (gender.isNotEmpty() && gender != userDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        if (userProfileImageUrl.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = userProfileImageUrl
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(this, userHashMap)

    }
    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.profile_updated),
            Toast.LENGTH_SHORT
        )
            .show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        selectedImageUri = data.data!!
                        GlideLoader(this).loadUserPicture(selectedImageUri!!, user_img)
                    } catch (e: Exception) {
                        showErrorSnackbar(e.message!!, true)
                    }
                }
            }
        }
    }

    fun imageUploadSuccess(imageUrl: String) {
        userProfileImageUrl = imageUrl
        updateUserProfileDetails()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.storage_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(ti_phone.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_phone_number), true)
                false
            }
            else -> {
                true
            }
        }
    }
}