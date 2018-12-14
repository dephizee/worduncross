package pro.wordsuncross;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class WordList extends AppCompatActivity {
    private ArrayList<String> mDict;
    private EditText enteredWord;
    private ListView mlistView;
    private Button mButton;
    private WordGen mWordGen;
    private LinearLayout mOptionBar;
    private ProgressBar mProgress;
    private ArrayAdapter<String> mAadpter;
    private ArrayAdapter<String> optionsAadpter;
    private ArrayAdapter<String> filterAadpter;
    private Spinner mSpinner;
    private Spinner mFilterSpinner;
    private AlertDialog.Builder mAlertDialogBuilder;
    private TextView tvv;
    private int[] optionCount = {2, 3, 4, 5, 6, 7, 8, 9};
    private ArrayList<Integer> optionIntCount = new ArrayList<Integer>();
    private int c, filterOptionSelected = 0;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        mWordGen = new WordGen(getApplicationContext());
        tvv = (TextView)findViewById(R.id.ex);
        if( savedInstanceState != null && savedInstanceState.containsKey("curDict")){
            mWordGen.setCurrentDictDict(savedInstanceState.getStringArrayList("curDict"));
            mWordGen.setFiltered(savedInstanceState.getBoolean("filtered"));
            if(savedInstanceState.getStringArrayList("curFilter").size() != 0){
                mWordGen.setFilteredDict(savedInstanceState.getStringArrayList("curFilter"));
            }
            mlistView = (ListView) findViewById(R.id.mlist_item);
            if(savedInstanceState.getBoolean("filtered")){
                mAadpter = new ArrayAdapter<>(this, R.layout.list_display_pane, savedInstanceState.getStringArrayList("curFilter"));
            }else {
                mAadpter = new ArrayAdapter<>(this, R.layout.list_display_pane, savedInstanceState.getStringArrayList("curDict"));
            }
            mlistView.setAdapter(mAadpter);
            tvv.setText(savedInstanceState.getString("head"));
//            Toast.makeText(getBaseContext(), ">>>>>>>>>> "+savedInstanceState.getStringArrayList("curDict").toString(), Toast.LENGTH_LONG).show();
//            Log.d(TAG, "onCreate: >>>>>>>>>>> "+savedInstanceState.getStringArrayList("curDict").toString());
//
//            mlistView.setAdapter(mAadpter);
//            mAadpter.notifyDataSetChanged();
        }else {
            mlistView = (ListView) findViewById(R.id.mlist_item);
        }
        mAlertDialogBuilder = new AlertDialog.Builder(WordList.this);
        mDict = new ArrayList<>();
        mSpinner = (Spinner) findViewById(R.id.option_size);
        mFilterSpinner = (Spinner) findViewById(R.id.filter_option);
        mOptionBar = (LinearLayout) findViewById(R.id.option_bar);
        mButton = (Button) findViewById(R.id.m_search_button);
        mProgress = (ProgressBar) findViewById(R.id.loading);
        optionsAadpter = new ArrayAdapter<>(this, R.layout.filter_display_pane, getResources().getStringArray(R.array.options_array));
        filterAadpter = new ArrayAdapter<>(this, R.layout.filter_display_pane, getResources().getStringArray(R.array.options_filter_array));
        mSpinner.setAdapter(optionsAadpter);
        mSpinner.getBackground().setColorFilter(getResources().getColor(R.color.mainfgColour), PorterDuff.Mode.SRC_ATOP);
        mFilterSpinner.setAdapter(filterAadpter);
        if (mSpinner != null) {
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i == 0){
                        return;
                    }
                    mAadpter = new ArrayAdapter<>(getBaseContext(), R.layout.list_display_pane, mWordGen.getWordWithSize(i + 1));
                    mlistView.setAdapter(mAadpter);
                    mAadpter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        enteredWord = (EditText) findViewById(R.id.entered_word);
        enteredWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE){
                    mButton.performClick();
                    return true;
                }
                return false;
            }
        });
        if(mFilterSpinner != null){
            mFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(getBaseContext(), i+" << vs >> "+l, Toast.LENGTH_LONG).show();
                    switch (i) {
                        case 0:
                            filterOptionSelected = 0;
                            return;
                        case 1:
                            filterOptionSelected = 1;
                            return;
                        default:
                            return;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        mFilterSpinner.getBackground().setColorFilter(getResources().getColor(R.color.mainfgColour), PorterDuff.Mode.SRC_ATOP);
        enteredWord = (EditText) findViewById(R.id.entered_word);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButton.setEnabled(false);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                mProgress.setVisibility(View.VISIBLE);
                new LoadWordsBehind().execute(enteredWord.getText().toString().toLowerCase());
                tvv.setText(enteredWord.getText().toString().toUpperCase());


            }
        });
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                final View mView1 = (View)getLayoutInflater().inflate(R.layout.definition_layout, null);
                if (networkInfo != null && networkInfo.isConnected()) {
                    mAlertDialogBuilder.setTitle(((String) adapterView.getItemAtPosition(i)).toUpperCase()).setView(mView1).create();
                    AlertDialog mAlertDialog = mAlertDialogBuilder.show();
                    new DictionarySearch(WordList.this, mView1, mAlertDialog).execute((String) adapterView.getItemAtPosition(i));
                    Log.d(TAG, (String) adapterView.getItemAtPosition(i) + "<<<<<<<onItemClick: >>>>>>>>>>");
                } else {
                    new AlertDialog.Builder(WordList.this).setTitle((String) adapterView.getItemAtPosition(i)).setMessage("No Internet Connection").show();
                }

            }
        });

    }

    public void filterResult(View view) {
        EditText temp = (EditText) findViewById(R.id.filter_option_chars);
        Log.d(TAG, filterOptionSelected + "<<<filterResult: " + temp.getText().toString());
//        Toast.makeText(getBaseContext(), filterOptionSelected+"<<<filterResult: "+temp, Toast.LENGTH_LONG).show();
        switch (filterOptionSelected) {
            case 0:
                mAadpter = new ArrayAdapter<>(getBaseContext(), R.layout.list_display_pane, mWordGen.getWordWithCharsStart(temp.getText().toString()));
                mlistView.setAdapter(mAadpter);
                mAadpter.notifyDataSetChanged();
                return;
            case 1:
                mAadpter = new ArrayAdapter<>(getBaseContext(), R.layout.list_display_pane, mWordGen.getWordWithChars(temp.getText().toString()));
                mlistView.setAdapter(mAadpter);
                mAadpter.notifyDataSetChanged();
                return;
            default:
                return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mAadpter != null){
            outState.putStringArrayList("curDict", mWordGen.getCurrentDictionary());
            outState.putStringArrayList("curFilter", mWordGen.getFilterDictionary());
            outState.putBoolean("filtered", mWordGen.getFiltered());
            outState.putString("head", tvv.getText().toString());
        }

    }

    private class LoadWordsBehind extends AsyncTask<String, Integer, ArrayList<String>> {

        @Override
        protected ArrayList doInBackground(String... strings) {
            ArrayList<String> mTemp = new ArrayList<>();
            try {
                mTemp = mWordGen.getWord(strings[0].toString());
                return mTemp;
            } catch (Exception e) {
                Log.e(TAG, "WordGen>>>>>: ", e);
            }
            return mTemp;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            mOptionBar.setVisibility(View.VISIBLE);
            mButton.setEnabled(true);
            mProgress.setVisibility(View.GONE);
            mAadpter = new ArrayAdapter<>(getBaseContext(), R.layout.list_display_pane, strings);
            mlistView.setAdapter(mAadpter);
            mAadpter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Saved Word").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getBaseContext(), RecordsActivity.class));
                return false;
            }
        }).setIcon(R.drawable.main_menu_icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        return super.onOptionsItemSelected(item);
    }
}
