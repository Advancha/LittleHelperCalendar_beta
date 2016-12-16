package com.example.myapplication;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

/**
 * Created by Ира on 14.12.2016.
 */

public class CaldroidCustomFragment extends CaldroidFragment {
    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        // TODO Auto-generated method stub
        return new CaldroidCustomAdapter(getActivity(), month, year,
                getCaldroidData(), extraData);
    }
}
