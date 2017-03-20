package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ира on 28.02.2017.
 */

public class VisitsListFragment extends Fragment implements OnStartDragListener {
    private OnFragmentInteractionListener mListener;
    private ItemTouchHelper mItemTouchHelper;
    public RecyclerView rvVisitList;
    private RecyclerView.LayoutManager mLayoutManager;
    private VisitsCursorRecyclerAdapter cursorAdapter;


    public VisitsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visits, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvVisitList = (RecyclerView) view.findViewById(R.id.visit_list);
        mLayoutManager=new LinearLayoutManager(getActivity());

        final DBHelper dbHelper = new DBHelper(getActivity());
        final Cursor cursor = dbHelper.getCursorForVisitList(mListener.onFragmentDataRequest());
        if (cursor!=null){
            cursorAdapter = new VisitsCursorRecyclerAdapter(cursor,dbHelper,this);
            rvVisitList.setAdapter(cursorAdapter);
        }

        rvVisitList.setHasFixedSize(true);
        rvVisitList.setLayoutManager(mLayoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(cursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvVisitList);
    }

    public void onCalDateChanged(String dateTimeStr){
        DBHelper dbHelper = new DBHelper(getActivity());
        cursorAdapter.swapCursor(dbHelper.getCursorForVisitList(dateTimeStr));
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentItemSelected(int position);
        String onFragmentDataRequest();
        //Context onFragmentContextRequest();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public long getItemIDByPosition(int position){
        long _id = cursorAdapter.getItemId(position);
        return _id;
    }

}
