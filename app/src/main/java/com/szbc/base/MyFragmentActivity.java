package com.szbc.base;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;

public class MyFragmentActivity extends FragmentActivity {

	private Handler handler = new Handler();
	private static int count = 91;
	  
    private Runnable myRunnable= new Runnable() {    
        public void run() {  
            
        	handler.postDelayed(this, 1000);
        	count--;
        	if(count == 0)
        	{
        		count = 91;
        		stopSendButton();
        	}
        	reflushSendButton(count);
        	
        }  
    }; 
    
    public void stopSendButton()
    {
    	handler.removeCallbacks(myRunnable);
    	count = 91;
    }
    public void reflushSendButton(int count)
    {
    	
    }
}
