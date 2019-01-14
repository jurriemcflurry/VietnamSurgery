package toning.juriaan.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import WebInterfaces.DetailClickListener;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private final List<User> userList;
    private LayoutInflater mInflater;
    private DetailClickListener detailClickListener;
    private Context context;

    public UserAdapter(List<User> users, Context context, DetailClickListener detailClickListener){
        userList = users;
        this.detailClickListener = detailClickListener;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listitem_title);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder vh, int i) {
        User node = getItem(i);
        vh.title.setText(node.username);

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailClickListener.onItemClick(vh.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public User getItem(int position){
        return userList.get(position);
    }
}
