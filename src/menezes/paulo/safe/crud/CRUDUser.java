package menezes.paulo.safe.crud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import menezes.paulo.safe.entity.User;
import menezes.paulo.safe.sqlite.SQLiteHelper;

public class CRUDUser extends SQLiteHelper {

	// Books table name
    private static final String TABLE = "user";
 
    // Books Table Columns names
    private static final String KEY_ID = "id";
 	
    public CRUDUser(Context context) {
		super(context);
	}
	
    public void addUser(User user){
        Log.d("addBook", user.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("idFacebook", user.idFacebook); 
        values.put("idGoogle", user.idGoogle); 
        values.put("firstname", user.firstname); 
        values.put("lastname", user.lastname); 
        values.put("email", user.email); 
        values.put("biografy", user.biografy); 
        values.put("photo", user.photo); 
        values.put("password", user.password); 
        values.put("gender", user.gender); 
        values.put("phone", user.phone); 
 
        // 3. insert
        db.insert(TABLE, null, values);
 
        // 4. close
        db.close(); 
    }
 
    public User getUser(int id){
    	if(countAllUsers() > 0) {
	        // 1. get reference to readable DB
	        SQLiteDatabase db = this.getReadableDatabase();
	 
	        // 2. build query
	        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE + " WHERE id = " + id, null);
	 
	        // 3. if we got results get the first one
	        if (cursor != null)
	            cursor.moveToFirst();
	        
	        if(cursor.getCount() > 0) {
		        // 4. build book object
		        User user = new User();
		        user.id = cursor.getInt(0);
		        user.idGoogle = cursor.getString(1);
		        user.idFacebook = cursor.getString(2);
		        user.firstname = cursor.getString(3);
		        user.lastname = cursor.getString(4);
		        user.email = cursor.getString(5);
		        user.biografy = cursor.getString(6);
		        user.photo = cursor.getString(7);
		        user.password = cursor.getString(8);
		        user.gender = cursor.getString(9);
		        user.phone = cursor.getString(10);
		        
		        return user;
	        } else {
	        	return null;
	        }
	 	} else {
    		return null;
    	}
    }
 
    // Get All Books
    /*public List<User> getAllUsers() {
        List<User> books = new LinkedList<User>();
 
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE;
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build book and add it to list
        User user = null;
        if (cursor.moveToFirst()) {
            do {
                user = new Book();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setTitle(cursor.getString(1));
                user.setAuthor(cursor.getString(2));
 
                // Add book to books
                books.add(user);
            } while (cursor.moveToNext());
        }
 
        Log.d("getAllBooks()", books.toString());
 
        // return books
        return books;
    }*/
    
    public int countAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE, null);
   
        return cursor.getCount();
    }
 
     // Updating single book
    public int updateUser(User user) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("idFacebook", user.idFacebook); 
        values.put("idGoogle", user.idGoogle); 
        values.put("firstname", user.firstname); 
        values.put("lastname", user.lastname); 
        values.put("email", user.email); 
        values.put("biografy", user.biografy); 
        values.put("photo", user.photo); 
        values.put("password", user.password); 
        values.put("gender", user.gender); 
        values.put("phone", user.phone); 
        
        // 3. updating row
        int i = db.update(TABLE, values, KEY_ID + " = ?", new String[] { String.valueOf(user.id) });
 
        // 4. close
        db.close();
 
        return i;
 
    }
 
    /*// Deleting single book
    public void deleteBook(Book book) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE,
                KEY_ID+" = ?",
                new String[] { String.valueOf(book.getId()) });
 
        // 3. close
        db.close();
 
        Log.d("deleteBook", book.toString());
    }*/
}
