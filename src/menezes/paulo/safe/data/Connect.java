package menezes.paulo.safe.data;

import java.net.MalformedURLException;

import android.content.Context;
import android.widget.Toast;

import menezes.paulo.safe.entity.User;
import menezes.paulo.safe.util.DiskLruImageCache;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

public class Connect {
	
	private static MobileServiceClient sClient;
    private static DiskLruImageCache sDiskCache;
	private static User sUser;
	
	public Connect(Context context) {
		if(sClient == null) {
			try {
				sClient = new MobileServiceClient(
					      "https://safe-app.azure-mobile.net/",
					      "eEFfdUsfZathVGggxVOkfaXFBqYhLX26",
					      context);
			} catch (MalformedURLException e) {
				Toast.makeText(context, "Houve um erro, tente novamente", Toast.LENGTH_LONG).show();
			}
		}
		
		if(Session.getInstance() == null)
			new Session(context);
		
		createCacheInstance(context);
	}

	public static MobileServiceClient getInstance() {
		return sClient;
	}
	
	public static User getUser() {
		return sUser;
	}
	
	public static void setUser(User s) {
		sUser = s;
	}
	
	public static void createCacheInstance(Context context) {
        sDiskCache = new DiskLruImageCache(context, "safe", 1024 * 1024 * 100, android.graphics.Bitmap.CompressFormat.PNG, 100);
    }

    public static DiskLruImageCache getCacheInstance() {
        return sDiskCache;
    }
}
