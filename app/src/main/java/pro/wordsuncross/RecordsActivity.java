package pro.wordsuncross;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {
    private  UncrossDBClass mUncrossDBClass;
    private ListView mListView;
    private ArrayAdapter<String> mStringArrayAdapter;
    private ArrayList<Integer> _id = new ArrayList<>();
    private ArrayList<String> wordArrayList = new ArrayList<>();
    private ArrayList<String> definitionArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        mUncrossDBClass = new UncrossDBClass(this);
//        Toast.makeText(getApplicationContext(), "REcords : " + mUncrossDBClass.recordsNum(),Toast.LENGTH_LONG).show();
        Cursor mCursor = mUncrossDBClass.getWords();
        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()){
            _id.add(mCursor.getInt(mCursor.getColumnIndex(UncrossDBClass.T_ID)));
            wordArrayList.add(mCursor.getString(mCursor.getColumnIndex(UncrossDBClass.T_WORD)));
            definitionArrayList.add(mCursor.getString(mCursor.getColumnIndex(UncrossDBClass.T_DIFINITION)));
            mCursor.moveToNext();
        }
        mStringArrayAdapter = new ArrayAdapter<String>(this,R.layout.list_display_pane,wordArrayList);
        mListView = (ListView)findViewById(R.id.records_list_view);
        mListView.setAdapter(mStringArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View mView1 = (View)getLayoutInflater().inflate(R.layout.definition_layout, null);
                TextView mTextView =(TextView)(mView1.findViewById(R.id.definition_text));
                mView1.findViewById(R.id.definition_loading).setVisibility(View.GONE);
                mTextView.setText(definitionArrayList.get(i));
                final int i1 = i;
                new AlertDialog.Builder(RecordsActivity.this).setTitle(((String) adapterView.getItemAtPosition(i)).toUpperCase()).setView(mView1).setNegativeButton("Remove Word??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i0) {
                        if(mUncrossDBClass.deleteWord(_id.get(i1)) > 0){
                            Toast.makeText(getApplicationContext(),wordArrayList.get(i1)+" has been removed",Toast.LENGTH_LONG).show();
                            _id.remove(i1);
                            wordArrayList.remove(i1);
                            definitionArrayList.remove(i1);
                            mStringArrayAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getApplicationContext(),"Error in removing removed",Toast.LENGTH_LONG).show();
                        }

                    }
                }).create().show();
                //Toast.makeText(getApplicationContext(), "Definition : " +definitionArrayList.get(i),Toast.LENGTH_LONG).show();
            }
        });
    }
}
