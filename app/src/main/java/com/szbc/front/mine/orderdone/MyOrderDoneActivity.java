package com.szbc.front.mine.orderdone;

import android.app.Activity;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.widget.dropdonwdemo.utils.DensityUtil;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.front.LoginActivity;
import com.szbc.model.OrderDone;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.config.EncryptedSharedPreferences;
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

/**
 * 维修保养记录
 */
public class MyOrderDoneActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.lv_order_done)
    XListView lvOrderDone;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    private EncryptedSharedPreferences sp;
    private List<OrderDone> list,listTotal;
    private int index = 1;
    private final static int rows = 20;
    private String token;
    private MyAdapter adapter;
    private int row = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder_done_activity);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        if(Build.VERSION.SDK_INT < 20)
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);
        context = this;

        sp = new EncryptedSharedPreferences(this);
        if(sp.getString("token")!=null)
            token = sp.getString("token");

        if(TextUtils.isEmpty(token)){
            showToast("请登录",2000);
            startActivity(new Intent(context,LoginActivity.class));
            finish();
            return;
        }
        initView();
        initData(index);
    }

    public void initView() {
        lvOrderDone.setPullRefreshEnable(true);
        lvOrderDone.setPullLoadEnable(false);
        lvOrderDone.setAutoLoadEnable(true);
        lvOrderDone.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                row=1;
                adapter=null;
                initData(row);
            }
            @Override
            public void onLoadMore() {
                initData(index++);
            }
        });
        lvOrderDone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.isNoData)
                    return;
                Intent i = new Intent(MyOrderDoneActivity.this,MyOrderDoneDetailActivity.class);
                i.putExtra("orderNum",list.get((int) id).getOrderNum());
                startActivity(i);
            }
        });
    }

    private void initData(int index) {
        String path = Config.httpIp + Config.Urls.maintainRecord;
        params = new RequestParams(path);
        params.addParameter("token", token);
        params.addParameter("startRow", index);
        params.addParameter("rows", rows);
        params.setConnectTimeout(10 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
//                    Log.e("zp",arg0);
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String message = object.getString("message");
                    if (result.equals("1")) {
                        if (object.has("data")) {
                            String str = object.getString("data");
                            try {
                                list = new Gson().fromJson(str, new TypeToken<List<OrderDone>>() {}.getType());
                                if (list.size() < 20)
                                    lvOrderDone.setPullLoadEnable(false);
                                else
                                    lvOrderDone.setPullLoadEnable(true);
                                if(adapter==null){
                                    listTotal = list;
                                    if(list==null || list.size()==0){
                                        adapter = new MyAdapter(list);
                                        lvOrderDone.setAdapter(adapter);
                                        int height = DensityUtil.getWindowHeight((Activity) context) - getResources().getDimensionPixelOffset(R.dimen.dimen_60);
                                        adapter.getNoDataEntity(height);
                                        return;
                                    }else{
                                        adapter = new MyAdapter(list);
                                        lvOrderDone.setAdapter(adapter);
                                    }
                                }else{
                                    listTotal.addAll(list);
                                    adapter.update(listTotal);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            list = new ArrayList<>(1);
                            adapter = new MyAdapter(list);
                            lvOrderDone.setAdapter(adapter);
                            int height = DensityUtil.getWindowHeight((Activity) context) - getResources().getDimensionPixelOffset(R.dimen.dimen_60);
                            adapter.getNoDataEntity(height);
                        }
                    } else if(TextUtils.equals(result,"-4")){
                        showToast(message, 2000);
                        startActivity(new Intent(context,LoginActivity.class));
                    } else {
                        showToast(message, 2000);
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
                lvOrderDone.stopRefresh();
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

    class MyAdapter extends BaseAdapter {
        List<OrderDone> list;
        private boolean isNoData;
        private int height;
        MyAdapter(List<OrderDone> list) {
            this.list = new ArrayList<>();
            this.list = list;
        }
        public void update(List<OrderDone> list){
            this.list = list;
            notifyDataSetChanged();
        }
        public void getNoDataEntity(int height) {
            List<OrderDone> list = new ArrayList<>();
            OrderDone entity = new OrderDone();
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
                convertView = LayoutInflater.from(MyOrderDoneActivity.this).inflate(R.layout.listview_order_done_item, parent, false);
                holder.tv_done_order_time = (TextView) convertView.findViewById(R.id.tv_done_order_time);
                holder.tv_done_store = (TextView) convertView.findViewById(R.id.tv_done_store);
                holder.tv_done_address = (TextView) convertView.findViewById(R.id.tv_done_address);
                holder.tv_done_count = (TextView) convertView.findViewById(R.id.tv_done_count);
                holder.tv_done_fee = (TextView) convertView.findViewById(R.id.tv_done_fee);
                holder.tv_done_doorServicePrice = (TextView) convertView.findViewById(R.id.tv_done_doorServicePrice);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tv_done_order_time.setText(list.get(position).getOrderTime());
            holder.tv_done_store.setText(list.get(position).getBusiShopName());
            holder.tv_done_address.setText(list.get(position).getBusiShopAddress());
            holder.tv_done_count.setText("共"+list.get(position).getMaintainSun()+"项");
            holder.tv_done_fee.setText("¥"+list.get(position).getPriceSum());
            holder.tv_done_doorServicePrice.setText("（含服务费¥"+list.get(position).getDoorServicePrice()+"）");
            return convertView;
        }
        class Holder {
            private TextView tv_done_order_time;
            private TextView tv_done_store;
            private TextView tv_done_address;
            private TextView tv_done_count;
            private TextView tv_done_fee;
            private TextView tv_done_doorServicePrice;
        }
    }
}