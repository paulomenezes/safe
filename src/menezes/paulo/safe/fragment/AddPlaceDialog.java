package menezes.paulo.safe.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import menezes.dd.processbutton.FlatButton;
import menezes.paulo.safe.MainActivity;
import menezes.paulo.safe.R;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.Place;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class AddPlaceDialog extends DialogFragment {
	
	private ProgressDialog mProgressDialog;
	private Bundle mBundle;
	private Context mContext;
	
	public AddPlaceDialog() {

	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_add_place, container);
        getDialog().setTitle(getResources().getString(R.string.addPlace));
        
        mBundle = getArguments();
        if(getActivity() != null) {
        	mContext = getActivity();
        }
        
        final Spinner type = (Spinner) view.findViewById(R.id.category);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.report_category, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeAdapter);
        
        final FlatButton addPlaceButton = (FlatButton)view.findViewById(R.id.addPlaceButton);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText name = (EditText)view.findViewById(R.id.name);
				EditText city = (EditText)view.findViewById(R.id.city);
				EditText address = (EditText)view.findViewById(R.id.address);
				EditText phone = (EditText)view.findViewById(R.id.phone);

				if (name.getText().toString().length() > 0) {
					Place place = new Place();
					place.idUser = Session.getInt("userID");
					place.category = type.getSelectedItem().toString();
					place.name = name.getText().toString();
					place.city = city.getText().toString();
					place.address = address.getText().toString();
					place.phone = phone.getText().toString();

					addPlace(place);
				} else {
					name.setError("Campo obrigatório");
				}
			}
		});
                
        return view;
    }
    
    private void addPlace(Place place) {
    	this.dismiss();
    	
    	mProgressDialog = ProgressDialog.show(getActivity(), "", "Carregando, aguarde...", true);
    	Connect.getInstance().getTable(Place.class).insert(place, new TableOperationCallback<Place>() {
			@Override
			public void onCompleted(Place place, Exception e, ServiceFilterResponse response) {
				if(e == null && mContext != null) {
					Intent main = new Intent(mContext, MainActivity.class);
					
					if (mBundle == null) {
						mBundle = new Bundle();
					}
					
					mBundle.putBoolean("addReport", true);
					mBundle.putInt("placeID", place.id);
					mBundle.putString("placeName", place.name);

					main.putExtras(mBundle);
					mContext.startActivity(main);
				}
				
				mProgressDialog.dismiss();
			}
		});
    }
}