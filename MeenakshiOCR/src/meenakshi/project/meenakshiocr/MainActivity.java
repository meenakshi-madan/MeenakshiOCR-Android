package meenakshi.project.meenakshiocr;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class MainActivity extends Activity {
	
	public static String PREFS_NAME = "OCRSettings";
	
	private SharedPreferences mPreferences;
	private static final String TAG = "MainActivity.java";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mPreferences = getSharedPreferences("MeenakshiOCRSharedPreferences", Context.MODE_PRIVATE);
        boolean firstTime = mPreferences.getBoolean("welcome", true);
        if (firstTime) { 
        	Log.v(TAG, "In first time if block");
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean("welcome", false);
            editor.putString("lang", "eng");
            editor.putString("OCRTextMode", "default");
            editor.putString("DATA_PATH", getExternalFilesDir(null).getAbsolutePath() + "/");
            editor.putString("CURRENT_IMAGE_PATH", getExternalFilesDir(null).getAbsolutePath() + "/" + "/currentocr.jpg");
            editor.commit();
            Constants.initializeConstants(this);
            new CopyDataToSDAsync(this).execute();
            
            AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.about_layout,
                    (ViewGroup) findViewById(R.id.layout_root));
          
            WebView webView = (WebView)layout.findViewById(R.id.wvabout);
            if(webView==null)
            {
            	Log.v("help", "webview is null o_o");
            }
            webView.loadUrl("file:///android_asset/UI/welcome.html");
            
            imageDialog.setView(layout);
            imageDialog.create();
            imageDialog.show();
        }
        
        Constants.initializeConstants(this);
     
    }
    
	
	public void goToOCR(View view)
	{
		Intent intent = new Intent(this, OCRActivity.class);
	    startActivity(intent);
	}
	
	
	public void goToSettings(View view)
	{
		Intent intent = new Intent(this, SettingsActivity.class);
	    startActivity(intent);
	}
	
	public void goToHistory(View view)
	{
		Intent intent = new Intent(this, HistoryActivity.class);
	    startActivity(intent);
	}
	
	
	public void goToHelp(View view)
	{
		Intent intent = new Intent(this, HelpActivity.class);
	    startActivity(intent);
	}

}
