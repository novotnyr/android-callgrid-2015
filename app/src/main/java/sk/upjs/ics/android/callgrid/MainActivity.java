package sk.upjs.ics.android.callgrid;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    public static final int LOADER_ID = 1;
    public static final Bundle NO_BUNDLE = null;
    public static final Cursor NO_CURSOR = null;
    public static final int NO_FLAGS = 0;

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoaderManager().initLoader(LOADER_ID, NO_BUNDLE, this);

        String[] from = { CallLog.Calls.NUMBER };
        int[] to = { R.id.grid_item_text };
        adapter = new SimpleCursorAdapter(this, R.layout.grid_item, NO_CURSOR, from, to, NO_FLAGS);

        adapter.setViewBinder(new CallLogViewBinder());

        GridView callLogGridView = (GridView) findViewById(R.id.callLogGridView);
        callLogGridView.setAdapter(adapter);

        callLogGridView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ID) {
            CursorLoader loader = new CursorLoader(this);
            loader.setUri(CallLog.Calls.CONTENT_URI);
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(NO_CURSOR);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                "_id = ?",
                null,
                null);
        if(cursor.moveToNext()) {
            String number
                = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));


            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(WebView.SCHEME_TEL + number));
            startActivity(callIntent);
        }
        cursor.close();
    }
}
