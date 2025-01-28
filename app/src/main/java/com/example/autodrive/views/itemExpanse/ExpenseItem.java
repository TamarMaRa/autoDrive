package com.example.autodrive.views.itemExpanse;

public class ExpenseItem {
    int expense;
    String date;
    String description;

    public ExpenseItem(int expense, String date, String description) {
        this.expense = expense;
        this.date = date;
        this.description = description;
    }

    public int getExpense() {
        return expense;
    }
    public void setExpense(int expense) {
        this.expense = expense;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "[" + expense + ", " + date +
                ", " + description +
                ']';
    }
}
