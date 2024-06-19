package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradeStatActivity extends AppCompatActivity {
    com.github.mikephil.charting.charts.BarChart chart;
    Button btn_back;
    TextView no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grade_stat);
        no=findViewById(R.id.no);
        chart=findViewById(R.id.chart);
        chart.getAxisRight().setDrawLabels(false);
        Intent intent = getIntent();
        Serializable serializableMap = intent.getSerializableExtra("gradeCountMap");
        if (serializableMap instanceof Map) {
            Map<Long, Integer> gradeCount = (Map<Long, Integer>) serializableMap;
            if (!gradeCount.isEmpty()) {
                setupChart(gradeCount);
                chart.setVisibility(View.VISIBLE);
                no.setVisibility(View.GONE);
            } else {
                chart.setVisibility(View.GONE);
                no.setVisibility(View.VISIBLE);
            }
        }

        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void setupChart(Map<Long, Integer> gradeCount) {
        List<BarEntry> entries = new ArrayList<>();
        final List<Long> xAxisLabels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<Long, Integer> entry : gradeCount.entrySet()) {
            if (entry.getKey() == -1) {
                continue; // Skip -1 entry
            }

            entries.add(new BarEntry(index, entry.getValue()));
            xAxisLabels.add(entry.getKey());
            index++;
        }

        boolean hasNotGraded = gradeCount.containsKey(-1);

        BarDataSet dataSet = new BarDataSet(entries, "Grades");
        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < xAxisLabels.size()) {
                    return String.valueOf(xAxisLabels.get((int) value));
                } else {
                    return "";
                }
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setGranularityEnabled(true);
        yAxisLeft.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == -1 && hasNotGraded) {
                    return "Not graded yet";
                }
                return String.valueOf((int) value);
            }
        });

        chart.getAxisRight().setEnabled(false);

        chart.invalidate();
    }
}