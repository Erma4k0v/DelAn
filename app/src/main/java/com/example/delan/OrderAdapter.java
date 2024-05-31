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
        private final TextView orderDetailsTextView;
        private final Button orderActionButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDetailsTextView = itemView.findViewById(R.id.order_details_text_view);
            orderActionButton = itemView.findViewById(R.id.order_action_button);
        }

        public void bind(final Order order, final OnOrderActionClickListener listener) {
            orderDetailsTextView.setText(String.format("Заказ: %s\nАдрес клиента: %s\nСклад поставщика: %s",
                    order.getProductName(),
                    order.getCustomerAddress(),
                    order.getWarehouseAddress()));

            switch (order.getStatus()) {
                case "Заказано":
                    orderActionButton.setText("Принять заказ");
                    orderActionButton.setEnabled(true);
                    break;
                case "Товар принят":
                    orderActionButton.setText("Доставить заказ");
                    orderActionButton.setEnabled(true);
                    break;
                case "Доставлено":
                default:
                    orderActionButton.setText("Заказ выполнен");
                    orderActionButton.setEnabled(false);
                    break;
            }

            orderActionButton.setOnClickListener(v -> listener.onOrderActionClick(order));
        }
    }
}
