package menezes.paulo.safe.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

import menezes.dd.processbutton.FlatButton;
import menezes.paulo.safe.PlaceSelectActivity;
import menezes.paulo.safe.R;
import menezes.paulo.safe.ReportActivity;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.Report;

public class AddReportDialog extends DialogFragment {
	
	private int mIDPlace = -1;
	private int mAnonymous = 0;
	private ProgressDialog mProgressDialog;
	
	private Context mContext;
	
	public AddReportDialog() {

	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_add_report, container);
        getDialog().setTitle(getResources().getString(R.string.addReport));
        
        if (getActivity() != null)
        	mContext = getActivity();
        
        final EditText title = (EditText)view.findViewById(R.id.title);
        final EditText description = (EditText)view.findViewById(R.id.description);
                
        final Spinner type = (Spinner) view.findViewById(R.id.type);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.report_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeAdapter);
        
        final FlatButton anonymousButton = (FlatButton)view.findViewById(R.id.anonymousButton);
        anonymousButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(anonymousButton.getTag() != null && Integer.parseInt(anonymousButton.getTag().toString()) != 1) {
					mAnonymous = 0;
					anonymousButton.setTag(1);
					Drawable img = getActivity().getResources().getDrawable( R.drawable.ic_action_flash_off );
					anonymousButton.setCompoundDrawablesRelativeWithIntrinsicBounds(img, null, null, null);
				} else {
					mAnonymous = 1;
					anonymousButton.setTag(0);
					Drawable img = getActivity().getResources().getDrawable( R.drawable.ic_action_flash_on );
					anonymousButton.setCompoundDrawablesRelativeWithIntrinsicBounds(img, null, null, null);
				}
			}
		});
                
		final Button place = (Button)view.findViewById(R.id.place);		
		place.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent selectPlace = new Intent(getActivity(), PlaceSelectActivity.class);
				
				Bundle b = new Bundle();
				b.putInt("type", type.getSelectedItemPosition());
				if(title.getText() != null)
					b.putString("title", title.getText().toString());
				
				if(description.getText() != null)
					b.putString("description", description.getText().toString());
				
				b.putInt("anonymous", mAnonymous);
				
				selectPlace.putExtras(b);
				startActivity(selectPlace);
			}
		});
		
        final FlatButton addReportButton = (FlatButton)view.findViewById(R.id.addReportButton);
        addReportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (title.getText().toString().length() > 0) {
					Report report = new Report();
					report.idPlace = mIDPlace;
					report.idUser = Session.getInt("userID");
					report.type = type.getSelectedItem().toString();
					report.title = title.getText().toString();
					report.description = description.getText().toString();
					report.anonymuos = mAnonymous;

					addReport(report);
				} else {
                    title.setError("Campo obrigatório");
				}
			}
		});
        
        if(getActivity().getIntent() != null) {
        	Bundle b = getActivity().getIntent().getExtras();
        	if(b != null) {
        		if (b.getInt("placeID") != -1) {
        			mIDPlace = b.getInt("placeID");
        		}
        		
        		if (b.getString("placeName") != null) {
        			place.setText(b.getString("placeName"));
        		}
        		
        		if (b.getInt("type") > -1) {
        			type.setSelection(b.getInt("type"));
        		}
        		
        		if(b.getString("title") != null) {
        			title.setText(b.getString("title"));
        		}
        		
        		if(b.getString("description") != null) {
        			description.setText(b.getString("description"));
        		}
        		
				mAnonymous =  b.getInt("anonymous");
				
				if (mAnonymous == 0) {
					mAnonymous = 0;
					anonymousButton.setTag(1);
					Drawable img = getActivity().getResources().getDrawable( R.drawable.ic_action_flash_off );
					anonymousButton.setCompoundDrawablesRelativeWithIntrinsicBounds(img, null, null, null);
				} else {
					mAnonymous = 1;
					anonymousButton.setTag(0);
					Drawable img = getActivity().getResources().getDrawable( R.drawable.ic_action_flash_on );
					anonymousButton.setCompoundDrawablesRelativeWithIntrinsicBounds(img, null, null, null);
				}
        	}
        }
                
        return view;
    }
    
    private void addReport(Report report) {
    	this.dismiss();
    	
    	mProgressDialog = ProgressDialog.show(getActivity(), "", "Carregando, aguarde...", true);
    	Connect.getInstance().getTable(Report.class).insert(report, new TableOperationCallback<Report>() {
			@Override
			public void onCompleted(Report report, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					Intent next = new Intent(mContext, ReportActivity.class);
					Bundle b = new Bundle();
					b.putSerializable("report", report);
					b.putBoolean("addPhoto", true);
					
					next.putExtras(b);
					
					mContext.startActivity(next);
				}
				mProgressDialog.dismiss();
			}
		});
    }

    /**/
}