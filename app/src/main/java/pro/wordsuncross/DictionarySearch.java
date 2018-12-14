package pro.wordsuncross;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by AbdulfataiAbdulhafiz on 7/28/2018.
 */

public class DictionarySearch extends AsyncTask<String , Integer , String > {
    private Context mContext;
    private View mView;
    private AlertDialog mAlertDialog;
    private TextView tv;
    private String cWord = "";
    public DictionarySearch(Context c,  View v, AlertDialog builder){
        this.mContext = c;
        this.mView = v;
        this.mAlertDialog = builder;
        tv = (TextView)(mView.findViewById(R.id.definition_text));
    }
    @Override
    protected String doInBackground(String... strings) {
        cWord = strings[0];
        String stringResp;
        HttpURLConnection mHttpURLConnection = null;
        try{
            URL mUrl = new URL(Uri.parse("https://www.owlbot.info/api/v2/dictionary/"+strings[0]).buildUpon().build().toString());
            mHttpURLConnection= (HttpURLConnection)mUrl.openConnection();
            mHttpURLConnection.setRequestMethod("GET");
            mHttpURLConnection.connect();
            InputStream mInputStream = mHttpURLConnection.getInputStream();
            StringBuffer mStringBuffer = new StringBuffer();
            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
            if (mInputStream == null){
                return null;
            }
            String line;
            while ((line = mBufferedReader.readLine()) != null){
                mStringBuffer.append(line+"\n");
            }
            mBufferedReader.close();
            if (mStringBuffer.length() == 0){
                return null;
            }
            stringResp = mStringBuffer.toString();
            return stringResp;
        }catch (Exception e){
            Log.d(TAG, "doInBackground: "+e);
        }finally {
            if(mHttpURLConnection != null){
                mHttpURLConnection.disconnect();
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s==null){
            mView.findViewById(R.id.definition_loading).setVisibility(View.GONE);
            tv.setText("No Network Connection");
        }
        String rStr = "";
        Log.d(TAG, "onPostExecute: >>>>>>"+s);

        try {
            JSONArray mJsonArray = new JSONArray(s);
            if(mJsonArray.length() == 0){
                mView.findViewById(R.id.definition_loading).setVisibility(View.GONE);
                tv.setText("Not Found");
                return;
            }
            for (int i =0; i<mJsonArray.length();i++){
                rStr += mJsonArray.getJSONObject(i).getString("type");
                rStr += "\n";
                rStr += mJsonArray.getJSONObject(i).getString("definition");
                rStr += "\n";
                if( !mJsonArray.getJSONObject(i).isNull("example")){
                    rStr += "Example : ";
                    rStr += mJsonArray.getJSONObject(i).getString("example");
                    rStr += "\n";
                }
                rStr += "\n";
            }
        }catch (Exception e){
            mView.findViewById(R.id.definition_loading).setVisibility(View.GONE);
            Log.d(TAG, "onPostExecute: "+e);
        }
        final String cDef = rStr;
        mView.findViewById(R.id.definition_loading).setVisibility(View.GONE);
        mView.findViewById(R.id.add_definition_button).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.add_definition_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UncrossDBClass mUncrossDBClass = new UncrossDBClass(mContext);
                if(mUncrossDBClass.addWordtoDB(cWord, cDef)){
                    Toast.makeText(mContext, "Added to Archive", Toast.LENGTH_LONG).show();
                    if(mAlertDialog.isShowing()){
                        mAlertDialog.dismiss();
                    }
                }else{
                    Toast.makeText(mContext, cWord+" exists in Archive", Toast.LENGTH_LONG).show();
                    mView.findViewById(R.id.add_definition_button).setVisibility(View.GONE);
                }


            }
        });
        if(rStr != null){
            tv.setText(rStr.replaceAll("<(.*?)\\>", ""));
            return;
        }

    }

}
