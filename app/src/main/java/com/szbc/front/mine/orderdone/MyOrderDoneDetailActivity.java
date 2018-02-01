package com.szbc.front.mine.orderdone;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.base.MainActivity;
import com.szbc.front.mine.myorder.ContractSigningActivity;
import com.szbc.model.Product;
import com.szbc.tool.StatusBarUtil;
import com.szbc.widget.ListView4ScrollView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 维修保养记录详情
 */
public class MyOrderDoneDetailActivity extends BaseActivity {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    @BindView(R.id.tv_done_detail_2_0)
    TextView tvDoneDetail20;
    @BindView(R.id.tv_done_detail_3_0)
    TextView tvDoneDetail30;
    @BindView(R.id.tv_done_detail_4_0)
    TextView tvDoneDetail40;
    @BindView(R.id.tv_done_detail_5_0)
    TextView tvDoneDetail50;
    @BindView(R.id.tv_done_detail_6_0)
    TextView tvDoneDetail60;
    @BindView(R.id.tv_done_detail_7_0)
    TextView tvDoneDetail70;
    @BindView(R.id.tv_done_detail_8_0)
    TextView tvDoneDetail80;
    @BindView(R.id.tv_done_detail_0)
    TextView tv_done_detail_0;
    @BindView(R.id.lv_detail_products)
    ListView4ScrollView lvDetailProducts;
    @BindView(R.id.tv_done_detail_order_num)
    TextView tvDoneDetailOrderNum;
    @BindView(R.id.tv_done_detail_pay_type)
    TextView tvDoneDetailPayType;
    @BindView(R.id.tv_done_detail_pay_time)
    TextView tvDoneDetailPayTime;
    @BindView(R.id.tv_done_detail_deliver)
    TextView tvDoneDetailDeliver;
    private List<Product> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder_done_detail_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        if (Build.VERSION.SDK_INT < 20) {
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);
        }
        context = this;
        if (getIntent() == null || getIntent().getStringExtra("orderNum") == null) {
            showToast("订单号为空", 2000);
            return;
        }
        initData();
    }

    private void initData() {
        String path = Config.httpIp + Config.Urls.queryReport;
        params = new RequestParams(path);
        params.addParameter("orderNum", getIntent().getStringExtra("orderNum"));
        params.setConnectTimeout(10 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    showLog("维护详情：" + arg0);
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String message = object.getString("message");
                    if (result.equals("1")) {
                        if (object.has("data")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (data.has("serviceInfo")) {
                                String str = data.getString("serviceInfo");
                                JSONObject info = new JSONObject(str);
                                if (info.has("carinfo"))
                                    tvDoneDetail20.setText(info.getString("carinfo"));
                                if (info.has("carNumber"))
                                    tvDoneDetail30.setText(info.getString("carNumber"));
                                if (info.has("carRunKm"))
                                    tvDoneDetail40.setText(info.getString("carRunKm") + "公里");
                                if (info.has("maintTime"))
                                    tvDoneDetail50.setText(info.getString("maintTime"));
                                if (info.has("busiShopName"))
                                    tvDoneDetail60.setText(info.getString("busiShopName"));
                                if (info.has("busiShopAddress"))
                                    tvDoneDetail70.setText(info.getString("busiShopAddress"));
                                if (info.has("busiShopPhone"))
                                    tvDoneDetail80.setText(info.getString("busiShopPhone"));
                                if (info.has("priceSum"))
                                    tv_done_detail_0.setText("合计：¥" + info.getString("priceSum"));
                                if (info.has("orderNum"))
                                    tvDoneDetailOrderNum.setText("订单号：" + info.getString("orderNum"));
                                if (info.has("payTypeName"))
                                    tvDoneDetailPayType.setText("支付类型：" + info.getString("payTypeName"));
                                if (info.has("payTime"))
                                    tvDoneDetailPayTime.setText("支付时间：" + info.getString("payTime"));
                                if (info.has("fetchDeliverState")){
                                    String fetchDeliverState = info.getString("fetchDeliverState");
                                    if(TextUtils.equals("0",fetchDeliverState))
                                        tvDoneDetailDeliver.setVisibility(View.GONE);
                                    else
                                        tvDoneDetailDeliver.setVisibility(View.VISIBLE);
                                }
                            }
                            if (data.has("detailed")) {
                                String str = data.getString("detailed");
                                try {
                                    list = new Gson().fromJson(str, new TypeToken<List<Product>>() {
                                    }.getType());
                                    MyAdapter adapter = new MyAdapter(list);
                                    lvDetailProducts.setAdapter(adapter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (TextUtils.equals(result, "-4")) {
                        showToast(message, 2000);
                    } else {
                        showToast(message, 1000);
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

    @OnClick({R.id.title_back,R.id.tv_done_detail_deliver, R.id.iv_back_home})
    public void onClick(View v) {
        if(v.getId()==R.id.title_back){
            this.finish();
        }else if(v.getId()==R.id.tv_done_detail_deliver){
            startActivity(new Intent(this, ContractSigningActivity.class).putExtra("isFromHistoryOrder",""));
        }else if(v.getId()==R.id.iv_back_home){
            startActivity(new Intent(context, MainActivity.class).putExtra("index",""));
        }
    }

    class MyAdapter extends BaseAdapter {
        List<Product> list;

        MyAdapter(List<Product> list) {
            this.list = new ArrayList<>();
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(MyOrderDoneDetailActivity.this).inflate(R.layout.item_detail_product, parent, false);
                holder.tv_done_order_time = (TextView) convertView.findViewById(R.id.tv_done_detail_10);
                holder.tv_done_detail_10_0 = (TextView) convertView.findViewById(R.id.tv_done_detail_10_0);
                holder.tv_done_address = (TextView) convertView.findViewById(R.id.tv_done_detail_10_1);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tv_done_order_time.setText(list.get(position).getGoodsInfo());
            if (list.get(position).getBuyCount() != null)
                holder.tv_done_detail_10_0.setText(list.get(position).getBuyCount() + "件");
            holder.tv_done_address.setText("¥" + list.get(position).getPrice());
            return convertView;
        }

        class Holder {
            private TextView tv_done_order_time;
            private TextView tv_done_detail_10_0;
            private TextView tv_done_address;
        }
    }
}