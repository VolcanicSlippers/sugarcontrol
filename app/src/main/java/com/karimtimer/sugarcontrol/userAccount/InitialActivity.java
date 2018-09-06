package com.karimtimer.sugarcontrol.userAccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.Insulina.VisibleToggleClickListener;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Recolor;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class InitialActivity extends AppCompatActivity implements View.OnClickListener{

    //TODO: Create an opening animation.


    private TextInputLayout emailLayout, passwordLayout;
    private Button joinNow, signIn, signInActual;
    private EditText inputEmail, inputPassword;
    private boolean isRotated;
    private String signInTxt;
    private FirebaseAuth auth;
    private ImageView insulinaGif;
    private TextView txtTitleSugarControl;
    private ViewGroup viewGroup;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure this is before calling super.onCreate
        setTheme(R.style.SplashTheme);

        loadLocale();


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            startActivity(new Intent(InitialActivity.this, MainActivity.class));
            finish();
        }



        // set the view now
        setContentView(R.layout.initial_activity);
        //ViewGroup viaewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_initial);

        emailLayout = (TextInputLayout) findViewById(R.id.sign_in_layout);
        passwordLayout = findViewById(R.id.password_layout);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.btn_sign_in);
        joinNow = (Button) findViewById(R.id.btn_join_now);
        txtTitleSugarControl = (TextView) findViewById(R.id.title_sugar_control);
        signInActual = findViewById(R.id.btn_sign_in_actual);

        emailLayout.setVisibility(View.INVISIBLE);
        passwordLayout.setVisibility(View.INVISIBLE);
        signInActual.setVisibility(View.INVISIBLE);

        signInTxt = signIn.getText().toString();

        final ViewGroup transitionsContainer = (ViewGroup) findViewById(R.id.initial_screen);



                    transitionsContainer.findViewById(R.id.btn_sign_in).setOnClickListener(new VisibleToggleClickListener() {


                        @Override
                        protected void changeVisibility(boolean visible) {
                            TransitionSet set = new TransitionSet()
                                    .addTransition(new Scale(0.7f))
                                    .addTransition(new Fade())
                                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() :
                                            new FastOutLinearInInterpolator());

                            TransitionManager.beginDelayedTransition(transitionsContainer, set);
                            TransitionManager.beginDelayedTransition(transitionsContainer, new Recolor());
                            //                TransitionManager.beginDelayedTransition(transitionsContainer, new Rotate());
                            //               isRotated = true;
                            //                recordBtn.setRotation(isRotated ? 90 : 0);
                                Log.e(TAG, "HERE!");

                                // setRotated(true);
                                signInActual.setText("Sign In");
                                signInActual.setBackground(getDrawable(R.drawable.button_border));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                signInActual.setTextColor(getColor(R.color.white));
                            }
                            setSignInTxt("Sign In");
                                signInActual.setVisibility(View.VISIBLE);
                                signIn.setVisibility(View.INVISIBLE);
                                emailLayout.setVisibility( View.VISIBLE);
                                passwordLayout.setVisibility( View.VISIBLE);



                        }

                    });



        signInActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    String email = inputEmail.getText().toString();
                    final String password = inputPassword.getText().toString();

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(InitialActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(InitialActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    //authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(InitialActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    progressBar.setVisibility(View.GONE);

                                    if(task.isSuccessful()){
                                        FirebaseUser user = auth.getCurrentUser();

//                                    writeNewUser(user.getUid(), getUsernameFromEmail(user.getEmail()), user.getEmail());
                                        Intent intent = new Intent(InitialActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        // there was an error
                                        if (password.length() < 6) {
                                            inputPassword.setError(getString(R.string.minimum_password));
                                        } else {
                                            Toast.makeText(InitialActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }



            }
        });


        insulinaGif = (ImageView) findViewById(R.id.insulina_gif);
        //txtTitleSugarControl.setVisibility(View.INVISIBLE);
        loadImageGif();

        joinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialActivity.this, SignupActivity.class));
                finish();
            }
        });
//
//        signIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(InitialActivity.this, LoginActivity.class));
//                finish();
//            }
//        });
//


    }

    @Override
    public void onClick(View v) {
        // default method for handling onClick Events..
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        //save data
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();


    }

    //load the languages saved in shared preferences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }


    private void loadImageGif() {
        Glide.with(this).asGif().load(R.drawable.intro_insulina_peaking)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                onGifFinished();
                return false;
            }

            @Override
            public boolean onResourceReady(final GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.setLoopCount(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            if(!resource.isRunning()) {
                                onGifFinished();

                                break;
                            }
                        }
                    }
                }).start();
                return false;
            }
        }).into(insulinaGif);    }



        private void onGifFinished() {
            //show Sugar Control title
        }

    public boolean isRotated() {
        return isRotated;
    }

    public void setRotated(boolean rotated) {
        isRotated = rotated;
    }

    public String getSignInTxt() {
        return signInTxt;
    }

    public void setSignInTxt(String signInTxt) {
        this.signInTxt = signInTxt;
    }
}
