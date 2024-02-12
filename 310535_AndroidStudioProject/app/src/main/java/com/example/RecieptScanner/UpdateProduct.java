package com.example.RecieptScanner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateProduct extends AppCompatActivity {

    EditText product_name_input, product_price_input;
    Button update_button, delete_button, product_category_input, purchase_date_input;
    String id, product_name, product_price, product_category, purchase_date;
    final String VISIBLE_FORMAT = "dd-MM-yyyy";
    final String HIDDEN_FORMAT = "yyyy-MM-dd";
    String selected_date;
    MyDatabaseHelper myDB;
    ArrayList<String> categories = new ArrayList<>();
    String[] listItems = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatepurchase_layout);

        Locale.setDefault(new Locale("pl"));

        product_name_input = findViewById(R.id.product_name_input2);
        product_price_input = findViewById(R.id.product_price_input2);
        purchase_date_input = findViewById(R.id.purchase_date_input2);
        product_category_input = findViewById(R.id.product_category_input2);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        product_price_input.setKeyListener(DigitsKeyListener.getInstance(true,true)); // decimals and positive/negative numbers.

        //First we call this
        getAndSetIntentData();

        //Set actionbar title after getAndSetIntentData method
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("xd");
        }


        myDB = new MyDatabaseHelper(UpdateProduct.this);
        Cursor cursor = myDB.readAllDataCategories();
        if(cursor.getCount() == 0){
        }else{
            while (cursor.moveToNext()){
                categories.add(cursor.getString(1));

            }

        }

        categories.add(0, "Brak kategorii");
        listItems = Arrays.copyOf(
                categories.toArray(), categories.size(), String[].class);

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //And only then we call this
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateProduct.this);
                product_name = product_name_input.getText().toString().trim();
                product_price = product_price_input.getText().toString().trim();
                product_category = product_category_input.getText().toString().trim();
                purchase_date = purchase_date_input.getText().toString().trim();
                String newDateString;
                SimpleDateFormat sdf = new SimpleDateFormat(VISIBLE_FORMAT);
                Date d;
                try {
                    d = sdf.parse(purchase_date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                sdf.applyPattern(HIDDEN_FORMAT);

                newDateString = sdf.format(d);
                if(product_name.trim().length()==0){
                    Toast.makeText(UpdateProduct.this, "Wprowadź nazwę produktu", Toast.LENGTH_SHORT).show();
                } else if (product_price.replaceAll("[.,]","").length()==0) {
                    Toast.makeText(UpdateProduct.this, "Wprowadź cenę", Toast.LENGTH_SHORT).show();

                } else {
                    String price_temp = product_price_input.getText().toString().trim().replaceAll("[,]", ".");
                    String[] parts = price_temp.split("[.]", 2);
                    if(parts.length>1) {
                        price_temp = parts[0] + "." + parts[1].replaceAll("[.]", "");
                    }
                    double rounded_price = Double.parseDouble(price_temp.trim());
                    rounded_price = (double) Math.round(rounded_price * 100) / 100;
                    myDB.updateData(id, product_name, String.valueOf(rounded_price), product_category, newDateString);
                    finish();
                }
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });

        product_category_input.setOnClickListener(v -> { // v is the button
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(UpdateProduct.this);
            mBuilder.setTitle("Wybierz kategorie");
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (listItems[i].equalsIgnoreCase("Brak kategorii")) {
                        product_category_input.setText("");
                        dialogInterface.dismiss();
                    } else {
                        product_category_input.setText(listItems[i]);
                        dialogInterface.dismiss();
                    }
                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

        purchase_date_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                if(!purchase_date_input.getText().toString().equals("Data od")){
                    DateTimeFormatter f = DateTimeFormatter.ofPattern( "dd-MM-yyyy" ) ;
                    LocalDate ld = LocalDate.parse( purchase_date_input.getText().toString() , f ) ;
                    year = ld.getYear();
                    month = ld.getMonthValue()-1;
                    day = ld.getDayOfMonth();
                }

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        UpdateProduct.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                String day_temp ;
                                String month_temp;
                                if (dayOfMonth<10){
                                    day_temp = "0" +dayOfMonth;
                                } else {
                                    day_temp=String.valueOf(dayOfMonth);
                                }

                                if (monthOfYear<9){
                                    month_temp = "0" +(monthOfYear + 1);
                                } else {
                                    month_temp=String.valueOf((monthOfYear + 1));
                                }
                                purchase_date_input.setText(day_temp + "-" + month_temp + "-" + year);
                                selected_date = year+ "-" + month_temp + "-" +day_temp;


                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("product_name") &&
                getIntent().hasExtra("product_price") && getIntent().hasExtra("product_category") &&
                getIntent().hasExtra("purchase_date")){
            //Getting Data from Intent
            id = getIntent().getStringExtra("id");
            product_name = getIntent().getStringExtra("product_name");
            product_price = getIntent().getStringExtra("product_price");
            product_category = getIntent().getStringExtra("product_category");
            purchase_date = getIntent().getStringExtra("purchase_date");

            //Setting Intent Data
            product_name_input.setText(product_name);
            product_price_input.setText(product_price.replaceAll("[,]", "."));
            product_category_input.setText(product_category);


            purchase_date_input.setText(purchase_date);
            //Log.d("stev", title+" "+author+" "+pages);
        }else{
            Toast.makeText(this, "Brak danych", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usunąć " + product_name + "?");
        builder.setMessage("Jesteś pewny, że chcesz usunąć " + product_name + "?");
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateProduct.this);
                myDB.deleteOneRowPurchases(id);
                finish();
            }
        });
        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}
