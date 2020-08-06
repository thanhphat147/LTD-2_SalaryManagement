package com.example.project.Interface.NhanVien;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.Database.DBNhanVien;
import com.example.project.Database.DBPhongBan;
import com.example.project.Library.CheckError;
import com.example.project.Model.NhanVien;
import com.example.project.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class UpdateNhanVien extends AppCompatActivity {
    final int RESQUEST_TAKE_PHOTO = 123;
    final int REQUEST_CHOOSE_PHOTO = 321;
    Calendar calendar;
    int year, month, day;
    EditText edtTenNV, edtNgaySinh, edtLuong;
    TextView tvMa;
    Button btnUpdate, btnChonHinh;
    RadioButton radNam, radNu;
    Spinner spPhongBan;
    ImageView imgAnhDaiDien;
    ImageButton btnDatePicker;
    ArrayList<NhanVien> dataNV = new ArrayList<>();
    DBPhongBan dbPhongBan;
    ArrayList<String> dsPhong;
    ArrayAdapter adapterPB;
    CheckError checkError = new CheckError(UpdateNhanVien.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nhan_vien);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setControl();
        setEvent();
    }

    private void setEvent() {
        String gioiTinh, phongBan;

        dbPhongBan = new DBPhongBan(getApplicationContext());
        dsPhong= dbPhongBan.LayDSPhong();
        adapterPB = new ArrayAdapter<>(UpdateNhanVien.this, R.layout.support_simple_spinner_dropdown_item, dsPhong);
        spPhongBan.setAdapter(adapterPB);
        String masv = getIntent().getExtras().getString("manv");
        DBNhanVien dbNhanVien  =new DBNhanVien(this);
        dataNV = dbNhanVien.LayNhanVien(masv);
        tvMa.setText(dataNV.get(0).getMaNV());
        edtTenNV.setText(dataNV.get(0).getTenNV());
        edtNgaySinh.setText(dataNV.get(0).getNgaySinh());
        gioiTinh = dataNV.get(0).getGioiTinh();
        if (gioiTinh.equals("Nam")) {
            radNam.setChecked(true);
        }
        if (gioiTinh.equals("Nữ")){
            radNu.setChecked(true);
        }

        phongBan = dataNV.get(0).getTenPhong();
        if(phongBan != null) {
            spPhongBan.setSelection(getIndex(spPhongBan, phongBan));
        }
        edtLuong.setText(dataNV.get(0).getBacLuong());
        Bitmap bmHinhDaiDien = BitmapFactory.decodeByteArray(dataNV.get(0).getImage(), 0, dataNV.get(0).getImage().length);
        imgAnhDaiDien.setImageBitmap(bmHinhDaiDien);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTenNV.getText().toString().isEmpty() ||edtLuong.getText().toString().isEmpty()) {
                    checkError.checkEmpty(edtTenNV,"Vui lòng nhập tên nhân viên");
                    checkError.checkEmpty(edtLuong,"Vui lòng nhập hệ số lương");
                } else {
                    UpdateDL();
                    Toast.makeText(UpdateNhanVien.this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateNhanVien.this, MainActivityNhanVien.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });

        imgAnhDaiDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        btnChonHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
    }

    private void setControl() {
        tvMa = findViewById(R.id.tvMaNV);
        edtTenNV = findViewById(R.id.txtTenNV);
        edtNgaySinh = findViewById(R.id.txtNgaySinh);
        radNam = findViewById(R.id.radNam);
        radNu = findViewById(R.id.radNu);
        spPhongBan = findViewById(R.id.spTenPB);
        edtLuong = findViewById(R.id.edtLuong);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnChonHinh = findViewById(R.id.btnChonHinh);
    }

    private void UpdateDL() {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNV(tvMa.getText().toString());
        nhanVien.setTenNV(edtTenNV.getText().toString());
        nhanVien.setNgaySinh(edtNgaySinh.getText().toString());
        if (radNam.isChecked()) {
            nhanVien.setGioiTinh("Nam");
        }
        if (radNu.isChecked()) {
            nhanVien.setGioiTinh("Nữ");
        }
        byte[] image = getByteArrayFromImageView(imgAnhDaiDien);
        nhanVien.setImage(image);
        nhanVien.setTenPhong(spPhongBan.getSelectedItem().toString());
        nhanVien.setBacLuong(edtLuong.getText().toString());
        DBNhanVien dbNhanVien = new DBNhanVien(this);
        dbNhanVien.Sua(nhanVien);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //lấy vị trí phòng cần tìm trong spinner
    private int getIndex(Spinner spinner, String key){
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(key)){
                return i;
            }
        }

        return 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            return new DatePickerDialog(this, dateSetListener, year, month, day);
        }
        return null;
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int day, int month, int year) {
            showDate(day, month + 1, year);
        }
    };

    private void showDate(int year, int month, int day) {
        edtNgaySinh.setText(new StringBuilder().append(day > 9 ? day : "0" + day).append("/").append(month > 9 ?
                month : "0" + month).append("/").append(year));
    }

    private void choosePhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHOOSE_PHOTO) {
                try {
                    Uri imageUri = data.getData();
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
                    imgAnhDaiDien.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == RESQUEST_TAKE_PHOTO) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
                imgAnhDaiDien.setImageBitmap(bitmap);
            }
        }
    }

    private byte[] getByteArrayFromImageView(ImageView imgv){

        BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        bmp=Bitmap.createScaledBitmap(bmp, 80,80, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}