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
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import menezes.paulo.safe.adapter.ListReportItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.Place;
import menezes.paulo.safe.entity.Place_Like;
import menezes.paulo.safe.entity.Place_Photo;
import menezes.paulo.safe.entity.Place_Review;
import menezes.paulo.safe.entity.Report;
import menezes.paulo.safe.entity.User;
import menezes.paulo.safe.task.ImageDownloaderTask;
import menezes.dd.processbutton.FlatButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class PlaceActivity extends Activity {
	MobileServiceClient mClient;
	Place mPlace;
	
    // Camera
    AlertDialog mPhotoDialog;
    
    private Uri mFileUri;
    private ProgressDialog mLoading;
    private int mServerResponseCode = 0;

    // Gallery
    private Bitmap mBitmap;
    private Drawable mIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

		mClient = Connect.getInstance();
		
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

		mIcon = getResources().getDrawable(R.drawable.ic_launcher);
		
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null) {
			mPlace = (Place)bundle.getSerializable("place");
			
			setTitle(mPlace.name);
			
			((TextView)findViewById(R.id.name)).setText(mPlace.name);
			((TextView)findViewById(R.id.address)).setText(mPlace.address);
			((TextView)findViewById(R.id.city)).setText(mPlace.city);
			((TextView)findViewById(R.id.phone)).setText(mPlace.phone);
			
			mClient.getTable(User.class).where().field("id").eq(mPlace.idUser).execute(new TableQueryCallback<User>() {
				@Override
				public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse response) {
					if(e == null) {
						User user = users.get(0);
						
						user.getPhoto((ImageView) findViewById(R.id.userPhoto), (TextView)findViewById(R.id.userPhotoName));
						
						((TextView) findViewById(R.id.userPhotoName)).setText(String.valueOf(user.firstname.toCharArray()[0]) + String.valueOf(user.lastname.toCharArray()[0]));
						((TextView) findViewById(R.id.userName)).setText(user.getName());
						((TextView) findViewById(R.id.userBiografy)).setText(user.biografy != null ? user.biografy : "");
						((TextView) findViewById(R.id.userPhone)).setText(user.phone != null ? user.phone : "");

						((LinearLayout)findViewById(R.id.user)).setVisibility(View.VISIBLE);						
						((ProgressBar)findViewById(R.id.userLoading)).setVisibility(View.GONE);
					}
				}
			});
			
			mClient.getTable(Place_Review.class).where().field("idPlace").eq(mPlace.id).orderBy("id", QueryOrder.Descending).execute(new TableQueryCallback<Place_Review>() {
				@Override
				public void onCompleted(final List<Place_Review> reviews, int i, Exception e, ServiceFilterResponse response) {
					if(e == null) {
						((TextView) findViewById(R.id.comments)).setText(String.valueOf(reviews.size()));
						((TextView) findViewById(R.id.comments)).setVisibility(View.VISIBLE);
						((ProgressBar)findViewById(R.id.commentsLoading)).setVisibility(View.GONE);
						
						if(reviews.size() == 0) {
							((ProgressBar)findViewById(R.id.commentLoading)).setVisibility(View.GONE);
							((TextView) findViewById(R.id.commentMsg)).setVisibility(View.VISIBLE);
						} else {
							final LinearLayout commentsLay = (LinearLayout)findViewById(R.id.listComments);
							for (int j = 0; j < reviews.size(); j++) {
								final Place_Review comments = reviews.get(j);
								
								mClient.getTable(User.class).where().field("id").eq(reviews.get(j).idUser).execute(new TableQueryCallback<User>() {
									@Override
									public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse response) {
										if(e == null) {											
											LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
										    View v =  inflater.inflate(R.layout.list_comments, null, true);
										    ((TextView) v.findViewById(R.id.name)).setText(users.get(i).getName());
										    ((TextView) v.findViewById(R.id.text)).setText(comments.text);
										    
											users.get(i).getPhoto((ImageView) v.findViewById(R.id.image), (TextView) v.findViewById(R.id.photo_name));
										    
										    commentsLay.addView(v);
										    
										    ((ProgressBar)findViewById(R.id.commentLoading)).setVisibility(View.GONE);
										} else {
											((ProgressBar)findViewById(R.id.commentLoading)).setVisibility(View.GONE);
											((TextView) findViewById(R.id.commentMsg)).setVisibility(View.VISIBLE);
										}
									}
								});
							}
						}
					}
				}
			});
			
			mClient.getTable(Place_Like.class).where().field("idPlace").eq(mPlace.id).execute(new TableQueryCallback<Place_Like>() {
				@Override
				public void onCompleted(List<Place_Like> likes, int i, Exception e, ServiceFilterResponse response) {
					if(e == null) {
						((TextView) findViewById(R.id.confirmed)).setText(String.valueOf(likes.size()));
						((TextView) findViewById(R.id.confirmed)).setVisibility(View.VISIBLE);
						((ProgressBar)findViewById(R.id.confirmedLoading)).setVisibility(View.GONE);
					}
				}
			});
			
			mClient.getTable(Place_Photo.class).where().field("idPlace").eq(mPlace.id).execute(new TableQueryCallback<Place_Photo>() {
				@Override
				public void onCompleted(List<Place_Photo> photos, int i, Exception e, ServiceFilterResponse response) {
					if(e == null) {
						((TextView) findViewById(R.id.photos)).setText(String.valueOf(photos.size()));
						((TextView) findViewById(R.id.photos)).setVisibility(View.VISIBLE);
						((ProgressBar)findViewById(R.id.photosLoading)).setVisibility(View.GONE);
						
						if(photos.size() == 0) {
							((ProgressBar)findViewById(R.id.photoLoading)).setVisibility(View.GONE);
							((TextView) findViewById(R.id.photoMsg)).setVisibility(View.VISIBLE);
						} else {
							((ProgressBar)findViewById(R.id.photoLoading)).setVisibility(View.GONE);
							
							LinearLayout photoView = (LinearLayout)findViewById(R.id.photoView);
							for (int j = 0; j < photos.size(); j++) {
								if(j == 0) {
									new ImageDownloaderTask(((ImageView)findViewById(R.id.photo))).execute("http://wisapp.azurewebsites.net/safe/" + photos.get(j).image);
								}
								
								ImageView iv = new ImageView(PlaceActivity.this);
								LayoutParams params = new LayoutParams(mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());
								params.setMargins(10, 10, 10, 10);
								
								iv.setLayoutParams(params);
								new ImageDownloaderTask(iv).execute("http://wisapp.azurewebsites.net/safe/" + photos.get(j).image);
								
								photoView.addView(iv);
							}
						}
					}
				}
			});
			
			mClient.getTable(Report.class).where().field("idPlace").eq(mPlace.id).execute(new TableQueryCallback<Report>() {
				@Override
				public void onCompleted(final List<Report> reports, int i, Exception e, ServiceFilterResponse response) {
					if(e == null) {
						if(reports.size() == 0) {
							((ProgressBar)findViewById(R.id.moreLoading)).setVisibility(View.GONE);
							((TextView) findViewById(R.id.moreMsg)).setVisibility(View.VISIBLE);
						} else {
							ListView listContent = (ListView)findViewById(R.id.listMoreReports);
							listContent.setVisibility(View.VISIBLE);
							
							listContent.setAdapter(new ListReportItemAdapter(PlaceActivity.this, reports));
							listContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
			                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
									Intent main = new Intent(PlaceActivity.this, ReportActivity.class);
									Bundle b = new Bundle();
									b.putSerializable("report", reports.get(position));
									main.putExtras(b);
									
									startActivity(main);
			                    }
							});
							
							((ProgressBar) findViewById(R.id.moreLoading)).setVisibility(View.GONE);
						}
					}
				}
			});
			
			final FlatButton addComment = (FlatButton) findViewById(R.id.addComment);
			addComment.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					LayoutInflater inflater = PlaceActivity.this.getLayoutInflater();
					final View v = inflater.inflate(R.layout.dialog_add_comment, null);

					AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
					final Dialog dialog;

					builder.setView(v); 
					
					dialog = builder.create();
					dialog.show();
					
					final FlatButton addCom = (FlatButton) v.findViewById(R.id.addComment);
					addCom.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
							mLoading = ProgressDialog.show(PlaceActivity.this, "", "Enviando comentário...");
							
							Place_Review review = new Place_Review();
							review.idPlace = mPlace.id;
							review.idUser = Session.getInt("userID");
							review.text = ((EditText) v.findViewById(R.id.text)).getText().toString();
							
							mClient.getTable(Place_Review.class).insert(review, new TableOperationCallback<Place_Review>() {
								@Override
								public void onCompleted(Place_Review review, Exception e, ServiceFilterResponse response) {
									mLoading.dismiss();
									if(e == null) {
										finish();
										startActivity(getIntent());
									} else {
										Toast.makeText(PlaceActivity.this, "Houve um error, tente novamente", Toast.LENGTH_LONG).show();
									}
								}
							});
						}
					});
				}
			});
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.place, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_photo:
				openPhotoDialog();
				break;
			case R.id.action_confirm:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				final Dialog dialog;
		        builder.setMessage(R.string.likeMsg)
		               .setPositiveButton(R.string.likeYes, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   mLoading = ProgressDialog.show(PlaceActivity.this, "", "Gostando, aguarde...");
		                	   
		                	   Place_Like like = new Place_Like();
		                	   like.idPlace = mPlace.id;
		                	   like.idUser = Session.getInt("userID");
		                	   
		                       mClient.getTable(Place_Like.class).insert(like, new TableOperationCallback<Place_Like>() {
		                    	   @Override
		                    	   public void onCompleted(Place_Like like, Exception e, ServiceFilterResponse response) {
		                    		   if(e == null) {
		                    			   finish();
		                    			   startActivity(getIntent());
		                    		   } else {
		                    			   Toast.makeText(getApplicationContext(), "Houve um erro, tente novamente", Toast.LENGTH_LONG).show();
		                    		   }
		                    		   
		                    		   mLoading.dismiss();
		                    	   }
		                       });
	                       }
		               })
		               .setNegativeButton(R.string.confirmNo, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                      dialog.dismiss(); 
		                   }
		               });
		        dialog = builder.create();
		        dialog.show();
				break;
		}
		return super.onOptionsItemSelected(item);
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
	
	/*************************** ADD PHOTOS ****************************/
	
	public void addPhoto() {
        if(mFileUri != null) {
            mLoading = ProgressDialog.show(this, "", "Enviando imagem", true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    uploadFile(mFileUri.getPath());
                }
            }).start();
        }
        
        Place_Photo photo = new Place_Photo();
        photo.idPlace = mPlace.id;
        photo.idUser = Session.getInt("userID");
        photo.image = mFileUri.getPathSegments().get(mFileUri.getPathSegments().size() - 1);

        mClient.getTable(Place_Photo.class).insert(photo, new TableOperationCallback<Place_Photo>() {
            @Override
            public void onCompleted(Place_Photo photo, Exception e, ServiceFilterResponse response) {
                if (e == null) {
                	finish();
                	startActivity(getIntent());
                    Toast.makeText(getApplicationContext(), "Foto enviada com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Houve um error, tente novamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
	
	public void openPhotoDialog() {
        final CharSequence[] items = {"Usar a câmera", "Escolher da galeria"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolher foto");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        mFileUri = getOutputMediaFileUri(); // create a file to save the image
                        intent2.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri); // set the image file name

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
    
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }
    
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        Log.d("debug", Environment.getExternalStorageState());

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Safe");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Safe", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "Safe_"+ timeStamp + ".jpg");

        return mediaFile;
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream = null;
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                if (mBitmap != null) {
                    mBitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                mBitmap = BitmapFactory.decodeStream(stream);

                Bitmap resize = Bitmap.createScaledBitmap(mBitmap, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight(), true);

                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Safe");
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                File mediaFile;
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Safe_"+ timeStamp + ".jpg");

                mFileUri = Uri.fromFile(mediaFile);

                FileOutputStream fos = new FileOutputStream(mFileUri.getPath());
                resize.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();
                
                addPhoto();
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
        } else if(requestCode == 2) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mFileUri);

                    final int IMAGE_SIZE = mIcon.getIntrinsicWidth();
                    boolean landscape = bmp.getWidth() > bmp.getHeight();

                    float scale_factor;
                    if (landscape) scale_factor = (float)IMAGE_SIZE / bmp.getHeight();
                    else scale_factor = (float)IMAGE_SIZE / bmp.getWidth();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale_factor, scale_factor);
                    matrix.postRotate(90);

                    Bitmap croppedBitmap;
                    if (landscape){
                        int start = (bmp.getWidth() - bmp.getHeight()) / 2;
                        croppedBitmap = Bitmap.createBitmap(bmp, start, 0, bmp.getHeight(), bmp.getHeight(), matrix, true);
                    } else {
                        int start = (bmp.getHeight() - bmp.getWidth()) / 2;
                        croppedBitmap = Bitmap.createBitmap(bmp, 0, start, bmp.getWidth(), bmp.getWidth(), matrix, true);
                    }

                    FileOutputStream fos = new FileOutputStream(mFileUri.getPath());
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    fos.flush();
                    fos.close();

                    addPhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            mLoading.dismiss();

            Log.e("uploadFile", "Source File not exist :" + sourceFileUri);

            runOnUiThread(new Runnable() {
                public void run() {
                    //messageText.setText("Source File not exist :" +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://wisapp.azurewebsites.net/uploadSafe.php");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + fileName + "" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
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
                mServerResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + mServerResponseCode);

                if(mServerResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {

                            //String msg = "File Upload Completed.\n\n See uploaded file here : \n\n" +" http://www.androidexample.com/media/uploads/" + uploadFileName;

                            //messageText.setText(msg);
                            //Toast.makeText(UploadToServer.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                mLoading.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("MalformedURLException Exception : check script url.");
                        //Toast.makeText(UploadToServer.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                mLoading.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("Got Exception : see logcat ");
                        //Toast.makeText(UploadToServer.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            mLoading.dismiss();
            return mServerResponseCode;

        } // End else block
    }
}