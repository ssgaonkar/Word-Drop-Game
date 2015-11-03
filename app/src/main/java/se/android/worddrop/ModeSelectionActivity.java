package se.android.worddrop;

/**
 * Created by PratikSanghvi on 10/23/2015.
 */


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;


public class ModeSelectionActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnMoves, btnEndless;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modeselection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnNext = (Button) findViewById(R.id.btnTimed);
        btnNext.setOnClickListener(this);

        btnMoves = (Button) findViewById(R.id.btnMoves);
        btnMoves.setOnClickListener(this);

        btnEndless = (Button) findViewById(R.id.btnEndless);
        btnEndless.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.modeselection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mi_exitApplicationModeSelection)
        {
            Intent exitAppIntent = new Intent(this, UserManagementActivity.class);
            exitAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            exitAppIntent.putExtra("EXIT_CALL", true);
            startActivity(exitAppIntent);
            finish();
            return true;
        }
        if(id ==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnTimed:{
                callPlayGameActivity("TIMED");
                break;
            }
            case R.id.btnMoves:{

                callPlayGameActivity("MOVES");
                break;
            }
            case R.id.btnEndless:{
                callPlayGameActivity("ENDLESS");
                break;
            }
        }
    }

    public void btnPowerupsClick(View view) {
        Intent powerupIntent = new Intent(this,PurchasePowerUp.class);
        Intent playerMgrIntent = getIntent();
        powerupIntent.putExtra("USER_ID", playerMgrIntent.getIntExtra("USER_ID", 0));
        startActivity(powerupIntent);
    }

    void callPlayGameActivity(String gameMode){
        Intent timedModeIntent = new Intent(ModeSelectionActivity.this, PlayGameActivity.class);
        timedModeIntent.putExtra("GAME_MODE", gameMode);
        Intent playerMgrIntent = getIntent();
        timedModeIntent.putExtra("USER_ID", playerMgrIntent.getIntExtra("USER_ID", 0));
        startActivity(timedModeIntent);
    }

}

