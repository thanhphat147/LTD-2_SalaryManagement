package com.example.project.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.project.Database.DBPhongBan;
import com.example.project.Interface.UpdatePhongBan;
import com.example.project.Model.PhongBan;
import com.example.project.R;

import java.util.ArrayList;

public class CustomAdapterPB extends ArrayAdapter {
    Context context;
    int resource;
    ArrayList<PhongBan> data;
    ArrayList<PhongBan> data_DS;

    public CustomAdapterPB(Context context, int resource, ArrayList<PhongBan> data) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data = data;
        this.data_DS = new ArrayList<PhongBan>();
        this.data_DS.addAll(data);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    private static class Holder {
        ImageButton btnXoa;
        ImageButton btnSua;
        TextView tvMaPB;
        TextView tvTenPB;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = null;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(resource, null);
            holder.tvMaPB = view.findViewById(R.id.tvMaPhong);
            holder.tvTenPB = view.findViewById(R.id.tvTenPhong);
            holder.btnSua = view.findViewById(R.id.btnSua);
            holder.btnXoa = view.findViewById(R.id.btnXoa);
            view.setTag(holder);
        }
        else {
            holder = (Holder) view.getTag();
        }
        final PhongBan phongBan = data.get(position);
        holder.tvMaPB.setText(phongBan.getMaPhong());
        holder.tvTenPB.setText(phongBan.getTenPhong());
        holder.btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdatePhongBan.class);
                Bundle bundle = new Bundle();
                bundle.putString("ma", phongBan.getMaPhong());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn có muốn xóa không?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XoaDL(phongBan);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return view;
    }
    private void XoaDL(PhongBan phongBan) {
        DBPhongBan dbPhongBan = new DBPhongBan(context);
        dbPhongBan.Xoa(phongBan);
        dbPhongBan.LayDL();
    }
}
