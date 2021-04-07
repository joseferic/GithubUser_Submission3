package com.example.githubuser.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubuser.CustomOnItemClickListener
import com.example.githubuser.Detail
import com.example.githubuser.R
import com.example.githubuser.Users
import com.example.githubuser.databinding.ItemUserBinding

class FavoriteAdapter(private val activity: Activity) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {



    var listFavorite = ArrayList<Users>()
        set(listNotes) {
            if (listNotes.size > 0) {
                this.listFavorite.clear()
            }
            this.listFavorite.addAll(listNotes)
            notifyDataSetChanged()
        }

    fun addItem(users: Users){
        this.listFavorite.add(users)
        notifyItemInserted(this.listFavorite.size-1)
    }

    fun updateItem(position: Int, users: Users){
        this.listFavorite[position] = users
        notifyItemChanged(position,users)
    }

    fun removeItem(position: Int){
        this.listFavorite.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,this.listFavorite.size)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.FavoriteViewHolder, position: Int) {
        holder.bind(listFavorite[position])
    }

    override fun getItemCount(): Int = this.listFavorite.size

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUserBinding.bind(itemView)
        fun bind(users: Users) {
            with(binding) {
                Glide.with(itemView.context)
                        .load(users.photo)
                        .apply(RequestOptions().override(55, 55))
                        .into(imgPhoto)
                txtName.text = users.UserName
                itemView.setOnClickListener (CustomOnItemClickListener(adapterPosition, object : CustomOnItemClickListener.OnItemClickCallback{
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, Detail::class.java)
                        intent.putExtra(Detail.EXTRA_POSITION, position)
                        intent.putExtra(Detail.EXTRA_USER, users)
                        activity.startActivityForResult(intent, Detail.REQUEST_UPDATE)
                    }
                }))
            }
        }
    }
}