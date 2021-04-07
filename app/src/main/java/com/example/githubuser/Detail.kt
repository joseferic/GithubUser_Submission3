package com.example.githubuser

import android.R.attr.name
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubuser.databinding.ActivityDetailBinding
import com.example.githubuser.db.DatabaseContract
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.example.githubuser.db.DatabaseHelper
import com.example.githubuser.db.UserHelper
import com.example.githubuser.helper.MappingHelper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject


class Detail : AppCompatActivity(), View.OnClickListener {

    private var isFavorite: Boolean = false
    private var stateUserFavorite: Boolean? = null
    private var userFavorite: Users? = null
    private var position: Int = 0
    private lateinit var userHelper: UserHelper

    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val EXTRA_USER = "user"
        const val EXTRA_DATA = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
        private val TAG = Detail::class.java.simpleName
    }

    var dummyAvatar :String? = ""
    var dummyID : Int? = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ambil username dari list
        val user = intent.getParcelableExtra<Users>(EXTRA_USER) as Users

        //buka database untuk list favorit
        userHelper = UserHelper.getInstance(applicationContext)
        userHelper.open()

        //memuat detail user
        loadDetail(user.UserName)

        //check state favorite
        isFavorite = stateFavorite(user.id)
        stateUserFavorite = !isFavorite
        // Menghilangkan bayangan dibawah Action Bar Tab
        supportActionBar?.elevation = 0F
        supportActionBar?.title = "Detail User"

        //page adapter
        //Fragment
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.username = user.UserName
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 1
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()


        if (userFavorite != null){
            position = intent.getIntExtra(EXTRA_POSITION, 0)
         /*   isFavorite = true*/
        } else{
            userFavorite = Users()
        }


        //Tombol Favorite
        binding.fabAddFavorite.setOnClickListener(this)
    }



    private fun stateFavorite(userID: Int?) : Boolean{
        Log.d(TAG, "Masuk sini")
        val cursor: Cursor = userHelper.queryById(userID.toString())
        if (cursor.moveToNext()){
            isFavorite = true
            setStatusFavorite(true)
            Log.d(TAG, "Ini user Favorite")
        }
        return isFavorite
    }

    private fun setStatusFavorite(b: Boolean) {
        if (b == true){
            Glide.with(this@Detail).load(R.drawable.ic_baseline_favorite_24)
                    .apply(RequestOptions())
                    .into(binding.fabAddFavorite)
        }
        else{
            Glide.with(this@Detail).load(R.drawable.ic_baseline_favorite_border_24)
                    .apply(RequestOptions())
                    .into(binding.fabAddFavorite)
        }
    }

    //menu diaction bar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.submenu_form, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_setting -> {
                val intent = Intent(this@Detail, Setting::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onClick(v: View) {
        if (v.id == R.id.fab_addFavorite){
            stateUserFavorite = !stateUserFavorite!!
            if (stateUserFavorite== true){
                Glide.with(this@Detail).load(R.drawable.ic_baseline_favorite_24)
                        .apply(RequestOptions())
                        .into(binding.fabAddFavorite)
                addToFavorite()
            }else if (stateUserFavorite == false){
                Glide.with(this@Detail).load(R.drawable.ic_baseline_favorite_border_24)
                        .apply(RequestOptions())
                        .into(binding.fabAddFavorite)
                deleteFromFavorite()
            }else{
                Toast.makeText(
                        this@Detail,
                        "stateUserFavorite NULL",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun deleteFromFavorite() {
        val result = userHelper.deleteById(dummyID.toString()).toLong()
        if (result > 0) {
            val intent = Intent()
            intent.putExtra(EXTRA_POSITION, position)
            setResult(RESULT_DELETE, intent)
            Toast.makeText(this@Detail, "Hapus Favorite User", Toast.LENGTH_SHORT).show()
        }else{
           Toast.makeText(
                this@Detail,
                "Gagal menghapus user dari list favorite",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addToFavorite() {
        val username = binding.username.text.toString().trim()
        val avatar_url = dummyAvatar
        val name = binding.nama.text.toString().trim()
        val follower = binding.follower.text.toString().trim()
        val following = binding.following.text.toString().trim()
        val company = binding.company.text.toString().trim()
        val location = binding.location.text.toString().trim()
        val repository = binding.repository.text.toString().trim()

        userFavorite?.id = dummyID
        userFavorite?.UserName = binding.username.text.toString().trim()
        userFavorite?.photo = binding.Avatar.toString().trim()
        userFavorite?.Nama = binding.nama.text.toString().trim()
        userFavorite?.Follower = binding.follower.text.toString().trim()
        userFavorite?.Following = binding.following.text.toString().trim()
        userFavorite?.Company = binding.company.text.toString().trim()
        userFavorite?.Location = binding.location.text.toString().trim()
        userFavorite?.Repository = binding.repository.text.toString().trim()

        val intent = Intent()
        intent.putExtra(EXTRA_DATA, userFavorite)
        intent.putExtra(EXTRA_POSITION, position)

        val values =  ContentValues()
        values.put(DatabaseContract.UserColumns._ID, dummyID)
        values.put(DatabaseContract.UserColumns.COLUMN_NAME_USERNAME, username)
        values.put(DatabaseContract.UserColumns.COLUMN_NAME_AVATAR_URL, avatar_url)
        values.put(DatabaseContract.UserColumns.COLUMN_NAME_NAME, name)
        values.put(DatabaseContract.UserColumns.COLUMN_NAME_FOLLOWWER, follower)
        values.put(DatabaseContract.UserColumns.COLUMN_NAME_FOLLOWWING, following)
        values.put(DatabaseContract.UserColumns.COLUMN_NAME_COMPANY, company)
        values.put(DatabaseContract.UserColumns.COLUMN_NAME_LOCATION, location)
        values.put(DatabaseContract.UserColumns.COLUMN_NAME_REPOSITORY, repository)

        val result = userHelper.insert(values)
        if (result > 0) {
            val result = userHelper.update(userFavorite?.id.toString(), values).toLong()
            userFavorite?.id = result.toInt()
            setResult(RESULT_ADD, intent)
            Toast.makeText(this@Detail, "Masuk Favorite User", Toast.LENGTH_SHORT).show()
            /*finish()*/
        } else {
            Toast.makeText(this@Detail, "Gagal menambah data", Toast.LENGTH_SHORT).show()
            deleteFromFavorite()
        }
    }


    private fun loadDetail(userName: String?)  {

        val userDetail =  Users()

        binding.progressBar.visibility = View.VISIBLE
        val asyncClient = AsyncHttpClient()
        val url = "https://api.github.com/users/${userName}"
        asyncClient.addHeader("Authorization", "token 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")
        asyncClient.addHeader("User-Agent", "request")
        asyncClient.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE

                val result = responseBody?.let { String(it) }
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val userID = responseObject.getInt("id")
                    dummyID = userID
                    val avatar = responseObject.getString("avatar_url")
                    dummyAvatar = avatar
                    val username = responseObject.getString("login")
                    val name = responseObject.getString("name")
                    val follower = responseObject.getString("followers").toString()
                    val following = responseObject.getString("following").toString()
                    val company = responseObject.getString("company")
                    val location = responseObject.getString("location")
                    val repository = responseObject.getString("repos_url").toString()

                    userDetail.id = userID
                    userDetail.photo = avatar
                    userDetail.UserName = username
                    userDetail.Nama = name
                    userDetail.Follower = follower
                    userDetail.Following = following
                    userDetail.Company = company
                    userDetail.Location = location
                    userDetail.Repository = repository

                    Glide.with(this@Detail).load(userDetail.photo).apply(RequestOptions()).into(
                        binding.Avatar
                    )
                    binding.username.text = userDetail.UserName
                    binding.nama.text = userDetail.Nama
                    binding.follower.text = userDetail.Follower
                    binding.following.text = userDetail.Following
                    binding.company.text = userDetail.Company
                    binding.location.text = userDetail.Location
                    binding.repository.text = userDetail.Repository

                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
            }
        })

    }





}