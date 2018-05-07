package in.etaminepgg.sfa.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import in.etaminepgg.sfa.Activities.DashboardActivity;
import in.etaminepgg.sfa.Activities.SkuDetailsActivity;
import in.etaminepgg.sfa.Activities.SkuVideoActivity;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.FileOpen;
import in.etaminepgg.sfa.Utilities.RoundedCornersTransformation;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_SKU_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.RS;
import static in.etaminepgg.sfa.Utilities.ConstantsA.sCorner;
import static in.etaminepgg.sfa.Utilities.ConstantsA.sMargin;

/**
 * Created by etamine on 6/6/17.
 */

public class AllSKUsAdapter extends RecyclerView.Adapter<AllSKUsAdapter.SkuInfoViewHolder>
{
    private List<Sku> skuList;

    int read = 1;


    public class Downloader {
        Context context;
        File directory;
        String fileURL;
        ProgressDialog progressDialog;

        private class DownloadFile extends AsyncTask<String, String, String> {
            private DownloadFile() {
            }

            protected void onPreExecute() {
                super.onPreExecute();
                Downloader.this.showDialog();
            }

            protected String doInBackground(String... params) {
                try {
                    FileOutputStream f = new FileOutputStream(Downloader.this.directory);
                    HttpURLConnection c = (HttpURLConnection) new URL(Downloader.this.fileURL).openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int len1 = in.read(buffer);
                        if (len1 <= 0) {
                            break;
                        }
                        f.write(buffer, 0, len1);
                    }
                    f.flush();
                    f.close();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String file_url) {
                AllSKUsAdapter.Downloader.this.progressDialog.dismiss();

                AllSKUsAdapter.Downloader.this.showPdf();
               /* if (Downloader.this.fileURL.equals("http://etaminepgg.in/sfa/sites/default/files/images/sku_1519904157.pdf")) {
                    Downloader.this.showPdf();
                }*/
            }
        }

        public void DownloadFile(Context mContext, String mFileURL, File mDirectory) {
            this.context = mContext;
            this.fileURL = mFileURL;
            this.directory = mDirectory;
            new DownloadFile().execute(new String[]{this.fileURL});
        }

        private void showDialog() {
            this.progressDialog = new ProgressDialog(this.context);
            this.progressDialog.setProgressStyle(0);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setTitle("Please Wait..");
            this.progressDialog.setMessage("Preparing to download ...");
            this.progressDialog.show();
        }

        private void showPdf() {

            //File myFile = new File(Environment.getExternalStorageDirectory() + "/pptx/Read.pdf");
            try
            {
                FileOpen.openFile(this.context, directory);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
           /* File file = new File(Environment.getExternalStorageDirectory() + "/pptx/Read.pptx");
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-powerpoint");
            this.context.startActivity(intent);*/
        }
    }

    public AllSKUsAdapter(List<Sku> skuList)
    {
        this.skuList = skuList;
    }

    @Override
    public SkuInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_sku, parent, false);
        return new SkuInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SkuInfoViewHolder skuInfoViewHolder, int position)
    {
        String skuID = skuList.get(position).getSkuId();
        String skuName = skuList.get(position).getSkuName();
        String skuPrice = skuList.get(position).getSkuPrice();
        String skuCategory = skuList.get(position).getSkuCategory();
        String sku_photo_url = skuList.get(position).getSkuPhotoSource();

        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_name, skuName);

        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(RS + skuPrice);
        skuInfoViewHolder.skuCategory_TextView.setText("Category : "+skuList.get(position).getSkuCategoryDescription());
        if(sku_photo_url == null){


        }else {


            Glide.with(skuInfoViewHolder.itemView.getContext()).load(sku_photo_url).error(R.drawable.ic_tiffin_box).bitmapTransform(new RoundedCornersTransformation( skuInfoViewHolder.itemView.getContext(),sCorner, sMargin)).into(skuInfoViewHolder.skuPhoto_ImageView);
        }
    }

    @Override
    public int getItemCount()
    {
        return skuList.size();
    }

    class SkuInfoViewHolder extends RecyclerView.ViewHolder
    {
        ImageView skuPhoto_ImageView, addSkuToCart_ImageView;
        TextView skuName_TextView, skuPrice_TextView, skuCategory_TextView;

        TextView sku_catalogue;
        TextView sku_video;

        SkuInfoViewHolder(final View itemView)
        {
            super(itemView);
            skuPhoto_ImageView = (ImageView) itemView.findViewById(R.id.skuPhoto_ImageView);
            addSkuToCart_ImageView = (ImageView) itemView.findViewById(R.id.addSkuToCart_ImageView);
            skuName_TextView = (TextView) itemView.findViewById(R.id.skuName_TextView);
            skuPrice_TextView = (TextView) itemView.findViewById(R.id.skuPrice_TextView);
            skuCategory_TextView = (TextView) itemView.findViewById(R.id.sku_SO_Attr_TextView);

            this.sku_video = (TextView) itemView.findViewById(R.id.video_id);
            this.sku_catalogue = (TextView) itemView.findViewById(R.id.ppt_id);

            addSkuToCart_ImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuName = itemView.getTag(R.string.tag_sku_name).toString();
                    //new DbUtils().addToSalesOrderOrPickRetailer(skuID, skuName, skuPrice, itemView.getContext());
                    new Utils().pickAttributeValuesOrSelectRetailer(skuID, skuName, skuPrice, itemView.getContext());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    Utils.launchActivityWithExtra(view.getContext(), SkuDetailsActivity.class, KEY_SKU_ID, skuID);
                }
            });




            skuPhoto_ImageView.setImageResource(R.drawable.ic_tumbler);


            this.sku_video.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    String video_url= DbUtils.getSkuVideoURL(itemView.getTag(R.string.tag_sku_id).toString());
                    if(video_url != null && video_url.length()>3){

                        Utils.launchActivityWithExtra(view.getContext(), SkuVideoActivity.class, ConstantsA.KEY_SKU_ID, video_url);

                    }else {

                        Utils.showToast(itemView.getContext(),"There is no video for this sku");
                    }
                }
            });
            this.sku_catalogue.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    AllSKUsAdapter.this.read = 1;
                    AllSKUsAdapter.SkuInfoViewHolder.this.Download_pdf(AllSKUsAdapter.this.read);
                }
            });

        }

        private void Download_pdf(int read) {
            if (isInternetOn()) {
                try {
                    File folder = new File(Constants.appSpecificDirectoryPath,"Catalogues");

                    if(!folder.exists()){


                        folder.mkdir();
                    }
                    File file=null;
                    String sku_catalogue_url= DbUtils.getSkuCatalogueURL(itemView.getTag(R.string.tag_sku_id).toString());
                    try {

                        if(sku_catalogue_url != null && sku_catalogue_url.length()>3){


                            file = new File(folder, DashboardActivity.substringAfterLastSeparator(sku_catalogue_url, "/"));
                            if(!file.exists()){

                                file.createNewFile();
                            }

                        }else {

                            Utils.showToast(this.itemView.getContext(),"There is no catalogue for this sku");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if (read == 1 && sku_catalogue_url !=null && sku_catalogue_url.length()>3 ) {

                        new Downloader().DownloadFile(this.itemView.getContext(), sku_catalogue_url, file);

                        return;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            Toast.makeText(this.itemView.getContext(), "Please enable your internet!", Toast.LENGTH_LONG).show();
        }

        public final boolean isInternetOn() {
            try {
                Context context = this.itemView.getContext();
                this.itemView.getContext();
                ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
                return (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED || connec.getNetworkInfo(1).getState() != NetworkInfo.State.DISCONNECTED) ? false : false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
