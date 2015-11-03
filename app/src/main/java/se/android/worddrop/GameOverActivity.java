package se.android.worddrop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends Activity {

    int userId;
    PowerUpManager powerup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(se.android.worddrop.R.layout.activity_gameover);
        getScoreDetails();
        powerup=new PowerUpManager(this);
        powerup.setUserId(userId);
        setTxtViewPowerupCount();
    }

    private void setTxtViewPowerupCount()
    {
        TextView tvScramble = (TextView) findViewById(R.id.tvScrambleCnt);
        tvScramble.setText(Integer.toString(powerup.getScrambleCnt()));

        TextView tvDeleteColor = (TextView) findViewById(R.id.tvDeleteColorCnt);
        tvDeleteColor.setText(Integer.toString(powerup.getDeleteColorCnt()));

        TextView tvShrinkLetter = (TextView) findViewById(R.id.tvShrinkLetterCnt);
        tvShrinkLetter.setText(Integer.toString(powerup.getShrinkLetterCnt()));
    }

    private void getScoreDetails(){
        Intent intent=getIntent();
        int score=intent.getIntExtra("Score", 0);
        userId=intent.getIntExtra("USER_ID",0);
        DBHelper helper=new DBHelper(this);
        int totalScore=helper.getTotalScore(userId);
        System.out.println("Total Score: "+totalScore);
        TextView currScore=(TextView)findViewById(R.id.currScoreVal);
        currScore.setText(""+score);
        totalScore=totalScore+score;
        TextView totScore=(TextView)findViewById(R.id.totalScoreVal);
        totScore.setText(""+totalScore);
        helper.updateTotalScore(totalScore,userId);
    }

    public void btnHomeClick(View view) {
        Intent plyermanagementIntent = new Intent(this, UserManagementActivity.class);
        startActivity(plyermanagementIntent);
        finish();
    }

    public void btnExitGameClick(View view) {
        Intent exitAppIntent = new Intent(this, UserManagementActivity.class);
        exitAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        exitAppIntent.putExtra("EXIT_CALL", true);
        startActivity(exitAppIntent);
        finish();
    }

    public void btnPlayAgainClick(View view) {
        Intent playAgainIntent = new Intent(this, PlayGameActivity.class);
        Intent playerMgrIntent = getIntent();
        playAgainIntent.putExtra("USER_ID", playerMgrIntent.getIntExtra("USER_ID", 0));
        playAgainIntent.putExtra("GAME_MODE", playerMgrIntent.getStringExtra("GAME_MODE"));
        startActivity(playAgainIntent);
        finish();
    }
}
