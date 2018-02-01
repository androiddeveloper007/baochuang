package com.szbc.tool;

import android.os.Handler;
import android.os.Message;

/**
 * Created by ZP on 2017/6/12.
 */

public abstract class AsyncTaskUtil<Result, Params> {

    private final static int SUCCESS = 0;
    private final static int FAIL = -1;

    private InternalHandler sHandler;

    public AsyncTaskUtil() {
        sHandler = new InternalHandler(this);
    }

    public abstract Result doInBackground(Params mParams) throws Exception;

    public void onStart() {
    };

    public abstract void onFail(Exception e);

    public abstract void onSuccess(Result mParam) throws Exception;

    public void onFinish() {
    };

    public void execute() {
        execute(null);
    }

    public void execute(Params mParams) {
        onStart();
        final Params params = mParams;
        ThreadPoolUtils.execute(new Runnable() {

            @Override
            public void run() {
                Message msg = sHandler.obtainMessage();
                try {
                    msg.what = SUCCESS;
                    msg.obj = doInBackground(params);
                } catch (Exception e) {
                    msg.what = FAIL;
                    if (e instanceof Exception) {
                        msg.obj = (Exception) e;
                    }
//                    else
//                        msg.obj = Exception.convertException(e);
                }
                sHandler.sendMessage(msg);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private static class InternalHandler extends Handler {

        private AsyncTaskUtil mAsync;

        public InternalHandler(AsyncTaskUtil mAsync) {
            this.mAsync = mAsync;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            mAsync.onFinish();
            switch (msg.what) {
                case SUCCESS:
                    try {
                        mAsync.onSuccess(msg.obj);
                    } catch (Exception e) {
//                        mAsync.onFail(Exception.convertException(e));
                    }
                    break;
                case FAIL:
                    if (msg.obj != null && msg.obj instanceof Exception) {
                        mAsync.onFail((Exception) msg.obj);
                    }
                    break;
            }
        }
    }
}
