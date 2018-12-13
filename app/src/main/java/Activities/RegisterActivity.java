package Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import ResponseModels.RegisterResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.R;
import toning.juriaan.vietnamsurgery.RegisterObject;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Callback<RegisterResponse> {

    private UserWebInterface userWebInterface;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private Spinner userrole;
    private EditText email;
    private Button register;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        username = (EditText) findViewById(R.id.registeruserNameEditText);
        password = (EditText) findViewById(R.id.registerpasswordEditText);
        confirmPassword = (EditText) findViewById(R.id.registerconfirmpasswordEditText);
        email = (EditText) findViewById(R.id.registeremailEditText);
        register = (Button) findViewById(R.id.register_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    public void registerNewUser(){
        String registerUsername = username.getText().toString();
        String registerPassword = password.getText().toString();
        String registerConfirmPassword = confirmPassword.getText().toString();
        String registerEmail = email.getText().toString();
        String registerUserRole = userrole.getSelectedItem().toString();

        if(registerUsername == null || registerPassword == null || registerConfirmPassword == null || registerEmail == null || registerUserRole == null){
            return;
        }

        if(!registerPassword.equals(registerConfirmPassword)){
            return;
        }

        RegisterObject registerObject = new RegisterObject(registerUsername, registerPassword, registerConfirmPassword, registerUserRole, registerEmail);

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

    @Override
    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
        System.out.println(response.message());
        if(response.isSuccessful() && response.body() != null){
            Intent backHome = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(backHome);
        }
    }

    @Override
    public void onFailure(Call<RegisterResponse> call, Throwable t) {
        t.printStackTrace();
    }
}