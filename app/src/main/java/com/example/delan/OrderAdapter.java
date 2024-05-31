package com.example.delan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final Context context;
    private final List<Order> orderList;
    private final OnOrderActionClickListener listener;

    public interface OnOrderActionClickListener {
        void onOrderActionClick(Order order);
    }

    public OrderAdapter(Context context, List<Order> orderList, OnOrderActionClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDetailsTextView;
        Button orderActionButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDetailsTextView = itemView.findViewById(R.id.order_details_text_view);
            orderActionButton = itemView.findViewById(R.id.order_action_button);
        }

        public void bind(final Order order, final OnOrderActionClickListener listener) {
            orderDetailsTextView.setText("Заказ: " + order.getProductName() +
                    "\nАдрес клиента: " + order.getCustomerAddress() +
                    "\nСклад поставщика: " + order.getWarehouseAddress());

            if ("Заказано".equals(order.getStatus())) {
                orderActionButton.setText("Принять заказ");
            } else if ("Товар принят".equals(order.getStatus())) {
                orderActionButton.setText("Доставить заказ");
            } else {
                orderActionButton.setText("Заказ выполнен");
                orderActionButton.setEnabled(false);
            }

            orderActionButton.setOnClickListener(v -> listener.onOrderActionClick(order));
        }
    }
}
