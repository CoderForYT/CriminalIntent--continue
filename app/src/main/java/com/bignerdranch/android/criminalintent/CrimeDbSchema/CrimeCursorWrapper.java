package com.bignerdranch.android.criminalintent.CrimeDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import com.bignerdranch.android.criminalintent.Crime;
import com.bignerdranch.android.criminalintent.CrimeDbSchema.CrimeDbSchema.CrimeTable;

/**
 * Created by zkhk on 2016/11/22.
 */
// 用于获取数据库数据的辅助类

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper (Cursor cursor) {
        super(cursor);
    }

    public Crime getCrimes() {
        String uuidSting = getString(getColumnIndex(CrimeTable.Cols.UUID));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString( uuidSting));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        return crime;
    }


}
