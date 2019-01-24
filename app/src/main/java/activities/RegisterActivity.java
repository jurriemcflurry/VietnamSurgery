package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import responsemodels.RegisterResponse;
import webinterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;
import toning.juriaan.models.RegisterObject;

public class RegisterActivity extends FormBaseActivity implements AdapterView.OnItemSelectedListener, Callback<RegisterResponse> {

    private UserWebInterface userWebInterface;
    private EditText password;
    private EditText confirmPassword;
    private Spinner userrole;
    private EditText email;
    private Button register;
    private FrameLayout frameLayout;
    private Helper helper;
    private ProgressBar pBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_register, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.addUserTitle));

        setupLayout();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);
    }

    //method to define the layout elements
    private void setupLayout(){
        frameLayout = findViewById(R.id.register_frame_layout);

        //set dropdown for userrole
        userrole = findViewById(R.id.spinner1);
        String[] items = new String[]{getString(R.string.adminRegisterDropdown), getString(R.string.userRegisterDropdown)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        userrole.setAdapter(adapter);
        userrole.setOnItemSelectedListener(this);

        //define layout elements
        password = findViewById(R.id.registerpasswordEditText);
        confirmPassword = findViewById(R.id.registerconfirmpasswordEditText);
        email = findViewById(R.id.registeremailEditText);
        register = findViewById(R.id.register_button);
        pBar = findViewById(R.id.pBar);
        pBar.setVisibility(View.INVISIBLE);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pBar.setVisibility(View.VISIBLE);
                registerNewUser();
            }
        });
    }

    //make call to register new user, after checks
    public void registerNewUser(){
        helper.hideKeyboard(this);

        //check if there is a network available; if not, return
        if(!isNetworkAvailable()){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.registerNoInternet),Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        //get userinput
        String registerPassword = password.getText().toString();
        String registerConfirmPassword = confirmPassword.getText().toString();
        String registerEmail = email.getText().toString();
        String registerUserRole = userrole.getSelectedItem().toString();

        //check if all fields are filled in
        if(registerPassword.isEmpty() || registerConfirmPassword.isEmpty() || registerEmail.isEmpty() || registerUserRole.isEmpty()){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.registerEmptyFields),Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        //when all fields are filled in, check if filled in email is valid
        if(!isEmailValid(registerEmail)){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.wrongEmailFormat),Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        //when fields are filled in and email is in valid format, check if confirm password matches the first password
        if(!registerPassword.equals(registerConfirmPassword)){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.passwordError),Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        //when all checks are OK, place input into an object for the call
        RegisterObject registerObject = new RegisterObject(registerPassword, registerConfirmPassword, registerUserRole, registerEmail);

        //make the call to register
        userWebInterface.register(registerObject).enqueue(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //method to check if a string has te format of an emailadress
    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    //method to check if there is a network available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
        helper.hideKeyboard(this);
        pBar.setVisibility(View.INVISIBLE);

        if(response.isSuccessful() && response.body() != null){ //user successfully added
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.userAdded), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.userOverview), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent userOverview = new Intent(RegisterActivity.this, UsersActivity.class);
                            userOverview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(userOverview);
                        }
                    }).show();
        }
        else{
            if(response.message().equals(getString(R.string.badRequest))){ //one of the parameters was not correct, where only password has not been validated yet
                Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.passwordFormatError),Snackbar.LENGTH_LONG)
                        .show();
            }
            else {
                Snackbar.make(findViewById(R.id.register_linear_layout), response.message(), Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public void onFailure(Call<RegisterResponse> call, Throwable t) {
        helper.hideKeyboard(this);
        pBar.setVisibility(View.INVISIBLE);
        Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.userNotAdded),Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exitRegisterTitle))
                .setMessage(getString(R.string.exitRegisterMessage))
                .setNegativeButton(getString(R.string.cancelExit), null)
                .setPositiveButton(getString(R.string.exitRegister), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.hideKeyboard(RegisterActivity.this);
                        RegisterActivity.this.finish();
                    }
                })
                .create().show();
    }
}