package app.user.com.userapp;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Button retrieve;
    private TextView displayContent;
    public static final String NAMES_URL = "content://app.server.com.provider.ServerProvider/names";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrieve = (Button) findViewById(R.id.retrieve);
        displayContent = (TextView) findViewById(R.id.result_area);
        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //触发查询操作
                getSupportLoaderManager().initLoader(1, null, MainActivity.this);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(NAMES_URL), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        StringBuilder res = new StringBuilder();
        while (!cursor.isAfterLast()) {
            res.append("\n" + cursor.getString(cursor.getColumnIndex("id")) + "-" + cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        displayContent.setText(res);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
