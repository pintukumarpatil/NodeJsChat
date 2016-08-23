package com.chat.pk;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class BaseFragment extends Fragment {
    public void finishFragment(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            Log.i("Hello",
                    "stack count: "
                            + fragmentManager.getBackStackEntryCount());
        }
    }
    public void  refresh(){
        Fragment fragment1 = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        String tag = (String) fragment1.getTag();

        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag(tag);
        final FragmentTransaction ft = getFragmentManager()
                .beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }
    public void changeFragment(Fragment fragment, String fragmentName) {
        Fragment fragment1 = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        String tag = (String) fragment1.getTag();

        FragmentTransaction fragmentTransaction = getActivity() .getSupportFragmentManager().beginTransaction();
        // remove fragment from fragment manager
        fragmentTransaction.remove(getActivity().getSupportFragmentManager().findFragmentByTag(tag));
        // add fragment in fragment manager
        fragmentTransaction.add(R.id.container, fragment, fragmentName);
        //fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }
    public void addFragment(Fragment fragment, String fragmentName) {
        Fragment fragment1 = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        String tag = (String) fragment1.getTag();

        FragmentTransaction fragmentTransaction = getActivity() .getSupportFragmentManager().beginTransaction();
         // add fragment in fragment manager
        fragmentTransaction.add(R.id.container, fragment, fragmentName);
        //fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentName) {
        Fragment fragment1 = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        String tag = (String) fragment1.getTag();

        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        // replace fragment in fragment manager
        fragmentTransaction.replace(R.id.container, fragment, fragmentName);
        //fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }
    public void replaceFragmentWithoutBack(Fragment fragment, String fragmentName) {
        Fragment fragment1 = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        String tag = (String) fragment1.getTag();

        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        // replace fragment in fragment manager
        fragmentTransaction.replace(R.id.container, fragment, fragmentName);
        fragmentTransaction.commit();
    }
    public void clearBackStack(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
    public void requestFocusAndOpenKeyboard(EditText editText){
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
    public void closeKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
    public static void closeKeyboard(final Context context, final View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
