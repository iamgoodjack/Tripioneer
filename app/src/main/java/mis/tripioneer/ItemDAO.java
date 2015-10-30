package mis.tripioneer;

/**
 * Created by user on 2015/10/29.
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// 資料功能類別
public class ItemDAO {
    // 表格名稱
    public static final String TABLE_NAME = "Collect_Trip";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String TITLE_COLUMN = "title";
    public static final String TRIP_ID_COLUMN = "trip_id";
    public static final String SPOT_ID_COLUMN = "trip_spot_id";
    public static final String SPOT_NAME_COLUMN = "trip_spot_name";
    public static final String SPOT_ORDER_COLUMN = "trip_spot_order";
    public static final String SPOT_STAY_TIME_COLUMN = "trip_spot_stay_time";
    public static final String SPOT_PIC_COLUMN = "trip_spot_pic";
    public static final String TRIP_TOTAL_TIME_COLUMN = "ttltime";
    public static final String DATETIME_COLUMN = "datetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRIP_ID_COLUMN+ " TEXT, " +
                    SPOT_ID_COLUMN + " TEXT, " +
                    SPOT_NAME_COLUMN + " TEXT, " +
                    TITLE_COLUMN + " TEXT, " +
                    SPOT_ORDER_COLUMN + " INTEGER, " +
                    SPOT_STAY_TIME_COLUMN + " TEXT, " +
                    SPOT_PIC_COLUMN + " TEXT, " +
                    TRIP_TOTAL_TIME_COLUMN + " INTEGER, " +
                    DATETIME_COLUMN + " INTEGER)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public ItemDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public Item insert(Item item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(TRIP_ID_COLUMN, item.getTripid());
        cv.put(SPOT_ID_COLUMN, item.getSpotid());
        cv.put(SPOT_NAME_COLUMN, item.getSpotname());
        cv.put(TITLE_COLUMN, item.getTitle());
        cv.put(SPOT_ORDER_COLUMN, item.getSpotorder());
        cv.put(SPOT_STAY_TIME_COLUMN, item.getSpottime());
        cv.put(SPOT_PIC_COLUMN, item.getSpotpic());
        cv.put(TRIP_TOTAL_TIME_COLUMN, item.getTtltime());
        cv.put(DATETIME_COLUMN, item.getDate());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        item.setID(id);
        // 回傳結果
        return item;
    }

    // 修改參數指定的物件
    public boolean update(Item item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(TRIP_ID_COLUMN, item.getTripid());
        cv.put(SPOT_ID_COLUMN, item.getSpotid());
        cv.put(SPOT_NAME_COLUMN, item.getSpotname());
        cv.put(TITLE_COLUMN, item.getTitle());
        cv.put(SPOT_ORDER_COLUMN, item.getSpotorder());
        cv.put(SPOT_STAY_TIME_COLUMN, item.getSpottime());
        cv.put(SPOT_PIC_COLUMN, item.getSpotpic());
        cv.put(TRIP_TOTAL_TIME_COLUMN, item.getTtltime());
        cv.put(DATETIME_COLUMN, item.getDate());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + item.getID();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    // 讀取所有記事資料
    public List<Item> getAll() {
        List<Item> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public Item get(long id) {
        // 準備回傳結果用的物件
        Item item = null;
        // 使用編號為查詢條件
        String where = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }

    // 把Cursor目前的資料包裝為物件
    public Item getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Item result = new Item();

        result.setID(cursor.getLong(0));
        result.setTripid(cursor.getString(1));
        result.setSpotid(cursor.getString(2));
        result.setSpotname(cursor.getString(3));
        result.setTitle(cursor.getString(4));
        result.setSpotorder(cursor.getInt(5));
        result.setSpottime(cursor.getString(6));
        result.setSpotpic(cursor.getString(7));
        result.setTtltime(cursor.getInt(8));
        result.setDate(cursor.getString(9));
        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }
    //Item(String tripid, String spotid, String spotname, String title, int spotorder, String spottime, String spotpic, int ttltime, long date)
    // 建立範例資料
    public void sample()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        String now = formatter.format(new Date());

        Item item = new Item("1", "1", "行天宮", "9025路線", 1, "1", "", 4, now);
        Item item2 = new Item("1", "2", "松山機場" , "9025路線", 2 ,"1", "", 4, now);
        Item item3 = new Item("1", "3","佑民醫院" , "9025路線", 3, "1", "", 4, now);
        Item item4 = new Item("1", "4","中央大學", "9025路線", 4, "1", "", 4, now);

        insert(item);
        insert(item2);
        insert(item3);
        insert(item4);
    }

}
