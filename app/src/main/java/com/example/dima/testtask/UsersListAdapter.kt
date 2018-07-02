package com.example.dima.testtask

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_card_view.view.*

public class UsersListAdapter(private val usersList: UsersList?, private val count: Int) :
        RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get()
                .load(usersList!!.items[holder.adapterPosition].avatarUrl)
                .resize(60, 60)
                .centerCrop()
                .into(holder.imageView)
        holder.userLoginView.text = usersList!!.items[holder.adapterPosition].login
        holder.scoreView.text = usersList!!.items[holder.adapterPosition].score.toString()

    }

    override fun getItemCount(): Int {
        return count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_card_view, parent, false) as CardView
        return ViewHolder(cardView)
    }


    class ViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
        val constrLayout = itemView.card_constraint_layout
        val imageView = constrLayout.user_profile_image
        val userLoginView = constrLayout.user_login_view
        val scoreView = constrLayout.score_view



    }

}