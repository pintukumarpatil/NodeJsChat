package com.chat.pk;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;


/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chat();
            }
        },200);

    }
    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        GroupChatFragment.isChatMinimize = true;
        SingleChatFragment.isPrivateChatMinimize=true;
    }
    public void chat() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.you_want_to_chat_with_group_or_friends));
        builder.setPositiveButton(R.string.groups, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
// Getting reference to the FragmentManager
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Creating a fragment transaction
                FragmentTransaction ft = fragmentManager.beginTransaction();
                // Adding a fragment to the fragment transaction
                ft.replace(R.id.container, new GroupsFragment(), "GroupsFragment");
                // Committing the transaction
                ft.commit();

            }
        });
        builder.setNegativeButton(R.string.friends, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
// Getting reference to the FragmentManager
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Creating a fragment transaction
                FragmentTransaction ft = fragmentManager.beginTransaction();
                // Adding a fragment to the fragment transaction
                ft.replace(R.id.container, new FriendsFragment(), "FriendsFragment");
                // Committing the transaction
                ft.commit();

            }
        });

        builder.show();
    }
}
