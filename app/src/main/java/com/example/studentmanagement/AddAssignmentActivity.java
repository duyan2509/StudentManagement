package com.example.studentmanagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class AddAssignmentActivity extends AppCompatActivity {

    Button btnSelectDate, btnSelectTime;
    EditText etSelectedDate, etSelectedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_assignment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        etSelectedDate = findViewById(R.id.etSelectedDate);
        etSelectedTime = findViewById(R.id.etSelectedTime);

        // Sự kiện click của button chọn ngày
        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddAssignmentActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        etSelectedDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Sự kiện click của button chọn giờ
        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddAssignmentActivity.this,
                    (view, hourOfDay, minute1) -> {
                        String selectedTime = hourOfDay + ":" + minute1;
                        etSelectedTime.setText(selectedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            String classID = getIntent().getStringExtra("classID");
            Intent intent = new Intent(AddAssignmentActivity.this, LectureDetailClassActivity.class);
            intent.putExtra("show_fragment_lecture_detail_class_assignment", true);
            intent.putExtra("classID", classID);
            startActivity(intent);
            finish();
        });
    }
}