package com.chat.pk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.pk.Adapter.GroupsAdapter;
import com.chat.pk.DAO.NodeJSDAO;
import com.chat.pk.DAO.UtilityDAO;
import com.chat.pk.DTO.GroupDTO;
import com.chat.pk.Util.Constants;
import com.chat.pk.Util.OnItemClickListener;
import com.chat.pk.Util.SpacesItemDecoration;
import com.chat.pk.Util.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class GroupsFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = "GroupsFragment";
    Utilities utilities;
    public static AppSession appSession;
    public static Context context;
    public static TextView tvMessage;
    public static Button btnRetry;
    public static LinearLayout  llLoadMore;
    public static int pageNo = 1, pageSize = 10;
    public static boolean bLoadMore = false;

    public static boolean isGroupActive=false;
    public static Handler mGroupHandler = new Handler();
    public static ArrayList<GroupDTO> list = new ArrayList<>();
    public static RecyclerView rvList;
    public static GroupsAdapter adapter;
    LinearLayoutManager mLinearLayoutManager;
    int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;
    public static  UtilityDAO utilityDAO;

    public static Socket mSocket;

    View view;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isGroupActive=true;
        context=activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.groups_fragment, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        isGroupActive=true;
        utilityDAO=new UtilityDAO(context);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        initView(view);
        initValues();
        getValues();
    }

    void initValues(){
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);
        pageNo = 1;
        bLoadMore = false;
        if (list != null) {
            list.clear();
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    void initView(View view) {
        tvMessage = (TextView) view.findViewById(R.id.tv_message);
        btnRetry = (Button) view.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(this);
        tvMessage.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(mLinearLayoutManager);
        rvList.addItemDecoration(new SpacesItemDecoration(0));
        llLoadMore = (LinearLayout) view.findViewById(R.id.ll_load_more);

        adapter = new GroupsAdapter(getActivity(), list, onItemClickCallback);
        rvList.setAdapter(adapter);
        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                /**Get next page record if we are on last item and we have more record on server*/
                if ((lastVisibleItem == totalItemCount - 1) && bLoadMore) {
                    Log.i(getClass().getName(), "LAST......................................");
                    getValues();
                }
            }
        });

    }

    public static void addTyping(Message message) {
        if (contains(list,message.getGroupId())){
            Log.i(TAG, "processPacket(-) : contains");
            int pos =containsPosition(list,message.getGroupId());
            Log.i(TAG, "processPacket(-) : contains " + pos);
            list.get(pos).setMessage(message.getMessage());
            list.get(pos).setCount(Constants.TYPING_COUNT);
            adapter.notifyItemChanged(pos);
        }
    }

    public static void removeTyping(Message message) {
        if (contains(list,message.getGroupId())){
            Log.i(TAG, "processPacket(-) : contains");
            int pos =containsPosition(list,message.getGroupId());
            Log.i(TAG, "processPacket(-) : contains "+pos);
            GroupDTO group=list.get(pos);
            list.remove(pos);
            addGroupAtPosition(pos,group);
        }
    }
    public static void refreshList(Message message){
        if (list==null)return;
        if (contains(list,message.getToId())){
            Log.i(TAG, "processPacket(-) : contains");
        int pos =containsPosition(list,message.getToId());
            Log.i(TAG, "processPacket(-) : contains "+pos);
            GroupDTO group=list.get(pos);
            list.remove(pos);
            addGroupAtPosition(0,group);
        }else{
            pageNo = 1;
            bLoadMore = false;
            groupList();
        }
    }
    public static boolean contains(ArrayList<GroupDTO> list, String name) {
        for (GroupDTO item : list) {
            if (item.getGroupId().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public static int  containsPosition(ArrayList<GroupDTO> list, String name) {
        for (GroupDTO item : list) {
            if (item.getGroupId().equals(name)) {
                return list.indexOf(item);
            }
        }
        return -1;
    }

    void getValues() {
        if (!utilities.isNetworkAvailable()) {
            if (pageNo == 1) {
                tvMessage.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
                tvMessage.setText(getResources().getString(R.string.network_error));
            }
        } else {
            if (pageNo == 1) {
                tvMessage.setVisibility(View.GONE);
                btnRetry.setVisibility(View.GONE);
                mProgressDialog = ProgressDialog.show(context, null, null);
                mProgressDialog.setContentView(R.layout.progress_loader);
                mProgressDialog.setCancelable(true);
            } else {
                llLoadMore.setVisibility(View.VISIBLE);
            }
            groupList();
        }
    }
    public static void groupList() {

        if (appSession==null)
            return;

        if (mSocket==null) {
            Toast.makeText(context, "Socket is null", Toast.LENGTH_SHORT).show();
        }else if (!mSocket.connected()) {
            Toast.makeText(context, "Socket is not connected", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Constants.PN_USER_ID, appSession.getUserId());
            } catch (JSONException e) { // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (mSocket != null) {
                Log.i(TAG, jsonObject.toString()+"response emit for " + Constants.CHAT_GROUP_LIST);
                mSocket.emit(Constants.CHAT_GROUP_LIST,
                        jsonObject.toString(), new Ack() {
                            @Override
                            public void call(final Object... args) {
                                // TODO Auto-generated method stub
                                mGroupHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (args != null && args.length > 0) {
                                            Log.i(TAG, "response" + args[0]);
                                            dto= new NodeJSDAO(context).parseGroupList(args[0].toString());
                                            setLayout();
                                        } else {
                                            Log.i(TAG, "login response null h");
                                        }
                                    }
                                });

                            }
                        });
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_retry:
                getValues();
                break;
        }
    }
    public static ProgressDialog mProgressDialog;

    public static GroupDTO dto;

    public static void setLayout(){
        try {
            if (pageNo == 1 && mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            else
                llLoadMore.setVisibility(View.GONE);
            bLoadMore = true;
            if (pageNo == 1) {
                if (list != null) {
                    list.clear();
                }
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
            if (dto == null) {
                if (pageNo == 1) {
                    tvMessage.setVisibility(View.VISIBLE);
                    btnRetry.setVisibility(View.VISIBLE);
                    tvMessage.setText(context.getResources().getString(R.string.server_error));
                }
            } else if (dto.getSuccess().equals(Constants.SUCCESS_0)) {
                if (pageNo == 1) {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText("" + dto.getMessage());
                } else bLoadMore = false;
            } else if (dto.getSuccess().equals(Constants.SUCCESS_1)) {
                setValues(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setValues(GroupDTO result) {
        if (result.getDtos() != null) {
            if (list == null)
                list = new ArrayList<>();
            Log.i(TAG, "SIZE : " + result.getDtos() .size());
            //  Log.i(getClass().getName(), "SIZE : " + result.getRequestDTOs().);
            for (int i=0;i<result.getDtos().size();i++){
                addGroup(result.getDtos().get(i));
            }

            if (result.getDtos() .size() >= pageSize) {
                bLoadMore = true;
                pageNo++;
            } else {
                bLoadMore = false;
            }
            adapter.notifyDataSetChanged();
        }
    }
    public static void addGroup(GroupDTO group){
        Cursor cursor =utilityDAO.getLastGroupMessages(group.getUserId(), group.getGroupId());
        if (cursor!=null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            group.setMessage(cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE)));
            group.setLastImage(cursor.getString(cursor.getColumnIndex(Constants.CN_FILE_NAME)));
            if (!TextUtils.isEmpty(group.getLastImage())){
                group.setMessage(group.getMessage()+" Image");
            }
            group.setTime(Utilities.getInstance(context).getTimeLog( cursor.getString(cursor.getColumnIndex(Constants.CN_TIME))));
        }
        group.setCount(utilityDAO.getCountGroup(group.getUserId(), group.getGroupId()));
        list.add(group);
    }
    public static void addGroupAtPosition(int position,GroupDTO group){
        Cursor cursor =utilityDAO.getLastGroupMessages(group.getUserId(), group.getGroupId());
        if (cursor!=null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            group.setMessage(cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE)));
            group.setLastImage(cursor.getString(cursor.getColumnIndex(Constants.CN_FILE_NAME)));
            if (!TextUtils.isEmpty(group.getLastImage())){
                group.setMessage(group.getMessage()+" Image");
            }
            group.setTime(Utilities.getInstance(context).getTimeLog(cursor.getString(cursor.getColumnIndex(Constants.CN_TIME))));
        }
        group.setCount(utilityDAO.getCountGroup(group.getUserId(), group.getGroupId()));
        list.add(position, group);
        adapter.notifyDataSetChanged();
    }


    private OnItemClickListener.OnItemClickCallback onItemClickCallback = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {

                GroupChatFragment groupChatFragment = new GroupChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PN_GROUP_ID, list.get(position).getGroupId());
                bundle.putString(Constants.PN_GROUP_NAME, list.get(position).getGroupName());
                groupChatFragment.setArguments(bundle);
                changeFragment(groupChatFragment, "GroupChatFragment");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        isGroupActive=false; // mSocket.disconnect();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isGroupActive=false;
    }
}
