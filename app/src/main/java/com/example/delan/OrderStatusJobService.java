package com.example.delan;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class OrderStatusJobService extends JobService {
    private static final String TAG = "OrderStatusJobService";
    private FirebaseFirestore db;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        db = FirebaseFirestore.getInstance();
        checkOrderStatus(params);
        return true;
    }

    private void checkOrderStatus(JobParameters params) {
        db.collection("orders")
                .whereNotEqualTo("notified", 1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String status = document.getString("status");
                            String orderId = document.getId();
                            String productName = document.getString("productName");

                            if ("Получено".equals(status)) {
                                Intent intent = new Intent(this, OrderStatusReceiver.class);
                                intent.putExtra("status", status);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("productName", productName);
                                Log.d(TAG, orderId);
                                sendBroadcast(intent);
                                db.collection("orders").document(orderId)
                                        .update("notified", 1)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Уведомление доставлено", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    }
                    jobFinished(params, false);
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderStatusJobService", "Ошибка при получении статуса заказа", e);
                    jobFinished(params, true);
                });
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job stopped");
        return true;
    }
}
