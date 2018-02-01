package com.szbc.front.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.widget.dropdonwdemo.utils.DensityUtil;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.LuckyDrawDialog_1;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.fragment.Mine;
import com.szbc.front.LoginActivity;
import com.szbc.front.addcar.CarBrandActivity;
import com.szbc.front.addcar.DriveRangeAndTimeActivity;
import com.szbc.model.UserCar;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.xlistview.XListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的座驾
 */
public class MyCarsActivity extends BaseActivity implements XListView.IXListViewListener {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.lv_myCar)
    XListView lvMyCar;
    List<UserCar> list;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    public static final int REQUEST_CODE_EDIT = 1;
    public static final int RESULT_CODE_EDIT = 2;
    public static final int REQUEST_CODE_REEDIT = 3;
    public static final int RESULT_CODE_REEDIT = 4;
    private EncryptedSharedPreferences sp;
    private String token;
    private boolean hasChangeDefaultCar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycar_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT < 20)
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        sp = new EncryptedSharedPreferences(this);
        token = sp.getString("token");
        if(TextUtils.isEmpty(token)){
            showToast("请登录",2000);
            startActivity(new Intent(context,LoginActivity.class));
            finish();
            return;
        }
        initData();
    }

    private void initData() {
        lvMyCar.setPullRefreshEnable(true);
        lvMyCar.setPullLoadEnable(false);
        lvMyCar.setXListViewListener(this);
        /*请求网络获取我的座驾数据*/
        loadData();
    }

    private void loadData() {
        String path = Config.httpIp + Config.Urls.queryUserCarlist;
        params = new RequestParams(path);
        params.addParameter("token", sp.getString("token"));
        params.setConnectTimeout(10 * 1000);
        params.setCacheMaxAge(1000 * 60 * 60 * 24 * 7);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String message = object.getString("message");
                    if (result.equals("1")) {
                        if (object.getString("data") != null) {
                            String str = object.getString("data");
                            try {
                                list = new Gson().fromJson(str, new TypeToken<List<UserCar>>() {}.getType());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(list==null || list.size()==0){
                                MyCarAdapter adapter = new MyCarAdapter(context,list);
                                lvMyCar.setAdapter(adapter);
                                int height = DensityUtil.getWindowHeight((Activity) context) - getResources().getDimensionPixelOffset(R.dimen.dimen_70);
                                adapter.getNoDataEntity(height);
                                return;
                            }
                            MyCarAdapter adapter = new MyCarAdapter(context,list);
                            lvMyCar.setAdapter(adapter);

                            adapter.setOnCheckbox(new MyCarAdapter.checkboxListener() {
                                @Override
                                public void run(boolean b,int position) {
                                    if(b)
                                        changeDefaultCar(position);
                                }
                            });
                            adapter.setOnEditListener(new MyCarAdapter.editListener() {
                                @Override
                                public void edit(int position) {
                                    editCar(position);
                                }
                            });
                            adapter.setOnDeleteListener(new MyCarAdapter.deleteListener() {
                                @Override
                                public void delete(final int position) {
                                    LuckyDrawDialog_1 d = new LuckyDrawDialog_1(context, true, 0.8);
                                    d.setMessage("确认删除该座驾?", true);
                                    d.setBtn1Text("取消");
                                    d.setBtn2Text("确定");
                                    d.setTitleVisible(false, "");
                                    d.setTextIvestVisible(false);
                                    d.show();
                                    d.setOnBtn2ClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteCar(position);
                                        }
                                    });
                                }
                            });
                        }
                    } else if(TextUtils.equals(result,"-4")){
                        showToast(message, 2000);
                        startActivity(new Intent(context,LoginActivity.class));
                    } else {
                        showToast(object.getString("message"), 1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showToast("服务器连接失败", 2000);
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                lvMyCar.stopRefresh();
                if (mDialog != null) {
                    mDialog.hideDialog();
                    d = null;
                    params=null;
                }
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void changeDefaultCar(int position) {
        String path = Config.httpIp + Config.Urls.updateIsDefault;
        params0 = new RequestParams(path);
        params0.addParameter("token", token);
        params0.addParameter("id", list.get(position).getId());
        params0.setConnectTimeout(10 * 1000);
        x.http().post(params0, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String msg = object.getString("message");
                    if (result.equals("1")) {
//                        showToast(msg, 1000);
                        hasChangeDefaultCar=true;
                        if(mDialog0!=null && mDialog0.isShowing()){
                            mDialog0.hideDialog();
                            mDialog0=null;
                            params0=null;
                        }
//                                loadData();
                        lvMyCar.autoRefresh();
                    } else {
                        showToast(msg, 1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showToast("服务器连接失败", 1000);
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void deleteCar(int position) {
        String path = Config.httpIp + Config.Urls.deleteUserCarlis;
        params0 = new RequestParams(path);
        params0.addParameter("token", sp.getString("token"));
        params0.addParameter("id", list.get(position).getId());
        params0.setConnectTimeout(10 * 1000);
        mDialog0 = new MyDialog(this, "加载中...");
        mDialog0.setDuration(300);
        mDialog0.setMyCallback(new MyCallback() {
            @Override
            public void callback() {
            }
            @Override
            public void doing() {
                x.http().post(params0, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            String msg = object.getString("message");
                            if (result.equals("1")) {
                                showToast(msg, 1200);
                                hasChangeDefaultCar=true;
                                if(mDialog0!=null && mDialog0.isShowing()){
                                    mDialog0.hideDialog();
                                    mDialog0=null;
                                    params0=null;
                                }
                                loadData();
                            } else {
                                showToast(msg, 2000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        showToast("服务器连接失败", 1000);
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                        if(mDialog0!=null && mDialog0.isShowing()){
                            mDialog0.hideDialog();
                            mDialog0=null;
                            params0=null;
                        }
                    }
                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });
            }
        });
        mDialog0.showDialog();
    }

    private void editCar(int position) {
        Intent i = new Intent(this, DriveRangeAndTimeActivity.class);
        i.putExtra("isFromEditPage","true");
        if(list!=null && list.size()>0){
            i.putExtra("id",list.get(position).getId());
            i.putExtra("brandPicture",list.get(position).getBrandPictureUrl());
        }
        startActivityForResult(i, REQUEST_CODE_REEDIT);
    }

    @OnClick({R.id.iv_add_car, R.id.title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_car:
                startActivityForResult(new Intent(this, CarBrandActivity.class).putExtra("ISFROMMYCAR", "true"),REQUEST_CODE_EDIT);
                break;
            case R.id.title_back:
                if(hasChangeDefaultCar)
                    setResult(Mine.RESULT_MY_CAR_CODE);
                finish();
                break;
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        if(hasChangeDefaultCar)
            setResult(Mine.RESULT_MY_CAR_CODE);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_CODE_EDIT) {
            hasChangeDefaultCar=true;
            //刷新页面
            loadData();
        }
        if (requestCode == REQUEST_CODE_REEDIT && resultCode == RESULT_CODE_REEDIT) {
            //修改车里程时间信息，刷新页面
            lvMyCar.autoRefresh();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onRefresh() {
        loadData();
    }
    @Override
    public void onLoadMore() {}
}
/*lvMyCar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if(list!=null && list.size()>0){
                                        Intent i = new Intent(context, ReEditCarInfo.class);
                                        startActivityForResult(i, REQUEST_CODE_EDIT);
                                    }
                                }
                            });*/