package meenakshi.project.meenakshiocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Enhance;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.Scale;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;

public class UnsharpMask extends AsyncTask<Void, Void, Void> {
 
	final static int KERNAL_WIDTH = 3;
	final static int KERNAL_HEIGHT = 3;

	int[][] kernal_blur = {
			{1, 1, 1},
			{1, 1, 1},
			{1, 1, 1}
	};

	final static int DIV_BY_9 = 9;

	/*ImageView imageAfter; // ,imageSource
	ProgressBar progressBar;
	protected TextView _field;*/
	
	
	Bitmap bitmap_Source;
	//private Handler handler;
	Bitmap afterProcess;
	String TAG = "UnsharpMask";
	
	boolean checkOnceForFurtherProcessing = true;
	int tessRepeatCount = 0;
	int tessRepeatMAXCOUNT = 5;
	int meanConfidence;
	
	/*String _path;
	public static String DATA_PATH;
	public static final String lang = "eng";
	 
	public static String recognizedText;*/
	
	private MainActivity mainActivity;

	public UnsharpMask(MainActivity act, Bitmap bitmap) {
		Log.v(TAG, "Begin constructor");
		mainActivity = act;
		
		//progressBar = new ProgressBar(act, null, android.R.attr.progressBarStyleSmall);
		//progressBar.setIndeterminate(true);
		//progressBar.set
		//progressBar.setVisibility(View.VISIBLE);

		bitmap_Source = bitmap; //BitmapFactory.decodeResource(getResources(), R.drawable.testpicture);

		//handler = new Handler();
		//StratBackgroundProcess();
	}


	private Bitmap processingBitmap(Bitmap src, int[][] knl){
		Bitmap dest = Bitmap.createBitmap(
				src.getWidth(), src.getHeight(), src.getConfig());

		int bmWidth = src.getWidth();
		int bmHeight = src.getHeight();
		int bmWidth_MINUS_2 = bmWidth - 2;
		int bmHeight_MINUS_2 = bmHeight - 2;
		int bmWidth_OFFSET_1 = 1;
		int bmHeight_OFFSET_1 = 1;

		for(int i = bmWidth_OFFSET_1; i <= bmWidth_MINUS_2; i++){
			for(int j = bmHeight_OFFSET_1; j <= bmHeight_MINUS_2; j++){

				//get the surround 7*7 pixel of current src[i][j] into a matrix subSrc[][]
				int[][] subSrc = new int[KERNAL_WIDTH][KERNAL_HEIGHT];
				for(int k = 0; k < KERNAL_WIDTH; k++){
					for(int l = 0; l < KERNAL_HEIGHT; l++){
						subSrc[k][l] = src.getPixel(i-bmWidth_OFFSET_1+k, j-bmHeight_OFFSET_1+l);
					}
				}

				//subSum = subSrc[][] * knl[][]
				long subSumA = 0;
				long subSumR = 0;
				long subSumG = 0;
				long subSumB = 0;

				for(int k = 0; k < KERNAL_WIDTH; k++){
					for(int l = 0; l < KERNAL_HEIGHT; l++){
						subSumA += (long)(Color.alpha(subSrc[k][l])) * (long)(knl[k][l]);
						subSumR += (long)(Color.red(subSrc[k][l])) * (long)(knl[k][l]);
						subSumG += (long)(Color.green(subSrc[k][l])) * (long)(knl[k][l]);
						subSumB += (long)(Color.blue(subSrc[k][l])) * (long)(knl[k][l]);
					}
				}

				subSumA = subSumA/DIV_BY_9;
				subSumR = subSumR/DIV_BY_9;
				subSumG = subSumG/DIV_BY_9;
				subSumB = subSumB/DIV_BY_9;

				int orgColor = src.getPixel(i, j);
				int orgA = Color.alpha(orgColor);
				int orgR = Color.red(orgColor);
				int orgG = Color.green(orgColor);
				int orgB = Color.blue(orgColor);

				subSumA = orgA + (orgA - subSumA);
				subSumR = orgR + (orgR - subSumR);
				subSumG = orgG + (orgG - subSumG);
				subSumB = orgB + (orgB - subSumB);

				if(subSumA <0){
					subSumA = 0;
				}else if(subSumA > 255){
					subSumA = 255; 
				}

				if(subSumR <0){
					subSumR = 0;
				}else if(subSumR > 255){
					subSumR = 255; 
				}

				if(subSumG <0){
					subSumG = 0;
				}else if(subSumG > 255){
					subSumG = 255;
				}

				if(subSumB <0){
					subSumB = 0;
				}else if(subSumB > 255){
					subSumB = 255;
				}

				dest.setPixel(i, j, Color.argb(
						(int)subSumA, 
						(int)subSumR, 
						(int)subSumG, 
						(int)subSumB));
			} 
		}

		return dest;
	}
	
	
	
	protected void performOCR()
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;

		Bitmap bitmap = BitmapFactory.decodeFile(mainActivity._path, options);
		
		
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
        

		try {
			ExifInterface exif = new ExifInterface(mainActivity._path);
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
		//Pix.
		//Binarize.otsuAdaptiveThreshold(pixs);
		TessBaseAPI baseApi = new TessBaseAPI();
		//baseApi.init(mainActivity.DATA_PATH, mainActivity.lang, TessBaseAPI.OEM_CUBE_ONLY);
		//baseApi.setPageSegMode(TessBaseAPI.PSM_AUTO);
		baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "#$%^&+=:;{}[]\\|><~`\"'*()");
		baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,
				"1234567890ABCDEFGHJKLMNPRSTVWXYZabcdefghijklmnopqrstuvwxyz");
		baseApi.setDebug(true);
		Log.v(TAG, "After setting variables");
		baseApi.init(mainActivity.DATA_PATH, mainActivity.lang); //, TessBaseAPI.OEM_CUBE_ONLY
		Log.v(TAG, "After init and before setting bitmap");
		baseApi.setImage(bitmap);
		Log.v(TAG, "After init and before getUTF8Text");
		mainActivity.recognizedText = baseApi.getUTF8Text();
		meanConfidence = baseApi.meanConfidence();
		Log.v(TAG, "OCRED TEXT: " + mainActivity.recognizedText);
		Log.v(TAG, "Mean Confidence: " + meanConfidence);
		
		/*int[] confidences = baseApi.wordConfidences();
		int netConfidence = 0;
		for(int i:confidences)
		{
			netConfidence +=i;
		}
		netConfidence /=confidences.length;*/
		
		baseApi.end();
		/*if (baseApi != null) {
		      baseApi.clear();
		    }*/
		
		/*if(!checkOnceForFurtherProcessing)
		{
			while(meanConfidence < 80 && tessRepeatCount < tessRepeatMAXCOUNT)
			{
				tessRepeatCount++;
				try{
					baseApi = new TessBaseAPI();
					//baseApi.init(mainActivity.DATA_PATH, mainActivity.lang, TessBaseAPI.OEM_CUBE_ONLY);
					baseApi.setPageSegMode(TessBaseAPI.PSM_AUTO);
					baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "#$%^&+=:;{}[]\\|><~`");
					baseApi.setDebug(true);
					baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,
							"1234567890ABCDEFGHJKLMNPRSTVWXYZabcdefghijklmnopqrstuvwxyz!.,-?");
					baseApi.init(mainActivity.DATA_PATH, mainActivity.lang);
					
					baseApi.setImage(bitmap);
					
					mainActivity.recognizedText = baseApi.getUTF8Text();
					meanConfidence = baseApi.meanConfidence();
					Log.v(TAG, "OCRED TEXT: " + mainActivity.recognizedText);
					Log.v(TAG, "Mean Confidence: " + meanConfidence);
					
					/*int[] confidences = baseApi.wordConfidences();
					int netConfidence = 0;
					for(int i:confidences)
					{
						netConfidence +=i;
					}
					netConfidence /=confidences.length; /* /
					
					baseApi.end();
				}
				catch(Exception e)
				{
					Log.v(TAG, "Error occurred while baseApi-ing the second time: ------ \n" + e.toString());
				}
			}
		}*/
		
		
		if (baseApi != null) {
		      baseApi.clear();
		    }
		
		

		// You now have the text in recognizedText var, you can do anything with it.
		// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
		// so that garbage doesn't make it to the display.

		Log.v(TAG, "OCRED TEXT: " + mainActivity.recognizedText);

		if ( mainActivity.lang.equalsIgnoreCase("eng") ) {
			mainActivity.recognizedText = mainActivity.recognizedText.replaceAll("[^a-zA-Z0-9.,!\\&*]+", " ");
		}
		
		mainActivity.recognizedText = mainActivity.recognizedText.trim();

		/*if ( recognizedText.length() != 0 ) {
			_field.setText(recognizedText);
			//_field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
			//_field.setSelection(_field.getText().toString().length());
		}*/
		
		// Cycle done.
		
		//return recognizedText;
		afterProcess = bitmap;
		
	}


	
	@Override
    protected void onPostExecute(Void result) {
        //mImageView.setImageBitmap(result);
		Log.v("AsyncTask Mein", "Entered onPostExecute");
		
		if ( mainActivity.recognizedText.length() != 0 ) {
			mainActivity._field.setText(mainActivity.recognizedText);
		}
		
		mainActivity.progressBar.setVisibility(View.GONE);
		mainActivity.processedImage.setVisibility(View.VISIBLE);
		mainActivity.processedImage.setImageBitmap(afterProcess);
		//afterProcess.recycle();
    }



	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		Log.v(TAG, "In runnable thread, before processing");
		/*int w = bitmap_Source.getWidth(), h = bitmap_Source.getHeight();
		if(w<300 && h<300)
			afterProcess = OCRImageProcessing.increaseDPI(bitmap_Source,w,h);
		//else if(w>1000 || h>1000)
			//afterProcess = OCRImageProcessing.decreaseDPI(bitmap_Source,w,h);
		else
			afterProcess=bitmap_Source;*/
		
		//afterProcess = OCRImageProcessing.createContrastBW(afterProcess, 50);
		//afterProcess = OCRImageProcessing.makeGreyScale(afterProcess);

		//afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);
		//afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);
		//afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);

		afterProcess = processingBitmap(bitmap_Source, kernal_blur);
		//afterProcess = processingBitmap(afterProcess, kernal_blur);
		//afterProcess = processingBitmap(afterProcess, kernal_blur);

		//afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);

		//afterProcess = OCRImageProcessing.makeGreyScale(afterProcess);
		//afterProcess = OCRImageProcessing.createContrastBW(afterProcess, 50);
		
		try{	
			FileOutputStream out = new FileOutputStream(mainActivity._path);
			afterProcess.compress(Bitmap.CompressFormat.JPEG, 100, out);
		}catch(Exception e)
		{
			Log.v(TAG, e.toString());
		}
		
		File pic = new File(mainActivity._path);
		Pix pix = ReadFile.readFile(pic);
		if(pix.getWidth() < 300 || pix.getHeight() < 300) pix = Scale.scale(pix, 2);
		else if(pix.getWidth() > 1000 || pix.getHeight() > 1000) pix = Scale.scale(pix, 1/2);
		pix = Convert.convertTo8(pix);
		//pix = Binarize.otsuAdaptiveThreshold(pix);
		//pix = Enhance.unsharpMasking(pix, 1, 0.2F); //gives OutOfMemoryError
		WriteFile.writeImpliedFormat(pix, pic, 100, true);
		afterProcess = WriteFile.writeBitmap(pix);
		
		Log.v(TAG, "In runnable thread, after processing");
		
		performOCR();
		/*checkOnceForFurtherProcessing = false;
		if(mainActivity.recognizedText.equals("") || mainActivity.recognizedText.equals(" ") || mainActivity.recognizedText == null || containsAny(mainActivity.recognizedText,"#$%^&+=:;{}[]\\|><~`") || meanConfidence<80)
		{
			Log.v(TAG, "Performed OCR once, return empty of null string or string containing ajeebogarid characters, performing again in background function");
			//pix.invert();
			pix = Rotate.rotate(pix, Skew.findSkew(pix));
			WriteFile.writeImpliedFormat(pix, pic, 100, true);
			afterProcess = WriteFile.writeBitmap(pix);
			performOCR();
		}*/
		Log.v("AsyncTask Mein", "End of do In Background");
		pix.recycle();
		
		return null;
	}
	
	public static boolean containsAny(String str, char[] searchChars) {
	      if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0) {
	          return false;
	      }
	      for (int i = 0; i < str.length(); i++) {
	          char ch = str.charAt(i);
	          for (int j = 0; j < searchChars.length; j++) {
	              if (searchChars[j] == ch) {
	                  return true;
	              }
	          }
	      }
	      return false;
	  }
	
	public static boolean containsAny(String str, String searchChars) {
	      if (searchChars == null) {
	          return false;
	      }
	      return containsAny(str, searchChars.toCharArray());
	  }

}