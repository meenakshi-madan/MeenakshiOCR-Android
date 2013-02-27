package meenakshi.project.meenakshiocr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

public class HelpActivity extends Activity {

	static String TAG="HelpActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/*@Override
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
		}
		return super.onOptionsItemSelected(item);
	}*/
	
	
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
	
	public void review(View v)
	{
		final SharedPreferences mPreferences = getSharedPreferences("MeenakshiOCRSharedPreferences", Context.MODE_PRIVATE);
        final String userName = mPreferences.getString("userName1", "1");
        if(userName.equals("1"))
        {
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);

        	alert.setTitle("New User");
        	alert.setMessage("Please enter an alias.");

        	// Set an EditText view to get user input 
        	final EditText input = new EditText(this);
        	alert.setView(input);

        	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			String value = input.getText().toString();
        			if(!value.matches("^[a-zA-Z ]*$") || value.equals(" "))
        			{
        				Toast.makeText(getApplicationContext(), "That does not look like a name!", Toast.LENGTH_SHORT).show();
        			}
        			else
        			{
        				SharedPreferences.Editor editor = mPreferences.edit();
        	            editor.putString("userName1", value);
        	            editor.commit();
        	            content(value);
        			}
        		}
        	});

        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			// Canceled.
        		}
        	});

        	alert.show();
        }
        else
        {
        	content(userName);
        }
        
	}
	
	public void content(final String user)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Product Review");
    	alert.setMessage("Please enter your review/feedback.");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			String value = input.getText().toString();
    			if(value.length()>0)
    			{
    				Log.v(TAG, "In value.length()>0");
    				submit(user, value);

    			}
    		}
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			// Canceled.
    		}
    	});

    	alert.show();
	}
	
	
	public void submit(String user, String msg)
	{
		new NetworkThread(this, user, msg).execute();
	}
	
	
	/*public void website(View v)
	{
		AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.about_layout,
                (ViewGroup) findViewById(R.id.layout_root));
        //ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        //image.setImageDrawable(tempImageView.getDrawable());
        //image.setImageBitmap(rt);
    
        
        WebView webView = (WebView)layout.findViewById(R.id.wvabout);
        webView.getSettings().setJavaScriptEnabled(true);
        if(webView==null)
        {
        	Log.v("help", "webview is null o_o");
        }
        webView.loadUrl("http://meenakshi-ocr.appspot.com");
        
        imageDialog.setView(layout);
        imageDialog.create();
        imageDialog.show();
	}*/
	
	public void website(View v)
	{
		Uri uriUrl = Uri.parse("http://meenakshi-ocr.appspot.com"); 
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		startActivity(launchBrowser); 

	}
	
	
	public void community(View v)
	{
		Uri uriUrl = Uri.parse("http://meenakshi-ocr-fofou.appspot.com/Too-Lazy-To-Type"); 
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		startActivity(launchBrowser); 

	}
}
