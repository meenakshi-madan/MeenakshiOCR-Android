package meenakshi.project.meenakshiocr;

/**This class hands the OCR processing and unsharp-masking for processing the image
 * author: Meenakshi Madan
 */

import java.io.IOException;

import magick.CompositeOperator;
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
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class UnsharpMask extends AsyncTask<Void, Integer, Void> {
 
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
	
	ProgressDialog pg;
	
	
	//Bitmap bitmap_Source;
	//private Handler handler;
	//Bitmap afterProcess;
	
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
	
	
	private String BLACK_LIST_AUTOMATIC = "#$%^&+=:;{}[]/,.!@\\|><~`\"'*()";
	private String WHITE_LIST_AUTOMATIC = "1234567890ABCDEFGHJKLMNPRSTVWXYZabcdefghijklmnopqrstuvwxyz";
	
	/*String _path;
	public static String DATA_PATH;
	public static final String lang = "eng";
	 
	public static String recognizedText;*/
	
	/** Object of OCRActivity, to access variables such as DATA_PATH and view elements **/
	private OCRActivity act;

	public UnsharpMask(OCRActivity act) {
		Log.v(TAG, "Begin constructor");
		this.act = act;
		
		ocrPref = act.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
		
		//progressBar = new ProgressBar(act, null, android.R.attr.progressBarStyleSmall);
		//progressBar.setIndeterminate(true);
		//progressBar.set
		//progressBar.setVisibility(View.VISIBLE);

		//bitmap_Source = bitmap; //BitmapFactory.decodeResource(getResources(), R.drawable.testpicture);

		//handler = new Handler();
		//StratBackgroundProcess();
	}
	
	
	@Override
    protected void onPreExecute() {
        //mImageView.setImageBitmap(result);
		Log.v("CopData AsyncTask Mein", "Entered onPreExecute");
		
		//pg = new ProgressDialog(act.getApplicationContext());
		//pg = new ProgressDialog(act.getApplicationContext());
		//pg.setTitle("Processing. . .");
		//pg.setMessage("Processing. . .");
		//pg.setButton(ProgressDialog.BUTTON_NEUTRAL, text, listener)
		//pg.setButton(ProgressDialog.BUTTON_NEUTRAL, "End", "Closing Dialog");
		//.setMax(10);
		//pg.setIndeterminate(false);
		//pg.show();
		
		//act.mImageView2.setImageBitmap(null);
    }
	
	
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
        //pg.setProgress(progress[0]);
       // if(progress[0]==1)
       // 	pg.setMessage(text_original);
    }
	
	
	/** Displays text to the user, hides progress bar
	 * 
	 */
	
	@Override
    protected void onPostExecute(Void result) {
        //mImageView.setImageBitmap(result);
		Log.v("AsyncTask Mein", "Entered onPostExecute");
		
		//if(afterProcess != null)
		//{
			//act.mImageView.setImageBitmap(beforeProcess);
			//act.mImageView2.setImageBitmap(afterProcess);
		//}
		//act.mImageView3.setImageBitmap(mask);
		
		//act.recognizedText = "bib";
		
		if(meanConfidence_original > meanConfidence_processed)
		{
			act.recognizedText = text_original;
		}
		else {
			act.recognizedText = text_processed;
		}
		
		//pg.dismiss();
		
		if ( act.recognizedText.length() != 0 ) {
			act._field.setText(act.recognizedText);
			//act._field.setVisibility(View.VISIBLE);
			//pg.setMessage("Done!");
			//pg.dismiss();
			//((Button)act.findViewById(R.id.btn_copyToClipBoard)).setVisibility(View.VISIBLE);
			//((Button)act.findViewById(R.id.btn_googleIt)).setVisibility(View.VISIBLE);
			//((Button)act.findViewById(R.id.btn_saveToFile)).setVisibility(View.VISIBLE);
			//((LinearLayout)act.findViewById(R.id.layout_bottombtns)).setVisibility(View.VISIBLE);
		}
		else
		{
			Toast.makeText(act, "Oops,  no text found!", Toast.LENGTH_SHORT).show();
			//pg.setMessage("Oops, no text found!");
			//pg.setCanceledOnTouchOutside(true);
		}
		
		//act.progressBar.setVisibility(View.GONE);
		//act.processedImage.setVisibility(View.VISIBLE);
		//act.processedImage.setImageBitmap(afterProcess);
		//afterProcess.recycle();
    }


	/**
	 * Function that performs unsharp masking on the Bitmap object
	 * @param src - Bitmap object, the bitmap image to perform the unsharp maskin on
	 * @param knl - kernal 2D array
	 * @return processed Bitmap
	 */
	
	/*private Bitmap processingBitmap(Bitmap src, int[][] knl){
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
	}*/
	
	
	
	/** Performs OCR on the bitmap at _path on SDCARD. Also performs any orientation required
	 * 
	 * 
	 */
	
	protected void performOCR(int level)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;

		Bitmap bitmap = BitmapFactory.decodeFile(Constants.CURRENT_IMAGE_PATH, options);
		
		
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

		// _image.setImageBitmap( bitmap );
		
		//mImageView.setImageBitmap(bitmap);
		
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
		if(level == this.LEVEL_ORIGINAL)
		{
			text_original = baseApi.getUTF8Text();
			meanConfidence_original = baseApi.meanConfidence();
			
			Log.v(TAG, "OCRED TEXT: " + text_original);
			Log.v(TAG, "Mean Confidence: " + meanConfidence_original);
		}
		else if(level == this.LEVEL_PROCESSED)
		{
			text_processed = baseApi.getUTF8Text();
			meanConfidence_processed = baseApi.meanConfidence();
			
			Log.v(TAG, "OCRED TEXT: " + text_processed);
			Log.v(TAG, "Mean Confidence: " + meanConfidence_processed);
		}
		//act.recognizedText = baseApi.getUTF8Text();
		//meanConfidence = baseApi.meanConfidence();
		//Log.v(TAG, "OCRED TEXT: " + act.recognizedText);
		//Log.v(TAG, "Mean Confidence: " + meanConfidence);
		
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

		//Log.v(TAG, "OCRED TEXT: " + act.recognizedText);

		//if ( Constants.LANG.equalsIgnoreCase("eng") ) {
		//	act.recognizedText = act.recognizedText.replaceAll("[^a-zA-Z0-9.,-:;'\"()@$><?!]+", " ");
		//}
		
		//act.recognizedText = act.recognizedText.trim();

		/*if ( recognizedText.length() != 0 ) {
			_field.setText(recognizedText);
			//_field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
			//_field.setSelection(_field.getText().toString().length());
		}*/
		
		// Cycle done.
		
		//return recognizedText;
		//afterProcess = bitmap;
		
	}
	
	
	void performProcessing()
	{
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

		//afterProcess = processingBitmap(afterProcess, kernal_blur);
		//afterProcess = processingBitmap(afterProcess, kernal_blur);
		//afterProcess = processingBitmap(afterProcess, kernal_blur);

		//afterProcess = OCRImageProcessing.applyGaussianBlur(afterProcess);

		//afterProcess = OCRImageProcessing.makeGreyScale(afterProcess);
		//afterProcess = OCRImageProcessing.createContrastBW(afterProcess, 50);
		
		//double skew=0;
		
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
			
			
			//QuantizeInfo qi = new QuantizeInfo();
			//qi.setNumberColors(2);
			//qi.setDither(100);
			//qi.setColorspace(ColorspaceType.HWBColorspace);
			//if(m.quantizeImage(qi)) Log.v(TAG, "quant conversion successful");
			//else Log.v(TAG, "quant conversion unsuccessful");
			
			//m.unsharpMaskImage(6.8, 3, 2.69, 0);
			//if(m.quantizeImage(new QuantizeInfo())) Log.v(TAG, "grayscale conversion successful");
			//else Log.v(TAG, "grayscale conversion unsuccessful");
			//m = m.enhanceImage();
			//m = m.unsharpMaskImage(6.8, 0, 2.69, 0);
			//if(m.contrastImage(true)) Log.v(TAG, "contrast conversion successful");
			//else Log.v(TAG, "contrast conversion unsuccessful");
			//m = m.medianFilterImage(3);
			//m = m.reduceNoiseImage(3);
			
			//m.setFileName(Constants.CURRENT_IMAGE_PATH); //give new location
			//m.writeImage(mi); //save
			
			//MagickImage mask = new MagickImage(mi);
			//mask = m.cloneImage(m.getWidth(), m.getHeight(), false);
			//if(mask == null) Log.v(TAG, "mask is indeed null");
			//mask.quantizeImage(new QuantizeInfo());
			//mask = mask.blurImage(6.8, 3);
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
			//mask = mask.gaussianBlurImage(3, 2);
			//mask = mask.unsharpMaskImage(6.8, 0, 2.69, 0);
			
			//mask.contrastImage(true);
			//m = m.despeckleImage();
			//mask = mask.reduceNoiseImage(3);
			//mask = mask.medianFilterImage(3);
			/*PixelPacket pp = mask.getBackgroundColor();
			int bg = pp.getBlue();
			Log.v(TAG, "BG color return by getBackgroundColor is: " + bg);
			if(bg < 0.5){
				pp.setOpacity(1);
				pp.setBlue(0);
				pp.setGreen(0);
				pp.setRed(0);
				mask.setBackgroundColor(pp);
				bg = 1;
			}
			else {
				pp.setOpacity(1);
				pp.setBlue(1);
				pp.setGreen(1);
				pp.setRed(1);
				mask.setBackgroundColor(pp);
				bg = 0;
			}*/
			//m.transformRgbImage(ColorspaceType.TransparentColorspace);
			//if(m.compositeImage(CompositeOperator.ChangeMaskCompositeOp, mask, 0, 0)) Log.v(TAG, "composite conversion successful");
			//else Log.v(TAG, "composite conversion unsuccessful");
			
			//if(m2.compositeImage(CompositeOperator.HardLightCompositeOp, m, 0, 0)) Log.v(TAG, "composite conversion successful");
			//else Log.v(TAG, "composite conversion unsuccessful");
			
			//m.setBackgroundColor(new PixelPacket(1,1,1,1));
			
			//m.setMatte(true);
			//m = m.reduceNoiseImage(3);
			//m = m.unsharpMaskImage(6.8, 0, 2.69, 0);
			//m.normalizeImage();
			//m = m.sharpenImage(3, 0);
			
			
			
			/*try {
				byte blob[] = mask.imageToBlob(mi);
				FileOutputStream fos = new FileOutputStream(new File(Constants.CURRENT_IMAGE_PATH));
				fos.write(blob);
				fos.close();
			}
			catch (Exception e) {
				e.printStackTrace();
				Log.v(TAG, "failed to write magick'd stuff to sdcard");
			}*/
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
		
		/*try{	
			FileOutputStream out = new FileOutputStream(Constants.CURRENT_IMAGE_PATH);
			afterProcess.compress(Bitmap.CompressFormat.JPEG, 100, out);
			
		}catch(Exception e)
		{
			Log.v(TAG, e.toString());
		}*/
		
		Log.v(TAG, "After saving file to sdcard");
		
		//File pic = new File(Constants.CURRENT_IMAGE_PATH);
		//Pix pix = ReadFile.readFile(pic);
		//pix = AdaptiveMap.backgroundNormMorph(pix, 16, 3, 200);
		//pix = Enhance.unsharpMasking(pix, 3, 0.7F);
		//if(pix.getWidth() < 300 || pix.getHeight() < 300) pix = Scale.scale(pix, 2);
		//else if(pix.getWidth() > 1200 || pix.getHeight() > 1200) pix = Scale.scale(pix, 1/2);
		//pix = Convert.convertTo8(pix);
		
		
		/*afterProcess = WriteFile.writeBitmap(pix);
		double skew = doIt(afterProcess);
		Log.v(TAG, "After doIt, skew = " + skew);*/
		
		/*afterProcess = WriteFile.writeBitmap(pix);
		Deskew d = new Deskew(afterProcess);
		double skew = d.GetSkewAngle();
		Log.v(TAG, "After Deskew, skew = " + skew);*/
		
		
		//Log.v(TAG, "Skew: " + Skew.findSkew(pix));
		
		/*afterProcess = WriteFile.writeBitmap(pix);
		Deskew d = new Deskew(afterProcess);
		double skew = -d.GetSkewAngle();
		
		pix = Rotate.rotate(pix, (float)skew, true);
		Log.v(TAG, "Leptonica rotated for skew angle: " + -skew);
		//pix = Binarize.otsuAdaptiveThreshold(pix);*/
		
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
		//pix.recycle();
		
		return null;
	}
	
	/** Check if a given String contains any of the characters in the given array
	 * 
	 * @param str source string to check for characters
	 * @param searchChars sequence of characters to check for in source string
	 * @return boolean value - true if string contains any character, false otherwise
	 */
	
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
	
	/** Computes whether the string contains any character from the given sequence of characters
	 * 
	 * @param str string to search
	 * @param searchChars sequence of characters to look for
	 * @return boolean value, true if string contains any characters from sequence, false otherwise
	 */
	
	public static boolean containsAny(String str, String searchChars) {
	      if (searchChars == null) {
	          return false;
	      }
	      return containsAny(str, searchChars.toCharArray());
	  }

}