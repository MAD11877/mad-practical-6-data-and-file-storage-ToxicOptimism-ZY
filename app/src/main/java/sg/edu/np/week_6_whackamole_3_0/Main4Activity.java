package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main4Activity extends AppCompatActivity {

    /* Hint:
        1. This creates the Whack-A-Mole layout and starts a countdown to ready the user
        2. The game difficulty is based on the selected level
        3. The levels are with the following difficulties.
            a. Level 1 will have a new mole at each 10000ms.
                - i.e. level 1 - 10000ms
                       level 2 - 9000ms
                       level 3 - 8000ms
                       ...
                       level 10 - 1000ms
            b. Each level up will shorten the time to next mole by 100ms with level 10 as 1000 second per mole.
            c. For level 1 ~ 5, there is only 1 mole.
            d. For level 6 ~ 10, there are 2 moles.
            e. Each location of the mole is randomised.
        4. There is an option return to the login page.
     */
    CountDownTimer readyTimer;
    CountDownTimer newMolePlaceTimer;

    private final List<Button> holeButtonList = new ArrayList<>();
    private final List<Button> randomLocations = new ArrayList<>();

    private static final int[] standardModeButtons = {
            R.id.holeButton4, R.id.holeButton5, R.id.holeButton9
    };

    private static final int[] advancedModeButtons = {
            R.id.holeButton1, R.id.holeButton2, R.id.holeButton3, R.id.holeButton4,
            R.id.holeButton6, R.id.holeButton7, R.id.holeButton8
    };

    private TextView resultTextView;
    private Button backButton;
    private Integer levelNo;
    private String username;
    private Integer score = 0;
    private Integer highScore;
    private Integer holeAmt;

    private MyDBHandler dbHandler = new MyDBHandler(this, "WhackAMole.db", null, 1);

    private static final String FILENAME = "Main4Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    private void readyTimer() {
        /*  HINT:
            The "Get Ready" Timer.
            Log.v(TAG, "Ready CountDown!" + millisUntilFinished/ 1000);
            Toast message -"Get Ready In X seconds"
            Log.v(TAG, "Ready CountDown Complete!");
            Toast message - "GO!"
            belongs here.
            This timer countdown from 10 seconds to 0 seconds and stops after "GO!" is shown.
         */
        readyTimer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                Long timeRemainingInSeconds = l / 1000;
                Log.v(TAG, "Ready CountDown!" + l / 1000);
                String text = "Get ready in " + timeRemainingInSeconds.toString() + " seconds!";
                final Toast t = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                t.show();

                Timer killToast = new Timer();
                killToast.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        t.cancel();
                    }
                }, 1000);
            }

            @Override
            public void onFinish() {
                Log.v(TAG, "Ready CountDown Complete!");
                Toast.makeText(getApplicationContext(), "GO!", Toast.LENGTH_SHORT).show();
                readyTimer.cancel();
                placeMoleTimer();
                setNewMole();
            }
        };

        readyTimer.start();
    }

    private void placeMoleTimer() {
        /* HINT:
           Creates new mole location each second.
           Log.v(TAG, "New Mole Location!");
           setNewMole();
           belongs here.
           This is an infinite countdown timer.
         */
        newMolePlaceTimer = new CountDownTimer((10 - levelNo + 1) * 1000,
                1000) {
            @Override
            public void onTick(long l) {
                Log.v(TAG, "New Mole Location!");
                resetMole();
                setNewMole();
            }

            @Override
            public void onFinish() {
                newMolePlaceTimer.start();
            }
        };

        newMolePlaceTimer.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        /*Hint:
            This starts the countdown timers one at a time and prepares the user.
            This also prepares level difficulty.
            It also prepares the button listeners to each button.
            You may wish to use the for loop to populate all 9 buttons with listeners.
            It also prepares the back button and updates the user score to the database
            if the back button is selected.
         */

        Intent dataReceiver = getIntent();
        levelNo = Integer.parseInt(dataReceiver.getStringExtra("levelNo"));
        username = dataReceiver.getStringExtra("currentUsername");
        highScore = Integer.parseInt(dataReceiver.getStringExtra("score"));
        backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserScore();
                Intent levelSelectPage = new Intent(Main4Activity.this, Main3Activity.class);
                levelSelectPage.putExtra("currentUsername", username);
                startActivity(levelSelectPage);
            }
        });

        //Set current score value
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        resultTextView.setText(score.toString());

        //Initialise listener
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button buttonPressed = (Button) v;
                doCheck(buttonPressed);
            }
        };

        //Initialise standard buttons
        addButtonToList(standardModeButtons, listener);

        if (levelNo <= 5) {
            holeAmt = 1;
        } else {
            holeAmt = 2;
            addButtonToList(advancedModeButtons, listener);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        //Once all initialized, start timer
        readyTimer();
    }

    private void doCheck(Button checkButton)
    {
        /* Hint:
            Checks for hit or miss
            Log.v(TAG, FILENAME + ": Hit, score added!");
            Log.v(TAG, FILENAME + ": Missed, point deducted!");
            belongs here.
        */
        switch (checkButton.getText().toString()) {
            case "0":
                Log.v(TAG, "Missed, score deducted!");
                score -= 1;
                resultTextView.setText(score.toString());
                break;
            case "*":
                Log.v(TAG, "Hit, score added!");
                score += 1;
                resultTextView.setText(score.toString());
                resetMole(); //If hit, reset mole
                setNewMole();
                newMolePlaceTimer.cancel();
                placeMoleTimer(); //Reset timer.
                break;
            default:
                Log.v(TAG, "Unknown button pressed, no case for it's text set.");
        }

    }

    public void resetMole()
    {
        for(Button button: randomLocations) {
            button.setText("0");
        }
        randomLocations.clear();
    }

    public void setNewMole()
    {
        /* Hint:
            Clears the previous mole location and gets a new random location of the next mole location.
            Sets the new location of the mole.
         */
        for(int i=0; i<holeAmt; i++) {
            Button buttonSelected = holeButtonList.get((int)(Math.random() * holeButtonList.size()));
            while (randomLocations.contains(buttonSelected)) {
                buttonSelected = holeButtonList.get((int)(Math.random() * holeButtonList.size()));
            }
            randomLocations.add(buttonSelected);
            buttonSelected.setText("*");
        }
    }

    private void addButtonToList(int[] buttonsIDList, View.OnClickListener listener) {
        for(final int id : buttonsIDList){
            /*  HINT:
            This creates a for loop to populate all 9 buttons with listeners.
            You may use if you wish to remove or change to suit your codes.
            */
            Button button = (Button) findViewById(id);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(listener);
            holeButtonList.add(button);
        }
    }

    private void updateUserScore()
    {

     /* Hint:
        This updates the user score to the database if needed. Also stops the timers.
        Log.v(TAG, FILENAME + ": Update User Score...");
      */
        Log.v(TAG, FILENAME + ": Update User Score...");
        try{
            newMolePlaceTimer.cancel();
            readyTimer.cancel();
        } catch (NullPointerException e) { }
        if (score > highScore) {
            UserData newUserData = dbHandler.findUser(username);
            newUserData.getScores().set(levelNo - 1, score);

            //Replace user data
            dbHandler.deleteAccount(username);
            dbHandler.addUser(newUserData);
        }
    }

}
