package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    /* Hint:
        1. This is the create new user page for user to log in
        2. The user can enter - Username and Password
        3. The user create is checked against the database for existence of the user and prompts
           accordingly via Toastbox if user already exists.
        4. For the purpose the practical, successful creation of new account will send the user
           back to the login page and display the "User account created successfully".
           the page remains if the user already exists and "User already exist" toastbox message will appear.
        5. There is an option to cancel. This loads the login user page.
     */

    private EditText signUpUsernameEditText;
    private EditText signUpPasswordEditText;
    private Button confirmSignUpButton;
    private Button cancelButton;

    private static final String FILENAME = "Main2Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    private MyDBHandler dbHandler = new MyDBHandler(this, "WhackAMole.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        /* Hint:
            This prepares the create and cancel account buttons and interacts with the database to determine
            if the new user created exists already or is new.
            If it exists, information is displayed to notify the user.
            If it does not exist, the user is created in the DB with default data "0" for all levels
            and the login page is loaded.

            Log.v(TAG, FILENAME + ": New user created successfully!");
            Log.v(TAG, FILENAME + ": User already exist during new user creation!");

         */

        Log.v(TAG, FILENAME + ": Testing");
        //Instantiate variables
        signUpUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        signUpPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmSignUpButton = (Button) findViewById(R.id.loginButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        confirmSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = signUpUsernameEditText.getText().toString();
                String passwordInput = signUpPasswordEditText.getText().toString();

                if (dbHandler.findUser(usernameInput) != null) {
                    //Since usernames are unique, check if user exists in db.
                    Log.v(TAG, FILENAME + ": User already exist during new user creation!");
                    displayMsg(signUpUsernameEditText, "A user with this username already exists!");
                }

                else {
                    ArrayList<Integer> levels = new ArrayList<>();
                    ArrayList<Integer> scores = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        levels.add(i + 1);
                        scores.add(0);
                    }

                    UserData userData = new UserData(usernameInput, passwordInput, levels, scores);
                    dbHandler.addUser(userData);
                    Log.v(TAG, FILENAME + ": New user created successfully!");

                    //return to log in page after creating account
                    Intent returnToLogin = new Intent(Main2Activity.this, MainActivity.class);
                    setResult(RESULT_OK, returnToLogin);
                    startActivity(returnToLogin);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToLogin = new Intent(Main2Activity.this, MainActivity.class);
                setResult(RESULT_CANCELED, returnToLogin);
                finish();
            }
        });
    }

    protected void onStop() {
        super.onStop();
        finish();
    }

    public void displayMsg(EditText editText, String errorMsg) {
        editText.setError(errorMsg);
        Toast.makeText(this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
        signUpPasswordEditText.setText("");
    }
}
