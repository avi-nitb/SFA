package in.etaminepgg.sfa.network_asynctask;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;


public class HttpRequestWorker
{


    public AsyncResponse delegate = null;
    Header[] headers;
    String[] value;
    String sessionKey;

    public HttpRequestWorker()
    {
        super();
    }

    /*
         * Method: GetRequest
         * @param: url:String,isHeaderRequired:boolean
         * @Desc : url				: URL to connect the server,
         *         isHeaderRequired	: Set dbName as Header for every request after Registration.
         */

    public String GetRequest(String url_str, String token)
    {

        ///old process//////

		/*try {

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url_str);
	*//*		httpGet.setHeader("device",   deviceId);
            httpGet.setHeader("session",  session);
			httpGet.setHeader("schoolId", schoolId);*//*
            HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String result = httpClient.execute(httpGet, responseHandler);
			result= URLDecoder.decode(result, "UTF-8");
			return result;
		}
		catch (Exception ex) {
			return "Failed " + ex;
		}*/


        /////new async get process/////

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try
        {
            URL url = new URL(url_str);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";


            while((line = reader.readLine()) != null)
            {
                buffer.append(line + "\n");
                Log.e("UTF_8_GET", line + ""); //here u ll get whole response...... :-)
            }

            return buffer.toString();


        }
        catch(MalformedURLException e)
        {

            e.printStackTrace();

        }
        catch(IOException e)
        {

            e.printStackTrace();
        }
        finally
        {
            if(connection != null)
            {
                connection.disconnect();
            }
            try
            {
                if(reader != null)
                {
                    reader.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;

    }

    /*
     * Method: POSTRequest
     * @param: url:String, content:String, isHeaderRequired:boolean
     * @Desc : url				: URL to connect the server,
     *         content			: JSON Content to send,
     *         isHeaderRequired	: Set dbName as Header for every request after Registration.
     */
    public String PostRequest(String url, String content, String requestNumber)
    {
        try
        {

            String mStatus = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost mHttpPost = new HttpPost(url);
				/*		httpGet.setHeader("device",   deviceId);
			httpGet.setHeader("session",  session);
			httpGet.setHeader("schoolId", schoolId);*/
            StringEntity se = new StringEntity(content);
            se.setContentType("application/json");
            mHttpPost.setEntity(se);

            HttpResponse httpresponse = httpclient.execute(mHttpPost);
            mStatus = EntityUtils.toString(httpresponse.getEntity(), "utf-8");

            try
            {
                mStatus = URLDecoder.decode(mStatus, "UTF-8");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            Log.e("UTF_8_POST", mStatus + "");
            return mStatus;

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return "Failed " + ex;
        }
    }
}






