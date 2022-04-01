package com.pasa.groundup_insta2.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.pasa.groundup_insta2.LoginActivity
import com.pasa.groundup_insta2.MainActivity
import com.pasa.groundup_insta2.Post
import com.pasa.groundup_insta2.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

class ComposeFragment : Fragment() {

    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034

    lateinit var ivPreview: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {

        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPreview = view.findViewById<ImageView>(R.id.imageView)

        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {

            val description = view.findViewById<EditText>(R.id.etDescription).text.toString()
            val user = ParseUser.getCurrentUser()

            if (photoFile != null) {

                submitPost(description, user, photoFile!!)

            }

            else {

                Toast.makeText(requireContext(), "Error! No picture found.", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnPhoto).setOnClickListener {

            onLaunchCamera()
        }
    }

    fun submitPost(description: String, user: ParseUser, file: File) {

        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))

        post.saveInBackground { exception ->

            if (exception != null) {

                Log.e(MainActivity.TAG, "Action failed.")
                Toast.makeText(requireContext(), "Error! Description required.", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()

            }

            else {

                Toast.makeText(requireContext(), "Success.", Toast.LENGTH_SHORT).show()
                Log.i(MainActivity.TAG, "Success.")
            }
        }
    }

    fun onLaunchCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri(photoFileName)

        if (photoFile != null) {

            val fileProvider: Uri = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (intent.resolveActivity(requireContext().packageManager) != null) {

                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {

        val mediaStorageDir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), MainActivity.TAG)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "Action failed.")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {

                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                ivPreview.setImageBitmap(takenImage)

            }

            else {

                Toast.makeText(requireContext(), "Action failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}