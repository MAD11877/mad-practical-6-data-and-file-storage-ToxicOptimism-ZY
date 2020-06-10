package sg.edu.np.week_6_whackamole_3_0;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /*
        1. This is the main page for user to log in
        2. The user can enter - Username and Password
        3. The user login is checked against the database for existence of the user and prompts
           accordingly via Toastbox if user does not exist. This loads the level selection page.
        4. There is an option to create a new user account. This loads the create user page.
     */
    //Variables
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;

    private MyDBHandler dbHandler = new MyDBHandler(this, "WhackAMole.db", null, 1);
    private static final String FILENAME = "MainActivity.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Hint:
            This method creates the necessary login inputs and the new user creation ontouch.
            It also does the checks on button selected.
            Log.v(TAG, FILENAME + ": Create new user!");
            Log.v(TAG, FILENAME + ": Logging in with: " + etUsername.getText().toString() + ": " + etPassword.getText().toString());
            Log.v(TAG, FILENAME + ": Valid User! Logging in");
            Log.v(TAG, FILENAME + ": Invalid user!");

        */

        //Instantiate variables
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameInput = usernameEditText.getText().toString();
                String passwordInput = passwordEditText.getText().toString();

                Log.v(TAG, FILENAME + ": Logging in with: " +
                        usernameInput + ": " + passwordInput);

                if(!isValidUser(usernameInput, passwordInput)) {
                    Log.v(TAG, FILENAME + ": Invalid user!");
                    return;
                }

                Log.v(TAG, FILENAME + ": Valid User! Logging in");

                Intent levelSelectPage = new Intent(MainActivity.this, Main3Activity.class);
                levelSelectPage.putExtra("currentUsername", usernameInput);
                startActivity(levelSelectPage);
                finish();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, FILENAME + ": Create new user!");
                Intent signUpActivity = new Intent(MainActivity.this, Main2Activity.class);
                Log.v(TAG, FILENAME + ": test");
                startActivityForResult(signUpActivity, REQUEST_CODE);
            }
        });
    }

    protected void onStop(){
        super.onStop();
    }

    public boolean isValidUser(String userName, String password){

        /* HINT:
            This method is called to access the database and return a true if user is valid and false if not.
            Log.v(TAG, FILENAME + ": Running Checks..." + dbData.getMyUserName() + ": " + dbData.getMyPassword() +" <--> "+ userName + " " + password);
            You may choose to use this or modify to suit your design.
         */

        UserData userData = dbHandler.findUser(userName);

        if (userData == null) {
            //Invalid Username
            displayMsg(usernameEditText, "Can't find an account with that username!");
            return false;
        }

        Log.v(TAG, FILENAME + ": Running Checks..." + userData.getMyUserName() + ": " +
                userData.getMyPassword() +" <--> "+ userName + " " + password);

        if (!userData.getMyPassword().equals(password)) {
            //Invalid password
            displayMsg(passwordEditText, "Incorrect password!");
            return false;
        }
        return true;
    }

    public void displayMsg(EditText editText, String errorMsg) {
        editText.setError(errorMsg);
        Toast.makeText(this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
        passwordEditText.setText("");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
        }
    }

}
