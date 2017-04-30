package menezes.paulo.safe;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class EditProfileActivity extends Activity {

	MobileServiceClient mClient;
	EditText mName, mLastName, mBiografy, mPhone, mGender;
	Button mSaveChanges;

	AlertDialog mLevelDialog;
	AlertDialog mPhotoDialog;

	ProgressDialog mLoading;
	
	private User mUser;

	// Gallery
	private Bitmap bitmap;
	private ImageView perfil;
	private Drawable icon;

	// Camera
	private Uri fileUri;
	private ProgressDialog dialog;
	private int serverResponseCode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
		
		mLoading = ProgressDialog.show(this, "", "Carregando, aguarde...");

		mClient = Connect.getInstance();
		
		mClient.getTable(User.class).where().field("id").eq(Session.getInt("userID")).execute(new TableQueryCallback<User>() {
			@Override
			public void onCompleted(List<User> accounts, int i, Exception e, ServiceFilterResponse serviceFilterResponse) {
				if (e == null) {
					((ScrollView) findViewById(R.id.login_form)).setVisibility(View.VISIBLE);

					mUser = accounts.get(i);

					perfil = (ImageView) findViewById(R.id.perfil);
					perfil.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							openPhotoDialog();
						}
					});

					icon = getResources().getDrawable(R.drawable.ic_launcher);
					
					mUser.getPhoto(perfil, null);

					mName = (EditText) findViewById(R.id.name);
					mName.setText(mUser.firstname);

					mLastName = (EditText) findViewById(R.id.lastname);
					mLastName.setText(mUser.lastname);

					mBiografy = (EditText) findViewById(R.id.biografy);
					mBiografy.setText(mUser.biografy);

					mPhone = (EditText) findViewById(R.id.phone);
					mPhone.setText(mUser.phone);

					mGender = (EditText) findViewById(R.id.gender);
					mGender.setText(mUser.gender);
					mGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v,
								boolean hasFocus) {
							if (hasFocus)
								openGenderDialog();
						}
					});
					
					mLoading.dismiss();
				}
			}
		});

		mSaveChanges = (Button) findViewById(R.id.saveChanges);
		mSaveChanges.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (checkValidation()) {
					mLoading = ProgressDialog.show(EditProfileActivity.this, "", "Salvando alterações...");
					
					register(view);
				}
			}
		});
	}

    @Override
    public boolean onNavigateUp() {
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(nextScreen);

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(nextScreen);
    }

	public void openGenderDialog() {
		final CharSequence[] items = { "Masculino", "Feminino",
				"Prefiro não dizer" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Sexo");
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case 0:
							mGender.setText("Masculino");
							break;
						case 1:
							mGender.setText("Feminino");
							break;
						case 2:
							mGender.setText("Prefiro não dizer");
							break;
						}

						EditText telefone = (EditText) findViewById(R.id.phone);
						telefone.requestFocus();
						mLevelDialog.dismiss();
					}
				});
		mLevelDialog = builder.create();
		mLevelDialog.show();
	}

	public void openPhotoDialog() {
		final CharSequence[] items = { "Usar a câmera", "Escolher da galeria" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Escolher foto");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					fileUri = getOutputMediaFileUri(); // create a file to save
														// the image
					intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set
																		// the
																		// image
																		// file
																		// name

					// start the image capture Intent
					startActivityForResult(intent2, 2);

					break;
				case 1:
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					startActivityForResult(intent, 1);

					break;
				}

				mPhotoDialog.dismiss();
			}
		});
		mPhotoDialog = builder.create();
		mPhotoDialog.show();
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		Log.d("debug", Environment.getExternalStorageState());

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Safe");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Safe", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "Safe_" + timeStamp + ".jpg");

		return mediaFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		InputStream stream = null;
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			try {
				if (bitmap != null) {
					bitmap.recycle();
				}
				stream = getContentResolver().openInputStream(data.getData());
				bitmap = BitmapFactory.decodeStream(stream);

				Bitmap resize = Bitmap.createScaledBitmap(bitmap,
						icon.getIntrinsicWidth(), icon.getIntrinsicHeight(),
						true);

				File mediaStorageDir = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						"Safe");
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				File mediaFile;
				mediaFile = new File(mediaStorageDir.getPath() + File.separator
						+ "Safe_" + timeStamp + ".jpg");

				fileUri = Uri.fromFile(mediaFile);

				FileOutputStream fos = new FileOutputStream(fileUri.getPath());
				resize.compress(Bitmap.CompressFormat.JPEG, 100, fos);

				fos.flush();
				fos.close();

				perfil.setImageBitmap(resize);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				if (stream != null)
					try {
						stream.close();
					} catch (IOException ex) {
						e.printStackTrace();
					}
			}
		} else if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				try {
					Bitmap bmp = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), fileUri);

					final int IMAGE_SIZE = icon.getIntrinsicWidth();
					boolean landscape = bmp.getWidth() > bmp.getHeight();

					float scale_factor;
					if (landscape)
						scale_factor = (float) IMAGE_SIZE / bmp.getHeight();
					else
						scale_factor = (float) IMAGE_SIZE / bmp.getWidth();
					Matrix matrix = new Matrix();
					matrix.postScale(scale_factor, scale_factor);
					matrix.postRotate(-90);

					Bitmap croppedBitmap;
					if (landscape) {
						int start = (bmp.getWidth() - bmp.getHeight()) / 2;
						croppedBitmap = Bitmap.createBitmap(bmp, start, 0,
								bmp.getHeight(), bmp.getHeight(), matrix, true);
					} else {
						int start = (bmp.getHeight() - bmp.getWidth()) / 2;
						croppedBitmap = Bitmap.createBitmap(bmp, 0, start,
								bmp.getWidth(), bmp.getWidth(), matrix, true);
					}

					FileOutputStream fos = new FileOutputStream(
							fileUri.getPath());
					croppedBitmap
							.compress(Bitmap.CompressFormat.JPEG, 100, fos);

					fos.flush();
					fos.close();

					perfil.setImageBitmap(croppedBitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void register(View view) {
		mUser.firstname = mName.getText().toString();
		mUser.lastname = mLastName.getText().toString();
		mUser.biografy = mBiografy.getText().toString();
		mUser.phone = mPhone.getText().toString();
		mUser.gender = mGender.getText().toString();
		if (fileUri != null) {
			mUser.photo = fileUri.getPathSegments().get(fileUri.getPathSegments().size() - 1);
			dialog = ProgressDialog.show(EditProfileActivity.this, "", "Enviando imagem", true);

			new Thread(new Runnable() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

						}
					});

					uploadFile(fileUri.getPath());
				}
			}).start();
		}

		mClient.getTable(User.class).update(mUser,
				new TableOperationCallback<User>() {
					@Override
					public void onCompleted(User account, Exception exception,
							ServiceFilterResponse response) {
						if (exception == null) {
							Toast.makeText(getApplicationContext(), "Perfil alterado com sucesso.", Toast.LENGTH_SHORT).show();

							Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
							startActivity(nextScreen);
						} else {
							mSaveChanges.setText("Salvar alterações");
							mSaveChanges.setEnabled(true);
							Toast.makeText(getApplicationContext(), "Houve um error, tente novamente", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private boolean checkValidation() {
		boolean ret = true;

		// if (!Validation.hasText(nome)) ret = false;
		// if (!Validation.hasText(sobrenome)) ret = false;

		return ret;
	}

	// ---------------- UPLOAD ---------------------- //
	public int uploadFile(String sourceFileUri) {
		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);

		if (!sourceFile.isFile()) {
			dialog.dismiss();

			Log.e("uploadFile", "Source File not exist :" + sourceFileUri);

			runOnUiThread(new Runnable() {
				public void run() {
					// messageText.setText("Source File not exist :"
					// +uploadFilePath + "" + uploadFileName);
				}
			});

			return 0;
		} else {
			try {

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL("http://wisapp.azurewebsites.net/uploadSafe.php");

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="
						+ fileName + "" + lineEnd);
				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					runOnUiThread(new Runnable() {
						public void run() {

							// String msg =
							// "File Upload Completed.\n\n See uploaded file here : \n\n"
							// +" http://www.androidexample.com/media/uploads/"
							// + uploadFileName;

							// messageText.setText(msg);
							// Toast.makeText(UploadToServer.this,
							// "File Upload Complete.",
							// Toast.LENGTH_SHORT).show();
						}
					});
				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				dialog.dismiss();
				ex.printStackTrace();

				runOnUiThread(new Runnable() {
					public void run() {
						// messageText.setText("MalformedURLException Exception : check script url.");
						// Toast.makeText(UploadToServer.this,
						// "MalformedURLException", Toast.LENGTH_SHORT).show();
					}
				});

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {

				dialog.dismiss();
				e.printStackTrace();

				runOnUiThread(new Runnable() {
					public void run() {
						// messageText.setText("Got Exception : see logcat ");
						// Toast.makeText(UploadToServer.this,
						// "Got Exception : see logcat ",
						// Toast.LENGTH_SHORT).show();
					}
				});
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
			dialog.dismiss();
			return serverResponseCode;

		} // End else block
	}
}