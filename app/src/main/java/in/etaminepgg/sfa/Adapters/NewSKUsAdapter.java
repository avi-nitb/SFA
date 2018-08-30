package in.etaminepgg.sfa.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
import in.etaminepgg.sfa.Activities.SkuListByGenreActivity;
import in.etaminepgg.sfa.Activities.SkuVideoActivity;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.FileOpen;
import in.etaminepgg.sfa.Utilities.RoundedCornersTransformation;
import in.etaminepgg.sfa.Utilities.Utils;

public class NewSKUsAdapter extends Adapter<NewSKUsAdapter.SkuInfoViewHolder> {
    int read = 1;
    private List<Sku> skuList;

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
                Downloader.this.progressDialog.dismiss();

                Downloader.this.showPdf();
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

    class SkuInfoViewHolder extends ViewHolder {
        ImageView addSkuToCart_ImageView;
        TextView skuCategory_TextView;
        TextView skuName_TextView;
        ImageView skuPhoto_ImageView;
        TextView skuPrice_TextView;
        TextView sku_catalogue;
        TextView sku_video;

        SkuInfoViewHolder(final View itemView) {
            super(itemView);
            this.skuPhoto_ImageView = (ImageView) itemView.findViewById(R.id.skuPhoto_ImageView);
            this.addSkuToCart_ImageView = (ImageView) itemView.findViewById(R.id.addSkuToCart_ImageView);
            this.skuName_TextView = (TextView) itemView.findViewById(R.id.skuName_TextView);
            this.skuPrice_TextView = (TextView) itemView.findViewById(R.id.skuPrice_TextView);
            this.skuCategory_TextView = (TextView) itemView.findViewById(R.id.sku_SO_Attr_TextView);
            this.sku_video = (TextView) itemView.findViewById(R.id.video_id);
            this.sku_catalogue = (TextView) itemView.findViewById(R.id.ppt_id);

            this.addSkuToCart_ImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    new Utils().pickAttributeValuesOrSelectRetailer(skuID, itemView.getTag(R.string.tag_sku_name).toString(), skuPrice, itemView.getContext(), SkuListByGenreActivity.retailer_id_from_SOT,SkuListByGenreActivity.mobile_retailer_id_from_SOT,SkuListByGenreActivity.isNewRegular_from_SOT);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Utils.launchActivityWithExtra(view.getContext(), SkuDetailsActivity.class, ConstantsA.KEY_SKU_ID, itemView.getTag(R.string.tag_sku_id).toString());
                }
            });
            this.sku_video.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    String video_url= DbUtils.getSkuVideoURL(itemView.getTag(R.string.tag_sku_id).toString());
                    if(video_url != null && video_url.length()>3 && !video_url.equalsIgnoreCase(ConstantsA.NOT_PRESENT)){

                        Utils.launchActivityWithExtra(view.getContext(), SkuVideoActivity.class, ConstantsA.KEY_SKU_ID, video_url);

                    }else {

                        Utils.showToast(itemView.getContext(),"There is no video for this sku");
                    }
                }
            });
            this.sku_catalogue.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    NewSKUsAdapter.this.read = 1;
                    SkuInfoViewHolder.this.Download_pdf(NewSKUsAdapter.this.read);
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

                        if(sku_catalogue_url != null && sku_catalogue_url.length()>3 && !sku_catalogue_url.equalsIgnoreCase(ConstantsA.NOT_PRESENT)){


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
                    if (read == 1 && sku_catalogue_url !=null && sku_catalogue_url.length()>3 && !sku_catalogue_url.equalsIgnoreCase(ConstantsA.NOT_PRESENT)) {

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
                if (connec.getNetworkInfo(0).getState() == State.CONNECTED || connec.getNetworkInfo(0).getState() == State.CONNECTING || connec.getNetworkInfo(1).getState() == State.CONNECTING || connec.getNetworkInfo(1).getState() == State.CONNECTED) {
                    return true;
                }
                return (connec.getNetworkInfo(0).getState() == State.DISCONNECTED || connec.getNetworkInfo(1).getState() != State.DISCONNECTED) ? false : false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public NewSKUsAdapter(List<Sku> skuList) {
        this.skuList = skuList;
    }

    public SkuInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SkuInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_sku, parent, false));
    }

    public void onBindViewHolder(SkuInfoViewHolder skuInfoViewHolder, int position) {
        String skuID = ((Sku) this.skuList.get(position)).getSkuId();
        String skuName = ((Sku) this.skuList.get(position)).getSkuName();
        String skuPrice = ((Sku) this.skuList.get(position)).getSkuPrice();
        String skuCategory = ((Sku) this.skuList.get(position)).getSkuCategory();
        String sku_photo_url = ((Sku) this.skuList.get(position)).getSkuPhotoSource();
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_name, skuName);
        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(ConstantsA.RS + skuPrice);
        skuInfoViewHolder.skuCategory_TextView.setText("Category : " + ((Sku) this.skuList.get(position)).getSkuCategoryDescription());
        if(sku_photo_url == null){


        }else {


            Glide.with(skuInfoViewHolder.itemView.getContext()).load(sku_photo_url).error(R.drawable.ic_tiffin_box).bitmapTransform(new RoundedCornersTransformation(skuInfoViewHolder.itemView.getContext(), 15, 2)).into(skuInfoViewHolder.skuPhoto_ImageView);
        }
    }

    public int getItemCount() {
        return this.skuList.size();
    }
}