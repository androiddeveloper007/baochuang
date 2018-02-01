package com.szbc.front.index;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.LocationModeSourceActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.LuckyDrawDialog_1;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.front.LoginActivity;
import com.szbc.front.addcar.CarBrandActivity;
import com.szbc.front.addcar.OrderEditActivity;
import com.szbc.front.mine.myorder.MyOrderActivity;
import com.szbc.model.Shop;
import com.szbc.model.UserCar;
import com.szbc.tool.StatusBarUtil;
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
import cn.bingoogolapple.bgabanner.BGABanner;

import static com.szbc.front.addcar.CarBrandActivity.REQUESTCODEFINISH;
import static com.szbc.front.mine.MyCarsActivity.RESULT_CODE_EDIT;


/**
 * 商家详情
 */
public class FourSStoreDetail extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.fours_banner)
    BGABanner foursBanner;
    @BindView(R.id.tv_shop_name)
    TextView tvShopName;
    @BindView(R.id.tv_shop_time)
    TextView tvShopTime;
    @BindView(R.id.tv_shop_address)
    TextView tvShopAddress;
    @BindView(R.id.rl_glass)
    RelativeLayout rl_glass;

    private String busiShopId;
    private List<Shop> shops;
    private Shop shop;
    private String phone, tel;
    private EncryptedSharedPreferences sp;
    List<UserCar> list;
    public final static int REQUESTLOGINFROMSTORE = 23456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fours_store_detail);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        if (getIntent() != null && getIntent().getStringExtra("busiShopId") != null) {
            busiShopId = getIntent().getStringExtra("busiShopId");
        }
        initData();
        sp = new EncryptedSharedPreferences(this);
    }

    private void initData() {
        String path = Config.httpIp + Config.Urls.queryBusiShopsDetails;
        params = new RequestParams(path);
        params.addParameter("id", busiShopId);
        params.setConnectTimeout(10 * 1000);
        mDialog = new MyDialog(this, "加载中...");
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
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            String msg = object.getString("message");
                            if (result.equals("1")) {
                                if (object.getString("data") != null) {
                                    String data = object.getString("data");
                                    shops = new Gson().fromJson(data, new TypeToken<List<Shop>>() {}.getType());
                                    if(shops!=null && shops.size()>0)
                                        shop = shops.get(0);
                                    if (shop != null) {
                                        foursBanner.setAdapter(new BGABanner.Adapter() {
                                            @Override
                                            public void fillBannerItem(final BGABanner banner, final View view, Object model, final int position) {
                                                Glide.with(banner.getContext())
                                                        .load(model)
                                                        .into((ImageView)view);
                                            }
                                        });
                                        foursBanner.setData(R.layout.view_image, shop.getPictures(), null);
                                        foursBanner.setOnItemClickListener(new BGABanner.OnItemClickListener() {
                                            @Override
                                            public void onBannerItemClick(BGABanner banner, View view, Object model, int position) {

                                            }
                                        });
                                        phone = shop.getLinkPhone();
                                        tel = shop.getLinkTel();
                                        tvShopName.setText(shop.getBusiShopName());
                                        tvShopTime.setText("营业时间："+shop.getBusiBeginTime()+"至"+shop.getBusiEndTime());
                                        tvShopAddress.setText("商家地址："+shop.getAddressDetail());
                                    }
                                }
                            } else {
                                showToast(msg, 2000);
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

    @OnClick({R.id.title_back, R.id.iv_phone_call, R.id.home_updateloginpwd_button,R.id.tv_shop_address})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.iv_phone_call:
                LuckyDrawDialog_1 d = new LuckyDrawDialog_1(this, true, 0.8);
                if(!TextUtils.isEmpty(tel))
                    d.setMessage(tel, true);
                else if(!TextUtils.isEmpty(phone))
                    d.setMessage(phone, true);
                else
                    d.setMessage("0000-00000", true);
                d.setBtn1Text("取消");
                d.setBtn2Text("呼叫");
                d.setTitleVisible(false, "");
                d.setTextIvestVisible(false);
                d.show();
                d.setOnBtn2ClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent var7;
                        var7 = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "0755-25625428"));
                        FourSStoreDetail.this.startActivity(var7);
                    }
                });
                break;
            case R.id.home_updateloginpwd_button:
                //请求数据后台获取当前座驾list
                loadData0(true);
                break;
            case R.id.tv_shop_address:
                if(shop==null){
                    showToast("数据不能为空",2000);
                    return;
                }
                String lat = shop.getBusiShopLatitude();
                String lon = shop.getBusiShopLongitude();
                Intent i = new Intent(context, LocationModeSourceActivity.class);
                i.putExtra("lat",lat);//当前商家的经度
                i.putExtra("lon",lon);//当前商家的纬度
                startActivity(i);
                break;
            default: break;
        }
    }

    private void loadData0(boolean showDialog) {
        String path = Config.httpIp + Config.Urls.validateOrder;
        params = new RequestParams(path);
        if(TextUtils.isEmpty(sp.getString("token"))){
            startActivity(new Intent(context,LoginActivity.class).putExtra("fromStore","true"));
            return;
        }
        params.addParameter("token", sp.getString("token"));
        if(shop==null || shop.getPrmyOperBrand()==null){
            showToast("经营品牌id为空",1200);
            return;
        }
        params.addParameter("prmyOperBrand", shop.getPrmyOperBrand());
        params.setConnectTimeout(10 * 1000);
        if(showDialog){
            mDialog = new MyDialog(this, "加载中...");
            mDialog.setDuration(300);
            mDialog.setMyCallback(new MyCallback() {
                @Override
                public void callback() {
                }
                @Override
                public void doing() {
                    postData();
                }
            });
            mDialog.showDialog();
        }else{
            postData();
        }
    }

    public void postData(){
        showLog("params: "+params.toString());
        x.http().post(params, new Callback.CacheCallback<String>() {
            String id="";
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String code = object.getString("code");
                    String msg = object.getString("message");
                    /*1：继续下面的流程
                         -1：提示已存在服务订单，请先完成取消即隐藏提示框，停留在当前页面，不写入数据
                        -3：提示“已存在申请服务，是否要取消？，”确认即取消原订单，写入新订单，取消即隐藏提示框*/
                    if (code.equals("0")) {
                        Intent i = new Intent(context, CarBrandActivity.class);
                        i.putExtra("busiShopId", shop.getId());
                        i.putExtra("busiShopName", shop.getBusiShopName());
                        i.putExtra("busiShopPhone", shop.getLinkPhone());
                        i.putExtra("busiShopAddress", shop.getAddressDetail());
                        i.putExtra("FROM4S", "");
                        startActivityForResult(i, REQUESTCODEFINISH);
                    } else if(TextUtils.equals("1",code)){
                        if(object.has("data")){
                            String data = object.getString("data");
                            JSONObject userCar = new JSONObject(data);
                            String userCarStr = userCar.getString("userCar");
                            list = new Gson().fromJson(userCarStr, new TypeToken<List<UserCar>>() {}.getType());
                        }
                        Intent i = new Intent(context, OrderEditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("userCar", list.get(0));
                        i.putExtras(bundle);
                        i.putExtra("busiShopId",shop.getId());
                        i.putExtra("busiShopName",shop.getBusiShopName());
                        i.putExtra("busiShopPhone",shop.getLinkPhone());
                        i.putExtra("busiShopAddress",shop.getAddressDetail());
                        startActivity(i);
                    }else if(TextUtils.equals("-1",code)){
                        showToast(msg,1200);
                        startActivity(new Intent(context, MyOrderActivity.class));//直接前往订单页
                    } else if(TextUtils.equals("-3",code)){
                        if(object.has("data")){
                            JSONObject data = new JSONObject(object.getString("data"));
                            if(data.has("id"))
                                id = data.getString("id");
                        }
                        showToast(msg,1200);
                        startActivity(new Intent(context, MyOrderActivity.class));//直接前往订单页
                        //已存在申请服务，是否取消原订单?
                        /*LuckyDrawDialog_1 d = new LuckyDrawDialog_1(context, true, 0.8);
                        d.setMessage("已存在申请服务，是否取消原订单?", true);
                        d.setBtn1Text("取消");
                        d.setBtn2Text("确定");
                        d.setTitleVisible(false, "");
                        d.setTextIvestVisible(false);
                        d.show();
                        d.setOnBtn2ClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reqestDeleteOrder(id);
                            }
                        });*/
                    } else if(TextUtils.equals("-2",code)){
                        showToast(msg,1200);
                    } else if(TextUtils.equals(code,"-4")){
                        showToast(msg, 2000);
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

    private void loadData(boolean showDialog) {
        String path = Config.httpIp + Config.Urls.queryUserCarlist;
        params = new RequestParams(path);
        if(TextUtils.isEmpty(sp.getString("token"))){
            startActivity(new Intent(context,LoginActivity.class).putExtra("fromStore","true"));
            return;
        }
        params.addParameter("token", sp.getString("token"));
        params.setConnectTimeout(10 * 1000);
        if(showDialog){
            mDialog = new MyDialog(this, "加载中...");
            mDialog.setDuration(300);
            mDialog.setMyCallback(new MyCallback() {
                @Override
                public void callback() {
                }
                @Override
                public void doing() {
                    postData();
                }
            });
            mDialog.showDialog();
        }else{
            postData();
        }
    }

    private void requestExitOrder() {
        String path = Config.httpIp + Config.Urls.validateOrder;
        params = new RequestParams(path);
        params.addParameter("token", sp.getString("token"));
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
        x.http().post(params, new Callback.CacheCallback<String>() {
            String id="";
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    if (TextUtils.equals("1",result)) {
                        //success
                        Intent i = new Intent(context, OrderEditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("userCar", list.get(0));
                        i.putExtras(bundle);
                        i.putExtra("busiShopId",shop.getId());
                        i.putExtra("busiShopName",shop.getBusiShopName());
                        i.putExtra("busiShopPhone",shop.getLinkPhone());
                        i.putExtra("busiShopAddress",shop.getAddressDetail());
                        startActivity(i);
                    } else if(TextUtils.equals("-1",result)){
                        //存在服务订单，请先完成
                        showToast(object.getString("message"), 1000);
                    } else if(TextUtils.equals("-3",result)){
                        if(object.has("data")){
                            JSONObject data = new JSONObject(object.getString("data"));
                            if(data.has("id"))
                                id = data.getString("id");
                        }
                        //已存在申请服务，是否取消原订单?
                        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(context, true, 0.8);
                        d.setMessage("已存在申请服务，是否取消原订单?", true);
                        d.setBtn1Text("取消");
                        d.setBtn2Text("确定");
                        d.setTitleVisible(false, "");
                        d.setTextIvestVisible(false);
                        d.show();
                        d.setOnBtn2ClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reqestDeleteOrder(id);
                            }
                        });
                    }else{
                        showToast(object.getString("message"), 1000);
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

    private void reqestDeleteOrder(String id) {
        String path = Config.httpIp + Config.Urls.udpateOrderStatus;
        params = new RequestParams(path);
        params.addParameter("id", id);
        params.addParameter("orderState", "9");
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String msg = object.getString("message");
                    if (result.equals("1")) {
                        showToast("订单取消成功", 1200);
                    }  else {
                        showToast(msg, 1200);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODEFINISH && resultCode == RESULT_CODE_EDIT) {
            setResult(RESULT_CODE_EDIT);
            finish();
        }
    }
}
