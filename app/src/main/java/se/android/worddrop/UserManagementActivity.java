package se.android.worddrop;

/**
 * Created by PratikSanghvi on 10/23/2015.
 */


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class UserManagementActivity extends AppCompatActivity implements OnClickListener, OnItemSelectedListener{

    Spinner spinnerExistingUsers;
    EditText eTxtUserName;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check whether activity is started for killing the application.
        Intent exitCallCheckIntent = getIntent();
        boolean isExitCall = false;
        if (exitCallCheckIntent != null){
            isExitCall = exitCallCheckIntent.getBooleanExtra("EXIT_CALL",false);
            if(isExitCall){
                finish();
            }
        }

        setContentView(se.android.worddrop.R.layout.activity_playermanagement);

        //Initializing DBHelper with this class Context
        dbHelper =  new DBHelper(this);

        spinnerExistingUsers = (Spinner)findViewById(se.android.worddrop.R.id.spinnerExistingPlayers);
        spinnerExistingUsers.setOnItemSelectedListener(this);
        loadSpinnerData();

        //Next button onClick Listener. It takes to Mode selection screen.
        Button btnNext = (Button) findViewById(se.android.worddrop.R.id.btnNext);
        btnNext.setOnClickListener(this);

        //New Player textbox
        eTxtUserName = (EditText)findViewById(se.android.worddrop.R.id.eTxtPlayerName);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Releases DB connections
    protected void finalize() throws Throwable{
        try{
            dbHelper.closeConnections();}
        catch (Throwable t)
        {
            Log.d("UserManagmentActivity", t.getMessage());
        }
        finally {
            super.finalize();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Exit Application handler
        if (id == R.id.mi_exitApplication) {
            Intent exitAppIntent = new Intent(this, UserManagementActivity.class);
            exitAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            exitAppIntent.putExtra("EXIT_CALL", true);
            startActivity(exitAppIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case se.android.worddrop.R.id.btnNext:{
                String userName = "";
                boolean isUserSelected=true;
                String newUserName = eTxtUserName.getText().toString();
                if (newUserName != null && newUserName != "" && !newUserName.equals("")) {
                    //Check username 15 character limit
                    if(newUserName.length() <= 15){
                        ArrayList existingUsersList = dbHelper.getAllUsers();
                        if(!existingUsersList.contains(newUserName)){
                            if (dbHelper.insertUser(newUserName)) {
                                Toast.makeText(UserManagementActivity.this, getString(R.string.newUserCreated), Toast.LENGTH_SHORT).show();
                                eTxtUserName.setText("");
                                loadSpinnerData();
                                userName = newUserName;
                                dbHelper.setPowerupCounts();
                            }
                        }
                        else{
                            showUserAlreadyExistsAlert();
                            eTxtUserName.setText("");
                            isUserSelected=false;
                        }
                    }
                    else {
                        showUserNameLengthAlert();
                        eTxtUserName.setText("");
                        isUserSelected=false;
                    }
                } else {
                    if(spinnerExistingUsers.getSelectedItem()!=null){
                        String existingUserName = spinnerExistingUsers.getSelectedItem().toString();
                        userName = existingUserName;
                    }else{
                        showNoUserSelectedAlert();
                        isUserSelected=false;
                    }
                }
                if(isUserSelected){
                    Intent levelSelectionIntent = new Intent(UserManagementActivity.this, ModeSelectionActivity.class);
                    levelSelectionIntent.putExtra("USER_ID", dbHelper.getUserId(userName));
                    startActivity(levelSelectionIntent);
                }
                break;
            }
        }
    }

    private void showNoUserSelectedAlert(){
        final AlertDialog alertDialog = new AlertDialog.Builder(UserManagementActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getString(R.string.noUserSelected));
        alertDialog.setMessage(getString(R.string.noUserSelectedMsg));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showUserNameLengthAlert(){
        final AlertDialog alertDialog = new AlertDialog.Builder(UserManagementActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getString(R.string.userRegistrationError));
        alertDialog.setMessage(getString(R.string.userRegistrationErrorMsg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showUserAlreadyExistsAlert(){
        final AlertDialog alertDialog = new AlertDialog.Builder(UserManagementActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getString(R.string.userRegistrationError));
        alertDialog.setMessage(getString(R.string.userNameAlreadyExistMsg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(parent.equals(spinnerExistingUsers)){
            if(spinnerExistingUsers.getSelectedItem() != null && spinnerExistingUsers.getSelectedItem() != ""
                    && !spinnerExistingUsers.getSelectedItem().equals("")){
                eTxtUserName.setText("");
                Log.d("UserManagmentActivity", "Selected Item in spinnerExistingUsers :: " + spinnerExistingUsers.getSelectedItem());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void loadSpinnerData(){
        ArrayList<String> userList = dbHelper.getAllUsers();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userList);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerExistingUsers.setAdapter(spinnerArrayAdapter);
    }

}
