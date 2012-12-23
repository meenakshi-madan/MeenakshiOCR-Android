package meenakshi.project.meenakshiocr;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import android.net.Uri;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private Uri mImageCaptureUri;
	private ImageView mImageView;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	//Fields Specific to OCR
	//public static final String DATA_PATH = Environment
			//.getExternalStorageDirectory().getAbsolutePath() + "/MeenakshiOCR/";
	public String DATA_PATH;
	public final String lang = "eng";
	protected String _path;
	private static final String TAG = "MainActivity.java";
	protected TextView _field;
	public String recognizedText;
	//END OF Fields Specific to OCR
	
	ProgressBar progressBar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        
        DATA_PATH = getExternalFilesDir(null).getAbsolutePath() + "/";
        Log.v("Meenakshi", DATA_PATH);
        
        _path = DATA_PATH + "/ocr.jpg";
		_field = (TextView)findViewById(R.id.textview);
		Button button 	= (Button) findViewById(R.id.btn_crop);
		mImageView		= (ImageView) findViewById(R.id.iv_photo);
		
        
        final String [] items			= new String [] {"Take from camera", "Select from gallery"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);
		
		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					
					//mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
					//				   "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
					
					File file = new File(_path);
					mImageCaptureUri = Uri.fromFile(file);

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);
						
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { //pick from file
					Intent intent = new Intent();
					
	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                
	                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
				}
			}
		} );
		
		final AlertDialog dialog = builder.create();
		
		button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
        
        
        //SPECIFIC TO OCR
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					Toast.makeText(getApplicationContext(), "Couldn't create required directories. OCR will not work.", Toast.LENGTH_LONG).show();
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		Log.v(TAG, "End of creating directories for loop");
		
		// lang.traineddata file with the app (in assets folder)
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/eng.traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/eng.traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();
				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}
		
		Log.v(TAG, "End of copying traineddata if block");
		//END OF SPECIFIC TO OCR
     
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	   
	    switch (requestCode) {
		    case PICK_FROM_CAMERA:
		    	doCrop();
		    	
		    	break;
		    	
		    case PICK_FROM_FILE: 
		    	mImageCaptureUri = data.getData();
		    	
		    	doCrop();
	    
		    	break;	    	
	    
		    case CROP_FROM_CAMERA:	    	
		        Bundle extras = data.getExtras();
	
		        if (extras != null) {	        	
		            Bitmap photo = extras.getParcelable("data");
		            
		            try {
		                FileOutputStream out = new FileOutputStream(_path);
		                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
		                
		                File file = new File(_path);
						mImageCaptureUri = Uri.fromFile(file);
		         } catch (Exception e) {
		                e.printStackTrace();
		                Log.v(TAG, "Oh noes, couldn't save cropped file to _path");
		         }
		            
		            mImageView.setImageBitmap(photo);
		            //performOCR();
		            //photo = OCRImageProcessing.applyGaussianBlur(photo); //works
		            //photo = OCRImageProcessing.unsharpMask(photo); //works but takes too long
		            //mImageView.setImageBitmap(photo);
		            //progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
		    		//progressBar.setIndeterminate(true);
		    		//progressBar.setId(id)
		    		//progressBar.set
		            Toast.makeText(getApplicationContext(), "Please hold on for a few seconds while your image is being processed", Toast.LENGTH_LONG).show();
		            progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		    		progressBar.setVisibility(View.VISIBLE);
		            new UnsharpMask(this, photo).execute();
		            //um.start();
		            /*try {
		            	um.join();
		            }catch(Exception e)
		            {
		            	Log.v("UM.JOIN EXCEPTION", e.toString());
		            }*/
		            
		            //performOCR();
		        }
	
		        /*COMMENTED CUZ I DON'T WANT TO DELETE THIS FILE, IT'LL BE USED FOR OCR
		         File f = new File(mImageCaptureUri.getPath());            
		         
		        
		        if (f.exists()) f.delete();
		        */
	
		        break;

	    }
	}
    
    private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        
        int size = list.size();
        
        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        	
            return;
        } else {
        	intent.setData(mImageCaptureUri);
            
            //intent.putExtra("outputX", 200);
            //intent.putExtra("outputY", 200);
            //intent.putExtra("aspectX", 1);
            //intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            
        	if (size == 1) {
        		Intent i 		= new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop App");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            public void onClick( DialogInterface dialog, int item ) {
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (mImageCaptureUri != null ) {
		                    getContentResolver().delete(mImageCaptureUri, null, null );
		                    mImageCaptureUri = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**protected void performOCR()
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
		
		
		//bitmap = OCRImageProcessing.makeGreyScale(bitmap);
		
		//bitmap = OCRImageProcessing.createContrastBW(bitmap, 80);
		
		// Getting width & height of the given image.
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		/*if(w<300 || h<300)
			bitmap = OCRImageProcessing.increaseDPI(bitmap,w,h);
		
		bitmap = OCRImageProcessing.applyGaussianBlur(bitmap);
		
		try{	
			FileOutputStream out = new FileOutputStream(_path);
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		}catch(Exception e)
		{
			Log.v(TAG, e.toString());
		}*/
        
/*		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		// _image.setImageBitmap( bitmap );
		
		//mImageView.setImageBitmap(bitmap);
		
		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		
		recognizedText = baseApi.getUTF8Text();
		
		baseApi.end();

		// You now have the text in recognizedText var, you can do anything with it.
		// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
		// so that garbage doesn't make it to the display.

		Log.v(TAG, "OCRED TEXT: " + recognizedText);

		if ( lang.equalsIgnoreCase("eng") ) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}
		
		recognizedText = recognizedText.trim();

		if ( recognizedText.length() != 0 ) {
			_field.setText(recognizedText);
			//_field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
			//_field.setSelection(_field.getText().toString().length());
		}
		
		// Cycle done.
		
		//return recognizedText;
	}
	
*/

}
