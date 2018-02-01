package com.szbc.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

public class MyFragment extends Fragment {
    public static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    private static Handler handler = new Handler();
    private int count = 91;

    private Runnable myRunnable = new Runnable() {
        public void run() {

            handler.postDelayed(this, 1000);
            count--;
            if (count == 0) {
                count = 91;
                stopSendButton();
            }
            reflushSendButton(count);

        }
    };

    public void stopSendButton() {
        handler.removeCallbacks(myRunnable);
        count = 91;
    }

    public void reflushSendButton(int count) {

    }
}
