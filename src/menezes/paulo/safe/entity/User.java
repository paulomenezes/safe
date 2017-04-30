package menezes.paulo.safe.entity;

import java.io.IOException;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import menezes.paulo.safe.task.ImageDownloaderTask;
import menezes.paulo.safe.util.JsonReader;
import com.google.android.gms.plus.model.people.Person;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int id;
	public String idFacebook;
	public String idGoogle;
	public String firstname;
	public String lastname;
	public String email;
	public String biografy;
	public String photo;
	public String password;
	public String gender;
	public String phone;
	
	public User() {}
	
	public User(String firstName, String lastName, String email, String password) {
		this.firstname = firstName;
		this.lastname = lastName;
		this.email = email;
		this.password = password;
	}

	public void setGoogleUser(Person user) {
		this.idGoogle = user.getId();
		this.firstname = isNull(user.getName().getGivenName());
		this.lastname = isNull(user.getName().getFamilyName());
		this.biografy = isNull(user.getAboutMe());
		this.photo = isNull(user.getImage().getUrl());
		this.gender = user.getGender() == 0 ? "Masculino" : user.getGender()== 1 ? "Feminino" : "";
	}
	
	public String isNull(String value) {
		if(value == null)
			return "";
		else
			return value;
	}
	
	public String getName() {
		return firstname + " " + lastname;
	}
	
	public void getPhoto(ImageView imageView, TextView photoName) {
		if(idFacebook != null) {
            JSONObject cover = null;
            try {
                cover = JsonReader.readJsonFromUrl("https://graph.facebook.com/" + idFacebook + "/picture?redirect=0&width=200");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "";
            if (cover != null) {
                try {
                    JSONObject cover2 = (JSONObject) cover.get("data");
                    url = cover2.getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        	new ImageDownloaderTask(imageView).execute(url);
        	if(photoName != null)
        		photoName.setVisibility(View.GONE);
		} else if(idGoogle != null && photo.substring(0, 4).equals("http")) {
        	new ImageDownloaderTask(imageView).execute(photo.replace("sz=50", "sz=200"));
        	if(photoName != null)
        		photoName.setVisibility(View.GONE);
        } else if(photo != null) {
            new ImageDownloaderTask(imageView).execute("http://wisapp.azurewebsites.net/safe/" + photo);
            if(photoName != null)
            	photoName.setVisibility(View.GONE);
		}
	}
}
