package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.bignerdranch.android.criminalintent.CrimeDbSchema.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.CrimeDbSchema.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.CrimeDbSchema.CrimeDbSchema;

import static com.bignerdranch.android.criminalintent.CrimeDbSchema.CrimeDbSchema.CrimeTable.Cols.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

//    private ArrayList<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
//        mCrimes = new ArrayList<>();
        // 如果引用的是actibity的话, 由于activity在应用运行过程中并不一定存在, 而CrimeLab 是单例,存在于应用的整个生命周期,如果应用了activity的话
        // 就会导致acitivity一直引用, 无法回收,
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(context).getWritableDatabase();
    }


    public List<Crime> getCrimes() {
//        return mCrimes;
//        return new ArrayList<>();
        ArrayList list = new ArrayList();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        // 依次获取所有的数据
        try {
            cursor.moveToFirst();
            while (! cursor.isAfterLast()) { // 判断是否是最后一个
                list.add(cursor.getCrimes());
                cursor.moveToNext();
            }

        } finally {
//            别忘了调用Cursor的close()方法关 它。否则，后果很严重:轻则看到应用出错， 重则导致应用崩溃。
            cursor.close();
        }
        return list;
    }

    public Crime getCrime(UUID id) {

        CrimeCursorWrapper cursor = queryCrimes(CrimeDbSchema.CrimeTable.Cols.UUID + "= ?", new String[]{ id.toString() });
        if (cursor.getCount() == 0) {
            return null;
        }

        try {
            cursor.moveToFirst();
            return cursor.getCrimes();
        } finally {
            cursor.close();
        }
    }

    // 创建contentValue
    private static ContentValues getContentValue(Crime crime) {
//        ContentValues的键就是数据表字
        ContentValues values = new ContentValues();
        values.put(UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    // 升级数据
    public void  updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValue(crime);

        // 事实上，很多时 ，String本身会包含SQL代码。如果将它直接放入query语句中，这些代码 可能会改变query语句的含义，甚至会修改数据库资 。这实际就是SQL脚本注入。
        // 升级数据 : 参数 : 表名, values, 条件语句 ? 用于拼接参数, 参数
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,  CrimeDbSchema.CrimeTable.Cols.UUID + "= ?", new String[]{uuidString} );
    }

    // 添加数据, 向数据库插入一条数据
    public void addCrime(Crime c) {
//        mCrimes.add(c);
        ContentValues values = getContentValue(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }
//
//    private Cursor queryCrimes(String whereClause, String[] whereArgs) {
//        Cursor cursor = mDatabase.query(CrimeDbSchema.CrimeTable.NAME, null, whereClause, whereArgs,null, null, null);
//        return cursor;
//    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeDbSchema.CrimeTable.NAME, null, whereClause, whereArgs,null, null, null);

        CrimeCursorWrapper wrapper = new CrimeCursorWrapper(cursor);
        return wrapper;
    }

    public void delete(Crime c) {
        mDatabase.delete(CrimeDbSchema.CrimeTable.NAME, CrimeDbSchema.CrimeTable.Cols.UUID + "= ?", new String[]{c.getId().toString()});
    }

    public File getPhotoFile (Crime crime) {
        // 获取外部图片文件夹
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        // 获取图片文件地址
        return new File(externalFilesDir,crime.getPhotoFileName());
    }


}
