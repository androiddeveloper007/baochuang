package com.szbc.base;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.szbc.android.R;
import com.szbc.tool.StringConverter;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.UUID;

public class Config {
    public static final long SPLASH_DELAY_MILLIS = 300;
    public static final boolean isTest = false;
//    public static String httpIp = "http://192.168.1.229:8082";
    public static String httpIp = isTest ? "http://carclient.wx.loc" : "http://carclient.dxwriter.com";
    public static String APP_NAME = "cred";
    public static final boolean isDebug = false;
    public class Urls{
        public static final String login = "/wx-ctrl-carload/userAppLogin";
        public static final String sendSMSCode = "/wx-ctrl-carload/sendSMSCode";//获取验证码
        public static final String verifySmsCode = "/wx-ctrl-carload/vidateSMSCode";
        public static final String register = "/wx-ctrl-carload/registerUser";
        public static final String mine = "/wx-ctrl-carload/getUserInfo";
        public static final String updatePwd = "/wx-ctrl-carload/updateLoginPwd";//修改密码
        public static final String logout = "/wx-ctrl-carload/logout";
        public static final String addCar = "/wx-ctrl-carload/saveUserCarLsit";//新增座驾
        public static final String updateIsDefault = "/wx-ctrl-carload/updateIsDefault";//切换车辆
        public static final String deleteUserCarlis = "/wx-ctrl-carload/deleteUserCarlis";//删除车辆
        public static final String updateUserCarLsit = "/wx-ctrl-carload/updateUserCarLsit";//修改车辆信息
        public static final String maintainRecordDetail = "/wx-ctrl-carload/maintainRecordDetail";//维护保养记录明细
        public static final String queryUserCarlist = "/wx-ctrl-carload/queryUserCarlist";//我的座驾
        public static final String queryCarBrandBs = "/wx-ctrl-carload/queryCarBrandBs";//车品牌
        public static final String queryCarModleBs = "/wx-ctrl-carload/queryCarModleBs";//车系列
        public static final String carConfiguration = "/wx-ctrl-carload/queryCaryearstylebs";//车配置
        public static final String updateUserPicture = "/wx-ctrl-carload/updateUserPicture";//用户头像
        public static final String uploadImage = "/wx-ctrl-carload/uploadImage";//用户头像
        public static final String queryBusiShopsList = "/wx-ctrl-carload/queryBusiShopsList";//首页4S店数据筛选
        public static final String queryBusiShopsDetails = "/wx-ctrl-carload/queryBusiShopsDetails";//商家详情
        public static final String saveOrderMaintList = "/wx-ctrl-carload/saveOrderMaintList";//生成订单
        public static final String queryTendingOrder = "/wx-ctrl-carload/queryTendingOrder";//全部订单
        public static final String queryInMaintenance = "/wx-ctrl-carload/queryInMaintenance";//进行中订单
        public static final String queryOrderInfoById = "/wx-ctrl-carload/queryOrderInfoById";//订单详情查询
        public static final String udpateOrderStatus = "/wx-ctrl-carload/udpateOrderStatus";//订单取消
        public static final String maintainRecord = "/wx-ctrl-carload/maintainRecord";//维护保养记录
        public static final String queryReport = "/wx-ctrl-carload/queryReport";//维护保养记录详情
        public static final String queryReportDetails = "/wx-ctrl-carload/queryReportDetails";//维护保养支付详情
        public static final String validateOrder = "/wx-ctrl-carload/validateOrder";//验证订单
        public static final String acceptAdjustDate = "/wx-ctrl-carload/acceptAdjustDate";//接受商家时间调整
        public static final String bbbb = "/wx-ctrl-carload/bbb";//代驾说明
        public static final String updateUserMess = "/wx-ctrl-carload/updateUserMess";//base64上传图片
        public static final String saveOrderOperationLog = "/wx-ctrl-carload/saveOrderOperationLog";//查看合约同意合约
        public static final String payment = "/wx-ctrl-carload/payment";//支付
    }
    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }
    public static void loadImageGlide(Context c,String url,ImageView iv){
        Glide.with(c)
                .load(url)
                .placeholder(R.drawable.icon_stub)
                .error(R.drawable.icon_stub)
                .into(iv);
    }
    public static void loadImageXUtils(ImageView iv, String url){
        ImageOptions options = new ImageOptions.Builder().setIgnoreGif(false)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.icon_stub)
                .setFailureDrawableId(R.drawable.icon_error)
                .build();
        x.image().bind(iv, url, options);
    }
    public static Gson getGson(){
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        gb.serializeNulls();
        Gson gson = gb.create();
        return gson;
    }
}