package in.etaminepgg.sfa.network_asynctask;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.net.URLDecoder;


public class AsyncWorker extends AsyncTask<String, String, String> {
	private ProgressDialog progressDialog;
	private String response;
    private String REQUEST_NUMBER;
	public Context currentContext;
	public AsyncResponse delegate	=	null;
    String token;

    public AsyncWorker(Context context) {
        currentContext = context;

    }
	
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(currentContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please Wait....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.show();
	}
	
	@Override
	protected String doInBackground(String... params) {
		try{
		String url	            =	params[0];
		String content	        =	params[1];
        String requestType      =   params[2];
        this.REQUEST_NUMBER     =   params[3];


		if(requestType.equals("POST")) {

            HttpRequestWorker mWorker = new HttpRequestWorker();
            response = mWorker.PostRequest(url, content, REQUEST_NUMBER);


        }else if(requestType.equals("GET")){

            HttpRequestWorker mWorker = new HttpRequestWorker();
            response = mWorker.GetRequest(url,token);
        }

		return response;

		}catch(Exception ex){
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
        progressDialog.dismiss();
            try {
                result = URLDecoder.decode(result, "UTF-8");
                delegate.ReceivedResponseFromServer(result, REQUEST_NUMBER);
            }catch (Exception e){
                e.printStackTrace();
                delegate.ReceivedResponseFromServer(result, REQUEST_NUMBER);
        }
    }
}
