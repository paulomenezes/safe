package menezes.paulo.safe;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.User;
import menezes.paulo.safe.util.ProgressGenerator;
import menezes.paulo.safe.util.Util;
import menezes.dd.processbutton.FlatButton;
import menezes.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class LoginActivity extends Activity implements ProgressGenerator.OnCompleteListener, ConnectionCallbacks, OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    
	public static GoogleApiClient mGoogleApiClient;
	private ConnectionResult mConnectionResult;
	private boolean mSignInClicked;
	private boolean mIntentInProgress;
    
	private ProgressDialog mProgressDialog;

	private MobileServiceClient mClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		new Connect(this);
		int userID = menezes.paulo.safe.data.Session.getInt("userID");
				
		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
				
		if(userID != -1 ) {
			logged();
		}
		
		mClient = Connect.getInstance();
		
		getActionBar().hide();

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
		// Google button
		mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
			.addConnectionCallbacks(LoginActivity.this)
			.addOnConnectionFailedListener(LoginActivity.this)
			.addApi(Plus.API, PlusOptions.builder().build())
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.build();
		final ActionProcessButton googleButton = (ActionProcessButton) findViewById(R.id.googleButton);
        googleButton.setMode(ActionProcessButton.Mode.ENDLESS);
        googleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				signInGoogle();
			}
		});
        
        // Login
        TextView signInTextView = (TextView)findViewById(R.id.signin);
        signInTextView.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("InflateParams")
			@Override
			public void onClick(View view) {
				LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
				View v = inflater.inflate(R.layout.dialog_login, null);

				AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
				final Dialog dialog;

				builder.setView(v); 
				
				dialog = builder.create();
				dialog.show();
				
				// Login
				final FlatButton enterButton = (FlatButton)v.findViewById(R.id.enterButton);
				final EditText emailText = (EditText)v.findViewById(R.id.textEmail);
				final EditText passwordText = (EditText)v.findViewById(R.id.textPassword);
				enterButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (emailText.length() <= 0)
							emailText.setError("Campo Obrigatório");
						else if (passwordText.length() <= 0)
							passwordText.setError("Campo Obrigatório");
						else if(!Util.validateEmail(emailText.getText().toString()))
							emailText.setError("E-mail inválido");
						else {
							dialog.dismiss();
							
							mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "Carregando, aguarde...", true);
							
							mClient.getTable(User.class).where().field("email").eq(emailText.getText().toString()).and().field("password").eq(Util.encodeAsMD5(passwordText.getText().toString())).execute(new TableQueryCallback<User>() {
								@Override
								public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse response) {
									if(e == null) {
										if(users.size() > 0) {
											login(users.get(i).id);
										} else {
											mProgressDialog.dismiss();
											Toast.makeText(LoginActivity.this, "E-mail ou senha inválidos", Toast.LENGTH_LONG).show();
										}
									} else {
										mProgressDialog.dismiss();
										Toast.makeText(LoginActivity.this, "E-mail ou senha inválidos", Toast.LENGTH_LONG).show();
									}
								}
							});
						}
					}
				});
				// Login
			}
		});
        
        // Register
        FlatButton emailButton = (FlatButton)findViewById(R.id.emailButon);
        emailButton.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("InflateParams")
			@Override
			public void onClick(View view) {
				LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
				View v = inflater.inflate(R.layout.dialog_register, null);
								
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
				final Dialog dialog;

				builder.setView(v); 
				
				dialog = builder.create();
				dialog.show();

				// Register
				final FlatButton enterButton = (FlatButton)v.findViewById(R.id.enterButton);
				final EditText firstNameText = (EditText)v.findViewById(R.id.textFirstName);
				final EditText lastNameText = (EditText)v.findViewById(R.id.textLastName);
				final EditText emailText = (EditText)v.findViewById(R.id.textEmail);
				final EditText passwordText = (EditText)v.findViewById(R.id.textPassword);
				enterButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						firstNameText.setError(null);
						lastNameText.setError(null);
						emailText.setError(null);
						passwordText.setError(null);
						
						if (firstNameText.length() <= 0)
							firstNameText.setError("Campo Obrigatório");
						else if (lastNameText.length() <= 0)
							lastNameText.setError("Campo Obrigatório");
						else if (emailText.length() <= 0)
							emailText.setError("Campo Obrigatório");
						else if (passwordText.length() <= 0)
							passwordText.setError("Campo Obrigatório");
						else if(!Util.validateEmail(emailText.getText().toString()))
							emailText.setError("E-mail inválido");
						else {
							dialog.dismiss();
							
							User user = new User(
									firstNameText.getText().toString(), 
									lastNameText.getText().toString(), 
									emailText.getText().toString(), 
									Util.encodeAsMD5(passwordText.getText().toString()));
							
							mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "Carregando, aguarde...", true);
							register(user);
						}						
					}
				});
				// Register
			}
		});
	}
	
	private void login(int id) {
		mProgressDialog.dismiss();
		
		menezes.paulo.safe.data.Session.putInt("userID", id);
		
		logged();
	}
	
	private void register(User user) {
		mClient.getTable(User.class).insert(user, new TableOperationCallback<User>() {
			@Override
			public void onCompleted(User u, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					login(u.id);
				} else {
					mProgressDialog.dismiss();
					Toast.makeText(LoginActivity.this, "Houve um erro, tente novamente", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void search(String field, String id, final User user) {
		mClient.getTable(User.class).where().field(field).eq(id).execute(new TableQueryCallback<User>() {
			@Override
			public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					if(users.size() > 0) {
						login(users.get(i).id);
					} else {
						register(user);
					}
				}
			}
		});
	}
	
	private void signInGoogle() {
	    if (!mGoogleApiClient.isConnecting()) {
	    	mSignInClicked = true;
	        resolveSignInError();
	    }
	}
	 
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
		    	mIntentInProgress = false;
		    	mGoogleApiClient.connect();
	    	}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == RC_SIGN_IN) {
			if(requestCode != RESULT_OK)
				mSignInClicked = false;
			
			mIntentInProgress = false;
			
			if(!mGoogleApiClient.isConnecting())
				mGoogleApiClient.connect();
		} else {

		}
	}

	@Override
	public void onComplete() {
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected())
			mGoogleApiClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if(!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}
		
		if(!mIntentInProgress) {
			mConnectionResult = result;
			
			if(mSignInClicked) {
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		mSignInClicked = false;
		
		User user = new User();
		user.setGoogleUser(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient));
		
		mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "Carregando, aguarde...", true);
		search("idGoogle", user.idGoogle, user);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}
	
	private void logged() {
		Intent next = new Intent(LoginActivity.this, MainActivity.class);
		finish();
		startActivity(next);
	}
}
