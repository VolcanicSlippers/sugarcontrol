package com.karimtimer.sugarcontrol.userAccount;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.Tour.TourActivity;
import com.karimtimer.sugarcontrol.models.User;

import java.util.ArrayList;
import java.util.List;


//add toast for sign up success
public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "SignUpActivity";

    private EditText inputEmail, inputPassword, inputfirstName, inputlastName, inputweight, inputheight;
    private Button btnDiabetesType, btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private CheckBox dType1, dType2;
    private String diabetesTypeChosen, chosenType, chosenWeight, chosenHeight;
    private Spinner spinnerDiabetesType, spinnerHeight, spinnerWeight;
    private int bglLowerRange, pos;
    private int bglUpperRange;
    private RangeBar bglRange;
    private Dialog privacyPolicyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        //btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        inputfirstName = findViewById(R.id.first_name);
        inputlastName = findViewById(R.id.last_name);
        inputweight = findViewById(R.id.weight);
        inputheight = findViewById(R.id.height);
        // dType1 = (CheckBox) findViewById(R.id.checkBox_diabetes_type_1);
        // dType2 = (CheckBox) findViewById(R.id.checkBox_diabetes_type_2);
        // btnDiabetesType = (Button) findViewById(R.id.btn_diabetes_type);


        bglRange = (RangeBar) findViewById(R.id.bgl_range);
        //bglRange.setInitialIndex(4);
        //bglRange.setRangeCount(14);
        bglRange.setTickStart(0);
        bglRange.setTickEnd(14);
        bglRange.setRangePinsByValue(4, 10);


        // Spinner element for diabetes type
        spinnerDiabetesType = (Spinner) findViewById(R.id.spinner);

        //Spinner element for weight
       spinnerWeight = (Spinner) findViewById(R.id.spinner_weight);
        //height
       spinnerHeight = (Spinner) findViewById(R.id.spinner_height);


        // Spinner click listener
        spinnerDiabetesType.setOnItemSelectedListener(SignupActivity.this);


        bglRange.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                setBglLowerRange(leftPinIndex+1);
                setBglUpperRange(rightPinIndex+1);
            }
        });

        privacyPolicyDialog = new Dialog(this);





        // Spinner Drop down elements
        List<String> categoriesDiabetes = new ArrayList<String>();
        categoriesDiabetes.add("Prediabetes");
        categoriesDiabetes.add("Type 1");
        categoriesDiabetes.add("Type 2");
        categoriesDiabetes.add("Gestational");
        categoriesDiabetes.add("LADA");

//        List<String> categoriesHeight = new ArrayList<String>();
//        categoriesHeight.add("metres");
//        categoriesHeight.add("feet");
//
//        List<String> categoriesWeight = new ArrayList<String>();
//        categoriesWeight.add("kilograms");
//        categoriesWeight.add("stones");

        spinnerDiabetesType.setPrompt("Diabetes Type");

        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesDiabetes);

     //   ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesHeight);
       // ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesWeight);


        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinnerDiabetesType.setAdapter(dataAdapter1);
        //spinnerWeight.setAdapter(dataAdapter2);
        //spinnerHeight.setAdapter(dataAdapter3);


//        btnDiabetesType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String[] singleChoiceItems = getResources().getStringArray(R.array.diabetes_type);
//                final int itemSelected = 0;
//                new AlertDialog.Builder(SignupActivity.this)
//                        .setTitle("Diabetes Type")
//                        .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int selectedIndex) {
//
//                                diabetesTypeChosen = singleChoiceItems[selectedIndex].toString();
//                                btnDiabetesType.setText(diabetesTypeChosen);
//                            }
//                        })
//                        .setPositiveButton("Ok", null)
//                        .setNegativeButton("Cancel", null)
//                        .show();
//            }
//        });


//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
//            }
//        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, InitialActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
            //    final String weight = inputweight.getText().toString().trim();
            //    final String height = inputheight.getText().toString().trim();
                final String firstName = inputfirstName.getText().toString().trim();
                final String lastName = inputlastName.getText().toString().trim();
                final String diaType = spinnerDiabetesType.getSelectedItem().toString();
                final String bglLowerRange = ""+ getBglLowerRange();
                final String bglUpperRange= ""+ getBglUpperRange();

                final String medType = null;

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(getApplicationContext(), "please enter your Last name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(bglLowerRange) || TextUtils.isEmpty(bglUpperRange)){
                    Toast.makeText(getApplicationContext(), "please enter a target range for your bgl.", Toast.LENGTH_SHORT).show();
                    return;
                }


//                if (TextUtils.isEmpty(height)) {
//                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(weight)) {
//                    Toast.makeText(getApplicationContext(), "please enter your weight.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter an email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter a password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Password too short, minimum 8 characters required.", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = auth.getCurrentUser();
                                try {
                                    writeNewUser(user.getUid(), getUsernameFromEmail(user.getEmail()), user.getEmail(), bglLowerRange, bglUpperRange, firstName, lastName, diaType, medType);
                                }catch(Exception e){
                                    Toast.makeText(SignupActivity.this, "sd",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Big error???");
                                }
                                Toast.makeText(SignupActivity.this, "account created!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(firstName).build();
                                    user.updateProfile(profileUpdates);

                                    startActivity(new Intent(SignupActivity.this, TourActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void writeNewUser(String userId, String username, String email, String bglLowerRange, String bglUpperRange, String firstName, String lastName, String type, String medType) {
        User user = new User(username, email, bglLowerRange, bglUpperRange, firstName, lastName, type, medType);

        FirebaseDatabase.getInstance().getReference().child("users").child(userId).setValue(user);
    }

    private String getUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    public int getBglLowerRange() {
        return bglLowerRange;
    }

    public void setBglLowerRange(int bglLowerRange) {
        this.bglLowerRange = bglLowerRange;
    }

    public int getBglUpperRange() {
        return bglUpperRange;
    }

    public void setBglUpperRange(int bglUpperRange) {
        this.bglUpperRange = bglUpperRange;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
        // On selecting a spinner item
        int id = view.getId();
        switch (id) {
            case R.id.spinner:
                String item1 = parent.getItemAtPosition(position).toString();
                pos = position;
                chosenType = item1;
                break;
//            case R.id.spinner_height:
//                String item2 = parent.getItemAtPosition(position).toString();
//                pos = position;
//                chosenWeight = item2;
//                break;
//            case R.id.spinner_weight:
//                String item3 = parent.getItemAtPosition(position).toString();
//                pos = position;
//                chosenHeight = item3;
//                break;
            default:
                break;
        }
    }

    /**
     * privacy policy dialog
     * @param view
     */
    public void showPopup(View view){
        TextView txtClose;
        privacyPolicyDialog.setContentView(R.layout.custom_pop_up);
        txtClose =  privacyPolicyDialog.findViewById(R.id.privacy_policy_close);

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacyPolicyDialog.dismiss();
            }
        });
//        privacyPolicyDialog.getWindow()
        privacyPolicyDialog.show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this, InitialActivity.class));
        finish();
    }
}