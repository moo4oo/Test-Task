package com.example.dima.testtask

import android.content.Context
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.example.dima.testtask.R.id.drawer_layout
import com.example.dima.testtask.R.id.nav_view
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.squareup.picasso.Picasso
import com.vk.sdk.VKSdk
import kotlinx.android.synthetic.main.activity_test_task.*
import kotlinx.android.synthetic.main.app_bar_test_task.*
import kotlinx.android.synthetic.main.content_test_task.*
import kotlinx.android.synthetic.main.nav_header_test_task.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestTaskActivity : AppCompatActivity() {
    var page = 1
    var maxPage = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_task)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        Picasso.get()
                .load(intent.extras.getString(this.resources.getString(R.string.imageUrl)))
                .resize(200, 200)
                .centerCrop()
                .into(nav_view.getHeaderView(0).findViewById<ImageView>(R.id.profile_image_view))
        nav_view.getHeaderView(0).first_name_view.text = intent.extras.getString(this.resources.getString(R.string.first_name))
        when (intent.extras.get(this.resources.getString(R.string.btn_clicked))) {
            1 -> vkLoginBtnClicked()
            2 -> facebookLoginBtnClicked()
            3 -> googleLoginBtnClicked()
        }


        search_start_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                rec_view.visibility = View.GONE
                progress_bar_view.visibility = View.VISIBLE
                find_user_view.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this@TestTaskActivity.currentFocus.windowToken, 0)
                Thread.sleep(600)
                val githubApi = GithubApi.create(this@TestTaskActivity.applicationContext)
                githubApi.searchUsers(find_user_view.text.toString(), page, 30).enqueue(object : Callback<UsersList>{
                    override fun onResponse(call: Call<UsersList>?, response: Response<UsersList>?) {
                        if(response!!.isSuccessful){

                            rec_view.apply {
                                setHasFixedSize(true)
                                rec_view.setHasFixedSize(true)
                                rec_view.layoutManager = LinearLayoutManager(this@TestTaskActivity)
                                rec_view.adapter = UsersListAdapter(response.body(), response.body()!!.items.size)

                                progress_bar_view.visibility = View.GONE
                                rec_view.visibility = View.VISIBLE
                            }

                        }
                    }

                    override fun onFailure(call: Call<UsersList>?, t: Throwable?) {

                    }
                })

            }
        })


        prev_page_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(--page < 1){
                    page = 1
                }else{
                    search_start_btn.performClick()
                }
            }
        })
        next_page_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(++page < 1){
                    page = 1
                }
                search_start_btn.performClick()
            }
        })

    }

    private fun vkLoginBtnClicked() {

        nav_view.getHeaderView(0).logout_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                VKSdk.logout()
                this@TestTaskActivity.finish()
            }
        })

    }

    private fun facebookLoginBtnClicked() {


        nav_view.getHeaderView(0).logout_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                LoginManager.getInstance().logOut()
                this@TestTaskActivity.finish()
            }
        })

    }
    private fun googleLoginBtnClicked(){
        nav_view.getHeaderView(0).logout_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {


                this@TestTaskActivity.finish()
            }
        })
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.test_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

