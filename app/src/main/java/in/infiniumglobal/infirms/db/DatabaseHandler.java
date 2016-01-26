package in.infiniumglobal.infirms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "prosimity";
    // Database Name
    private static final String DATABASE_PATH = "/data/data/in.infiniumglobal.infirms/databases/";
    private static DatabaseHandler mInstance;
    private SQLiteDatabase db;

    private static final String TABLE_TBLA_User = "TBLA_User";
    private static final String TABLE_TBLR_RevenueType = "TBLR_RevenueType";
    private static final String TABLE_TBLR_RevenueRate = "TBLR_RevenueRate";
    private static final String TABLE_TBLR_Area = "TBLR_Area";
    private static final String TABLE_TBLR_Location = "TBLR_Location";
    private static final String TABLE_TBLR_RevenueCustomer = "TBLR_RevenueCustomer";
    private static final String TABLE_TBLR_RevenueReceipt = "TBLR_RevenueReceipt";
    private static final String TABLE_TBLR_Adjustment = "TBLR_Adjustment";

    //    TBLA_User
    public static final String KEY_USERID = "UserID";
    public static final String KEY_USERNAME = "UserName";
    public static final String KEY_PASSWORD = "UserPassword";

    //    TBLR_RevenueType
    public static final String KEY_REVENUE_ID = "_id";
    public static final String KEY_REVENUE_TYPEID = "RevenueTypeId";
    public static final String KEY_REVENUE_NAME = "RevenueName";
    public static final String KEY_REVENUE_TYPE = "ReceiptType";

    //TBLR_Area
    public static final String KEY_AREAID = "AreaId";
    public static final String KEY_AREANAME = "AreaName";

    //TBLR_Location
    public static final String KEY_LOCATIONID = "LocationId";
    public static final String KEY_LOCATIONNAME = "LocationName";

    //TBLR_RevenueType
    public static final String KEY_REVENUETYPEID = "RevenueTypeId";
    public static final String KEY_REVENUENAME = "RevenueName";
    public static final String KEY_RECEIPTTYPE = "ReceiptType";

    //    TBLR_RevenueRate
    public static final String KEY_REVENUERATEID = "RevenueRateId";
    public static final String KEY_REVENUEUNIT = "RevenueUnit";
    public static final String KEY_REVENUERATETYPE = "RevenueRateType";
    public static final String KEY_REVENUERATE = "RevenueRate";

    //    TBLR_RevenueCustomer
    public static final String KEY_RCUSTOMERID = "RCustomerId";
    public static final String KEY_CUSTOMERNO = "CustomerNo";
    public static final String KEY_CUSTOMERBARCODE = "CustomerBarcode";
    //    public static final String KEY_REVENUETYPEID = "RevenueTypeId";
    public static final String KEY_BUSINESSNAME = "BusinessName";
    public static final String KEY_OWNERNAME = "OwnerName";
    public static final String KEY_BUSINESSLICNO = "BusinessLicNo";
    public static final String KEY_TINNO = "TINNo";
    public static final String KEY_VNRNO = "VNRNo";
    public static final String KEY_OUTSTANDINGAMT = "OutstandingAmt";
    public static final String KEY_POSTALADDRESS = "PostalAddress";
    public static final String KEY_POSTCODE = "Postcode";
    public static final String KEY_CONTACTPERSON = "ContactPerson";
    public static final String KEY_CONTACTNO = "ContactNo";
    //    public static final String KEY_AREAID = "AreaId";
//    public static final String KEY_LOCATIONID = "LocationId";
    public static final String KEY_EMAIL = "Email";

    //    TBLR_RevenueReceipt
    public static final String KEY_RRECEIPTID = "RReceiptId";
    //    public static final String KEY_REVENUETYPEID = "RevenueTypeId";
//    public static final String KEY_RCUSTOMERID = "RCustomerId";
    public static final String KEY_CUSTOMERNAME = "CustomerName";
    public static final String KEY_RRECEIPTDATE = "RReceiptDate";
    public static final String KEY_RECEIPTNO = "ReceiptNo";
    public static final String KEY_RECEIPTBARCODE = "ReceiptBarcode";
    //    public static final String KEY_REVENUERATEID = "RevenueRateID";
//    public static final String KEY_REVENUERATE = "RevenueRate";
    public static final String KEY_TOTALUNIT = "TotalUnit";
    public static final String KEY_TOTALAMOUNT = "TotalAmount";
    public static final String KEY_OTHERCHARGSE = "OtherChargse";
    public static final String KEY_ADJUSTMENTAMT = "AdjustmentAmt";
    public static final String KEY_PAIDAMOUNT = "PaidAmount";
    public static final String KEY_PAYTYPE = "PayType";
    public static final String KEY_BANKNAME = "BankName";
    public static final String KEY_CHEQUENO = "ChequeNo";
    public static final String KEY_PAYREMARKS = "PayRemarks";
    public static final String KEY_CREATEDBY = "CreatedBy";
    public static final String KEY_CREATEDDATE = "CreatedDate";

    //    TBLR_Adjustment
    public static final String KEY_ADJUSTMENTID = "AdjustmentId";
    public static final String KEY_CUSTOMERID = "CustomerId";
    //    public static final String KEY_REVENUETYPEID = "RevenueTypeId";
    public static final String KEY_ADJUSTMENTDATE = "AdjustmentDate";
    public static final String KEY_ADJUSTMENTTYPE = "AdjustmentType";
    public static final String KEY_AMOUNT = "Amount";
    public static final String KEY_REMARKS = "Remarks";
//    public static final String KEY_CREATEDBY = "CreatedBy";
//    public static final String KEY_CREATEDDATE = "CreatedDate";

    // User Table Columns names
    public static final String KEY_ID = "_id";
    private Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static DatabaseHandler getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void copyDataBaseFromAsset() throws IOException {
        InputStream in = mContext.getAssets().open("rms.db");
        Log.e("sample", "Starting copying");
        String outputFileName = DATABASE_PATH + DATABASE_NAME;
        File databaseFile = new File(DATABASE_PATH);
        // check if databases folder exists, if not create one and its subfolders
        if (!databaseFile.exists()) {
            databaseFile.mkdir();

            OutputStream out = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            Log.e("sample", "Completed");
            out.flush();
            out.close();
            in.close();
        }

    }

    public void openDataBase() throws SQLException {
        String path = DATABASE_PATH + DATABASE_NAME;
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    public void closeDatabase() {
        db.close();
    }

    public int loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TBLA_User, null, KEY_USERNAME + "=? and " + KEY_PASSWORD + "=?", new String[]{username, password}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USERID));
        }
        return -1;
    }

    // Getting not sync user badges
    public Cursor getAreas() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TBLR_Area, null, null, null, null, null, null, null);
        return cursor;
    }

    // Getting not sync user badges
    public Cursor getLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TBLR_Location, null, null, null, null, null, null, null);
        return cursor;
    }

    // Getting not sync user badges
    public Cursor getLocationsByID(int locationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TBLR_Location, null, KEY_AREAID + "=?", new String[]{String.valueOf(locationID)}, null, null, null, null);
        return cursor;
    }

    // Getting not sync user badges
    public Cursor getRevenues() {
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_TBLR_RevenueType, null, null, null, null, null, null, null);
        String selectQuery = "SELECT  * FROM " + TABLE_TBLR_RevenueType;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public boolean deleteAllRecords(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("DELETE FROM SQLITE_SEQUENCE WHERE name='" + tableName + "'", null);
        if (cursor == null)
            return true;
        else if (cursor.getCount() == 0)
            return true;
        else
            return false;
    }

//    public Cursor searchCustomer(String TIN, String ReceiptNumber, String BusinessName, String CustomerName) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_TBLR_RevenueCustomer, null, KEY_TINNO + "=? or " + KEY_RCUSTOMERID + "=? or " + KEY_BUSINESSNAME+ "=? or " + KEY_CUSTOMERNAME + "=?", new String[]{TIN, ReceiptNumber, BusinessName, CustomerName}, null, null, null, null);
//        return cursor;
//    }

    public Cursor searchCustomer(String TIN, String ReceiptNumber, String BusinessName, String CustomerName, String RevenueTypeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "";

        sql = "Select * from " + TABLE_TBLR_RevenueCustomer + " WHERE " + KEY_REVENUETYPEID + "=" + RevenueTypeId;

        if (TIN.length() > 0) {
            sql = sql + " AND (" + KEY_TINNO + "='" + TIN + "')";
        }
        if (BusinessName.length() > 0) {
            sql = sql + " AND (" + KEY_BUSINESSNAME + " like '%" + BusinessName + "%')";
        }
        if (CustomerName.length() > 0) {
            sql = sql + " AND (" + KEY_OWNERNAME + " like '%" + CustomerName + "%')";
        }
        if (ReceiptNumber.length() > 0) {
            sql = sql + " AND (" + KEY_RCUSTOMERID + " in (SELECT " + KEY_RCUSTOMERID + " FROM " + TABLE_TBLR_RevenueReceipt + " WHERE " + KEY_REVENUETYPEID + " = " + RevenueTypeId + " AND ReceiptNo  = '" + ReceiptNumber + "' ))";
        }


        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }
/*
    // Limited data original

    public Cursor searchCustomer(String TIN, String ReceiptNumber, String BusinessName, String CustomerName, String RevenueTypeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "";

        sql = "Select " + KEY_RCUSTOMERID + "," + KEY_BUSINESSNAME + "," + KEY_OWNERNAME + " from " + TABLE_TBLR_RevenueCustomer + " WHERE " + KEY_REVENUETYPEID + "=" + RevenueTypeId;

        if (TIN.length() > 0) {
            sql = sql + " AND (" + KEY_TINNO + "='" + TIN + "')";
        }
        if (BusinessName.length() > 0) {
            sql = sql + " AND (" + KEY_BUSINESSNAME + " like '%" + BusinessName + "%')";
        }
        if (CustomerName.length() > 0) {
            sql = sql + " AND (" + KEY_OWNERNAME + " like '%" + CustomerName + "%')";
        }
        if (ReceiptNumber.length() > 0) {
            sql = sql + " AND (" + KEY_RCUSTOMERID + " in (SELECT " + KEY_RCUSTOMERID + " FROM " + TABLE_TBLR_RevenueReceipt + " WHERE " + KEY_REVENUETYPEID + " = " + RevenueTypeId + " AND ReceiptNo  = '" + ReceiptNumber + "' ))";
        }


        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }*/

/*
    public int getIfAvailable(String ruleId, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RULES, null, KEY_RULE_ID + "=? and " + KEY_TYPE + "=?", new String[]{ruleId, type}, null, null, KEY_ID + " DESC", null);
        return cursor.getCount();
    }

    // Getting not sync user badges
    public Cursor getRules() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RULES, null, null, null, null, null, KEY_ID + " DESC", null);
        return cursor;
    }

    public long addRules(ContentValues ruleValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(TABLE_RULES, null, ruleValues);
        return id;
    }

    public long updateRule(ContentValues values, String rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.update(TABLE_RULES, values, KEY_ID + " = ?", new String[]{rowId});
        return id;
    }

    public int deleteRule(String rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id = db.delete(TABLE_RULES, KEY_ID + " = ?", new String[]{rowId});
        return id;
    }
*/


//	public ArrayList<Badges> getUserBadgesList(String badgeid) {
//		ArrayList<Badges> badges = new ArrayList<Badges>();
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_BADGETABLE, null, KEY_BADGEID + "<=?", new String[] { badgeid }, null, null, null, null);
//		if (cursor != null && cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			do {
//				Badges badge = new Badges(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_TITLE)),
//						cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_IMAGEPATH)), cursor.getString(cursor
//								.getColumnIndex(DatabaseHandler.KEY_BADGEID)));
//				badges.add(badge);
//			} while (cursor.moveToNext());
//		}
//
//		return badges;
//
//	}
//
//	public ArrayList<Badges> getUserBadgesList(int distance) {
//		ArrayList<Badges> badges = new ArrayList<Badges>();
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db
//				.query(TABLE_BADGETABLE, null, KEY_GOAL + "<=?", new String[] { distance + "" }, null, null, null, null);
//		if (cursor != null && cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			do {
//				Badges badge = new Badges(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_TITLE)),
//						cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_IMAGEPATH)), cursor.getString(cursor
//								.getColumnIndex(DatabaseHandler.KEY_BADGEID)));
//				badges.add(badge);
//			} while (cursor.moveToNext());
//		}
//
//		return badges;
//
//	}
//
//	public Cursor getBadges() {
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_BADGETABLE, null, null, null, null, null, null);
//		return cursor;
//	}
//
//	public Cursor getAppliedBadge(String distance) {
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_BADGETABLE, null, KEY_GOAL + "<=?", new String[] { distance + "" }, null, null, KEY_GOAL
//				+ " DESC");
//		return cursor;
//	}
//
//	// Getting not sync settings
//	public Cursor getUnSyncedSettings() {
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_SETTING, null, KEY_ISSYNC + "=?", new String[] { "N" }, null, null, null, null);
//		return cursor;
//	}
//
//	public long addWorkout(ContentValues workoutValues) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		long id = db.insert(TABLE_RULES, null, workoutValues);
//		return id;
//	}
//
//	public long editWorkout(ContentValues workoutValues) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		long id = db.update(TABLE_RULES, workoutValues, KEY_WORKOUTID + " = ?",
//				new String[] { (String) workoutValues.get(KEY_WORKOUTID) });
//		return id;
//	}
//
//	public long addUserBadge(ContentValues userBadgeValues) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		long id = db.insert(TABLE_USERBADGE, null, userBadgeValues);
//		return id;
//	}

    // public void addAchivedBadges(String userid, String badgeid) {
    // SQLiteDatabase db = this.getWritableDatabase();
    //
    // ContentValues userBadgeValues = new ContentValues();
    // userBadgeValues.put(KEY_USERID, userid);
    //
    // Cursor cursor = db.query(TABLE_USERBADGE, null, KEY_USERID + " = ?", new String[] { userid }, null, null, null);
    // if (cursor != null && cursor.getCount() > 0) {
    // for (int i = 0; i < cursor.getCount(); i++) {
    // String bageId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_BADGEID));
    //
    // userBadgeValues.put(KEY_BADGEID, badgeid);
    // userBadgeValues.put(KEY_ISSYNC, "N");
    // db.insert(TABLE_USERBADGE, null, userBadgeValues);
    // cursor.moveToNext();
    // }
    //
    // } else {
    // int badge = Integer.parseInt(badgeid);
    // for (int i = 1; i <= badge; i++) {
    // userBadgeValues.put(KEY_BADGEID, badgeid);
    // userBadgeValues.put(KEY_ISSYNC, "N");
    // db.insert(TABLE_USERBADGE, null, userBadgeValues);
    // }
    // }
    //
    // }

//	public long addBadge(ContentValues badgeValues) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		long id = db.insert(TABLE_BADGETABLE, null, badgeValues);
//		return id;
//	}
//
//	public long updateBadge(ContentValues badgeValues, String badgeId) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		long id = db.update(TABLE_BADGETABLE, badgeValues, KEY_BADGEID + " = ?", new String[] { badgeId });
//		return id;
//	}
//
//	public int updateBadge(ContentValues badgeValues) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		int rowsAffected = db.update(TABLE_BADGETABLE, badgeValues, null, null);
//		return rowsAffected;
//	}
//
//	public long addSettings(String userid, String username, String firstname, String lastname, String email, String gender,
//			String dob, String country, String height, String weight, String distanceunit, String heightunit, String weightunit,
//			String imageurl, String imagepath, String goal, String activityViewedBy, String isSync, String view) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues settingsValues = new ContentValues();
//		settingsValues.put(KEY_USERID, userid);
//		settingsValues.put(KEY_USERNAME, username);
//		settingsValues.put(KEY_FIRSTNAME, firstname);
//		settingsValues.put(KEY_LASTNAME, lastname);
//		settingsValues.put(KEY_EMAIL, email);
//		settingsValues.put(KEY_GENDER, gender);
//		settingsValues.put(KEY_DOB, dob);
//		settingsValues.put(KEY_COUNTRY, country);
//		settingsValues.put(KEY_HEIGHT, height);
//		settingsValues.put(KEY_WEIGHT, weight);
//		settingsValues.put(KEY_DISTANCEUNIT, distanceunit);
//		settingsValues.put(KEY_HEIGHTUNIT, heightunit);
//		settingsValues.put(KEY_WEIGHTUNIT, weightunit);
//		if (!TextUtils.isEmpty(imageurl)) {
//			if (imageurl.startsWith("http"))
//				settingsValues.put(DatabaseHandler.KEY_IMAGEURL, imageurl);
//			else
//				settingsValues.put(DatabaseHandler.KEY_IMAGEURL, UserFunctions.SERVER + imageurl);
//		}
//		settingsValues.put(KEY_IMAGEPATH, imagepath);
//		settingsValues.put(KEY_GOAL, goal);
//		settingsValues.put(KEY_ACTIVITYVIEWEDBY, activityViewedBy);
//		settingsValues.put(KEY_ISSYNC, isSync);
//		settingsValues.put(KEY_ACTIVITYVIEWEDBY, view);
//		long id = db.insert(TABLE_SETTING, null, settingsValues);
//		return id;
//	}
//
//	public ArrayList<Workout> getWorkoutList(String userid) {
//		String workoutDate;
//		String workoutDistance;
//		String workoutTime;
//		String workoutCalories;
//		SQLiteDatabase db = this.getReadableDatabase();
//		int totalDistance = getTotalDistance(db, userid);
//		Cursor cursor = db.query(TABLE_RULES, null, KEY_USERID + "=?", new String[] { userid }, null, null, KEY_WORKOUTDATE
//				+ " DESC");
//		ArrayList<Workout> workoutList = new ArrayList<Workout>();
//		if (cursor != null && cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			do {
//				workoutDate = cursor.getString(cursor.getColumnIndex(KEY_WORKOUTDATE));
//				workoutDistance = cursor.getString(cursor.getColumnIndex(KEY_DISTANCE));
//				workoutTime = cursor.getString(cursor.getColumnIndex(KEY_TIME));
//				workoutCalories = cursor.getString(cursor.getColumnIndex(KEY_CALORIES));
//				String workoutLocation = cursor.getString(cursor.getColumnIndex(KEY_LOCATION));
//				String workoutImageUrl = cursor.getString(cursor.getColumnIndex(KEY_IMAGEURL));
//				String workoutImagePath = cursor.getString(cursor.getColumnIndex(KEY_IMAGEPATH));
//				String workoutNotes = cursor.getString(cursor.getColumnIndex(KEY_NOTES));
//				String workoutRate = cursor.getString(cursor.getColumnIndex(KEY_RATE));
//				String workoutId = cursor.getString(cursor.getColumnIndex(KEY_WORKOUTID));
//				Workout workoutObj = new Workout(workoutDate, workoutDistance, workoutTime, workoutCalories, workoutImageUrl,
//						workoutImagePath, totalDistance, workoutLocation, workoutNotes, workoutRate, workoutId);
//				workoutList.add(workoutObj);
//			} while (cursor.moveToNext());
//		}
//
//		return workoutList;
//	}
//
//	public int getTotalDistance(SQLiteDatabase db, String userid) {
//		if (db == null)
//			db = this.getWritableDatabase();
//		String[] columns = new String[] { "sum(" + KEY_DISTANCE + ")" };
//		Cursor sum = db.query(TABLE_RULES, columns, KEY_USERID + "=?", new String[] { userid }, null, null, null);
//		if (sum.moveToFirst()) {
//			return sum.getInt(0);
//		}
//		return 0;
//	}
//
//	public int getTotalWorkouts(String userid) {
//		db = this.getWritableDatabase();
//
//		Cursor sum = db.query(TABLE_RULES, null, KEY_USERID + "=?", new String[] { userid }, null, null, null);
//		if (sum != null && sum.moveToFirst()) {
//			return sum.getCount();
//		}
//		return 0;
//	}
//
//	public int getTotalTime(String userid) {
//		if (db == null)
//			db = this.getWritableDatabase();
//		String[] columns = new String[] { "sum(" + KEY_TIME + ")" };
//		Cursor sum = db.query(TABLE_RULES, columns, KEY_USERID + "=?", new String[] { userid }, null, null, null);
//		if (sum.moveToFirst()) {
//			return sum.getInt(0);
//		}
//
//		return 0;
//	}
//
//	public int getTotalCalories(String userid) {
//		if (db == null)
//			db = this.getWritableDatabase();
//		String[] columns = new String[] { "sum(" + KEY_CALORIES + ")" };
//		Cursor sum = db.query(TABLE_RULES, columns, KEY_USERID + "=?", new String[] { userid }, null, null, null);
//		if (sum.moveToFirst()) {
//			return sum.getInt(0);
//		}
//
//		return 0;
//	}
//
//	public long updateSettings(ContentValues settingsValues, String userid) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		long id = db.update(TABLE_SETTING, settingsValues, KEY_USERID + " = ?", new String[] { userid });
//		return id;
//	}
//
//	public long updateWorkout(ContentValues workoutValues, String userid, String workoutdate) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		long id = db.update(TABLE_RULES, workoutValues, KEY_USERID + " = ?" + " AND " + KEY_WORKOUTDATE + " = ?",
//				new String[] { userid, workoutdate });
//		return id;
//	}
//
//	public Cursor getSettings(String userid) {
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_SETTING, null, KEY_USERID + " = ?", new String[] { userid }, null, null, null);
//		return cursor;
//	}
//
//	public boolean checkBadge(String userid, String bageId) {
//		Cursor getcursor = db.query(TABLE_USERBADGE, null, null, null, null, null, null);
//		if (getcursor != null && getcursor.getCount() == 0) {
//			return true;
//		}
//
//		Cursor cursor = db.query(TABLE_USERBADGE, null, KEY_USERID + " = ?" + " AND " + KEY_BADGEID + " = ?", new String[] {
//				userid, bageId }, null, null, null);
//		if (cursor.getCount() > 0)
//			return false;
//		else
//			return true;
//	}
//
//	public Cursor getGraphData(String userid, String startDay, String endDay, int selected, int type) {
//		String rawQuery = "";
//		String function = "";
//		String key = "";
//
//		switch (type) {
//		case 1:
//			function = "%w";
//			break;
//		case 2:
//			function = "%d";
//			break;
//		case 3:
//			function = "%m";
//			break;
//
//		default:
//			break;
//		}
//		switch (selected) {
//		case 1:
//			key = KEY_DISTANCE;
//			break;
//		case 2:
//			key = KEY_TIME;
//			break;
//		case 3:
//			key = KEY_CALORIES;
//			break;
//		default:
//			break;
//		}
//
//		rawQuery = "SELECT SUM(" + key + ") as total,strftime('" + function + "'," + KEY_WORKOUTDATE
//				+ ",'unixepoch') as wday FROM " + TABLE_RULES + " where " + KEY_WORKOUTDATE + " >=" + startDay + " AND "
//				+ KEY_WORKOUTDATE + " <=" + endDay + " AND " + KEY_USERID + " = " + userid + " group by wday";
//
//		return db.rawQuery(rawQuery, null);
//	}
}
