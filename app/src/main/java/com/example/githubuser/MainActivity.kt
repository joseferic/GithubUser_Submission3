package com.example.githubuser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject




class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<Users>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menghilangkan bayangan dibawah Action Bar Tab
        supportActionBar?.elevation = 0F
        supportActionBar?.title = "Github User's Search"

        binding.lvList.setHasFixedSize(true)

        //reset list agar gk ada duplikasi data
        list.clear()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        //search
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchbar = findViewById<SearchView>(R.id.search_bar)
        searchbar.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchbar.queryHint = "Search username"
        searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { getListUser(it) }
                searchbar.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                list.clear()
                return false
            }
        })

    }

    //menu diaction bar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_form,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_favorite -> {
                val intent = Intent(this@MainActivity, FavoriteList::class.java)
                startActivity(intent)
            }
            R.id.action_setting -> {
                val intent = Intent(this@MainActivity, Setting::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getListUser(username: String){
        //agar tidak ada duplikasi data
        list.clear()
        binding.progressBar.visibility = View.VISIBLE
        val asyncClient = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=$username"
        asyncClient.addHeader("Authorization","token ed25a00aefd9fe5fc0290af721e3177d26468450")
        asyncClient.addHeader("User-Agent", "request")
        asyncClient.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE
                // Parsing JSON
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val items = responseObject.getJSONArray("items")
                    val listUser = ArrayList<Users>()
                    for (i in 0 until items.length()){
                        val item = items.getJSONObject(i)
                        val user =  Users()
                        val idUser = item.getString("id").toInt()
                        val username = item.getString("login")
                        val avatar = item.getString("avatar_url")
                        user.id= idUser
                        user.UserName = username
                        user.photo = avatar
                        if (!listUser.contains(user)){
                            listUser.add(user)
                        }
                        list.clear()
                        list.addAll(listUser)
                        showRecyclerList()

                    }
                }catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                binding.progressBar.visibility = View.VISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
            }
        })
    }


    private fun showRecyclerList() {
        binding.lvList.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = UserAdapter(list)
        binding.lvList.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Users){
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(users: Users) {
        val intent = Intent(this@MainActivity, Detail::class.java)
        intent.putExtra(Detail.EXTRA_USER, users)
        startActivity(intent)
    }

}