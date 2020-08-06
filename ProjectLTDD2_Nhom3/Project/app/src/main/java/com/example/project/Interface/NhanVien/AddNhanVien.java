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

public class AddNhanVien extends AppCompatActivity {
    final int RESQUEST_TAKE_PHOTO = 123;
    final int REQUEST_CHOOSE_PHOTO = 321;
    Calendar calendar;
    int year, month, day;
    EditText edtMaNV, edtTenNV, edtNgaySinh, edtLuong;
    RadioButton radNam, radNu;
    Spinner spPhongBan;
    ImageView imgAnhDaiDien;
    Button btnAdd, btnChonHinh;
    ImageButton btnDatePicker;
    DBPhongBan dbPhongBan;
    CheckError checkError = new CheckError(AddNhanVien.this);
    ArrayList<String> dsPhong;
    ArrayAdapter dataPhong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nhan_vien);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setControl();
        setEvent();
    }

    private void setEvent() {
        imgAnhDaiDien.setImageResource(R.drawable.men);
        dbPhongBan = new DBPhongBan(this);
        dsPhong = dbPhongBan.LayDSPhong();
        dataPhong = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, dsPhong);
        spPhongBan.setAdapter(dataPhong);
        showDate(year, month + 1, day);
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBNhanVien dbNhanVien = new DBNhanVien(getApplicationContext());
                boolean check = dbNhanVien.checkMaNhanVien(edtMaNV.getText().toString());
                if (edtMaNV.getText().toString().isEmpty() || edtTenNV.getText().toString().isEmpty() || edtLuong.getText().toString().isEmpty()) {
                    checkError.checkEmpty(edtMaNV,"Vui lòng nhập mã nhân viên");
                    checkError.checkEmpty(edtTenNV,"Vui lòng nhập tên nhân viên");
                    checkError.checkEmpty(edtLuong,"Vui lòng nhập hệ số lương");
                } else if (check == true) {
                    edtMaNV.setError("Mã nhân viên đã tồn tại");
                    edtMaNV.isFocused();
                } else {
                    ThemDL();
                    Toast.makeText(AddNhanVien.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddNhanVien.this, MainActivityNhanVien.class);
                    startActivity(intent);
                    finish();
                }
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
        });

    }

    private void setControl() {
        edtMaNV = findViewById(R.id.txtMaNV);
        edtTenNV = findViewById(R.id.txtTenNV);
        edtNgaySinh = findViewById(R.id.txtNgaySinh);
        radNam = findViewById(R.id.radNam);
        radNu = findViewById(R.id.radNu);
        btnChonHinh = findViewById(R.id.btnChonHinh);
        spPhongBan = findViewById(R.id.spTenPB);
        imgAnhDaiDien = findViewById(R.id.imgHinhDaiDien);
        edtLuong = findViewById(R.id.edtLuong);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnAdd = findViewById(R.id.btnThem);
    }

    private void ThemDL() {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNV(edtMaNV.getText().toString());
        nhanVien.setTenNV(edtTenNV.getText().toString());
        nhanVien.setNgaySinh(edtNgaySinh.getText().toString());
        if (radNu.isChecked() == true) {
            nhanVien.setGioiTinh("Nữ");
            radNam.setChecked(false);
        }
        if (radNam.isChecked() == true) {
            nhanVien.setGioiTinh("Nam");
        }
        nhanVien.setTenPhong(spPhongBan.getSelectedItem().toString());
        nhanVien.setBacLuong(edtLuong.getText().toString());
        byte[] anh = getByteArrayFromImageView(imgAnhDaiDien);
        nhanVien.setImage(anh);
        DBNhanVien dbNhanVien = new DBNhanVien(this);
        dbNhanVien.Them(nhanVien);
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
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            showDate(i, i1 + 1, i2);
        }
    };

    private void showDate(int year, int month, int day) {
        edtNgaySinh.setText(new StringBuilder().append(day > 9 ? day : "0" + day).append("/").append(month > 9 ?
                month : "0" + month).append("/").append(year));
    }

    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    private void takePicture() {
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

    private byte[] getByteArrayFromImageView(ImageView imgv) {

        BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        bmp = Bitmap.createScaledBitmap(bmp, 80, 80, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
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



}