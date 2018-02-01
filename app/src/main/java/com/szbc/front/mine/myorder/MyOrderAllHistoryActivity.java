package com.szbc.front.mine.myorder;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.model.OrderAllDetail;
import com.szbc.tool.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 历史订单详情
 */
public class MyOrderAllHistoryActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    @BindView(R.id.tv_history_detail_carStyle)
    TextView tvHistoryDetailCarStyle;
    @BindView(R.id.tv_history_detail_carNum)
    TextView tvHistoryDetailCarNum;
    @BindView(R.id.tv_history_detail_range)
    TextView tvHistoryDetailRange;
    @BindView(R.id.tv_history_detail_workTime)
    TextView tvHistoryDetailWorkTime;
    @BindView(R.id.tv_history_detail_shopName)
    TextView tvHistoryDetailShopName;
    @BindView(R.id.tv_history_detail_shopAddress)
    TextView tvHistoryDetailShopAddress;
    @BindView(R.id.tv_history_detail_shopTel)
    TextView tvHistoryDetailShopTel;
    @BindView(R.id.orderNum)
    TextView orderNum;
    @BindView(R.id.orderState)
    TextView orderState;
    @BindView(R.id.orderTime)
    TextView orderTime;
    @BindView(R.id.receiverName)
    TextView receiverName;
    @BindView(R.id.receiverPhone)
    TextView receiverPhone;
    @BindView(R.id.carFetchAddress)
    TextView carFetchAddress;
    @BindView(R.id.carFetchTime)
    TextView carFetchTime;
    @BindView(R.id.carDeliverAddress)
    TextView carDeliverAddress;


    private String orderId;
    private OrderAllDetail order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder_all_history_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        if (Build.VERSION.SDK_INT < 20)
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);
        if (getIntent() != null && getIntent().getStringExtra("orderId") != null)
            orderId = getIntent().getStringExtra("orderId");
        initData();
    }

    private void initData() {
        String path = Config.httpIp + Config.Urls.queryOrderInfoById;
        params = new RequestParams(path);
        params.addParameter("orderId", orderId);
        params.setConnectTimeout(10 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String msg = object.getString("message");
                    if (result.equals("1")) {
                        if (object.getString("data") != null) {
                            String str = object.getString("data");
                            order = new Gson().fromJson(str, OrderAllDetail.class);
                            if (order != null) {
                                tvHistoryDetailCarStyle.setText(order.getCarinfo());
                                tvHistoryDetailShopAddress.setText(order.getBusiShopAddress());
                                tvHistoryDetailCarNum.setText(order.getCarNumber());
                                tvHistoryDetailRange.setText(order.getCarRunKm()+"公里");
                                tvHistoryDetailShopName.setText(order.getBusiShopName());
                                tvHistoryDetailShopTel.setText(order.getBusiShopPhone());
                                tvHistoryDetailWorkTime.setText(order.getMaintTime());

                                orderNum.setText(order.getOrderNum());
                                orderState.setText(order.getStatusInfo());
                                orderTime.setText(order.getOrderTime());
                                receiverName.setText(order.getReceiverName());
                                receiverPhone.setText(order.getReceiverPhone());
                                carFetchAddress.setText(order.getCarFetchAddress());
                                carFetchTime.setText(order.getCarFetchTime());
                                carDeliverAddress.setText(order.getCarDeliverAddress());
                            }
                        }
                    } else {
                        showToast(msg, 2000);
                    }
                } catch (JSONException e) {
                    showToast("数据解析异常", 1000);
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

    @OnClick(R.id.title_back)
    public void onClick() {
        this.finish();
    }
}
