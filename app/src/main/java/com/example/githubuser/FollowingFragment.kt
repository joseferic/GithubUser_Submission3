package com.example.githubuser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.databinding.FragmentFollowingBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray


class FollowingFragment : Fragment() {

    private lateinit var adapter: UserAdapter
    private var list = ArrayList<Users>()
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding as FragmentFollowingBinding

    companion object {
        private const val ARG_USERNAME = "username"
        private val TAG = FollowingFragment::class.java.simpleName

        fun newInstance(username: String): FollowingFragment {
            val fragment = FollowingFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        list = ArrayList<Users>()
        adapter = UserAdapter(list)
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        binding.lvList.layoutManager = LinearLayoutManager(requireContext())
        binding.lvList.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lvList.setHasFixedSize(true)
        list.clear()
        adapter.notifyDataSetChanged()
        val username = arguments?.getString(ARG_USERNAME).toString()
        binding.lvList.layoutManager = LinearLayoutManager(activity)
        binding.lvList.adapter = adapter

        getListFollowing(username)
    }

    private fun getListFollowing(username: String){
        //agar tidak ada duplikasi data
        list.clear()

        binding.progressBar.visibility = View.VISIBLE
        val asyncClient = AsyncHttpClient()
        val url = "https://api.github.com/users/$username/following"
        asyncClient.addHeader("Authorization","token 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")
        asyncClient.addHeader("User-Agent", "request")
        asyncClient.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE
                // Parsing JSON
                val result = String(responseBody)
                Log.d(TAG, result)
                try {

                    val responseArray = JSONArray(result)
                    for (i in 0 until responseArray.length()){
                        val data = responseArray.getJSONObject(i)
                        val user =  Users()
                        val listUser = ArrayList<Users>()
                        val username = data.getString("login")
                        val avatar = data.getString("avatar_url")
                        user.UserName = username
                        user.photo = avatar
                        listUser.add(user)
                        list.addAll(listUser)
                        adapter.notifyDataSetChanged()
                        showRecyclerList()
                        adapter.notifyDataSetChanged()

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
        binding.lvList.layoutManager = LinearLayoutManager(activity)
        val listUserAdapter = UserAdapter(list)
        binding.lvList.adapter = listUserAdapter
        listUserAdapter.notifyDataSetChanged()
        listUserAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Users){
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(users: Users) {
        val intent = Intent(activity, Detail::class.java)
        intent.putExtra(Detail.EXTRA_USER, users)
        startActivity(intent)
    }
}