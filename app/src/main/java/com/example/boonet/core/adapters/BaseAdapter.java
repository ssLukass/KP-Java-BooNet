package com.example.boonet.core.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Базовый адаптер с поддержкой генериков
 * @param <T> тип данных элемента
 * @param <VH> тип ViewHolder
 * @param <L> тип слушателя кликов
 */
public abstract class BaseAdapter<T, VH extends BaseAdapter.BaseViewHolder<T>, L> 
    extends RecyclerView.Adapter<VH> {

    protected List<T> items;
    protected L clickListener;

    public BaseAdapter(List<T> items, L clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getLayoutId(), parent, false);
        return createViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = items.get(position);
        holder.bind(item, clickListener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<T> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // Абстрактные методы, которые должны быть реализованы в дочерних классах
    protected abstract int getLayoutId();
    protected abstract VH createViewHolder(View view);

    /**
     * Базовый класс для ViewHolder
     * @param <T> тип данных элемента
     */
    public static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bind(T item, Object listener);
    }
} 