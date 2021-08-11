package dev.demo.shoppal.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val READ_STORAGE_PERMISSION_CODE: Int = 123
    const val USERS: String = "users"
    const val SHOPPAL_PREFERENCES: String = "ShopPalPrefs"
    const val LOGGED_IN_USERNAME: String = "LoggedInUsername"
    const val EXTRA_USER_DETAILS: String = "ExtraUserDetails"
    const val PICK_IMAGE_REQUEST_CODE = 456

    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"

    const val USER_PROFILE_IMAGE: String = "UserProfileImage"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}