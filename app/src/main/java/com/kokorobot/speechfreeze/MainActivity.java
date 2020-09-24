package com.kokorobot.speechfreeze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.kokorobot.speechfreeze.KebbiRobot.KebbiEvent;

public class MainActivity extends AppCompatActivity {

    KebbiRobot mRobot;
    private static final int fragmentHolderId = R.id.main_holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        mRobot = new KebbiRobot(this, "RoboLibrary");
        // use delay to wait for robot to be ready
        new Handler().postDelayed(() -> {
            switchFragment(new Fragment_1());
        }, 600);
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            mRobot.release();
        } catch (NullPointerException err) {
            err.printStackTrace();
        }
        super.onPause();
    }

    public KebbiRobot getRobot() {
        return mRobot;
    }

    public void nextFragment() {
        switchFragment(new Fragment_2());
    }

    public void backFragment() {
        switchFragment(new Fragment_1());
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment);
        fragmentTransaction.commit();
    }
}