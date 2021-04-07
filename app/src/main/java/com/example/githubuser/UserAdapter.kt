package com.example.githubuser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubuser.databinding.ItemUserBinding



class UserAdapter(private val listUsers: ArrayList<Users>) : RecyclerView.Adapter<UserAdapter.ListViewHolder>(){

    private var onItemClickCallback: OnItemClickCallback? = null



    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.ListViewHolder, position: Int) {
        holder.bind(listUsers[position])
    }

    override fun getItemCount(): Int = listUsers.size

    inner class ListViewHolder(private val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(users: Users){
            with(binding){
                Glide.with(itemView.context)
                        .load(users.photo)
                        .apply(RequestOptions().override(55, 55))
                        .into(imgPhoto)
                txtName.text = users.UserName
                itemView.setOnClickListener { onItemClickCallback?.onItemClicked(users) }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Users)
    }
}