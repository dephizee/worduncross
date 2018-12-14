package pro.wordsuncross;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        Glide.with(this).load(R.drawable.bgicon).override((mDisplayMetrics.widthPixels/10)*7, (mDisplayMetrics.heightPixels/10)*7).into((ImageView)findViewById(R.id.slide_image));
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    Thread.sleep(3000);
                }catch (Exception e){
                    Log.e("TAG", "onCreate: ", e);
                }
                Intent i = new Intent(getBaseContext(), WordList.class);
                startActivity(i);
                finish();
            }
        }.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}
