package com.szbc.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.adapter.PopupAdapter;
import com.szbc.adapter.PopupWindowCarAdapter;
import com.szbc.android.R;
import com.szbc.base.Config;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.front.LoginActivity;
import com.szbc.front.mine.AboutUsActivity;
import com.szbc.front.mine.MyCarsActivity;
import com.szbc.front.mine.myorder.MyOrderActivity;
import com.szbc.front.mine.orderdone.MyOrderDoneActivity;
import com.szbc.front.mine.popupwindow.PersonInfo;
import com.szbc.front.mine.popupwindow.UpdatePwdActivity;
import com.szbc.model.User;
import com.szbc.model.UserCar;
import com.szbc.tool.DividerItemDecoration;
import com.szbc.tool.config.EncryptedSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 我Fragment
 */
public class Mine extends BaseFragment {
    @BindView(R.id.iv_user_img)
    ImageView ivUserImg;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_car_selected)
    TextView tv_car_selected;
    @BindView(R.id.rl_driver_track)
    RelativeLayout rlDriverTrack;
    @BindView(R.id.view_added)
    View viewAdded;
    private PopupWindow pw, pw_car;
    public int[] item_image_id = {R.drawable.set_icon1, R.drawable.set_icon2, R.drawable.set_icon3, R.drawable.set_icon4};
    public int[] item_image_id_car = {R.drawable.binly, R.drawable.martin, R.drawable.alpha_romio};
    public String[] item_text = {"个人资料", "修改密码", "分享", "退出"};
    public View.OnClickListener[] onClickListeners;
    public View.OnClickListener[] onClickListeners_car;
    private Context mContext;
    private EncryptedSharedPreferences sp;
    private View view;
    private boolean isPrepared;// 标志fragment是否初始化完成
    public final static int REQUESTLOGINFROMMAIN = 23456;
    public final static int RESULTLOGINFROMMAIN = 23457;
    private List<UserCar> userCars;
    public static final int REQUEST_INFO_CODE = 123;
    public static final int RESULT_INFO_CODE = 124;
    public static final int REQUEST_MY_CAR_CODE = 125;
    public static final int RESULT_MY_CAR_CODE = 126;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.mine, container, false);
            initViews();
            isPrepared = true;
            mContext = getActivity();
            sp = new EncryptedSharedPreferences(mContext);
            lazyLoad();
        }
        ButterKnife.bind(this, view);
        return view;
    }

    private void initViews() {
        initListeners();
        initPopupWindow();
    }

    public void initData() {
        String userImgUri = sp.getString("userImgUri");
        if (!TextUtils.isEmpty(userImgUri)) {
            Glide.with(getActivity())
                    .load("file://" + userImgUri)
                    .error(R.drawable.mine_user)
                    .into(ivUserImg);
        }
        loadData();
    }

    private void loadData() {
        String path = Config.httpIp + Config.Urls.mine;
        params = new RequestParams(path);
        if (TextUtils.isEmpty(sp.getString("token"))) {
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQUESTLOGINFROMMAIN);
            return;
        }
        params.addParameter("token", sp.getString("token"));
        params.setConnectTimeout(10 * 1000);
        mDialog = new MyDialog(getActivity(), "加载中...");
        mDialog.setDuration(300);
        mDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {
            }

            @Override
            public void doing() {
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            showLog(arg0);
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            String message = object.getString("message");
                            if (result.equals("1")) {
                                if (object.has("data")) {
                                    JSONObject data = new JSONObject(object.getString("data"));
                                    if (data.has("user")) {
                                        JSONObject userObj = data.getJSONObject("user");
                                        User user;
                                        try {
                                            Gson gson = new Gson();
                                            user = gson.fromJson(userObj.toString(), User.class);
                                            //将用户的数据展示到个人中心（头像和电话号码）
//                                            if(!TextUtils.isEmpty(user.getUserPictureUrl()))//.placeholder(R.drawable.icon_stub).error(R.drawable.icon_stub)
//                                                Glide.with(getActivity()).load(user.getUserPictureUrl()).into(ivUserImg);
                                            tvUsername.setText(user.getUserPhone());
                                            sp.setUser(user);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (data.has("userCar")) {
                                        String str = data.getString("userCar");
                                        userCars = new Gson().fromJson(str, new TypeToken<List<UserCar>>() {
                                        }.getType());
                                        if (userCars != null && userCars.size() > 0) {
                                            UserCar car = userCars.get(0);
                                            tv_car_selected.setText(car.getCarBrand() + car.getCarModle() + car.getCarYearStyle());
                                            sp.putString("brandIdSelected", car.getCarBrandId());//设置全局变量brandIdSelected
                                            showLog("设置全局变量brandIdSelected:" + car.getCarBrandId());
                                            //得到集合后显示在popwindow
                                            initPopupWindowCar();
                                        } else {
                                            sp.putString("brandIdSelected", "");
                                            tv_car_selected.setText("未添加默认座驾");
                                        }
                                    }
                                    if (data.has("steerInfo")) {
                                        String fetchDeliverState = data.getJSONObject("steerInfo").getString("fetchDeliverState");
                                        if (TextUtils.equals("0",fetchDeliverState)) {
                                            viewAdded.setVisibility(View.GONE);
                                            rlDriverTrack.setVisibility(View.GONE);
                                        } else {
                                            rlDriverTrack.setVisibility(View.VISIBLE);
                                            viewAdded.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else if (TextUtils.equals(result, "-4")) {
                                showToast(message, 2000);
                                startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQUESTLOGINFROMMAIN);
                            } else {
                                showToast(message, 2000);
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
                        if (mDialog != null) {
                            mDialog.hideDialog();
                            d = null;
                        }
                    }

                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });
            }
        });
        mDialog.showDialog();
    }

    private void initListeners() {
        onClickListeners = new View.OnClickListener[item_image_id.length];
        onClickListeners_car = new View.OnClickListener[item_image_id_car.length];
        onClickListeners[0] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getActivity(), PersonInfo.class);
                startActivityForResult(i, REQUEST_INFO_CODE);
                pw.dismiss();
            }
        };
        onClickListeners[1] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getActivity(), UpdatePwdActivity.class);
                getActivity().startActivity(i);
                pw.dismiss();
            }
        };
        onClickListeners[2] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
                pw.dismiss();
            }
        };
        onClickListeners[3] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                showQuitDialog();
            }
        };
        onClickListeners_car[0] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw_car.dismiss();
            }
        };
        onClickListeners_car[1] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw_car.dismiss();
            }
        };
        onClickListeners_car[2] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw_car.dismiss();
            }
        };
    }

    private void showQuitDialog() {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.commit_quit))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLogout();
                        sp.putBooleanValue("isLogined", false);
                        sp.putString("token", "");
                        sp.putString("brandIdSelected", "");
                        getActivity().finish();
                    }
                }).setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setDimAmount(0.3f);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.show();
    }

    private void requestLogout() {
        String path = Config.httpIp + Config.Urls.logout;
        params = new RequestParams(path);
        params.addParameter("token", sp.getString("token"));
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    if (TextUtils.equals("1", object.getString("code"))) {
                        showToast(object.getString("msg"), 1000);
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
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void popupWindowShow(View view) {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int w = getResources().getDimensionPixelOffset(R.dimen.dimen_100);
        int h = getResources().getDimensionPixelOffset(R.dimen.dimen_60);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);
        int x = p.x - view.getMeasuredWidth() - w;
        pw.showAtLocation(view, Gravity.NO_GRAVITY, x, h);
    }

    private void popupWindowCarShow(View view) {
        if (!pw_car.isShowing()) {
            int[] position = new int[2];
            view.getLocationOnScreen(position);
            //计算偏移量：popup宽度减textview宽度再除2  position[0]
            int screenWidth = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth();
            int width = (int) screenWidth / 10;
            pw_car.showAtLocation(tv_car_selected, Gravity.NO_GRAVITY, width, position[1] + tv_car_selected.getHeight());
        }
    }

    private void initPopupWindow() {
        View content = LayoutInflater.from(getActivity()).inflate(R.layout.pop_up_window, null);
        RecyclerView recyclerView = (RecyclerView) content.findViewById(R.id.rv_pw);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(new PopupAdapter(getActivity(), item_image_id, item_text, onClickListeners));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity());
        recyclerView.addItemDecoration(decoration);
        pw = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = getResources().getDrawable(R.drawable.popupwindow_bg_car);
        pw.setBackgroundDrawable(drawable);
        pw.setFocusable(true);
        pw.setOutsideTouchable(true);
        pw.setAnimationStyle(R.style.PopupWindowAnimation);
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeWindowAlpha(1.0f);
            }
        });
    }

    private void initPopupWindowCar() {
        View content = LayoutInflater.from(getActivity()).inflate(R.layout.pop_up_cars, null);
        RecyclerView recyclerView = (RecyclerView) content.findViewById(R.id.rv_pw);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        PopupWindowCarAdapter adapter = new PopupWindowCarAdapter(getActivity(), userCars);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity());
        recyclerView.addItemDecoration(decoration);
        int screenWidth = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth();
        int windowWidth = (int) (screenWidth * 0.8);
        pw_car = new PopupWindow(getActivity());
        pw_car.setWidth(windowWidth);
        pw_car.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw_car.setFocusable(true);
        pw_car.setTouchable(true);
        pw_car.setContentView(content);
        Drawable drawable = getResources().getDrawable(R.drawable.popupwindow_bg);
        pw_car.setBackgroundDrawable(drawable);
        pw_car.setOutsideTouchable(true);
        pw_car.setAnimationStyle(R.style.PopupCarAnimStyle);
        pw_car.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeWindowAlpha(1.0f);
            }
        });
        adapter.setOnItemClickListener(new PopupWindowCarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switchDefaultCar(position);
            }
        });
    }

    private void switchDefaultCar(int position) {
        String path = Config.httpIp + Config.Urls.updateIsDefault;
        RequestParams params0 = new RequestParams(path);
        params0.addParameter("token", sp.getString("token"));
        params0.addParameter("id", userCars.get(position).getId());
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
                        pw_car.dismiss();
                        loadData();
                    } else {
                        showToast(msg, 1000);
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
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    /*改变界面的透明度*/
    private void changeWindowAlpha(float alpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getActivity().getWindow().setAttributes(lp);
    }

    @OnClick({R.id.iv_setting, R.id.tv_car_selected, R.id.ll_myorder, R.id.ll_order_done, R.id.layout_mycar, R.id.ll_aboutus,R.id.iv_user_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                if (pw.isShowing()) {
                    pw.dismiss();
                } else {
                    changeWindowAlpha(0.8f);
                    popupWindowShow(view);
                }
                break;
            case R.id.tv_car_selected:
                if (pw_car == null) {
                    showToast("车辆数据为空", 2000);
                    return;
                }
                if (pw_car.isShowing()) {
                    pw_car.dismiss();
                } else {
                    if (userCars != null && userCars.size() > 0) {
                        changeWindowAlpha(0.8f);
                        popupWindowCarShow(view);
                    } else if (userCars != null && userCars.size() == 0) {
                        showToast("车辆数据为空，请添加车辆", 2000);
                    }
                }
                break;
            case R.id.ll_myorder:
                startActivityForResult(new Intent(getActivity(), MyOrderActivity.class), REQUEST_MY_CAR_CODE);
                break;
            case R.id.ll_order_done:
                startActivity(new Intent(getActivity(), MyOrderDoneActivity.class));
                break;
            case R.id.layout_mycar:
                startActivityForResult(new Intent(getActivity(), MyCarsActivity.class), REQUEST_MY_CAR_CODE);
                break;
            case R.id.ll_aboutus:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            case R.id.iv_user_img:
                startActivityForResult(new Intent(getActivity(), PersonInfo.class), REQUEST_INFO_CODE);
                break;
            default:break;
        }
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initData();
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTLOGINFROMMAIN && resultCode == RESULTLOGINFROMMAIN) {
            //登录成功的操作
//            Toast.makeText(mContext, "登录成功的操作", Toast.LENGTH_SHORT).show();
            initData();
        }
        if (requestCode == REQUEST_INFO_CODE && resultCode == RESULT_INFO_CODE) {
            //修改头像成功的回掉
//            ToastMakeText.showToast(getActivity(), "更新头像数据", 1000);
//            if(data!=null && data.getStringExtra("imgUrl")!=null){
//                String imgUrl = data.getStringExtra("imgUrl");
//                Glide.with(getActivity())
//                        .load("file://" + imgUrl)
//                        .error(R.drawable.mine_user)
//                        .into(ivUserImg);
//            }
            loadData();
        } else if (requestCode == REQUEST_MY_CAR_CODE && resultCode == RESULT_MY_CAR_CODE) {//修改车辆信息成功后
            loadData();
        }
    }
}