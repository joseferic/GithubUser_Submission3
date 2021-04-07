package com.example.githubuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.adapter.FavoriteAdapter
import com.example.githubuser.databinding.ActivityFavoriteListBinding
import com.example.githubuser.db.UserHelper
import com.example.githubuser.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteList : AppCompatActivity() {

    private lateinit var adapter: FavoriteAdapter

    private lateinit var  binding: ActivityFavoriteListBinding

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menghilangkan bayangan dibawah Action Bar Tab
        supportActionBar?.elevation = 0F
        supportActionBar?.title = "Favorite User"

        binding.lvList.layoutManager = LinearLayoutManager(this)
        binding.lvList.setHasFixedSize(true)
        adapter = FavoriteAdapter(this)
        binding.lvList.adapter = adapter


        if (savedInstanceState == null){
            loadFavoriteAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Users>(EXTRA_STATE)
            if (list != null){
                adapter.listFavorite = list
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listFavorite)
    }

    //menu diaction bar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.submenu_form,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_setting -> {
                val intent = Intent(this@FavoriteList, Setting::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun loadFavoriteAsync() {
        GlobalScope.launch(Dispatchers.Main) {

            val userHelper = UserHelper.getInstance(applicationContext)
            userHelper.open()
            val deferredUser = async(Dispatchers.IO) {
                val cursor = userHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
//            userHelper.close()

            val users = deferredUser.await()
            if (users.size > 0){
                adapter.listFavorite = users
            } else{
                adapter.listFavorite = ArrayList()
                Toast.makeText(this@FavoriteList, "Tidak ada Satu user difavorite list", Toast.LENGTH_SHORT).show()
            }
            userHelper.close()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data!= null){
            when(requestCode){
                Detail.REQUEST_ADD -> if (resultCode == Detail.RESULT_ADD){
                    val users = data.getParcelableExtra<Users>(Detail.EXTRA_DATA) as Users

                    adapter.addItem(users)
                    binding.lvList.smoothScrollToPosition(adapter.itemCount-1)
                    Toast.makeText(this@FavoriteList, "Satu user masuk favorite list", Toast.LENGTH_SHORT).show()
                }
                Detail.REQUEST_UPDATE ->
                    when(resultCode){
                        Detail.RESULT_UPDATE -> {

                        }
                        Detail.RESULT_DELETE -> {
                            val position = data.getIntExtra(Detail.EXTRA_POSITION, 0)
                            adapter.removeItem(position)
                            Toast.makeText(this@FavoriteList, "Satu user dihapus favorite list", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }


}