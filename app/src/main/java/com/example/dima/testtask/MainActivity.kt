package com.example.dima.testtask

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.vk.sdk.api.VKError
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.api.VKApi
import com.vk.sdk.api.model.VKApiUser
import com.vk.sdk.api.model.VKList
import com.vk.sdk.api.VKResponse
import com.vk.sdk.api.VKRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(){

    private val callbackManager = CallbackManager.Factory.create()

    var btnClicked: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFacebookLogin()
        initVkLogin()

    }
    private fun initVkLogin(){
        vk_login_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                btnClicked = 1
                VKSdk.login(this@MainActivity, VKScope.NOTIFY)
            }
        })

    }
    private fun initFacebookLogin(){
        facebook_login_button.setReadPermissions(Arrays.asList("public_profile"))
        facebook_login_button.loginBehavior = LoginBehavior.WEB_VIEW_ONLY



        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                setResult(Activity.RESULT_OK)
                val request = GraphRequest.newMeRequest(result?.accessToken,
                        object : GraphRequest.GraphJSONObjectCallback {
                            override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                                val name = `object`?.getString("name")
                                val imageUrl = `object`?.getJSONObject("picture")?.getJSONObject("data")
                                        ?.getString("url")

                                if (name != null && imageUrl != null) {
                                    val intent = Intent(this@MainActivity, TestTaskActivity::class.java)
                                    intent.putExtra(this@MainActivity.resources.getString(R.string.first_name), name)
                                    intent.putExtra(this@MainActivity.resources.getString(R.string.imageUrl), imageUrl)
                                    intent.putExtra(this@MainActivity.resources.getString(R.string.btn_clicked), 2)


                                    startActivity(intent)
                                }
                            }
                        })
                var parametres = Bundle()
                parametres.putString("fields", "id,name,email,picture.type(large)")
                request.parameters = parametres
                request.executeAsync()
            }


            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }
        })

    }

    private fun initGoogleLogin() {

        val googleSignInOptions = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestId().requestProfile().build()
        val signInClient = GoogleSignIn.getClient(this, googleSignInOptions)


        signInClient.signOut()
        //signInClient.revokeAccess()

        google_login_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = signInClient.signInIntent
                startActivityForResult(intent, 3)


            }
        })
    }

    override fun onPostResume() {
        super.onPostResume()
        initGoogleLogin()
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired) {
            LoginManager.getInstance().logOut()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (btnClicked) {
            1 -> vkLoginBtnClicked(requestCode, resultCode, data)
            0 -> callbackManager.onActivityResult(requestCode, resultCode, data)

        }
        if(requestCode == 3){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleSignInResult(task)

        }
        btnClicked = 0


    }
    private fun googleSignInResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account = completedTask.getResult(ApiException::class.java)
            val intent = Intent(this@MainActivity, TestTaskActivity::class.java)
            intent.putExtra(this@MainActivity.resources.getString(R.string.first_name), account.displayName)

            intent.putExtra(this@MainActivity.resources.getString(R.string.imageUrl), account.photoUrl.toString())
            intent.putExtra(this@MainActivity.resources.getString(R.string.btn_clicked), 3)
            startActivity(intent)

        }catch (e: ApiException){

        }
    }

    private fun vkLoginBtnClicked(requestCode: Int, resultCode: Int, data: Intent?) {
        VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                VKApi.users().get().executeWithListener(object : VKRequest.VKRequestListener() {
                    override fun onComplete(response: VKResponse?) {
                        val user = (response!!.parsedModel as VKList<VKApiUser>)[0]
                        val intent = Intent(this@MainActivity, TestTaskActivity::class.java)
                        intent.putExtra(this@MainActivity.resources.getString(R.string.first_name), user.first_name)
                        intent.putExtra(this@MainActivity.resources.getString(R.string.imageUrl), user.photo_max_orig)
                        intent.putExtra(this@MainActivity.resources.getString(R.string.btn_clicked), 1)


                        startActivity(intent)
                    }
                })

            }

            override fun onError(error: VKError) {}

        })

    }


}
