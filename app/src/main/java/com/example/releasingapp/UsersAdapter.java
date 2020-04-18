package com.example.releasingapp;


import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private static final String TAG = "UsersAdapter";
    List<User> users = new ArrayList<>();
    private UserClicked userClickedListener;

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public interface UserClicked {
        void onUserSwitch(User user);
    }

    public void setUserClickedListener(UserClicked userClickedListener) {
        this.userClickedListener = userClickedListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        //Button switchButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.accountName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(userClickedListener != null && position != RecyclerView.NO_POSITION){
                        userClickedListener.onUserSwitch(users.get(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_users, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        User user = users.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(user.getName());
        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userClickedListener != null) {
                    //getting ticketNumber
                    userClickedListener.onUserSwitch(users.get(position).getIdNo());

                }
            }
        });*/

    }
    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return users.size();
    }
}
