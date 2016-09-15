package app.server.com;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.server.com.provider.ServerProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private TextView sureView;
    private TextView queryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.input_name);
        sureView = (TextView) findViewById(R.id.sure);
        queryView = (TextView) findViewById(R.id.query);
        sureView.setOnClickListener(this);
        queryView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.query) {
            StringBuilder retValue = new StringBuilder();
            String sortOrder = "id";//可以尝试"name"
            Cursor cursor = getContentResolver().query(ServerProvider.CONTENT_URI, null, "", null, sortOrder);
            if (cursor == null) return;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String content = cursor.getString(cursor.getColumnIndex(ServerProvider.KEY_FIELD_NAME));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                retValue.append(String.valueOf(id)).append(" ").append(content).append("\n");
                cursor.moveToNext();
            }
            Toast.makeText(getBaseContext(), retValue, Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.sure) {
            ContentValues values = new ContentValues();
            values.put(ServerProvider.KEY_FIELD_NAME, editText.getText().toString().trim());
            Uri uri = getContentResolver().insert(ServerProvider.CONTENT_URI, values);
            if (uri != null) {
                Toast.makeText(getBaseContext(), "成功插入一条纪录 -> " + uri.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
