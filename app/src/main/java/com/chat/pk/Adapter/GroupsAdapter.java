package com.chat.pk.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.pk.AppSession;
import com.chat.pk.DAO.UtilityDAO;
import com.chat.pk.DTO.GroupDTO;
import com.chat.pk.R;
import com.chat.pk.Util.Constants;
import com.chat.pk.Util.OnItemClickListener;
import com.chat.pk.View.ImageViewCircular;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.CustomViewHolder> {
    private ArrayList<GroupDTO> list;
    static Context context;
    AppSession appSession;
    UtilityDAO utilityDAO;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback;

    public GroupsAdapter(Context context, ArrayList<GroupDTO> list, OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        super();
        this.list = list;
        this.context = context;
        appSession = new AppSession(context);
        this.onItemClickCallback = onItemClickCallback;
        utilityDAO=new UtilityDAO(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_groups, viewGroup, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        GroupDTO dto = list.get(position);
        customViewHolder.setGroupName(dto.getGroupName());
        customViewHolder.setMessage(dto.getCount(),dto.getMessage());
        customViewHolder.setTime(dto.getTime());
        customViewHolder.setCount(dto.getCount());

       // customViewHolder.setGroupImage(dto.getImage());
        //Handle click event
        customViewHolder.llItem.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));

    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageViewCircular ivImage;
        protected TextView tvTitle, tv_time,tv_messages,tv_wadges;
        protected LinearLayout llItem;

        public CustomViewHolder(View view) {
            super(view);
            this.llItem = (LinearLayout) view.findViewById(R.id.ll_item);
            this.ivImage = (ImageViewCircular) view.findViewById(R.id.iv_image);
            this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            this.tv_time = (TextView) view.findViewById(R.id.tv_time);
            this.tv_messages = (TextView) view.findViewById(R.id.tv_messages);
            this.tv_wadges = (TextView) view.findViewById(R.id.tv_wadges);

        }

        public void setGroupName(String title) {
            if (null == tvTitle) return;
            tvTitle.setText(title);
        }
        public void setMessage(int count ,String msg) {
            if (null == tv_messages) return;
            tv_messages.setText(msg);
            if (count== Constants.TYPING_COUNT){
                tv_messages.setTextColor(context.getResources().getColor(R.color.username4));
            }if (count==0){
                tv_messages.setTextColor(context.getResources().getColor(R.color.dim_foreground_light_disabled));
            }else {
                tv_messages.setTextColor(context.getResources().getColor(R.color.dim_foreground_light_disabled));
            }
        }
        public void setTime(String time) {
            if (null == tv_time) return;
            tv_time.setText(time.toUpperCase());
        }
        public void setCount(int count) {
            if (null == tv_wadges) return;
            tv_wadges.setText(count+"");
            if (count<1)
                tv_wadges.setVisibility(View.GONE);
            else
                tv_wadges.setVisibility(View.VISIBLE);
        }
        public void setGroupImage(String image) {
            if (null == ivImage) return;
              Picasso.with(context).load(image)
                .error(R.drawable.avatar_group_tmp)
                .placeholder(R.drawable.avatar_group_tmp).resize(70, 70).centerCrop().into(ivImage);
        }
    }
}