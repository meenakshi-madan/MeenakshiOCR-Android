package meenakshi.project.meenakshiocr;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_help, menu);
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
	
	
	public void about(View v) {


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.about_layout,
                (ViewGroup) findViewById(R.id.layout_root));
        //ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        //image.setImageDrawable(tempImageView.getDrawable());
        //image.setImageBitmap(rt);
    
        
        WebView webView = (WebView)layout.findViewById(R.id.wvabout);
        if(webView==null)
        {
        	Log.v("help", "webview is null o_o");
        }
        webView.loadUrl("file:///android_asset/UI/about.html");
        
        imageDialog.setView(layout);
        imageDialog.create();
        imageDialog.show();
            
    }
	
	public void tips(View v) {


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.about_layout,
                (ViewGroup) findViewById(R.id.layout_root));
        //ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        //image.setImageDrawable(tempImageView.getDrawable());
        //image.setImageBitmap(rt);
    
        
        WebView webView = (WebView)layout.findViewById(R.id.wvabout);
        if(webView==null)
        {
        	Log.v("help", "webview is null o_o");
        }
        webView.loadUrl("file:///android_asset/UI/tips.html");
        
        imageDialog.setView(layout);
        imageDialog.create();
        imageDialog.show();
            
    }
	
	
	
	public void buttons(View v) {


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.about_layout,
                (ViewGroup) findViewById(R.id.layout_root));
        //ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        //image.setImageDrawable(tempImageView.getDrawable());
        //image.setImageBitmap(rt);
    
        
        WebView webView = (WebView)layout.findViewById(R.id.wvabout);
        if(webView==null)
        {
        	Log.v("help", "webview is null o_o");
        }
        webView.loadUrl("file:///android_asset/UI/buttons.html");
        
        imageDialog.setView(layout);
        imageDialog.create();
        imageDialog.show();
            
    }
	
	
	public void tutorial(View v) {


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.about_layout,
                (ViewGroup) findViewById(R.id.layout_root));
        //ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        //image.setImageDrawable(tempImageView.getDrawable());
        //image.setImageBitmap(rt);
    
        
        WebView webView = (WebView)layout.findViewById(R.id.wvabout);
        if(webView==null)
        {
        	Log.v("help", "webview is null o_o");
        }
        webView.loadUrl("file:///android_asset/UI/tutorial.html");
        
        imageDialog.setView(layout);
        imageDialog.create();
        imageDialog.show();
            
    }

}
