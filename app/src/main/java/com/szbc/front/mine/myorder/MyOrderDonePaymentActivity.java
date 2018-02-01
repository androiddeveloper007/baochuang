package com.szbc.front.mine.myorder;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.LuckyDrawDialog_1;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.front.mine.myorder.model.ServiceInfo;
import com.szbc.front.mine.orderdone.OrderDoneAdapter;
import com.szbc.model.Product;
import com.szbc.tool.StatusBarUtil;
import com.szbc.widget.ListView4ScrollView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.nicespinner.NiceSpinner;

/**
 * 维修保养记录支付详情
 */
public class MyOrderDonePaymentActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    @BindView(R.id.tv_done_detail_0)
    TextView tvDoneDetail0;
    @BindView(R.id.tv_done_detail_1)
    TextView tvDoneDetail1;
    @BindView(R.id.tv_done_detail_2)
    TextView tvDoneDetail2;
    @BindView(R.id.tv_done_detail_3)
    TextView tvDoneDetail3;
    @BindView(R.id.tv_done_detail_4)
    TextView tvDoneDetail4;
    @BindView(R.id.tv_done_detail_20_1)
    TextView tv_done_detail_20_1;
    @BindView(R.id.tv_call_phone)
    TextView tv_call_phone;
    @BindView(R.id.lv_order_done)
    ListView4ScrollView lvOrderDone;
    @BindView(R.id.ll_orderDone_details)
    LinearLayout llOrderDoneDetails;

    private String orderNum;
    private ServiceInfo info;
    private List<Product> products;
    private OrderDoneAdapter adapter;
    private List<List<Product>> detaileds;
    private List<String> dataset1;
    public static final int REQUEST_CODE_PAY = 1;
    public static final int RESPONSE_CODE_PAY = 2;
    private Map<Integer, Integer> selectedIndex;
    private Map<String, String> priceSum;//计算总价格的map
    private int detailedsCount;
    private int pricesSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder_done_payment_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        if (Build.VERSION.SDK_INT < 20)
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);
        context = this;

        if (getIntent() != null && getIntent().getStringExtra("orderNum") != null)
            orderNum = getIntent().getStringExtra("orderNum");

        if (TextUtils.isEmpty(orderNum)) {
            showToast("订单号为空", 2000);
            return;
        } else {
            initData();
        }
    }

    private void initData() {
        String path = Config.httpIp + Config.Urls.queryReportDetails;
        params = new RequestParams(path);
        params.addParameter("orderNum", orderNum);
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
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
                            showLog(arg0);
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            String msg = object.getString("message");
                            if (result.equals("1")) {
                                if (object.has("data")) {
                                    JSONObject data = new JSONObject(object.getString("data"));
                                    if (data.has("serviceInfo")) {
                                        String serviceInfo = data.getString("serviceInfo");
                                        info = new Gson().fromJson(serviceInfo, ServiceInfo.class);
                                        if (info != null) {
                                            if (!TextUtils.isEmpty(info.getOrderTime()))
                                                tvDoneDetail0.setText("订单时间：" + info.getOrderTime());
                                            else
                                                tvDoneDetail0.setText("订单时间为空，联系后台");
                                            if (!TextUtils.isEmpty(info.getOrderTime()))
                                                tvDoneDetail1.setText(info.getBusiShopName());
                                            else
                                                tvDoneDetail1.setText("商家名称为空，联系后台");
                                            if (TextUtils.isEmpty(info.getBusiShopAddress()))
                                                tvDoneDetail2.setText("商家地址为空，联系后台");
                                            else
                                                tvDoneDetail2.setText(info.getBusiShopAddress());
                                            if (!TextUtils.isEmpty(info.getCarRunKm()))
                                                tvDoneDetail3.setText(info.getCarNumber() + "(" + info.getCarRunKm() + "公里)");
                                            else
                                                tvDoneDetail3.setText("汽车里程数为空，联系后台");
                                            if (!TextUtils.isEmpty(info.getMaintTime()))
                                                tvDoneDetail4.setText(info.getMaintTime());
                                            else
                                                tvDoneDetail4.setText("汽车保养时间为空，联系后台");
                                            if (!TextUtils.isEmpty(info.getPriceSum()))
                                                tv_done_detail_20_1.setText("¥" + info.getPriceSum());
                                            else
                                                tv_done_detail_20_1.setText("汽车保养总金额为空，联系后台");
                                            if (TextUtils.isEmpty(info.getBusiShopPhone()))
                                                tv_call_phone.setText("400-000-2658");
                                            else
                                                tv_call_phone.setText(info.getBusiShopPhone());
                                        }
                                    }
                                    priceSum = new HashMap<>();
                                    if (data.has("detailed")) {
                                        String details = data.getString("detailed");
                                        products = new Gson().fromJson(details, new TypeToken<List<Product>>() {}.getType());
                                        if(products!=null && products.size()>0 && products.get(0)!=null){
                                            adapter = new OrderDoneAdapter(context, products);
                                            lvOrderDone.setAdapter(adapter);
                                            for(Product a:products){
                                                priceSum.put(a.getId(),a.getPrice());
                                            }
                                        }
                                    }
                                    if (data.has("detaileds")) {
                                        String detailedStr = data.getString("detaileds");
                                        detaileds = new Gson().fromJson(detailedStr, new TypeToken<List<List<Product>>>() {}.getType());
                                        //遍历集合依次在布局中添加menudown的listView
                                        if(detaileds!=null && detaileds.size()>0 && detaileds.get(0)!=null && detaileds.get(0).size()>0){
                                            detailedsCount = detaileds.size();
                                            int i=0;
                                            for(List<Product> p : detaileds){//遍历所有的分组
                                                addSpinner(p,i++);
                                            }
                                        }
                                    }
                                    //重新计算价格
                                    if(priceSum!=null && priceSum.size()>0){
                                        pricesSum=0;
                                        for(String key : priceSum.keySet()){
                                            pricesSum += Integer.parseInt(priceSum.get(key));
                                        }
                                        tv_done_detail_20_1.setText("¥"+pricesSum);
                                    }
                                }
                            } else {
                                showToast(msg, 1000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void addSpinner(final List<Product> productList, final int index) {
                        NiceSpinner spinner = new NiceSpinner(context);
                        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                getResources().getDimensionPixelOffset(R.dimen.dimen_40));
                        spinnerParams.setMargins(0,0,0,getResources().getDimensionPixelOffset(R.dimen.dimen_5));
                        spinner.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.dimen_13));
                        spinner.setLayoutParams(spinnerParams);
                        spinner.setBackgroundResource(R.drawable.edittext_bg);
                        spinner.setBackgroundSelector(R.drawable.spinner_drawable);
                        spinner.setTextColor(getResources().getColor(R.color.text_color_1));
                        spinner.setSingleLine();
                        List<String> productLists = new ArrayList<>(detaileds.size());
                        for(Product p: productList){
                            productLists.add(p.getGoodsInfo());
                        }
                        dataset1 = new LinkedList<>(productLists);
                        spinner.attachDataSource(dataset1);
                        llOrderDoneDetails.addView(spinner);
                        llOrderDoneDetails.setVisibility(View.VISIBLE);
                        //添加所选配件价格
                        final TextView tv = new TextView(context);
                        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        tv.setGravity(Gravity.RIGHT);
                        tv.setLayoutParams(tvParams);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.dimen_13));
                        tv.setText("所选配件价格：¥"+productList.get(0).getPrice());
                        llOrderDoneDetails.addView(tv);
                        if(selectedIndex==null)
                            selectedIndex = new HashMap();
                        selectedIndex.put(index,0);
                        //添加价格信息到map
                        priceSum.put(""+index,productList.get(0).getPrice());
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                showLog("position:"+position+"id:"+id);
                                selectedIndex.put(index,position);
                                tv.setText("所选配件价格：¥"+productList.get(position).getPrice());
                                priceSum.put(""+index,productList.get(position).getPrice());
                                //重新计算价格
                                if(priceSum!=null && priceSum.size()>0){
                                    pricesSum=0;
                                    for(String key : priceSum.keySet()){
                                        pricesSum += Integer.parseInt(priceSum.get(key));
                                    }
                                    tv_done_detail_20_1.setText("¥"+pricesSum);
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                showLog("onNothindSelected");
                            }
                        });
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

    @OnClick({R.id.title_back, R.id.ll_btn_pay, R.id.rl_call_phone})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.ll_btn_pay:
                Intent i = new Intent(this, MyPaymentOrderActivity.class);
                if(info==null||info.getPriceSum()==null){showToast("总金额数据为空",1200);return;}
                if(detaileds!=null && detaileds.size()>0)
                    i.putExtra("priceTotal",pricesSum+"");
                else
                    i.putExtra("priceTotal",info.getPriceSum());
                i.putExtra("orderNum",info.getOrderNum());
                if(detaileds!=null && detaileds.size()>0){
                    String partIds = "";
                    for(int j=0;j<detailedsCount;j++){
                        if(j!=selectedIndex.get(j)){
                            if(TextUtils.isEmpty(partIds))
                                partIds += detaileds.get(j).get(selectedIndex.get(j)).getId();
                            else
                                partIds += ","+detaileds.get(j).get(selectedIndex.get(j)).getId();
                        }
                    }
                    i.putExtra("partIds",partIds);
                }
                if(info!=null && info.getOrderId()!=null)
                    i.putExtra("orderId",info.getOrderId());
                startActivityForResult(i,REQUEST_CODE_PAY);
                break;
            case R.id.rl_call_phone:
                LuckyDrawDialog_1 d = new LuckyDrawDialog_1(this, true, 0.8);
                d.setMessage("400-000-2658", true);
                d.setBtn1Text("取消");
                d.setBtn2Text("呼叫");
                d.setTitleVisible(false, "");
                d.setTextIvestVisible(false);
                d.show();
                d.setOnBtn2ClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "0755-25625428"));
                        startActivity(i);
                    }
                });
                break;
            default: break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_PAY&&resultCode==RESPONSE_CODE_PAY){
            setResult(RESPONSE_CODE_PAY);
            finish();
        }
    }
}