package com.ota.updates.db.helpers;
/*
 * Copyright (C) 2015 Matt Booth.
 *
 * Licensed under the Attribution-NonCommercial-ShareAlike 4.0 International
 * (the "License") you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ota.updates.items.VersionItem;

import java.util.ArrayList;

public class VersionSQLiteHelper extends BaseSQLiteHelper {

    public VersionSQLiteHelper(Context context) {
        super(context);
    }

    /**
     * Adds an VersionItem to the database
     * @param item  the VersionItem to be added
     */
    public void addVersion(VersionItem item) {
        ContentValues values = new ContentValues();
        values.put(NAME_ID, item.getId());
        values.put(NAME_FULL_NAME, item.getFullName());
        values.put(NAME_SLUG, item.getSlug());
        values.put(NAME_ANDROID_VERSION, item.getAndroidVersion());
        values.put(NAME_CHANGELOG, item.getChangelog());
        values.put(NAME_CREATED_AT, item.getCreatedAt());
        values.put(NAME_PUBLISHED_AT, item.getPublishedAt());
        values.put(NAME_DOWNLOADS, item.getDownloads());
        values.put(NAME_VERSION_NUMBER, item.getVersionNumber());
        values.put(NAME_FULL_ID, item.getFullUploadId());
        values.put(NAME_DELTA_ID, item.getDeltaUploadId());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insertWithOnConflict(VERSION_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /**
     * Get a single VersionItem from the database
     * @param id  the ID of the item to be retrieved
     * @return the selected VersionItem
     */
    public VersionItem getVersion(int id) {
        String query = "SELECT * FROM " + VERSION_TABLE_NAME + " WHERE " + NAME_ID + " =  \"" + id + "\"";
        return getVersionItem(query);
    }

    /**
     * Gets the lastest VersionItem in the database
     * @return the VersionItem that was requested
     */
    public VersionItem getLastVersionItem() {
        String query = "SELECT * FROM " + VERSION_TABLE_NAME + " ORDER BY " + NAME_ID + " DESC LIMIT 1";
        return getVersionItem(query);
    }

    /**
     * This should be the latest version
     * @return  The last version item's version number
     */
    public Integer getLastVersionNumber() {
        int versionNumber;
        String query = "SELECT " + NAME_VERSION_NUMBER + " FROM " + VERSION_TABLE_NAME + " ORDER BY " + NAME_ID + " DESC LIMIT 1";
        String versionString = getSingleVersionColumn(query);
        versionNumber = Integer.parseInt(versionString);
        return versionNumber;
    }

    /**
     * Runs a query on the Version table in the database
     * @param query  The query to be executed
     * @return The resulting VersionItem
     */
    @Nullable
    private VersionItem getVersionItem(String query) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        VersionItem versionItem = new VersionItem();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            versionItem = getVersionItemFromCursor(cursor);
            cursor.close();
        } else {
            versionItem = null;
        }
        db.close();
        return versionItem;
    }

    private String getSingleVersionColumn(String query) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        String columnItem = "";

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            columnItem = cursor.getString(0);
            cursor.close();
        }
        db.close();

        return columnItem;
    }

    public int getCountOfVersions() {
        String query = "SELECT COUNT(*) FROM " + VERSION_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        int result = 0;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            result =  Integer.parseInt(cursor.getString(0));
            cursor.close();
        }
        db.close();

        return result;
    }

    public ArrayList<VersionItem> getListOfVersions() {
        ArrayList<VersionItem> list = new ArrayList<>();

        Cursor cursor = getAllEntries(VERSION_TABLE_NAME);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                VersionItem versionItem = getVersionItemFromCursor(cursor);
                list.add(versionItem);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    @NonNull
    private VersionItem getVersionItemFromCursor(Cursor cursor) {
        VersionItem versionItem = new VersionItem();
        versionItem.setId(Integer.parseInt(cursor.getString(0)));
        versionItem.setFullName(cursor.getString(1));
        versionItem.setSlug(cursor.getString(2));
        versionItem.setAndroidVersion(cursor.getString(3));
        versionItem.setChangelog(cursor.getString(4));
        versionItem.setCreatedAt(cursor.getString(5));
        versionItem.setPublishedAt(cursor.getString(6));
        versionItem.setDownloads(Integer.parseInt(cursor.getString(7)));
        versionItem.setVersionNumber(Integer.parseInt(cursor.getString(8)));
        versionItem.setFullUploadId(Integer.parseInt(cursor.getString(9)));
        versionItem.setDeltaUploadId(Integer.parseInt(cursor.getString(10)));
        return versionItem;
    }
}
