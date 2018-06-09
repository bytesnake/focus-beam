package com.blubblub.FocusBeam;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

public class FocusBeam extends AppCompatActivity {
    private GameState state;
    private GameFieldView game_field;
    private Subscriber subscribe;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kein_plan);

        // setup the toolbar at the top
        mToolbar = (Toolbar)findViewById(R.id.top_toolbar);
        setSupportActionBar(mToolbar);

        // wire everything
        state = new GameState();
        game_field = (GameFieldView)findViewById(R.id.game_field);
        game_field.state = state;
        subscribe = new Subscriber(this);

        subscribe.setListener(new SubscriberListener() {
            @Override
            public void gotValues(String topic, GameState.DataSet val) {
                // update the state with new values of player "topic"
                state.update(topic, val);
                // update the game field according to the state
                game_field.update();
            }
        });

        // we have a continuously running score, update it every 100ms
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                state.updateScore();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game_field.invalidate();
                    }
                });
            }
        },1000,100);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            // create a dialog where we can enter a new server address
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Server adress");
            dialog.setMessage("Please enter a valid address:");

            // create a new edit text with the previous server adress
            final EditText editText = new EditText(this);
            editText.setText(subscribe.getAddr());
            dialog.setView(editText);

            // and a confirm button for fun!
            dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    subscribe.setAddr(editText.getText().toString());
                }
            });

            // now show the whole thing
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static public void log(Activity act, String text) {
        Log.d("Focus Beam", text);
        Snackbar.make(act.findViewById(R.id.game_field), text, Snackbar.LENGTH_LONG).show();
    }
}
