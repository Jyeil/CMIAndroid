package com.univ.lorraine.cmi;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Livre;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    Integer[] imageIDs = {
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,

    };

    String[] titles = {
            "book1",
            "book2",
            "book3",
            "book4",
            "book5",
            "book6",
            "book7",
            "book8",
            "book9",
            "book10",
            "book11",
            "book12",
            "book13",
            "book14",
            "book15",


    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(this));
        try {
            testDatabase();
        } catch (SQLException e){
            Toast.makeText(getApplicationContext(), "bugbug", Toast.LENGTH_LONG).show();
        }
        // Création du dossier interne de l'app
        getApplicationContext().getDir("CallMeIshmael", Context.MODE_PRIVATE);

       testFilePicker();
    }

    private void testFilePicker() {
        // This always works
        Intent i = new Intent(getApplicationContext(), FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, 1);
    }

    private void testDatabase() throws SQLException{
        CmidbaOpenDatabaseHelper dbhelper = OpenHelperManager.getHelper(this, CmidbaOpenDatabaseHelper.class);

        Dao<Livre, Long> daolivre = dbhelper.getLivreDao();
        Date currentTime = new Date(System.currentTimeMillis());

        daolivre.create(new Livre(currentTime));

        List<Livre> ll = daolivre.queryForAll();
        Toast.makeText(getApplicationContext(), ll.get(0).toString(), Toast.LENGTH_LONG).show();
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c)
        {
            context = c;
        }


        @Override
        public int getCount() {
            return imageIDs.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView icon;
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.grid_item, parent, false);
            TextView label=(TextView)row.findViewById(R.id.icon_text);
            label.setText(titles[position]);
            icon=(ImageView)row.findViewById(R.id.icon_image);
            icon.setImageResource(imageIDs[position]);
            return row;
        }
    }
}

