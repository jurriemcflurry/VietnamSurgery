package Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ResponseModels.RegisterResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;
import toning.juriaan.Models.RegisterObject;

public class RegisterActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, Callback<RegisterResponse> {

    private UserWebInterface userWebInterface;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;
    private Spinner userrole;
    private TextInputEditText email;
    private Button register;
    private FrameLayout frameLayout;
    private Helper helper;
    private ProgressBar pBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_register, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.register));

        setupLayout();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);
    }

    private void setupLayout(){
        frameLayout = (FrameLayout) findViewById(R.id.register_frame_layout);
        //get the spinner from the xml.
        userrole = findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"Admin", "User"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        userrole.setAdapter(adapter);

        userrole.setOnItemSelectedListener(this);

        password = (TextInputEditText) findViewById(R.id.registerpasswordEditText);
        confirmPassword = (TextInputEditText) findViewById(R.id.registerconfirmpasswordEditText);
        email = (TextInputEditText) findViewById(R.id.registeremailEditText);
        register = (Button) findViewById(R.id.register_button);
        pBar = (ProgressBar) findViewById(R.id.pBar);
        pBar.setVisibility(View.INVISIBLE);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pBar.setVisibility(View.VISIBLE);
                registerNewUser();
            }
        });
    }

    public void registerNewUser(){
        String registerPassword = password.getText().toString();
        String registerConfirmPassword = confirmPassword.getText().toString();
        String registerEmail = email.getText().toString();
        String registerUserRole = userrole.getSelectedItem().toString();

        if(registerPassword.isEmpty() || registerConfirmPassword.isEmpty() || registerEmail.isEmpty() || registerUserRole.isEmpty()){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.emptyFields),Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if(!isEmailValid(registerEmail)){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.wrongEmailFormat),Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if(!registerPassword.equals(registerConfirmPassword)){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.passwordError),Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        RegisterObject registerObject = new RegisterObject(registerPassword, registerConfirmPassword, registerUserRole, registerEmail);

        userWebInterface.register(registerObject).enqueue(this);
        helper.hideKeyboard(this);
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

    @Override
    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
        pBar.setVisibility(View.INVISIBLE);

        if(response.isSuccessful() && response.body() != null){
            Snackbar.make(findViewById(R.id.register_linear_layout), getString(R.string.userOverview), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.homeCaps), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent backHome = new Intent(RegisterActivity.this, UsersActivity.class);
                            startActivity(backHome);
                        }
                    }).show();
        }
        else{
            if(response.message().equals("Bad Request")){
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
        t.printStackTrace();
    }
}