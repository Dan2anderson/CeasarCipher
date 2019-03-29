package com.anderson.danny.ceasercipher

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity(),
    ScrollSelector.OnButtonSelectedListener,
    KeyEvent.Callback {

    internal object RequestCode {
        const val PICK_CONTACT = 100
        const val PERM_REQ_READ_CONTACTS = 101
    }

    private var cipherViewModel: CipherViewModel? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                userText.isEnabled = true
                cipherViewModel?.mode = UiState.EDIT
                userText.setText(cipherViewModel?.getUserText())
                @Suppress("UsePropertyAccessSyntax")
                explanation.setText(resources.getText(R.string.cipher_explanation))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_encode -> {
                userText.isEnabled = false
                cipherViewModel?.mode = UiState.ENCODE
                userText.setText(cipherViewModel?.getModeText())
                @Suppress("UsePropertyAccessSyntax")
                explanation.setText(resources.getText(R.string.cipher_explanation))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_decode -> {
                userText.isEnabled = false
                cipherViewModel?.mode = UiState.DECODE
                userText.setText(cipherViewModel?.getModeText())
                @Suppress("UsePropertyAccessSyntax")
                explanation.setText(resources.getText(R.string.decode_explanation))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        cipherViewModel = ViewModelProviders.of(this).get(CipherViewModel::class.java)
    }


    override fun onStart() {
        super.onStart()
        cipherViewModel?.mode = UiState.EDIT
        scrollSelector.setOnClickListener(this)
        userText.addTextChangedListener(InterceptTextWatcher())
        scrollSelector.selectFirstOption()
        send_fab.setOnClickListener {
            selectContact()
        }
    }


    override fun onButtonSelected(button: Button) {
        cipherViewModel?.setOffset(button.text[0])
        userText.setText(cipherViewModel?.getModeText())
    }

    private fun selectContact() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) { // not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {//show explanation
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setMessage(resources.getText(R.string.we_need_permission))
                alertBuilder.setPositiveButton(resources.getText(R.string.positive_button_text)) { _, _ ->
                    requestContactPermission()
                }
                alertBuilder.setNegativeButton("no", null)
                alertBuilder.show()
            } else {
                requestContactPermission()
            }
        } else {
            //has permission
            launchSelectContactIntent()
        }
    }

    private fun requestContactPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            RequestCode.PERM_REQ_READ_CONTACTS
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RequestCode.PERM_REQ_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    launchSelectContactIntent()
                }
                return
            }
        }
    }

    /**
     * Do not call with out checking for permissions.
     */
    private fun launchSelectContactIntent() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, RequestCode.PICK_CONTACT)
    }

    private fun sendTextIntent(number: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null))
        intent.putExtra("sms_body", cipherViewModel?.getModeText())

        startActivity(intent)
    }

    public override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        when (reqCode) {
            RequestCode.PICK_CONTACT ->
                if (resultCode == Activity.RESULT_OK) {
                    val contactUri = data?.data ?: return
                    contentResolver.query(contactUri, null, null, null, ContactsContract.Contacts.DISPLAY_NAME)
                        .use { cursor ->
                            if (cursor == null) {
                                return
                            }
                            if (cursor.moveToFirst()) {
                                val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                                val hasPhone =
                                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                                if (hasPhone.equals("1", ignoreCase = true)) {
                                    contentResolver.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null
                                    ).use { phoneCursor ->
                                        if (phoneCursor == null) {
                                            return
                                        }
                                        phoneCursor.moveToFirst()
                                        val phoneNumber =
                                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                        sendTextIntent(phoneNumber)
                                    }
                                }
                            }
                        }
                }
        }
    }

    inner class InterceptTextWatcher : TextWatcher {
        /**
         * @param s if s is changed it will call this function recursivly.
         */
        override fun afterTextChanged(s: Editable?) {
            cipherViewModel?.setText(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /**no op*/        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /**no op*/        }
    }

}
