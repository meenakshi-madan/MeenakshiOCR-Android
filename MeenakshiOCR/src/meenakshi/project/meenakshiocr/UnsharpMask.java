package meenakshi.project.meenakshiocr;

/**This class hands the OCR processing and unsharp-masking for processing the image
 * author: Meenakshi Madan
 */

import java.io.IOException;

import magick.CompressionType;
import magick.ImageInfo;
import magick.MagickImage;
import magick.PixelPacket;
import magick.util.MagickBitmap;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class UnsharpMask extends AsyncTask<Void, Integer, Void> {

	ProgressDialog pg;
	
	/** Tag for logging purposes **/
	String TAG = "UnsharpMask";
	
	/** To check if OCR needs to be performed again on the same image - if the confidence value is very low **/
	boolean checkOnceForFurtherProcessing = true;
	
	/** Number of times ocr has been performed in this transaction **/
	int tessRepeatCount = 0;
	
	/** Maximum number of times that OCR can be performed on the image in this transaction **/
	int tessRepeatMAXCOUNT = 5;
	
	/** Mean confidence as returned by tesseract on the recognized text **/
	int meanConfidence_original, meanConfidence_processed=0;
	String text_original, text_processed="";
	
	static int LEVEL_ORIGINAL = 0, LEVEL_PROCESSED=1;

	private SharedPreferences ocrPref;
	private SharedPreferences mPreferences;
	
	
	private String BLACK_LIST_AUTOMATIC = "#$%^&+=:;{}[]/,.!@\\|><~`\"'*()";
	private String WHITE_LIST_AUTOMATIC = "1234567890ABCDEFGHJKLMNPRSTVWXYZabcdefghijklmnopqrstuvwxyz";
	
	/** Object of OCRActivity, to access variables such as DATA_PATH and view elements **/
	private OCRActivity act;

	public UnsharpMask(OCRActivity act) {
		Log.v(TAG, "Begin constructor");
		this.act = act;
		
		ocrPref = act.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	
	@Override
    protected void onPreExecute() {
		Log.v("CopData AsyncTask Mein", "Entered onPreExecute");
		pg = ProgressDialog.show(act, "", 
                "Processing. . .", true); 
    }
	

	
	
	/** Displays text to the user, hides progress bar
	 * 
	 */
	
	@Override
    protected void onPostExecute(Void result) {
        //mImageView.setImageBitmap(result);
		Log.v("AsyncTask Mein", "Entered onPostExecute");
		
		
		if(meanConfidence_original > meanConfidence_processed)
		{
			act.recognizedText = text_original;
		}
		else {
			act.recognizedText = text_processed;
		}
		
		pg.dismiss();
		
		if ( act.recognizedText.length() != 0 ) {
			act._field.setText(act.recognizedText);
			
			((TableRow)act.findViewById(R.id.tableRow3)).setVisibility(View.VISIBLE);
			((TableRow)act.findViewById(R.id.tableRow4)).setVisibility(View.VISIBLE);
			((ImageView)act.findViewById(R.id.dbrobotarms)).setImageResource(R.drawable.ocrscreen20);
			
			mPreferences = act.getSharedPreferences("MeenakshiOCRSharedPreferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = mPreferences.edit();
			for(int i=4; i>=1; i--)
			{
				editor.putString("his" + i, mPreferences.getString("his" + (i-1), " "));
			}
			editor.putString("his0", act.recognizedText);
			editor.commit();
		}
		else
		{
			Toast.makeText(act, "Oops,  no text found!", Toast.LENGTH_SHORT).show();
		}
    }


	
	/** Performs OCR on the bitmap at _path on SDCARD. Also performs any orientation required
	 * 
	 * 
	 */
	
	protected void performOCR(int level)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;

		Bitmap bitmap = BitmapFactory.decodeFile(Constants.CURRENT_IMAGE_PATH, options);

		
		// Getting width & height of the given image.
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
        

		try {
			ExifInterface exif = new ExifInterface(Constants.CURRENT_IMAGE_PATH);
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

		
		Log.v(TAG, "Before baseApi");
		//Pix.
		//Binarize.otsuAdaptiveThreshold(pixs);
		TessBaseAPI baseApi = new TessBaseAPI();
		//baseApi.init(mainActivity.DATA_PATH, mainActivity.lang, TessBaseAPI.OEM_CUBE_ONLY);
		//baseApi.setPageSegMode(TessBaseAPI.PSM_AUTO);
		if(ocrPref.getString("whitelist", "None").equals("None"))
		{
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, BLACK_LIST_AUTOMATIC);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, WHITE_LIST_AUTOMATIC);
			
			Log.v(TAG, "whitelist preferences returned None");
		}
		else
		{
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, ocrPref.getString("whitelist", "None"));
			
			Log.v(TAG, "whitelist preferences returned " + ocrPref.getString("whitelist", "None"));
		}
		
		if(!ocrPref.getString("psm", "None").equals("None"))
		{
			baseApi.setPageSegMode(Integer.parseInt(ocrPref.getString("psm", "None")));
			Log.v(TAG, "PSM preferences returned " + ocrPref.getString("psm", "None"));
		}
		
		Log.v(TAG, "PSM preferences returned None");
	
		baseApi.setDebug(true);
		Log.v(TAG, "After setting variables");
		baseApi.init(Constants.DATA_PATH, Constants.LANG); //, TessBaseAPI.OEM_CUBE_ONLY
		Log.v(TAG, "After init and before setting bitmap");
		baseApi.setImage(bitmap);
		Log.v(TAG, "After init and before getUTF8Text");
		if(level == UnsharpMask.LEVEL_ORIGINAL)
		{
			text_original = baseApi.getUTF8Text();
			meanConfidence_original = baseApi.meanConfidence();
			
			Log.v(TAG, "OCRED TEXT: " + text_original);
			Log.v(TAG, "Mean Confidence: " + meanConfidence_original);
		}
		else if(level == UnsharpMask.LEVEL_PROCESSED)
		{
			text_processed = baseApi.getUTF8Text();
			meanConfidence_processed = baseApi.meanConfidence();
			
			Log.v(TAG, "OCRED TEXT: " + text_processed);
			Log.v(TAG, "Mean Confidence: " + meanConfidence_processed);
		}
		
		baseApi.end();

		if (baseApi != null) {
		      baseApi.clear();
		    }

		
	}
	
	
	void performProcessing()
	{
		try{
			ImageInfo mi = new ImageInfo(Constants.CURRENT_IMAGE_PATH);
			MagickImage m = new MagickImage(mi);
			//MagickImage m2 = new MagickImage(mi);
			//beforeProcess = MagickBitmap.ToBitmap(m);
			
			
			if(m.normalizeImage()) Log.v(TAG, "normalize conversion successful");
			else Log.v(TAG, "normalize conversion unsuccessful");
			
			Deskew d = new Deskew(MagickBitmap.ToBitmap(m));
			double skew = d.GetSkewAngle();
			Log.v(TAG, "After Deskew, skew = " + skew);
			
			m = m.rotateImage(-skew); ///57.295779513082320876798154814105
			m.setDepth(8);
			
			m = m.sharpenImage(10, 8);
			
			if(m.negateImage(0)) Log.v(TAG, "negate conversion successful");
			else Log.v(TAG, "negate conversion unsuccessful");
			PixelPacket pp = m.getBackgroundColor();
			int bg = pp.getBlue(), thresh;
			Log.v(TAG, "BG color return by getBackgroundColor is: " + bg);
			if (bg<32757) thresh = 60000;
			else thresh = 10000;
			if(m.thresholdImage(32757)) Log.v(TAG, "thresh conversion successful"); //15000
			else Log.v(TAG, "thresh conversion unsuccessful");
			if(m.negateImage(0)) Log.v(TAG, "negate conversion successful");
			else Log.v(TAG, "negate conversion unsuccessful");
			
			m = m.scaleImage(m.getWidth()+100, m.getHeight() + 100);

			mi.setDensity("300");
			m.setCompression(CompressionType.NoCompression);
			//m = m.unsharpMaskImage(6.8, 3, 2.69, 0);
			m.setFileName(Constants.CURRENT_IMAGE_PATH); //give new location
			if(m.writeImage(mi)) Log.v(TAG, "Successfully wrote image to path"); //save
			else Log.v(TAG, "Image save unsuccessful");
			//afterProcess = MagickBitmap.ToBitmap(m);
			//mask = MagickBitmap.ToBitmap(m);
		}
		catch(Exception e)
		{
			Log.v(TAG, "exception occured performing magick functions: " + e.toString());
		}
		
		publishProgress(4);
		
		Log.v(TAG, "After unsharp");
		

		Log.v(TAG, "After saving file to sdcard");
		
		Log.v(TAG, "After scale and binarize");
		 
		//pix = Enhance.unsharpMasking(pix, 3, 0.7F); //gives OutOfMemoryError
		//WriteFile.writeImpliedFormat(pix, pic, 100, true);
		//afterProcess = WriteFile.writeBitmap(pix);
		
		Log.v(TAG, "In runnable thread, after processing");
		
		publishProgress(8);
		
	}




	/**
	 * Calls functions to perform required preprocessing and OCR
	 */

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		//afterProcess = bitmap_Source;
		Log.v(TAG, "In runnable thread, before processing");
		performOCR(UnsharpMask.LEVEL_ORIGINAL);
		publishProgress(1);
		
		if(ocrPref.getBoolean("processimage", true)){
			performProcessing();
			performOCR(UnsharpMask.LEVEL_PROCESSED);
			
			Log.v(TAG, "Processimage preferences returned true");
		}
		
		Log.v(TAG, "Processimage preferences returned false");
		
		publishProgress(10);
		Log.v("AsyncTask Mein", "End of do In Background");
		
		return null;
	}
	
	/** Check if a given String contains any of the characters in the given array
	 * 
	 * @param str source string to check for characters
	 * @param searchChars sequence of characters to check for in source string
	 * @return boolean value - true if string contains any character, false otherwise
	 */


}