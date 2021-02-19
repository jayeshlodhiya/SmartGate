package com.payphi.visitorsregister;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Reports extends AppCompatActivity {
    private static final String TAG = "ShopReports";
    BarChart barChart;
    PieChart pieChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> barEntryLabels;
    private float[] yData = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f};
    private String[] xData = {"Mitch", "Jessica", "Mohammad", "Kelsey", "Sam", "Robert", "Ashley"};
    int bhk1,bhk2,bhk3;
    int docSize;
    String socityCode = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    ArrayList<Entry> yvalues = new ArrayList<Entry>();
    int localcount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        // barChart = (BarChart) findViewById(R.id.bargraph);
        sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        socityCode = sharedPreferences.getString("SOC_CODE", null);
        pieChart = (PieChart) findViewById(R.id.idPieChart);
        barChart = (BarChart) findViewById(R.id.idBieChart1);

        pieChart.setDescription("Sales by employee (In Thousands $) ");
        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);

        pieChart.setCenterTextSize(10);
        pieChart.animateXY(1400, 1400);
        barEntries = new ArrayList<>();

        barEntryLabels = new ArrayList<String>();

        AddValuesToBARENTRY();

        AddValuesToBarEntryLabels();

        barDataSet = new BarDataSet(barEntries, "Projects");

        barData = new BarData(barEntryLabels, barDataSet);

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.setData(barData);

        barChart.notifyDataSetChanged();
        barChart.setDrawBarShadow(true);
        barChart.animateY(1000);
        //addDataSet(bhk1, bhk2, bhk3);
        GetRoomTypeReport();
//        CreatePoll(10,20,"test1-test2");

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {


            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;
                Log.i("VAL SELECTED",
                        "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                                + ", DataSet index: " + dataSetIndex);
            }

            @Override
            public void onNothingSelected() {
                Log.i("PieChart", "nothing selected");

            }
        });
    }

    private void GetRoomTypeReport() {

        CollectionReference docRef = db.collection(socityCode).document("Sales").collection("EVisitors");

            docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    docSize = size;
                    System.out.println("Documents size====" + size);
                    //System.out.println("bookingsList size="+visitorsList.size());


                    if (yvalues.size() > 0 && yvalues.size() > 0) {
                        yvalues.removeAll(yvalues);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                    }
                    if (size == 0) {

                        yvalues.removeAll(yvalues);

                    }



                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //  System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        CreateRoomTypeData(documentSnapshot);
                        localcount = localcount+1;
                    }

                    if(localcount==docSize){
                        addDataSet(bhk1,bhk2,bhk3);
                    }


                }



            });




    }

    private void CreateRoomTypeData(DocumentSnapshot documentSnapshot) {
        String type = String.valueOf(documentSnapshot.get("bhk"));
        if(type!=null && type.equals("1")){
            bhk1 = bhk1+1;
        }
        if(type!=null && type.equals("2")){
            bhk2 = bhk2+1;
        }
        if ( type!=null && type.equals("3")){
            bhk3 = bhk3+1;
        }


    }

    public void AddValuesToBARENTRY() {

        barEntries.add(new BarEntry(2f, 0));
        barEntries.add(new BarEntry(4f, 1));
        barEntries.add(new BarEntry(6f, 2));
        barEntries.add(new BarEntry(8f, 3));
        barEntries.add(new BarEntry(7f, 4));
        barEntries.add(new BarEntry(3f, 5));

    }

    public void AddValuesToBarEntryLabels() {

        barEntryLabels.add("January");
        barEntryLabels.add("February");
        barEntryLabels.add("March");
        barEntryLabels.add("April");
        barEntryLabels.add("May");
        barEntryLabels.add("June");

    }

    private void addDataSet(int bhk1, int bhk2, int bhk3) {

        yvalues.add(new Entry(bhk1, 0));
        yvalues.add(new Entry(bhk2, 1));
        yvalues.add(new Entry(bhk3, 2));
        /*yvalues.add(new Entry(25f, 3));
        yvalues.add(new Entry(23f, 4));
        yvalues.add(new Entry(17f, 5));*/

        PieDataSet dataSet = new PieDataSet(yvalues, "");

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("1 BHK");
        xVals.add("2 BHK");
        xVals.add("3 BHK");


        PieData data = new PieData(xVals, dataSet);

       // data.setValueFormatter(new DefaultValueFormatter(0));
        // Default value
        //data.setValueFormatter(new DefaultValueFormatter(0));
        pieChart.setData(data);
        pieChart.setCenterText("Total "+docSize+" Enquires");
        pieChart.setDescription("");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(38f);

        pieChart.setHoleRadius(30f);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);

//for bar chart


    }


    private void CreatePoll(long opt1val, long opt2val, String options) {
        String arry[] = options.split("-");
        ArrayList<BarEntry> list = new ArrayList<>();
        list.add(new BarEntry(opt1val, 0));
        list.add(new BarEntry(opt2val, 1));
        ArrayList namlist = new ArrayList();
        namlist.add(arry[0]);
        namlist.add(arry[1]);

        BarDataSet barDataSet = new BarDataSet(list, "Test");
        BarData barData = new BarData(namlist, barDataSet);
        barDataSet.notifyDataSetChanged();
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(barData);
        // barChart.invalidate();
        barChart.notifyDataSetChanged();
        barChart.setDrawBarShadow(true);
        barChart.animateY(1000);
        barChart.setDescription("My First Bar Graph!");

    }

    public void openMonthSelector(View view) {
        createDialogWithoutDateField().show();
    }


    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return dpd;
    }

}
