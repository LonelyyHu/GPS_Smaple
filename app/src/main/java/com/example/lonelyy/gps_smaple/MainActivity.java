package com.example.lonelyy.gps_smaple;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lonelyy.gps_smaple.db.DBHelper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements LocationListener {

    private boolean getService = false;
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;    //最佳資訊提供者

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ListView listView = null;
    private Cursor maincursor; // 記錄目前資料庫查詢指標
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        Log.println(Log.ERROR, "Main-test-run", "Main-test-run");

        //取得系統定位服務
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            getService = true;    //確認開啟定位服務
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面
        }

        refreshListView();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (getService) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lms.requestLocationUpdates(bestProvider, 1000, 1, this);
            //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (getService) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lms.removeUpdates(this);    //離開頁面時停止更新
        }
    }

    @Override
    public void onLocationChanged(Location location) {    //當地點改變時
        getLocation(location);

    }

    @Override
    public void onProviderDisabled(String arg0) {    //當GPS或網路定位功能關閉時
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {    //當GPS或網路定位功能開啟
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    //定位狀態改變
        //status=OUT_OF_SERVICE 供應商停止服務
        //status=TEMPORARILY_UNAVAILABLE 供應商暫停服務
    }

    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);    //取得系統定位服務
        Criteria criteria = new Criteria();    //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lms.getLastKnownLocation(bestProvider);
        getLocation(location);
    }

    private void getLocation(Location location) {	//將定位資訊顯示在畫面中
        if(location != null) {
            TextView longitude_txt = (TextView) findViewById(R.id.longitude);
            TextView latitude_txt = (TextView) findViewById(R.id.latitude);

            Double longitude = location.getLongitude();	//取得經度
            Double latitude = location.getLatitude();	//取得緯度

            longitude_txt.setText(String.valueOf(longitude));
            latitude_txt.setText(String.valueOf(latitude));

            // 將記錄新增到coffee資料表的參數
            ContentValues cv = new ContentValues();
            cv.put("lon", longitude);
            cv.put("lat", latitude);
            cv.put("trip_id", 1);

            // 執行SQL語句
            long id = db.insert("gps_records", null, cv);

            refreshListView();
        }
        else {
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }

    // 重新整理ListView（將資料重新匯入）
    private void refreshListView() {
        if (maincursor == null) {
            // 1.取得查詢所有資料的cursor
            maincursor = db.rawQuery(
                    "SELECT id as _id, trip_id, lon, lat, created_time FROM gps_records order by id desc", null);
            // 2.設定ListAdapter適配器(使用SimpleCursorAdapter)
            adapter = new SimpleCursorAdapter(this, R.layout.row,
                    maincursor,
                    new String[] { "_id", "trip_id", "lon", "lat", "created_time" },
                    new int[] { R.id.itemId, R.id.tripId, R.id.lon,  R.id.lat,  R.id.createTime},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            // 3.注入適配器
            listView.setAdapter(adapter);
        } else {
            if (maincursor.isClosed()) { // 彌補requery()不會檢查cursor closed的問題
                maincursor = null;
                refreshListView();
            } else {
                maincursor.requery(); // 若資料龐大不建議使用此法（應改用 CursorLoader）
                adapter.changeCursor(maincursor);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void onClearButtonClick(View view){
        db.delete("gps_records", "id > 0", null);
        refreshListView();
    }
}
