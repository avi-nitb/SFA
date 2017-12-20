package in.etaminepgg.sfa.Utilities;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import in.etaminepgg.sfa.Activities.LoginActivity;


public class FileFunctions {

    private static String device_name = android.os.Build.MODEL;
    public static final String[] imageExtensions = {"jpg"};

    public static void downloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        //MyUi.popUp( "url = " + imageURL + ", file = " + fileName);

        try {
            URL url = new URL(imageURL); //you can write here any link
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            //Read bytes to the Buffer until there is nothing more to read(-1).

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // Convert the Bytes read to a String.
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(baf.toByteArray());
            fos.close();


        } catch (Exception e) {
            MyUi.popUp("No internet connection : " + e.toString());
        }

        //MyUi.popUp( "URL access ok");
        return;

    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static boolean move(String src, String dst) {
        File ff = new File(src);
        File ft = new File(dst);
        try {
            copy(ff, ft);
            ff.delete();
        } catch (Exception e) {
            return (false);
        }
        return (true);
    }


    public static boolean deleteFile(String fileName) // fully qualified name
    {
        if (!fileExists(fileName)) return (true);
        try {
            File file = new File(fileName);
            boolean deleted = file.delete();
            return true;
        } catch (Exception e) {
            MyUi.popUp("deleteFile : Unable to delete " + fileName);
            return (false);
        }

    }

    public static boolean fileExists(String myPath) {
        //SQLiteDatabase checkdb = null;
        boolean checkdb = false;
        try {
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();

        } catch (Exception e) {
            return (false);
        }
        return true;
    }

    public static boolean writeTextFile(String s, String fileName) {
        // Write the string s into the text file fileName

        try {
            File myFile = new File(fileName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(s);
            myOutWriter.close();
            fOut.close();

            return (true);
        } catch (Exception e) {
            MyUi.popUp("Unable to write file " + fileName + " : " + e.toString());
            //Toast.makeText(DiudamanMain.baseContext, "Unable to write file " + fileName + " : " + e.toString(),
            //		Toast.LENGTH_LONG).show();
        }
        return (false);
    }


    // This function is not working - due to
    public static boolean writeTextFile_defunct(String s, String fileName) {
        try {
            FileOutputStream fOut = LoginActivity.baseContext.openFileOutput(fileName,
                    Activity.MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(s);

			/* ensure that everything is
			 * really written out and close */
            osw.flush();
            osw.close();

            return (true);
        } catch (Exception e) {
            Toast.makeText(LoginActivity.baseContext, "Unable to write file " + fileName + " : " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
        return (false);
    }

    public static boolean downloadDirectFile(String fileName, String destDir, String servUrl) {
        try {

            URL url = new URL(servUrl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            FileOutputStream f = new FileOutputStream(new File(destDir, fileName));


            InputStream in = c.getInputStream();

            Log.d("InputStream", "" + in);

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();


            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

    }

    public static boolean downloadBinaryFile(String fileName, String destDir, String servUrl) {
        try {

            URL url = new URL(servUrl + fileName);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            FileOutputStream f = new FileOutputStream(new File(destDir, fileName));


            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();


            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean downloadFile(String fileName, String destDir, String servUrl) {
        String loc = servUrl + fileName;
        String destFileName = destDir + File.separator + fileName;

        //String loc = "http://180.92.161.131/irshousing/toMobile/irs_logins.csv";
        //String loc = "http://www.google.com";
        HttpURLConnection urlConnection = null;

        try {
            //URL url = new URL("http://180.92.161.131/irshousing/toMobile/irs_logins.csv" );
            URL url = new URL(loc);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String s = readStream(in);
            if (s != null) {

                writeTextFile(s, destFileName);
            } else {
                Toast.makeText(LoginActivity.baseContext, "File NOT downloaded.",
                        Toast.LENGTH_LONG).show();
            }
            return (true);
        } catch (Exception e) {
            MyUi.popUp("Failed - " + e.toString());
            //e.printStackTrace();
        } finally {
            urlConnection.disconnect();

        }
        return (false);

    }

    public static String getUrlResponse(String servUrl) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost(servUrl);


            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            String Response = EntityUtils.toString(resEntity);

            if (Response.equals("ERR") || Response.equals("-1")) {
                return ("ERR");
            } else
                return (Response);

        } catch (Exception e) {
            e.printStackTrace();
            return ("ERR");
        }


    }


    public static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public static String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            MyUi.popUp("The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    public static boolean downloadFile_defunct(String fileName, String destDir, String servUrl) {
        String loc = servUrl + fileName;

        try {
            //URL url = new URL("http://180.92.161.131/irshousing/toMobile/irs_logins.csv" ); // loc );

            Toast.makeText(LoginActivity.baseContext, "accessing URL " + loc,
                    Toast.LENGTH_LONG).show();

            URL url = new URL(loc);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int fileLength = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(destDir + "/" + fileName);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                //publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            //        Toast.makeText(this, "reached here.",
            //	            Toast.LENGTH_LONG).show();

            return (true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(LoginActivity.baseContext, "returning false.",
                Toast.LENGTH_LONG).show();

        return (false);
    }

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }

    public static int uploadFile(String fileName, String toServUrl) throws ClientProtocolException, IOException {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost(toServUrl);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

/* example for setting a HttpMultipartMode */
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

///* example for adding an image part */
            FileBody fileBody = new FileBody(new File(fileName)); //image should be a String
//            builder.addPart("my_file", fileBody);

            File file = new File(fileName);
//            ContentBody cbFile = new FileBody(file, "image/jpeg");
            builder.addPart("data", fileBody);
            builder.addPart("name", new StringBody(fileName, "text/plain",
                    Charset.forName("UTF-8")));

            HttpEntity entity = builder.build();

            httppost.setEntity(entity);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            String Response = EntityUtils.toString(resEntity);

//            Log.d("Response",Response);

            if (Response.equals("ERR") || Response.equals("-1")) {
                return (-1);
            } else
                return (Integer.parseInt((String) Response));

        } catch (Exception e) {
            e.printStackTrace();
            return (-1);
        }


    }


    public static String getPathFromFileName(String fname) {

        Pattern p = Pattern.compile("^(.*)/([^/]+)$");
        Matcher m = p.matcher(fname);
        if (m.find()) {
            String path = m.group(1);
            //String name = m.group(2);
            return (path);
        }
        MyUi.popUp("Unable to get path from " + fname);
        return (fname);
    }

    public static String getNameFromFileName(String fname) {

        Pattern p = Pattern.compile("^(.*)/([^/]+)$");
        Matcher m = p.matcher(fname);
        if (m.find()) {
            //String path = m.group(1);
            String name = m.group(2);
            return (name);
        }
        MyUi.popUp("Unable to get path from " + fname);
        return (fname);
    }

    public static boolean downloadFileIfNotPresent(String fname, String servUrl) {
        // download the file if it is not there already
        java.io.File file = new java.io.File(fname);
        String n = FileFunctions.getNameFromFileName(fname);
        String p = FileFunctions.getPathFromFileName(fname);
        //MyUi.popUp( "path = " + p + " , " + " name = " + n);
        if (file.length() == 0) {
            boolean status = FileFunctions.downloadFile(n, p, servUrl);
            return (status);
        }

        // File already exists. Just return true.

        return (true);

    }


    // taken from http://stackoverflow.com/questions/3873496/how-to-get-image-path-from-images-stored-on-sd-card
    public static FileFilter filterForImageFolders = new FileFilter() {
        public boolean accept(File folder) {
            try {
                //Checking only directories, since we are checking for files within
                //a directory
                if (folder.isDirectory()) {
                    File[] listOfFiles = folder.listFiles();

                    if (listOfFiles == null) return false;

                    //For each file in the directory...
                    for (File file : listOfFiles) {
                        //Check if the extension is one of the supported filetypes
                        //imageExtensions is a String[] containing image filetypes (e.g. "png")
                        for (String ext : imageExtensions) {
                            if (file.getName().endsWith("." + ext)) return true;
                        }
                    }
                }
                return false;
            } catch (SecurityException e) {
                Log.v("debug", "Access Denied");
                return false;
            }
        }
    };

    public static File findImageDir(File aFile, String sDir) {
        if (aFile.isFile() && (!aFile.getName().startsWith(".")) &&
                aFile.getAbsolutePath().contains(sDir) &&
                aFile.getName().endsWith(".jpg")) {
            return aFile;
        } else if (aFile.isDirectory() && (!aFile.getName().startsWith("."))) {
            for (File child : aFile.listFiles()) {
                //MyUi.popUpQuick("seeing " + child + " in " + sDir );
                File found = findImageDir(child, sDir);
                if (found != null) {
                    return found;
                }//if
            }//for
        }//else
        return null;
    }//met


    public static String getPhotoDir() {

        if (device_name.equals("XOLO A700")) {
            return (Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
        }

        String ret = null;
        try {
            File extStore = Environment.getExternalStorageDirectory();
            File[] fileList = extStore.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory() && (!fileList[i].getName().startsWith("."))) {
                    //MyUi.popUpQuick("Looking at " + fileList[i].getAbsolutePath());
                    File f = FileFunctions.findImageDir(fileList[i], extStore.getAbsolutePath());
                    if (f != null) {
                        //MyUi.popUp(" match : " + f.getAbsolutePath());
                        //if( f.getName().matches(".*\\d\\d\\d\\d\\.jpg") && f.getAbsolutePath().contains("DCIM" + File.separator) )
                        if (f.getName().matches(".*\\d\\d\\d\\d.jpg") &&
                                f.getAbsolutePath().contains("DCIM") &&
                                (f.getAbsolutePath().contains("100ANDRO") || f.getAbsolutePath().contains("Camera"))
                                ) {
                            //MyUi.popUp("final match : " + f.getAbsolutePath());
                            ret = f.getParent();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            MyUi.popUp("Unable to find photo directory : " + e.toString());
            return (null);
        }
        return (ret);

    }


    public static byte[] generateKey(String password) throws Exception {
        byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }


    public static byte[] encodeFile(String key, String dbFile, String fileName, String destDir) throws Exception {

        byte[] keyStart = key.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();

        byte[] newKYE = skey.getEncoded();


        File file = new File(dbFile);

        FileInputStream fin = null;
        try {
            // create FileInputStream object
            fin = new FileInputStream(file);

            byte fileContent[] = new byte[(int) file.length()];

            // Reads up to certain bytes of data from this input stream into an array of bytes.
            fin.read(fileContent);
            //create string from byte array
//            String s = new String(fileContent);
//            System.out.println("File content: " + s);

            SecretKeySpec skeySpec = new SecretKeySpec(newKYE, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] encrypted = cipher.doFinal(fileContent);

            FileFunctions.deleteFile(dbFile);

            FileOutputStream f = new FileOutputStream(new File(destDir,fileName));

            f.write(encrypted,0,encrypted.length);


            f.close();


            return encrypted;

        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }

        return null;

    }


    public static byte[] decodeFile(String key, String dbFile, String fileName, String destDir) throws Exception {


        byte[] keyStart = key.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();

        byte[] newKYE = skey.getEncoded();


        File file = new File(dbFile);

        FileInputStream fin = null;
        try {
            // create FileInputStream object
            fin = new FileInputStream(file);

            byte fileContent[] = new byte[(int) file.length()];

            // Reads up to certain bytes of data from this input stream into an array of bytes.
            fin.read(fileContent);
            //create string from byte array
//            String s = new String(fileContent);
//            System.out.println("File content: " + s);

            SecretKeySpec skeySpec = new SecretKeySpec(newKYE, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] decrypted = cipher.doFinal(fileContent);

            FileFunctions.deleteFile(dbFile);

            FileOutputStream f = new FileOutputStream(new File(destDir,fileName));

            f.write(decrypted,0,decrypted.length);


            f.close();


            return decrypted;

        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }

        return null;

    }



}


