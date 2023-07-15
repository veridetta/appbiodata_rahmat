package sultanbisri.blogspot.com.appbiodata;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    DBHelper helper;
    LayoutInflater inflater;
    View dialogView;
    TextView Tv_Nomor, Tv_Nama, Tv_TempatLahir, Tv_JK, Tv_Tanggal, Tv_Blog;
    Button  wvButton;
    FloatingActionButton addDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        wvButton = findViewById(R.id.wvButton);
        addDataButton  = findViewById(R.id.fab);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });

        helper = new DBHelper(this);
        listView = (ListView)findViewById(R.id.list_data);
        listView.setOnItemClickListener(this);

        // web view
        wvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka WebviewActivity
                Intent intent = new Intent(MainActivity.this, webAct.class);
                startActivity(intent);
            }
        });
        // web view akhir
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

    public void setListView(){
        Cursor cursor = helper.allData();
        CustomCursorAdapter customCursorAdapter = new CustomCursorAdapter(this, cursor, 1);
        listView.setAdapter(customCursorAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long x) {
        TextView getId = (TextView)view.findViewById(R.id.listID);
        final long id = Long.parseLong(getId.getText().toString());
        final Cursor cur = helper.oneData(id);
        cur.moveToFirst();

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pilih Opsi");

        //Add a list
        String[] options = {"Lihat Data", "Edit Data", "Hapus Data"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        final AlertDialog.Builder viewData = new AlertDialog.Builder(MainActivity.this);
                        inflater = getLayoutInflater();
                        dialogView = inflater.inflate(R.layout.view_data, null);
                        viewData.setView(dialogView);
                        viewData.setTitle("Lihat Data");

                        Tv_Nomor = (TextView)dialogView.findViewById(R.id.tv_No);
                        Tv_Nama = (TextView)dialogView.findViewById(R.id.tv_Nama);
                        Tv_TempatLahir = (TextView)dialogView.findViewById(R.id.tv_TempatLahir);
                        Tv_Tanggal = (TextView)dialogView.findViewById(R.id.tv_Tanggal);
                        Tv_JK = (TextView)dialogView.findViewById(R.id.tv_JK);
                        Tv_Blog = (TextView)dialogView.findViewById(R.id.tv_Alamat);

                        Tv_Nomor.setText("NRP: " + cur.getString(cur.getColumnIndexOrThrow(DBHelper.row_nomor)));
                        Tv_Nama.setText("Nama: " + cur.getString(cur.getColumnIndexOrThrow(DBHelper.row_nama)));
                        Tv_TempatLahir.setText("Tempat Lahir: " + cur.getString(cur.getColumnIndexOrThrow(DBHelper.row_tempatLahir)));
                        Tv_Tanggal.setText("Tanggal Lahir: " + cur.getString(cur.getColumnIndexOrThrow(DBHelper.row_tglLahir)));
                        Tv_JK.setText("Kelas: " + cur.getString(cur.getColumnIndexOrThrow(DBHelper.row_kelas)));
                        Tv_Blog.setText("Blog: " + cur.getString(cur.getColumnIndexOrThrow(DBHelper.row_blog)));

                        viewData.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        viewData.show();
                }
                switch (which){
                    case 1:
                        Intent iddata = new Intent(MainActivity.this, EditActivity.class);
                        iddata.putExtra(DBHelper.row_id, id);
                        startActivity(iddata);
                }
                switch (which){
                    case 2:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setMessage("Data ini akan dihapus.");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                helper.deleteData(id);
                                Toast.makeText(MainActivity.this, "Data Terhapus", Toast.LENGTH_SHORT).show();
                                setListView();
                            }
                        });
                        builder1.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder1.create();
                        alertDialog.show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListView();
    }
}
