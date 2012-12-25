package meenakshi.project.meenakshiocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;



public class CopyDataToSDAsync extends AsyncTask<Void, Void, Void> {
	
	MainActivity act;
	boolean allDone = true;
	String TAG = "CopyDataToSDAsync";
	ProgressDialog pg;
	
	
	CopyDataToSDAsync(MainActivity a)
	{
		act = a;
	}
	
	@Override
    protected void onPostExecute(Void result) {
        //mImageView.setImageBitmap(result);
		Log.v("CopData AsyncTask Mein", "Entered onPostExecute");
		
		//pg.
		
		if ( allDone )
		{
			pg.setMessage("All files have been successfully copied!");
			pg.dismiss();
		}
		else
		{
			pg.setMessage("Some files were not copied. Please try restarting the application.");
			pg.setCanceledOnTouchOutside(true);
		}
		//pg.setCancelable(true);
		//pg.dismiss();
    }
	
	
	@Override
    protected void onPreExecute() {
        //mImageView.setImageBitmap(result);
		Log.v("CopData AsyncTask Mein", "Entered onPreExecute");
		
		pg = new ProgressDialog(act);
		pg.setTitle("Data Copy Dialog");
		pg.setMessage("Please wait while necessary data is being copied to your phone.");
		//pg.setButton(ProgressDialog.BUTTON_NEUTRAL, text, listener)
		//pg.setButton(ProgressDialog.BUTTON_NEUTRAL, "End", "Closing Dialog");
		pg.show();
    }
	

	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub


		//SPECIFIC TO OCR
		String[] paths = new String[] { act.DATA_PATH, act.DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					allDone = false;
					return null;
					//Toast.makeText(getApplicationContext(), "Couldn't create required directories. OCR will not work.", Toast.LENGTH_LONG).show();
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		Log.v(TAG, "End of creating directories for loop");

		// act.lang.traineddata file with the app (in assets folder)
		// This area needs work and optimization

		if(new File(act.DATA_PATH + "tessdata/" + act.lang + ".traineddata").exists() && new File(act.DATA_PATH + "tessdata/" + act.lang + ".traineddata").delete())
			Log.v(TAG, "Deleted old eng.trainned data");
		else
			Log.v(TAG, "Did not delete old eng.trainned data");
		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".traineddata");

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

				Log.v(TAG, "Copied " + act.lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " traineddata " + e.toString());
				allDone=false;
				return null;
			}
		}

		if (!(new File(act.DATA_PATH + "tessdata/" + "equ" + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + "equ" + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + "equ" + ".traineddata");

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

				Log.v(TAG, "Copied " + "equ" + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + "equ" + " traineddata " + e.toString());
				allDone=false;
				return null;
			}
		}

		if (!(new File(act.DATA_PATH + "tessdata/" + "osd" + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + "osd" + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + "osd" + ".traineddata");

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

				Log.v(TAG, "Copied " + "osd" + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + "osd" + " traineddata " + e.toString());
				allDone=false;
				return null;
			}
		}


		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".cube.bigrams")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".cube.bigrams");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".cube.bigrams");

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

				Log.v(TAG, "Copied " + act.lang + " cube.bigrams");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " cube.bigrams " + e.toString());
				allDone=false;
				return null;
			}
		}

		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".cube.fold")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".cube.fold");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".cube.fold");

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

				Log.v(TAG, "Copied " + act.lang + " cube.fold");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " cube.fold " + e.toString());
				allDone=false;
				return null;
			}
		}


		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".cube.lm")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".cube.lm");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".cube.lm");

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

				Log.v(TAG, "Copied " + act.lang + " cube.lm");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " cube.lm " + e.toString());
				allDone=false;
				return null;
			}
		}


		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".cube.lm_")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".cube.lm_");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".cube.lm_");

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

				Log.v(TAG, "Copied " + act.lang + " cube.lm_");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " cube.lm_ " + e.toString());
				allDone = false; return null;
			}
		}


		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".cube.nn")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".cube.nn");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".cube.nn");

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

				Log.v(TAG, "Copied " + act.lang + " cube.nn");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " cube.nn " + e.toString());
				allDone = false; return null;
			}
		}

		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".cube.params")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".cube.params");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".cube.params");

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

				Log.v(TAG, "Copied " + act.lang + " cube.params");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " cube.params " + e.toString());
				allDone = false; return null;
			}
		}


		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".cube.size")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".cube.size");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".cube.size");

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

				Log.v(TAG, "Copied " + act.lang + " cube.size");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " cube.size " + e.toString());
				allDone = false; return null;
			}
		}


		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".cube.word-freq")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".cube.word-freq");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + ".cube.word-freq");

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

				Log.v(TAG, "Copied " + act.lang + " cube.word-freq");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " cube.word-freq " + e.toString());
				allDone = false; return null;
			}
		}


		if (!(new File(act.DATA_PATH + "tessdata/" + act.lang + ".tesseract_cube.nn")).exists()) {
			try {

				AssetManager assetManager = act.getAssets();
				InputStream in = assetManager.open("tessdata/" + act.lang + ".tesseract_cube.nn");
				//GZIPInputStream gin = new GZIPInputStream(in);1
				OutputStream out = new FileOutputStream(act.DATA_PATH
						+ "tessdata/" + act.lang + "tesseract_cube.nn");

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

				Log.v(TAG, "Copied " + act.lang + " tesseract_cube.nn");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + act.lang + " tesseract_cube.nn " + e.toString());
				allDone = false; 
				return null;
			}
		}


		Log.v(TAG, "End of copying traineddata if block");
		//END OF SPECIFIC TO OCR

		return null;
	}

}
