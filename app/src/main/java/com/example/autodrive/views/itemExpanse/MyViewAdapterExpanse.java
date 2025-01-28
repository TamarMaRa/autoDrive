package com.example.autodrive.views.itemExpanse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;

import java.util.List;

public class MyViewAdapterExpanse extends RecyclerView.Adapter<MyViewAdapterExpanse.MyViewHolder> {

    private Context context;
    private List<ExpenseItem> expenseList;

    public MyViewAdapterExpanse(Context context, List<ExpenseItem> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ExpenseItem expense = expenseList.get(position);
        holder.expenseAmountTextView.setText("Amount: ₪" + expense.getExpense());
        holder.expenseDateTextView.setText("" + expense.getDate());
        holder.expenseDescriptionTextView.setText("Description: " + expense.getDescription());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView expenseAmountTextView;
        TextView expenseDateTextView;
        TextView expenseDescriptionTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseAmountTextView = itemView.findViewById(R.id.tvExpenseAmount);
            expenseDateTextView = itemView.findViewById(R.id.tvExpenseDate);
            expenseDescriptionTextView = itemView.findViewById(R.id.tvExpenseDescription);
        }
    }
}

