package com.karimtimer.sugarcontrol.userAccount;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Options extends android.support.v4.app.Fragment {

    private Button btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser,
            changeEmail, changePassword, sendEmail, remove, signOut, backToHome, btnTheme,btnChangeLanguage, btnPrivacyPolicy;

    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DialogFragment privacyPolicyDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_options, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        loadLocale();




        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        };



        btnChangeEmail = (Button) view.findViewById(R.id.change_email_button);
        btnChangePassword = (Button) view.findViewById(R.id.change_password_button);
        btnSendResetEmail = (Button) view.findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) view.findViewById(R.id.remove_user_button);
        changeEmail = (Button) view.findViewById(R.id.changeEmail);
        changePassword = (Button) view.findViewById(R.id.changePass);
        sendEmail = (Button) view.findViewById(R.id.send);
        remove = (Button) view.findViewById(R.id.remove);
//        signOut = (Button) view.findViewById(R.id.sign_out);
//        backToHome = (Button) view.findViewById(R.id.btn_backHome);
//        btnTheme = (Button) view.findViewById(R.id.btn_change_theme);
        btnChangeLanguage = view.findViewById(R.id.btn_change_language);


        oldEmail = (EditText) view.findViewById(R.id.old_email);
        newEmail = (EditText) view.findViewById(R.id.new_email);
        password = (EditText) view.findViewById(R.id.password);
        newPassword = (EditText) view.findViewById(R.id.newPassword);

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to update email!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), SignupActivity.class));
                                        getActivity().getSupportFragmentManager().popBackStack();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signOut();
//            }
//        });
//
//        backToHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), MainActivity.class));
//
//            }
//        });
//
//        btnTheme.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), ChooseTheme.class));
//
//            }
//        });


        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show AlertDialog to display list of languages, one can then be selected.
                showChangeLanguagesDialog();
            }
        });
        
        
    }

    //Create seperate strings.xml for each language that will be supported.
    private void showChangeLanguagesDialog() {
        //array of languages to display in the alert dialog
        final String[] listItems = {"French", "Somali", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //French
                    setLocale("fr");
                    getActivity().recreate();
                }else if(i==1){
                    //French
                    setLocale("so");
                    getActivity().recreate();
                }else if(i==2){
                    setLocale("en");
                    getActivity().recreate();
                }

//                else if(i==3){
//                    setLocale("ur-rPK");
//                    getActivity().recreate();
//                }
                //dismiss alert dialog when the language has been selected
                dialogInterface.dismiss();

            }
        });


        AlertDialog mDialog = mBuilder.create();
        //show alert dialog
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,getActivity().getBaseContext().getResources().getDisplayMetrics());
        //save data
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }
    //load the languages saved in shared preferences
    public void loadLocale(){
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }



    //sign out method
    public void signOut() {
        auth.signOut();
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }




    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
