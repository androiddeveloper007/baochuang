package com.szbc.tool.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.szbc.model.MyCar;
import com.szbc.model.User;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedSharedPreferences {
    private static final String TAG = "SP_ENCRYPTED";
    private static final String SHARED_PREFERENCES = "SHARED_PREFERENCES_ENCRYPTED";
    private static final String PRIVATE_KEY = "wjCLYiJbzHk";//PRIVATE_KEY
    private static final String UTF_8 = "UTF-8";
    private static final String AES = "AES";

    private Context mContext;

    public EncryptedSharedPreferences(Context context) {
        this.mContext = context;
    }

    public void setUser(User user){
        SharedPreferences preferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(user.getUserId()!=null&& !TextUtils.isEmpty(user.getUserId()))
            editor.putString("userId", user.getUserId());
        if(user.getUserName()!=null&& !TextUtils.isEmpty(user.getUserName()))
            editor.putString("userName", user.getUserName());
        if(user.getUserPhone()!=null&& !TextUtils.isEmpty(user.getUserPhone()))
            editor.putString("phone", user.getUserPhone());
        if(user.getUserSex()!=null&& !TextUtils.isEmpty(user.getUserSex()))
            editor.putString("sex", user.getUserSex());
        if(user.getUserPictureUrl()!=null&& !TextUtils.isEmpty(user.getUserPictureUrl()))
            editor.putString("pictureUrl", user.getUserPictureUrl());
        editor.commit();
    }

    public User getUser(){
        SharedPreferences preferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        try{
            if(TextUtils.isEmpty(preferences.getString("userId","")))
                return null;
            User user = new User();
            user.setUserId(preferences.getString("userId",""));
            user.setUserName(preferences.getString("userName",""));
            user.setUserPhone(preferences.getString("phone",""));
            user.setUserSex(preferences.getString("sex",""));
            user.setUserPictureUrl(preferences.getString("pictureUrl",""));
            return user;
        }catch (Exception e){
            return null;
        }
    }
    public void setCarInfo(MyCar car,String prefName){
        SharedPreferences preferences = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(car.getBrand()!=null&& !TextUtils.isEmpty(car.getBrand()))
            editor.putString("brand", car.getBrand());
        if(car.getSeries()!=null&& !TextUtils.isEmpty(car.getSeries()))
            editor.putString("series", car.getSeries());
        if(car.getRange()!=null&& !TextUtils.isEmpty(car.getRange()))
            editor.putString("range", car.getRange());
        if(car.getConfiguration()!=null&& !TextUtils.isEmpty(car.getConfiguration()))
            editor.putString("configuration", car.getConfiguration());
        if(car.getBuyTime()!=null&& !TextUtils.isEmpty(car.getBuyTime()))
            editor.putString("buyTime", car.getBuyTime());
        editor.commit();
    }

    public MyCar getCar(String prefName){
        SharedPreferences preferences = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        try{
            if(TextUtils.isEmpty(preferences.getString("brand","")))
                return null;
            MyCar car = new MyCar();
            car.setBrand(preferences.getString("brand",""));
            car.setSeries(preferences.getString("series",""));
            car.setRange(preferences.getString("range",""));
            car.setConfiguration(preferences.getString("configuration",""));
            car.setBuyTime(preferences.getString("buyTime",""));
            return car;
        }catch (Exception e){
            return null;
        }
    }

    public void putString(String key, String value) {
        try {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(key, Base64.encodeToString(encrypt(value), Base64.DEFAULT));
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "Exception during putting encrypted String", e);
        }
    }

    public String getString(String key) {
        try {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);
            String encrypted = preferences.getString(key, null);
            if (encrypted == null) {
                return null;
            } else {
                return decrypt(Base64.decode(encrypted, Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during getting String", e);
            return null;
        }
    }

    public void putBooleanValue(String key, Boolean value) {
        try {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "Exception during putting encrypted String", e);
        }
    }

    public Boolean getBoolean(String key) {
        try {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);
            Boolean b = preferences.getBoolean(key, false);
            if (b == null) {
                return false;
            } else {
                return b;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during getting String", e);
            return false;
        }
    }

    public void removeString(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        preferences.edit().remove(key).commit();
    }

    public void removeAll(){
        SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    private byte[] encrypt(String clear) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(clear.getBytes(UTF_8));
        return encrypted;
    }

    private String decrypt(byte[] encrypted) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, UTF_8);
    }

    private byte[] getKey() throws Exception {
        SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        String key = preferences.getString(PRIVATE_KEY, null);
        if (key == null) {
            SharedPreferences.Editor editor = preferences.edit();
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(128);
            keyGenerator.generateKey();
            SecretKey secretKey = keyGenerator.generateKey();
            String stringKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
            editor.putString(PRIVATE_KEY, stringKey);
//            editor.putInt("UserAge", 22);
            editor.commit();
            return secretKey.getEncoded();
        }
        byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, AES);
        return originalKey.getEncoded();
    }
}
