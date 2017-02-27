package com.example.myapplication;

/**
 * Created by Ира on 27.02.2017.
 */
import android.support.v7.widget.RecyclerView;

/**
 * Listener for manual initiation of a drag.
 */
public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(ClientsCursorRecyclerAdapter.ViewHolder viewHolder);

}