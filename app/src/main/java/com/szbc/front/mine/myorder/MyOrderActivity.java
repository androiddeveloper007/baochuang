package com.szbc.front.mine.myorder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.LocationModeSourceActivity;
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
import com.szbc.front.mine.orderdone.MyOrderDoneDetailActivity;
import com.szbc.model.Order;
import com.szbc.model.OrderAll;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.MySlider;
import com.szbc.widget.xlistview.XListView;

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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.szbc.front.mine.myorder.MyOrderDonePaymentActivity.REQUEST_CODE_PAY;
import static com.szbc.front.mine.myorder.MyOrderDonePaymentActivity.RESPONSE_CODE_PAY;

/**
 * 我的订单
 */
public class MyOrderActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.myslider)
    MySlider mSlider;
    @BindView(R.id.lv_order_doing)
    XListView lvOrderDoing;
    @BindView(R.id.lv_order_all)
    XListView lvOrderAll;

    private EncryptedSharedPreferences sp;
    private List<Order> orderList,orderListTotal;
    private List<OrderAll> orderAllList,orderAllListTotal;
    private String token;
    private MyAdapter adapter;
    private MyAllAdapter allAdapter;
    private int row0=1,row1=1;
    public final static int REQUEST_CODE_CONTRACT = 1;
    public final static int RESPONSE_CODE_CONTRACT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder_activity);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
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
        initData(token,"1",1);
        initView();
    }

    public void initView() {
        mSlider.setOnClickSlider(new MyOnClickListener());
        lvOrderDoing.setVisibility(VISIBLE);
        lvOrderAll.setVisibility(GONE);
        lvOrderDoing.setPullRefreshEnable(true);
        lvOrderDoing.setPullLoadEnable(false);
        lvOrderDoing.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if(orderList!=null && orderListTotal!=null){
                    orderList.clear();
                    orderListTotal.clear();
                }
                row0=1;
                adapter=null;
                initData(token,"1",row0);
            }
            @Override
            public void onLoadMore() {
            }
        });
        lvOrderAll.setPullRefreshEnable(true);
        lvOrderAll.setPullLoadEnable(true);
        lvOrderAll.setAutoLoadEnable(true);
        lvOrderAll.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                row1=1;
                allAdapter=null;
                initData(token,"2",row1);
            }
            @Override
            public void onLoadMore() {
                initData(token,"2",++row1);
            }
        });
    }

    private class MyOnClickListener implements MySlider.OnClickSlider {
        @Override
        public void getIndex(int index) {
            if (index == 0) {
                lvOrderDoing.setVisibility(VISIBLE);
                lvOrderAll.setVisibility(GONE);
                lvOrderDoing.autoRefresh();
            } else {
                lvOrderDoing.setVisibility(GONE);
                lvOrderAll.setVisibility(VISIBLE);
                lvOrderAll.autoRefresh();
            }
        }
    }

    private void initData(String token,final String type,final int index){//type:1维护中订单，2全部订单
        String path;
        if(TextUtils.equals(type,"1"))
            path = Config.httpIp + Config.Urls.queryInMaintenance;
        else
            path = Config.httpIp + Config.Urls.queryTendingOrder;
        params = new RequestParams(path);
        params.addParameter("token", token);
        if(TextUtils.equals(type,"2")){
            params.addParameter("startRow", index);
            params.addParameter("rows", 20);
        }
        params.setConnectTimeout(10 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    showLog(arg0);
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String msg = object.getString("message");
                    if (result.equals("1")) {
                        if (object.getString("data") != null) {
                            String str = object.getString("data");
                            showLog(str);
                            try {
                                if(TextUtils.equals(type,"1")){
                                    orderList = new Gson().fromJson(str, new TypeToken<List<Order>>() {}.getType());
                                    if(adapter==null){
                                        orderListTotal = orderList;
                                        if(orderList==null || orderList.size()==0){
                                            if(orderListTotal!=null && orderListTotal.size()>0)
                                                orderListTotal.clear();
                                            adapter = new MyAdapter(orderList);
                                            lvOrderDoing.setAdapter(adapter);
                                            int height = DensityUtil.getWindowHeight((Activity) context) - getResources().getDimensionPixelOffset(R.dimen.dimen_170);
                                            adapter.getNoDataEntity(height);
                                            return;
                                        }else{
                                            adapter = new MyAdapter(orderList);
                                            lvOrderDoing.setAdapter(adapter);
                                        }
                                    }else{
                                        orderListTotal.addAll(orderList);
                                        adapter.update(orderListTotal);
                                    }
                                }else{
                                    orderAllList = new Gson().fromJson(str, new TypeToken<List<OrderAll>>() {}.getType());
                                    if (orderAllList.size() < 20)
                                        lvOrderAll.setPullLoadEnable(false);
                                    else
                                        lvOrderAll.setPullLoadEnable(true);
                                    if(orderAllList==null || orderAllList.size()==0){
                                        if(orderAllListTotal!=null && orderAllListTotal.size()>0){
                                            if(index==1)
                                                orderAllListTotal.clear();
                                        } else{
                                            orderAllList.add(new OrderAll());
                                            allAdapter = new MyAllAdapter(orderAllList);
                                            lvOrderAll.setAdapter(allAdapter);
                                            int height = DensityUtil.getWindowHeight((Activity) context) - getResources().getDimensionPixelOffset(R.dimen.dimen_170);
                                            allAdapter.getNoDataEntity(height);
                                        }
                                        return;
                                    }
                                    if(allAdapter==null){
                                        allAdapter = new MyAllAdapter(orderAllList);
                                        lvOrderAll.setAdapter(allAdapter);
                                        orderAllListTotal = orderAllList;
                                    }else{
//                                        orderAllListTotal=orderAllList;
                                        orderAllListTotal.addAll(orderAllList);
                                        allAdapter.update(orderAllListTotal);
                                    }
                                    lvOrderAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            if(orderAllListTotal!=null && orderAllListTotal.size()>0){
                                                Intent i = new Intent(MyOrderActivity.this, MyOrderAllHistoryActivity.class);
                                                i.putExtra("orderId", orderAllListTotal.get((int) id).getId());
                                                MyOrderActivity.this.startActivity(i);
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if(TextUtils.equals(result,"-4")){
                        showToast(msg, 2000);
                        startActivity(new Intent(context,LoginActivity.class));
                    } else {
                        if(type.equals("1")){
                            orderList = new ArrayList<>(1);
                            orderList.add(new Order());
                            adapter = new MyAdapter(orderList);
                            lvOrderDoing.setAdapter(adapter);
                            int height = DensityUtil.getWindowHeight((Activity) context) - getResources().getDimensionPixelOffset(R.dimen.dimen_170);
                            adapter.getNoDataEntity(height);
                            lvOrderDoing.setPullLoadEnable(false);
                        }else{
                            orderAllList = new ArrayList<>(1);
                            orderAllList.add(new OrderAll());
                            allAdapter = new MyAllAdapter(orderAllList);
                            lvOrderAll.setAdapter(allAdapter);
                            int height = DensityUtil.getWindowHeight((Activity) context) - getResources().getDimensionPixelOffset(R.dimen.dimen_170);
                            allAdapter.getNoDataEntity(height);
                            lvOrderAll.setPullLoadEnable(false);
                        }
//                        showToast(msg, 2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("数据解析异常",1000);
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
                    lvOrderDoing.stopRefresh();
                    lvOrderAll.stopRefresh();
                    lvOrderAll.stopLoadMore();
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    @OnClick(R.id.title_back)
    public void onClick() {
        setResult(Mine.RESULT_MY_CAR_CODE);
        this.finish();
    }
    @Override
    public void onBackPressed() {
        setResult(Mine.RESULT_MY_CAR_CODE);
        super.onBackPressed();
    }

    class MyAdapter extends BaseAdapter {
        List<Order> list;
        private boolean isNoData;
        private int height;
        private boolean hasChangeTime;
        private boolean state0Gone;
        private boolean state10Gone;
        private boolean state1Gone;
        private boolean state3Gone;
        private boolean state4Gone;
        private boolean state6Gone;
        private boolean state12Gone;
        private boolean state13Gone;
        private boolean state15Gone;

        MyAdapter(List<Order> list) {
            this.list = new ArrayList<>();
            this.list = list;
            for(Order o :list){
                if(TextUtils.equals("10",o.getOrderState())){
                    hasChangeTime=true;
                    state0Gone=true;
                }
                if(TextUtils.equals("11",o.getOrderState())){
                    state10Gone=true;
                }
                if(TextUtils.equals("1",o.getOrderState())){
                    state0Gone=true;
                    state10Gone=true;
                }
                if(TextUtils.equals("2",o.getOrderState())){
                    state1Gone=true;
                    state12Gone=true;
                    state15Gone=true;
                }
                if(TextUtils.equals("4",o.getOrderState())){
                    state3Gone=true;
                }
                if(TextUtils.equals("5",o.getOrderState())){
                    state4Gone=true;
                }
                if(TextUtils.equals("12",o.getOrderState())){
                    state1Gone=true;
                }
                if(TextUtils.equals("14",o.getOrderState())){
                    state3Gone=true;
//                    state13Gone=true;
                }
                if(TextUtils.equals("15",o.getOrderState())){
                    state12Gone=true;
                }
                if(TextUtils.equals("16",o.getOrderState())){
                    state6Gone=true;
                }
            }
        }
        public void update(List<Order> list){
            this.list = list;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return list.size();
        }
        // 暂无数据
        public void getNoDataEntity(int height) {
            List<Order> list = new ArrayList<>();
            Order entity = new Order();
            this.isNoData = true;
            this.height = height;
            list.add(entity);
            this.list = list;
            notifyDataSetChanged();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            Order order = list.get(position);
            if (isNoData) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_no_data_layout, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
                rootView.setLayoutParams(params);
                return convertView;
            }
            holder = new Holder();
            convertView = LayoutInflater.from(MyOrderActivity.this).inflate(R.layout.listview_order_doing_item, parent, false);
            if (convertView == null) {
//                convertView.setTag(holder);
            } else {
//                holder = (Holder) convertView.getTag();
            }
            holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
            holder.tv_status_info = (TextView) convertView.findViewById(R.id.tv_status_info);
            holder.tv_extra_info = (TextView) convertView.findViewById(R.id.tv_extra_info);
            holder.tv_order_time = (TextView) convertView.findViewById(R.id.tv_order_time);
            holder.tv_status_code = (TextView) convertView.findViewById(R.id.tv_status_code);
            holder.tv_order_subscribe_time = (TextView) convertView.findViewById(R.id.tv_order_subscribe_time);
            holder.tv_order_back_time = (TextView) convertView.findViewById(R.id.tv_order_back_time);
            holder.tv_status_code_call = (TextView) convertView.findViewById(R.id.tv_status_code_call);
            holder.tv_status_code_affirm = (TextView) convertView.findViewById(R.id.tv_status_code_affirm);
            holder.ll_doing_btn = (LinearLayout) convertView.findViewById(R.id.ll_doing_btn);
            holder.ll_doing_btn_affirm = (LinearLayout) convertView.findViewById(R.id.ll_doing_btn_affirm);
            holder.ll_doing_btn_call = (LinearLayout) convertView.findViewById(R.id.ll_doing_btn_call);
            holder.tv_status_info.setText(list.get(position).getStatusInfo());
            holder.tv_extra_info.setText("商家地址："+list.get(position).getBusiShopAddress());
            holder.tv_order_time.setText("操作时间："+list.get(position).getHandleTime());
            if(TextUtils.isEmpty(list.get(position).getStatusInfo()))
                holder.ll_doing_btn.setVisibility(GONE);
            else{
                holder.ll_doing_btn.setVisibility(VISIBLE);
                holder.tv_status_code.setText(list.get(position).getStatusInfo());
            }
            if(position==0)
                holder.iv_status.setBackgroundResource(R.drawable.order_doing_0);
            else if(position == list.size()-1)
                holder.iv_status.setBackgroundResource(R.drawable.order_doing_2);
            else
                holder.iv_status.setBackgroundResource(R.drawable.order_doing_1);

            if(TextUtils.equals("10",list.get(position).getOrderState())){
                holder.tv_status_code.setVisibility(GONE);
                holder.tv_order_subscribe_time.setVisibility(VISIBLE);
                holder.tv_order_back_time.setVisibility(VISIBLE);
                holder.ll_doing_btn.setVisibility(GONE);
                holder.tv_extra_info.setVisibility(GONE);
                holder.tv_order_time.setVisibility(GONE);
                holder.tv_order_subscribe_time.setVisibility(VISIBLE);
                holder.tv_order_back_time.setVisibility(VISIBLE);
                holder.ll_doing_btn_affirm.setVisibility(VISIBLE);
                holder.ll_doing_btn_call.setVisibility(VISIBLE);
                holder.tv_order_subscribe_time.setText("预约保养时间："+list.get(position).getMaintTime());
                holder.tv_order_back_time.setText("取车时间："+list.get(position).getCarFetchTime());
                holder.ll_doing_btn_affirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(MyOrderActivity.this, true, 0.80);
                        d.setMessage("接受商家的预约时间调整？", true,R.dimen.dimen_14);
                        d.setBtn1Text("取消");
                        d.setBtn2Text("接受");
                        d.setTitleVisible(false, "");
                        d.setTextIvestVisible(false);
                        d.show();
                        d.setOnBtn2ClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestChangeTime(list.get(position).getOrderNum(),list.get(position).getHandleId());
                            }
                        });
                    }
                });
                holder.ll_doing_btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(MyOrderActivity.this, true, 0.8);
                        d.setMessage("0755-25625428", true);
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
                                MyOrderActivity.this.startActivity(var7);
                            }
                        });
                    }
                });
            }else{
                holder.tv_status_code.setVisibility(VISIBLE);
                holder.tv_extra_info.setVisibility(VISIBLE);
                holder.tv_order_time.setVisibility(VISIBLE);
                holder.ll_doing_btn.setVisibility(VISIBLE);
                holder.tv_order_subscribe_time.setVisibility(GONE);
                holder.tv_order_back_time.setVisibility(GONE);
                holder.tv_order_subscribe_time.setVisibility(GONE);
                holder.tv_order_back_time.setVisibility(GONE);
                holder.ll_doing_btn_affirm.setVisibility(GONE);
                holder.ll_doing_btn_call.setVisibility(GONE);
            }
            final String statusCode = list.get(position).getOrderState();
            holder.ll_doing_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.equals(statusCode,"0")||TextUtils.equals(statusCode,"1")){
                        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(MyOrderActivity.this, true, 0.8);
                        d.setMessage("确认取消订单？", true,R.dimen.dimen_20);
                        d.setBtn1Text("取消");
                        d.setBtn2Text("确认");
                        d.setTitleVisible(false, "");
                        d.setTextIvestVisible(false);
                        d.show();
                        d.setOnBtn2ClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestCancelOrder(list.get(position),"9");//请求后台，取消当前订单
                            }
                        });
                    }else if(TextUtils.equals(statusCode,"3")){
                        Intent i = new Intent(MyOrderActivity.this,MyOrderDonePaymentActivity.class);
                        i.putExtra("orderNum",list.get(position).getOrderNum());
                        startActivityForResult(i, REQUEST_CODE_PAY);
                    }else if(TextUtils.equals(statusCode,"4")){//查看报告
                        Intent i = new Intent(MyOrderActivity.this,MyOrderDoneDetailActivity.class);
                        i.putExtra("orderNum",list.get(position).getOrderNum());
                        startActivity(i);
                    }else if(TextUtils.equals(statusCode,"6")){
                        if(state6Gone){//返程行车轨迹
                            Intent i = new Intent(context, LocationModeSourceActivity.class).putExtra("title","行车轨迹");
                            startActivity(i);
                        }else{
                            LuckyDrawDialog_1 d = new LuckyDrawDialog_1(MyOrderActivity.this, true, 0.8);
                            d.setMessage("确认完成订单？", true,R.dimen.dimen_20);
                            d.setBtn1Text("取消");
                            d.setBtn2Text("确认");
                            d.setTitleVisible(false, "");
                            d.setTextIvestVisible(false);
                            d.show();
                            d.setOnBtn2ClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestCancelOrder(list.get(position),"7");
                                }
                            });
                        }
                    }else if(TextUtils.equals(statusCode,"7")){
                        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(MyOrderActivity.this, true, 0.8);
                        d.setMessage("确认完成订单？", true,R.dimen.dimen_20);
                        d.setBtn1Text("取消");
                        d.setBtn2Text("确认");
                        d.setTitleVisible(false, "");
                        d.setTextIvestVisible(false);
                        d.show();
                        d.setOnBtn2ClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                requestCancelOrder(list.get(position),"7");
                            }
                        });
                    } else if(TextUtils.equals(statusCode,"12")){//签署合约
                        Intent i = new Intent(MyOrderActivity.this,ContractSigningActivity.class);
                        i.putExtra("orderNum",list.get(position).getOrderNum());
                        if(state12Gone)
                            i.putExtra("state12Gone","");
                        startActivityForResult(i, REQUEST_CODE_CONTRACT);
                    }else if(TextUtils.equals(statusCode,"13")){
                        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(MyOrderActivity.this, true, 0.8);
                        d.setMessage("到店取车并完成订单？", true,R.dimen.dimen_20);
                        d.setBtn1Text("取消");
                        d.setBtn2Text("确认");
                        d.setTitleVisible(false, "");
                        d.setTextIvestVisible(false);
                        d.show();
                        d.setOnBtn2ClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestCancelOrder(list.get(position),"7");
                            }
                        });
                    } else if(TextUtils.equals(statusCode,"14")){
                        /*查看报告*/
                        Intent i = new Intent(MyOrderActivity.this,MyOrderDoneDetailActivity.class);
                        i.putExtra("orderNum",list.get(position).getOrderNum());
                        startActivity(i);
                    }else if(TextUtils.equals(statusCode,"15")){//取车行车轨迹
                        Intent i = new Intent(context, LocationModeSourceActivity.class).putExtra("title","行车轨迹");
                        startActivity(i);
                    }else if(TextUtils.equals(statusCode,"16")){//返程行车轨迹
                        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(MyOrderActivity.this, true, 0.8);
                        d.setMessage("确认完成订单？", true,R.dimen.dimen_20);
                        d.setBtn1Text("取消");
                        d.setBtn2Text("确认");
                        d.setTitleVisible(false, "");
                        d.setTextIvestVisible(false);
                        d.show();
                        d.setOnBtn2ClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestCancelOrder(list.get(position),"7");
                            }
                        });
                    }
                }
            });
            //0待确认，1已确认，2检查中，3待付款，4已付款，5维护中，6返程中，7已完成，
            // 8商家取消，9用户取消，10商家调整保养时间，11用户接受调整时间
            // 12司机已到达取车地点，13维护完毕，请到店取车，14到店付款
            switch(list.get(position).getOrderState()){
                case "0":
                    if(state0Gone){
                        holder.ll_doing_btn.setBackgroundResource(R.drawable.button_order_done);
                        holder.ll_doing_btn.setVisibility(GONE);
                        holder.tv_status_code.setTextColor(getResources().getColor(R.color.colorGray));
                    }
                    holder.tv_status_code.setText("取消订单");
                    holder.tv_order_subscribe_time.setVisibility(VISIBLE);
                    holder.tv_order_subscribe_time.setText("预约保养时间："+list.get(position).getMaintTime());
                    break;
                case "1":
                    if(state1Gone){
                        holder.ll_doing_btn.setBackgroundResource(R.drawable.button_order_done);
                        holder.ll_doing_btn.setClickable(false);
                        holder.tv_status_code.setTextColor(getResources().getColor(R.color.colorGray));
                    }
                    holder.tv_status_code.setText("取消订单");
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                case "2":
//                    holder.tv_status_info.setText("检查中");
                    holder.ll_doing_btn.setVisibility(GONE);
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                case "3":
                    if(state3Gone){
                        holder.ll_doing_btn.setBackgroundResource(R.drawable.button_order_done);
                        holder.ll_doing_btn.setClickable(false);
                        holder.tv_status_code.setTextColor(getResources().getColor(R.color.colorGray));
                    }
                    holder.tv_status_code.setText("付款");
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                case "4":
                    holder.tv_status_code.setText("查看报告");
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                case "5":
                    holder.ll_doing_btn.setVisibility(GONE);
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                case "6":
                    holder.ll_doing_btn.setVisibility(VISIBLE);
                    if(state6Gone){
                        holder.tv_status_code.setText("行车轨迹");
                    }else{
                        holder.tv_status_code.setText("确认服务");
                    }
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                case "7":
                    break;
                case "10":
                    if(state10Gone){
                        holder.ll_doing_btn_affirm.setBackgroundResource(R.drawable.button_order_done);
                        holder.ll_doing_btn_affirm.setClickable(false);
                        holder.tv_status_code_affirm.setTextColor(getResources().getColor(R.color.colorGray));
                        holder.ll_doing_btn_call.setBackgroundResource(R.drawable.button_order_done);
                        holder.ll_doing_btn_call.setClickable(false);
                        holder.tv_status_code_call.setTextColor(getResources().getColor(R.color.colorGray));
                    }
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                case "11":
                    holder.ll_doing_btn.setVisibility(GONE);
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                case "12":
                    holder.ll_doing_btn.setVisibility(VISIBLE);
                    holder.tv_extra_info.setVisibility(GONE);
                    holder.tv_status_code.setText("签署合约");
                    if(state12Gone){
//                        holder.ll_doing_btn.setBackgroundResource(R.drawable.button_order_done);
//                        holder.ll_doing_btn.setClickable(false);
//                        holder.tv_status_code.setTextColor(getResources().getColor(R.color.colorGray));
                        holder.tv_status_code.setText("查看合约");
                    }
                    break;
                case "13"://维护完毕，请到店取车
                    holder.ll_doing_btn.setVisibility(VISIBLE);
                    holder.tv_extra_info.setVisibility(GONE);
                    holder.tv_status_code.setText("确认");
                    if(state13Gone){
                        holder.ll_doing_btn.setBackgroundResource(R.drawable.button_order_done);
                        holder.ll_doing_btn.setClickable(false);
                        holder.tv_status_code.setTextColor(getResources().getColor(R.color.colorGray));}
                    break;
                case "14"://到店付款
                    holder.ll_doing_btn.setVisibility(VISIBLE);
                    holder.tv_extra_info.setVisibility(GONE);
                    holder.tv_status_code.setText("查看报告");
                    break;
                case "15":
                    holder.ll_doing_btn.setVisibility(VISIBLE);
                    holder.tv_status_code.setText("行车轨迹");
                    holder.tv_extra_info.setVisibility(GONE);
                    if(state15Gone){
                        holder.ll_doing_btn.setBackgroundResource(R.drawable.button_order_done);
                        holder.ll_doing_btn.setClickable(false);
                        holder.tv_status_code.setTextColor(getResources().getColor(R.color.colorGray));
                    }
                    break;
                case "16":
                    holder.ll_doing_btn.setVisibility(VISIBLE);
                    holder.tv_status_code.setText("确认服务");
                    holder.tv_extra_info.setVisibility(GONE);
                    break;
                default:break;
            }
            return convertView;
        }
        class Holder {
            private ImageView iv_status;
            private TextView tv_status_info;
            private TextView tv_extra_info;
            private TextView tv_order_time;
            private TextView tv_status_code;
            private TextView tv_order_subscribe_time;
            private TextView tv_order_back_time;
            private TextView tv_status_code_call;
            private TextView tv_status_code_affirm;
            private LinearLayout ll_doing_btn;
            private LinearLayout ll_doing_btn_affirm;
            private LinearLayout ll_doing_btn_call;
        }
    }

    private void requestChangeTime(String orderNum, String handleId) {
        String path = Config.httpIp + Config.Urls.acceptAdjustDate;
        params = new RequestParams(path);
        params.addParameter("orderNum", orderNum);
        params.addParameter("handleId", handleId);
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
        mDialog = new MyDialog(this, "加载中...");
        mDialog.setDuration(300);
        mDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() { }
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
                                showToast(msg, 2000);
                                lvOrderDoing.autoRefresh();
                            }  else {
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

    private void requestCancelOrder(Order order, final String stateCode) {
        String path = Config.httpIp + Config.Urls.udpateOrderStatus;
        params = new RequestParams(path);
        params.addParameter("id", order.getId());
        params.addParameter("orderState", stateCode);
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
        mDialog = new MyDialog(this, "加载中...");
        mDialog.setDuration(300);
        mDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() { }
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
                                if(!TextUtils.equals("7",stateCode))
                                    showToast("订单已取消", 1200);//msg
                                else
                                    showToast("订单已完成，欢迎下次光临！",2000);
                                lvOrderDoing.autoRefresh();
                            }  else {
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

    class MyAllAdapter extends BaseAdapter {
        List<OrderAll> list;
        private boolean isNoData;
        private int height;
        MyAllAdapter(List<OrderAll> list) {
            this.list = new ArrayList<>();
            this.list = list;
        }
        public void update(List<OrderAll> list){
            this.list = list;
            notifyDataSetChanged();
        }
        // 暂无数据
        public void getNoDataEntity(int height) {
            List<OrderAll> list = new ArrayList<>();
            OrderAll entity = new OrderAll();
            this.isNoData = true;
            this.height = height;
            list.add(entity);
            this.list = list;
            notifyDataSetChanged();
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
            if (isNoData) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_no_data_layout, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
                rootView.setLayoutParams(params);
                return convertView;
            }
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(MyOrderActivity.this).inflate(R.layout.listview_order_all_item, parent, false);
                holder.tv_all_order_time = (TextView) convertView.findViewById(R.id.tv_all_order_time);
                holder.tv_all_status = (TextView) convertView.findViewById(R.id.tv_all_status);
                holder.tv_all_store = (TextView) convertView.findViewById(R.id.tv_all_store);
                holder.tv_all_address = (TextView) convertView.findViewById(R.id.tv_all_address);
                holder.tv_all_seriesandrange = (TextView) convertView.findViewById(R.id.tv_all_seriesandrange);
                holder.tv_all_endtime = (TextView) convertView.findViewById(R.id.tv_all_endtime);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tv_all_order_time.setText("订单时间："+list.get(position).getOrderTime());
            holder.tv_all_status.setText(list.get(position).getStatusInfo());
            holder.tv_all_store.setText(list.get(position).getBusiShopName());
            holder.tv_all_address.setText(list.get(position).getBusiShopAddress());
            holder.tv_all_seriesandrange.setText(list.get(position).getCarNumber()+"（"+list.get(position).getCarRunKm()+"公里）");
            holder.tv_all_endtime.setText("保养时间："+list.get(position).getCarFetchTime());
            return convertView;
        }
        class Holder {
            private TextView tv_all_order_time;
            private TextView tv_all_status;
            private TextView tv_all_store;
            private TextView tv_all_address;
            private TextView tv_all_seriesandrange;
            private TextView tv_all_endtime;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_PAY&&resultCode==RESPONSE_CODE_PAY){
            lvOrderDoing.autoRefresh();
        }
        if(requestCode==REQUEST_CODE_CONTRACT && resultCode==RESPONSE_CODE_CONTRACT){
            lvOrderDoing.autoRefresh();
        }
    }
}