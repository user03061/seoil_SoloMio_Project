package com.example.commuuu;

import DB.items;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class PaymentActivity extends AppCompatActivity {

    private Spinner spinnerPg;
    private Spinner spinnerMethod;
    private TextView editPrice;
    private TextView editNonTax;
    private DatabaseReference mDatabase;
    private List<items> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bootpay_set_activity);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        spinnerPg = findViewById(R.id.spinner_pg);
        spinnerMethod = findViewById(R.id.spinner_method);
        editPrice = findViewById(R.id.edit_price);
        editNonTax = findViewById(R.id.edit_non_tax);

        // Fetch items and update editPrice
        fetchItemsAndUpdatePrice();

        // Set up button listeners
        Button paymentButton = findViewById(R.id.payment_button);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePayment();
            }
        });
    }

    private void fetchItemsAndUpdatePrice() {
        mDatabase.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemsList.clear();
                double totalPrice = 0.0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    items item = snapshot.getValue(items.class);
                    if (item != null) {
                        Log.d("MainActivity", "Item fetched: " + item.itemName + ", Price: " + item.itemPrice);
                        itemsList.add(item);
                        totalPrice += item.itemPrice; // Accumulate total price
                    }
                }

                // Set the total price to editPrice TextView
                editPrice.setText(String.format("%.2f", totalPrice));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "loadItems:onCancelled", databaseError.toException());
                Toast.makeText(PaymentActivity.this, "Failed to load items: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initiatePayment() {
        try {
            String pg = spinnerPg.getSelectedItem().toString();
            String method = spinnerMethod.getSelectedItem().toString();
            double price = Double.parseDouble(editPrice.getText().toString());
            double nonTax = Double.parseDouble(editNonTax.getText().toString());

            Log.d("MainActivity", "Selected PG: " + pg + ", Method: " + method);
            Log.d("MainActivity", "Price: " + price);

            BootUser user = new BootUser().setPhone("010-1234-5678");
            BootExtra extra = new BootExtra().setCardQuota("0,2,3");

            List<BootItem> bootItems = new ArrayList<>();
            StringBuilder titleSet = new StringBuilder();
            for (items item : itemsList) {
                BootItem bootItem = new BootItem()
                        .setName(item.itemName)
                        .setId(item.itemId)
                        .setQty(item.Qty)
                        .setPrice(item.itemPrice);
                bootItems.add(bootItem);
                if (titleSet.length() > 0) {
                    titleSet.append(", ");
                }
                titleSet.append(item.itemName); // Collect names for order name
            }

            // Create and configure the payload for payment
            Payload payload = new Payload();
            payload.setApplicationId("5b8f6a4d396fa665fdc2b5e8")
                    .setOrderName(titleSet.toString()) // Use combined item names for order name
                    .setPg(pg)
                    .setMethod(method)
                    .setOrderId("1234")
                    .setPrice(price)
                    .setUser(user)
                    .setExtra(extra)
                    .setItems(bootItems);

            // Optional metadata
            Map<String, Object> map = new HashMap<>();
            map.put("1", "abcdef");
            map.put("2", "abcdef55");
            map.put("3", 1234);
            payload.setMetadata(map);

            // Initialize and request payment
            Bootpay.init(getSupportFragmentManager(), getApplicationContext())
                    .setPayload(payload)
                    .setEventListener(new BootpayEventListener() {
                        @Override
                        public void onCancel(String data) {
                            Log.d("bootpay", "cancel: " + data);
                        }

                        @Override
                        public void onError(String data) {
                            Log.d("bootpay", "error: " + data);
                        }


                        public void onClose() {
                            Log.d("bootpay", "close");
                            Bootpay.removePaymentWindow();

                            Intent intent = new Intent(PaymentActivity.this, OrderDetailActivity.class);
                            startActivity(intent);

                            // 이 Activity를 종료하려면 finish() 호출
                            finish();
                        }

                        @Override
                        public void onIssued(String data) {
                            Log.d("bootpay", "issued: " + data);
                        }

                        @Override
                        public boolean onConfirm(String data) {
                            Log.d("bootpay", "confirm: " + data);
                            return true;
                        }

                        @Override
                        public void onDone(String data) {
                            Log.d("done", data);
                        }
                    }).requestPayment();
        } catch (Exception e) {
            Log.e("MainActivity", "Payment initiation failed", e);
            Toast.makeText(PaymentActivity.this, "Payment initiation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
