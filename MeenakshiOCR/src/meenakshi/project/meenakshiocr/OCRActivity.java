package meenakshi.project.meenakshiocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OCRActivity extends Activity {
	
	private Uri mImageCaptureUri;
	//public ImageView mImageView; //, processedImage
	//public ImageView mImageView2;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	//Fields Specific to OCR
	//public static final String DATA_PATH = Environment
			//.getExternalStorageDirectory().getAbsolutePath() + "/MeenakshiOCR/";
	private static final String TAG = "OCRActivity.java";
	protected TextView _field;
	public String recognizedText;
	
	private SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ocr);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);

		_field = (TextView)findViewById(R.id.recogText);
		ImageButton button 	= (ImageButton) findViewById(R.id.btn_startOCR);
		//mImageView		= (ImageView) findViewById(R.id.selectedImage);
		//mImageView2		= (ImageView) findViewById(R.id.processedImage);
		//mImageView3		= (ImageView) findViewById(R.id.mask);
		//processedImage= (ImageView) findViewById(R.id.ocrphoto);
		//processedImage.setVisibility(View.INVISIBLE);
		_field.clearFocus();
		button.requestFocus();
		
        
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
					
					File file = new File(Constants.CURRENT_IMAGE_PATH);
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
		dialog.setCanceledOnTouchOutside(true);
		
		button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
        
        //Log.v(TAG,"Before calling async task.");
		//if(!copyTessDataToSD())
			//Toast.makeText(getApplicationContext(), "Hmm, necessary data could not be copied. Please try restarting the application.", Toast.LENGTH_LONG);
        
        /*mPreferences = getSharedPreferences("MeenakshiOCRSharedPreferences", Context.MODE_PRIVATE);
        boolean firstTime = mPreferences.getBoolean("firstTimev3", true);
        if (firstTime) { 
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            new CopyDataToSDAsync(this).execute();
        }*/
        
	}
	
	
	
	public void copyRTToClipBoard(View v)
	{
		ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE); 
		clipboard.setText(recognizedText); 
		Toast.makeText(getApplicationContext(), "Text copied to clipboard!", Toast.LENGTH_SHORT).show();
	}
	
	
	public void googleRT(View v)
	{
		//AlertDialog.Builder builder = new AlterDialog.Builder(this);
		Uri uriUrl = Uri.parse("http://www.google.com/search?q=" + recognizedText); 
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);  
		startActivity(launchBrowser); 
	}
	
	
	public void saveRTToFile(View v)
	{
		mPreferences = getSharedPreferences("MeenakshiOCRSharedPreferences", Context.MODE_PRIVATE);
		
		try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "OCRNotes");
	        if (!root.exists()) 
	        {
	            root.mkdirs();
	        }
	        int count = mPreferences.getInt("textFileCounter", 1);
	        File gpxfile = new File(root, count +  ".txt");
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.append(recognizedText);
	        writer.flush();
	        writer.close();

	        Toast.makeText(this, "Saved to OCRNotes/" + count +  ".txt", Toast.LENGTH_SHORT).show();
	        
	        SharedPreferences.Editor editor = mPreferences.edit();
	        editor.putInt("textFileCounter", ++count);
	        editor.commit();
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	         Log.v(TAG, e.toString());
	    }
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
		                FileOutputStream out = new FileOutputStream(Constants.CURRENT_IMAGE_PATH);
		                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
		                
		                File file = new File(Constants.CURRENT_IMAGE_PATH);
						mImageCaptureUri = Uri.fromFile(file);
		         } catch (Exception e) {
		                e.printStackTrace();
		                Log.v(TAG, "Oh noes, couldn't save cropped file to _path" + e.toString());
		         }
		            
		            //mImageView.setImageBitmap(photo);
		            //performOCR();
		            //photo = OCRImageProcessing.applyGaussianBlur(photo); //works
		            //photo = OCRImageProcessing.unsharpMask(photo); //works but takes too long
		            //mImageView.setImageBitmap(photo);
		            //progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
		    		//progressBar.setIndeterminate(true);
		    		//progressBar.setId(id)
		    		//progressBar.set
		            //Toast.makeText(getApplicationContext(), "Please hold on for a few seconds while your image is being processed", Toast.LENGTH_LONG).show();
		            //progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		    		//progressBar.setVisibility(View.VISIBLE);
		            new UnsharpMask(this).execute();
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
		getMenuInflater().inflate(R.menu.activity_ocr, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

}
