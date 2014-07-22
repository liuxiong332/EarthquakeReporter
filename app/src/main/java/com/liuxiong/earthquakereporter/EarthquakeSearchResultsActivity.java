package com.liuxiong.earthquakereporter;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * Created by liuxi_000 on 2014/7/22.
 */
public class EarthquakeSearchResultsActivity extends ListActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                new String[] { EarthquakeProvider.KEY_SUMMARY },
                new int[] { android.R.id.text1 }, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
        parseIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(getIntent());
    }
    private static final String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";
    private void parseIntent(Intent intent) {
        if(intent.getAction() == Intent.ACTION_SEARCH) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);

            Bundle args = new Bundle();
            args.putString(QUERY_EXTRA_KEY, searchQuery);
            getLoaderManager().restartLoader(0, args, this);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String queryString = "";
        if(args!=null) {
            queryString = args.getString(QUERY_EXTRA_KEY);
        }

        String[] projection = { EarthquakeProvider.KEY_ID,
            EarthquakeProvider.KEY_SUMMARY };
        String where = EarthquakeProvider.KEY_SUMMARY + " LIKE \"%"
                + queryString + "%\"";
        return new CursorLoader(this, EarthquakeProvider.CONTENT_URI, projection,
                where, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
