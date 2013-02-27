package meenakshi.project.meenakshiocr;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class NetworkThread extends AsyncTask<Void, Void, Void> {

	HelpActivity act;
	String user, content, TAG="NetworkThread";
	boolean success;
	NetworkThread(HelpActivity act, String user, String content)
	{
		this.act = act;
		this.user = user;
		this.content = content;
	}
	
	@Override
    protected void onPostExecute(Void result) {
		
		Log.v(TAG, "Entered OnPostExecute");
		if(success)
			Toast.makeText(act.getApplicationContext(), "Your review has been submitted!", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(act.getApplicationContext(), "Something went wrong. Try again!", Toast.LENGTH_SHORT).show();
    }
	

	
	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		
		Log.v(TAG, "Entered doInBackground");
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://meenakshi-ocr.appspot.com/reviews");

		try {
			// Execute HTTP Post Request
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("user", user));
			nameValuePairs.add(new BasicNameValuePair("content", content));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			Log.v(TAG, "executed httppost ");

			HttpEntity entity = response.getEntity();

			InputStream instream = response.getEntity().getContent();

			if (entity.getContentLength() > Integer.MAX_VALUE) { throw new IllegalArgumentException(

					"HTTP entity too large to be buffered in memory"); }

			String charset = getContentCharSet(entity);

			if (charset == null) {

				charset = HTTP.DEFAULT_CONTENT_CHARSET;

			}

			Reader reader = new InputStreamReader(instream, charset);

			StringBuilder buffer = new StringBuilder();

			try {

				char[] tmp = new char[1024];

				int l;

				while ((l = reader.read(tmp)) != -1) {

					buffer.append(tmp, 0, l);

				}

			} finally {

				reader.close();

			}

			String text = buffer.toString();
			Log.v(TAG, "Response body: " + text);

			if(text.equals("success"))
				success = true;
			else
				success = false;

		} catch (ClientProtocolException e) {
			Log.v(TAG, e.toString());
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.v(TAG, "about to leave doInBackground");


		return null;
	}


	public String getContentCharSet(final HttpEntity entity) throws ParseException {

		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

		String charset = null;

		if (entity.getContentType() != null) {

			HeaderElement values[] = entity.getContentType().getElements();

			if (values.length > 0) {

				NameValuePair param = values[0].getParameterByName("charset");

				if (param != null) {

					charset = param.getValue();

				}

			}

		}

		return charset;

	}

}
