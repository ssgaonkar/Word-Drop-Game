package se.android.worddrop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PurchasePowerUp extends AppCompatActivity {
    int totalScore, ctShrinkLetter, ctDeleteColor,ctScramble,userId;
    PowerUpManager powerUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(se.android.worddrop.R.layout.activity_purchase_pw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent ModeSelectionIntent = getIntent();
        userId = ModeSelectionIntent.getIntExtra("USER_ID", 0);
        powerUp = new PowerUpManager(this);
        powerUp.setUserId(userId);
        setTxtViewValues();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.purchasepowerups_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.mi_exitapplicationPurchasePower :{
                Intent exitAppIntent = new Intent(this, UserManagementActivity.class);
                exitAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                exitAppIntent.putExtra("EXIT_CALL", true);
                startActivity(exitAppIntent);
                finish();
                return true;
            }
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTxtViewValues()
    {
        //setting total score
        totalScore = powerUp.getTotalScore();
        TextView txtTotalScore = (TextView)findViewById(se.android.worddrop.R.id.txtTotalScore);
        txtTotalScore.setText(Integer.toString(totalScore));

        TextView txtviewShrinkLetter = (TextView)findViewById(se.android.worddrop.R.id.txtviewShrinkLetter);
        ctShrinkLetter =  powerUp.getShrinkLetterCnt();
        txtviewShrinkLetter.setText(Integer.toString(ctShrinkLetter));

        TextView txtViewDeleteColor = (TextView)findViewById(se.android.worddrop.R.id.txtViewDeleteColor);
        ctDeleteColor =  powerUp.getDeleteColorCnt();
        txtViewDeleteColor.setText(Integer.toString(ctDeleteColor));

        TextView txtViewScramble = (TextView)findViewById(se.android.worddrop.R.id.txtViewScramble);
        ctScramble = powerUp.getScrambleCnt();
        txtViewScramble.setText(Integer.toString(ctScramble));
    }
    public void btnPurchaseShrinkLetterClick(View view) {
        purchasePowerupWithType(PowerUpManager.PowerupType.SHRINK_LETTER);
    }

    public void btnPurchaseScrambleClick(View view) {
        purchasePowerupWithType(PowerUpManager.PowerupType.SCRAMBLE);
    }

    public void btnPurchaseDeleteColorClick(View view) {
        purchasePowerupWithType(PowerUpManager.PowerupType.DELETE_COLOR);
    }

    private void purchasePowerupWithType(PowerUpManager.PowerupType selectedPowerUp)
    {
        switch (selectedPowerUp)
        {
            case SCRAMBLE:
                if(totalScore < 250)
                {
                    Toast.makeText(PurchasePowerUp.this, getString(R.string.sorryScramblePower), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    totalScore -=250;
                    ctScramble+=5;
                    powerUp.setTotalScore(totalScore);
                    powerUp.setScrambleCnt(ctScramble);
                    setTxtViewValues();
                }
                break;

            case DELETE_COLOR:
                if(totalScore < 250)
                {
                    Toast.makeText(PurchasePowerUp.this, getString(R.string.sorryDeleteColorPower), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    totalScore -=250;
                    ctDeleteColor+=3;
                    powerUp.setTotalScore(totalScore);
                    powerUp.setDeleteColorCnt(ctDeleteColor);
                    setTxtViewValues();
                }
                break;

            case SHRINK_LETTER:
                if(totalScore < 150)
                {
                    Toast.makeText(PurchasePowerUp.this, getString(R.string.sorryShrinkLetterPower), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    totalScore -=150;
                    ctShrinkLetter+=5;
                    powerUp.setTotalScore(totalScore);
                    powerUp.setShrinkLetterCnt(ctShrinkLetter);
                    setTxtViewValues();
                }
                break;
        }

    }
}