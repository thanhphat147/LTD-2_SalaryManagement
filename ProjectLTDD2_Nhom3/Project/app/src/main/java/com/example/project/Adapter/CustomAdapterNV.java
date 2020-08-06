package com.example.project.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.Database.DBNhanVien;
import com.example.project.Interface.ChamCong.AddChamCong;
import com.example.project.Interface.TamUng.AddTamUng;
import com.example.project.Interface.NhanVien.MainActivityNhanVien;
import com.example.project.Interface.NhanVien.UpdateNhanVien;
import com.example.project.Model.NhanVien;
import com.example.project.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class CustomAdapterNV extends ArrayAdapter {
    Context context;
    int resource;
    ArrayList<NhanVien> data;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
    final DBNhanVien dbNhanVien = new DBNhanVien(getContext());

    public CustomAdapterNV(Context context, int resource, ArrayList<NhanVien> data) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data = data;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    private static class Holder {
        TextView tvMaNV,  tvTenNV, tvNgaySinh, tvGioiTinh, tvPhongBan, tvLuong;
        ImageView imgAnhDaiDien;
        ImageButton btnXoa, btnSua;
        Button btnChamCong, btnTamUng;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = null;
        if(view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(resource, null);
            holder.tvMaNV = view.findViewById(R.id.tvMaNV);
            holder.tvTenNV = view.findViewById(R.id.tvTenNV);
            holder.tvNgaySinh = view.findViewById(R.id.tvNgaySinh);
            holder.tvGioiTinh = view.findViewById(R.id.tvGioiTinh);
            holder.tvPhongBan = view.findViewById(R.id.tvPhongBan);
            holder.tvLuong = view.findViewById(R.id.tvLuong);
            holder.imgAnhDaiDien = view.findViewById(R.id.imgHinhDaiDien);
            holder.btnXoa = view.findViewById(R.id.btnXoa);
            holder.btnSua = view.findViewById(R.id.btnSua);
            holder.btnChamCong = view.findViewById(R.id.btnChamCong);
            holder.btnTamUng = view.findViewById(R.id.btnTamUng);
            view.setTag(holder);
        }
        else
            holder=(Holder)view.getTag();

        final NhanVien nhanVien = data.get(position);

        holder.tvMaNV.setText(nhanVien.getMaNV());
        holder.tvTenNV.setText(nhanVien.getTenNV());
        holder.tvNgaySinh.setText(nhanVien.getNgaySinh());
        if (nhanVien.getGioiTinh().equals("Nam")) {
            holder.tvGioiTinh.setText(nhanVien.getGioiTinh());

        }
        if (nhanVien.getGioiTinh().equals("Nữ")) {
            holder.tvGioiTinh.setText(nhanVien.getGioiTinh());
        }
        holder.tvPhongBan.setText(nhanVien.getMaPhong());
        holder.tvLuong.setText(currencyVN.format(Integer.parseInt(nhanVien.getBacLuong())));
        if(nhanVien.getImage() == null){
            holder.imgAnhDaiDien.setImageResource(R.drawable.men);
        }else {
            Bitmap bmHinhDaiDien = BitmapFactory.decodeByteArray(nhanVien.getImage(), 0, nhanVien.getImage().length);
            bmHinhDaiDien = Bitmap.createScaledBitmap(bmHinhDaiDien, 80, 80, true);
            holder.imgAnhDaiDien.setImageBitmap(bmHinhDaiDien);
        }
        holder.btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateNhanVien.class);
                Bundle bundle = new Bundle();
                bundle.putString("manv",nhanVien.getMaNV());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkChamCong = false;
                boolean checkTamUng = false;
                checkChamCong = dbNhanVien.checkXoaNhanVienChamCong(nhanVien.getMaNV());
                checkTamUng = dbNhanVien.checkXoaNhanVienTamUng(nhanVien.getMaNV());
                if (checkChamCong == true && checkTamUng == false) {
                    Toast.makeText(context, "Cần xóa dữ liệu chấm công của Nhân viên trước", Toast.LENGTH_LONG).show();
                }
                if (checkTamUng == true && checkChamCong == false) {
                    Toast.makeText(context, "Cần xóa dữ liệu tạm ứng của Nhân viên trước", Toast.LENGTH_LONG).show();
                }
                if (checkTamUng == true && checkChamCong == true) {
                    Toast.makeText(context, "Cần xóa dữ liệu chấm công và tạm ứng của Nhân viên trước", Toast.LENGTH_LONG).show();
                }
                if (checkTamUng == false && checkChamCong == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn có muốn xóa không");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbNhanVien.Xoa(nhanVien);
                            Intent intent = new Intent(getContext(), MainActivityNhanVien.class);
                            context.startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });
        holder.btnChamCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddChamCong.class);
                Bundle bundle = new Bundle();
                bundle.putString("ma",nhanVien.getMaNV());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.btnTamUng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddTamUng.class);
                Bundle bundle = new Bundle();
                bundle.putString("ma",nhanVien.getMaNV());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        return view;
    }

//    //filter
//    public void filter (String charText){
//        charText = charText.toLowerCase(Locale.getDefault());
//        data.clear();
//        if (charText.length()==0){
//            data.addAll(data_DS);
//        }
//        else {
//            for (SinhVien model : data_DS){
//                if (model.getTenSV().toLowerCase(Locale.getDefault())
//                        .contains(charText) ){
//                    data.add(model);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
    public void XoaDL(NhanVien nhanVien) {
        DBNhanVien dbNhanVien = new DBNhanVien(context);
        dbNhanVien.Xoa(nhanVien);
        dbNhanVien.LayDSNhanVien();
    }


}
