package com.szbc.front.mine;

/**
 * Created by ZP on 2017/6/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.model.UserCar;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class MyCarAdapter extends BaseAdapter {
    List<UserCar> list;
    private Context context;
    private Map<Integer, Boolean> isSelected;
    private List beSelectedData = new ArrayList();
    private checkboxListener checkboxListener;
    private editListener editListener;
    private deleteListener deleteListener;
    private boolean isNoData;
    private int height;

    public MyCarAdapter(Context context, List<UserCar> list) {
        this.context = context;
        this.list = new ArrayList<>();
        this.list = list;
        if(list==null || list.size()==0){
            list.add(new UserCar());
        }
        if (isSelected != null)
            isSelected = null;
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
        isSelected.put(0, true);
        // 清除已经选择的项
        if (beSelectedData.size() > 0) {
            beSelectedData.clear();
        }
    }
    // 暂无数据
    public void getNoDataEntity(int height) {
        List<UserCar> list = new ArrayList<>();
        UserCar entity = new UserCar();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final int position1 = position;
        if (isNoData) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_no_car_layout, null);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
            rootView.setLayoutParams(params);
            return convertView;
        }
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_mycar_item, parent, false);
            holder.tv_brand = (TextView) convertView.findViewById(R.id.tv_car_brand);
            holder.tv_configuration = (TextView) convertView.findViewById(R.id.tv_car_configuration);
            holder.tv_range = (TextView) convertView.findViewById(R.id.tv_car_range);
            holder.tv_buyTime = (TextView) convertView.findViewById(R.id.tv_car_buyTime);
            holder.tv_myCar_selection = (TextView) convertView.findViewById(R.id.tv_myCar_selection);
            holder.iv_brand = (ImageView) convertView.findViewById(R.id.iv_brand);
            holder.iv_myCar_edit = (ImageView) convertView.findViewById(R.id.iv_myCar_edit);
            holder.iv_myCar_delete = (ImageView) convertView.findViewById(R.id.iv_myCar_delete);
            holder.checkbox_mycar_default = (CheckBox) convertView.findViewById(R.id.checkbox_mycar_default);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ImageOptions options = new ImageOptions.Builder().setIgnoreGif(false)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.icon_stub)
                .setFailureDrawableId(R.drawable.icon_error)
                .build();
        x.image().bind(holder.iv_brand, list.get(position).getBrandPictureUrl(), options);
        holder.tv_brand.setText(list.get(position).getCarBrand() + list.get(position).getCarModle());
        holder.tv_configuration.setText(list.get(position).getCarYearStyle());
        holder.tv_range.setText("里程："+list.get(position).getCarRunKm() + "公里");
        holder.tv_buyTime.setText("购车时间："+list.get(position).getCarBuyTime());
        if (position == 0) {
            holder.tv_myCar_selection.setText("默认座驾");
            holder.tv_myCar_selection.setVisibility(View.VISIBLE);
        } else if (position == 1) {
            holder.tv_myCar_selection.setText("其它座驾");
            holder.tv_myCar_selection.setVisibility(View.VISIBLE);
        } else
            holder.tv_myCar_selection.setVisibility(View.GONE);
        holder.checkbox_mycar_default.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 当前点击的CB
                boolean cu = !isSelected.get(position1);
                // 先将所有的置为FALSE
                for(Integer p : isSelected.keySet()) {
                    isSelected.put(p, false);
                }
                // 再将当前选择CB的实际状态
                isSelected.put(position1, cu);
                MyCarAdapter.this.notifyDataSetChanged();
                beSelectedData.clear();
                if(cu) beSelectedData.add(list.get(position1));
            }
        });
        holder.checkbox_mycar_default.setChecked(isSelected.get(position));
        holder.checkbox_mycar_default.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isSelected.get(position1)){
                    return true;
                }
                return false;
            }
        });
        holder.checkbox_mycar_default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkboxListener!=null){
                    checkboxListener.run(isChecked,position);
                }
            }
        });
        holder.iv_myCar_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editListener!=null){
                    editListener.edit(position);
                }
            }
        });
        holder.iv_myCar_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteListener!=null){
                    deleteListener.delete(position);
                }
            }
        });
        return convertView;
    }
    class Holder {
        private ImageView iv_brand;
        private ImageView iv_myCar_edit;
        private ImageView iv_myCar_delete;
        private CheckBox checkbox_mycar_default;
        private TextView tv_brand;
        private TextView tv_configuration;
        private TextView tv_range;
        private TextView tv_buyTime;
        private TextView tv_myCar_selection;
    }
    interface checkboxListener{
        void run(boolean b, int position);
    }
    interface editListener{
        void edit(int position);
    }
    interface deleteListener{
        void delete(int position);
    }
    public void setOnCheckbox(checkboxListener listener){
        this.checkboxListener = listener;
    }
    public void setOnEditListener(editListener listener){
        this.editListener = listener;
    }
    public void setOnDeleteListener(deleteListener listener){
        this.deleteListener = listener;
    }
}
