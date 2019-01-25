package activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import toning.juriaan.models.AccessToken;
import toning.juriaan.models.FormContentUploadHandler;
import toning.juriaan.models.FormContentUploadProgress;
import toning.juriaan.models.ProgressListener;
import webinterfaces.FormWebInterface;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.models.Form;
import toning.juriaan.models.FormContent;
import toning.juriaan.models.FormListAdapter;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;
import toning.juriaan.models.Storage;

public class FormListActivity extends BaseActivity implements FormListAdapter.FormContentlistener, ProgressListener {

    private RecyclerView formListRecycler;
    private FormListAdapter formListAdapter;
    private FormContentUploadProgress uploadProgress;
    private FormContentUploadHandler uploadHandler;

    private ProgressBar uploadProgressBar;
    private TextView uploadProgressInfo;

    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_form_list, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.formContentFormList));

        ArrayList<String> formContentNames = Storage.getFormContentNames(this);
        formListAdapter = new FormListAdapter(formContentNames, this);
        formListRecycler = findViewById(R.id.form_list_recycler);
        formListRecycler.setAdapter(formListAdapter);
        formListRecycler.setLayoutManager(new LinearLayoutManager(this));

        loadProgressView();
        updateProgressView();
    }

    private void loadProgressView() {
        uploadProgressBar = findViewById(R.id.upload_progress_bar);
        uploadProgressBar.setVisibility(View.INVISIBLE);
        uploadProgress = new FormContentUploadProgress(this);

        uploadProgressInfo = findViewById(R.id.upload_progress_info);
        uploadProgressInfo.setVisibility(View.INVISIBLE);
    }

    protected void postFormContentList() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        final FormWebInterface client = retrofit.create(FormWebInterface.class);

        final ArrayList<FormContent> formContents = Storage.getFormContents(this);
        uploadProgress.clearErrors();
        uploadProgress.clearResponses();
        uploadProgress.setUploadTotal(formContents.size());

        uploadHandler = new FormContentUploadHandler(formContents, this, client, this);

        handler.post(uploadHandler);
        updateProgressView();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_list_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(FormContent formContent) {
        Form form = Storage.getFormById(formContent.getFormId(), this);
        Intent formOverviewIntent = new Intent(getApplicationContext(), FormOverviewActivity.class);
        formOverviewIntent.putExtra(Helper.FORM, form.getFormattedFormName());
        formOverviewIntent.putExtra(Helper.FORM_CONTENT_ID, formContent.getFormContentId());
        startActivityForResult(formOverviewIntent, Helper.FORM_OVERVIEW_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Helper.FORM_OVERVIEW_CODE) {
            updateRecyclerView();
        }
    }

    private void updateRecyclerView() {
        formListAdapter.setFormContentNames(Storage.getFormContentNames(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_menu_item:
                tryPostFormContentList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void tryPostFormContentList() {
        if (isNetworkAvailable()) {
            if (AccessToken.getAccess_token() != null) {
                getUploadDialog().show();
            } else {
                getLoginDialog().show();
            }
        } else {
            getNoInternetDialog().show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private AlertDialog getNoInternetDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.uploadNoInternetDialogTitle))
                .setMessage(getString(R.string.uploadNoInternetDialogMessage))
                .setPositiveButton(getString(R.string.ok), null)
                .create();
    }

    private AlertDialog getLoginDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.uploadLoginDialogTitle)
                .setMessage(R.string.uploadLoginDialogMessage)
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginIntent);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private AlertDialog getUploadDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.uploadDialogTitle))
                .setMessage(R.string.uploadDialogMessage)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postFormContentList();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create();
    }

    private AlertDialog getUploadErrorDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.deleteDialogTitle)
                .setMessage(uploadProgress.getErrorMessage())
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postFormContentList();
                    }
                }).create();
    }

    @Override
    public void updateProgressView() {
        if (uploadProgress == null || uploadProgress.getUploadTotal() < 1) return;

        uploadProgressInfo.setOnClickListener(null);

        if (!uploadProgress.isDone()) {
            uploadProgressInfo.setVisibility(View.VISIBLE);
            uploadProgressBar.setVisibility(View.VISIBLE);

            uploadProgressBar.setProgress(uploadProgress.getResponses());
            uploadProgressBar.setMax(uploadProgress.getUploadTotal());

            uploadProgressInfo.setText(String.format(
                    getString(R.string.uploadProgressInfo),
                    "" + uploadProgress.getResponses(),
                    "" + uploadProgress.getUploadTotal()));
        } else {
            uploadProgressBar.setVisibility(View.INVISIBLE);
            Storage.cleanStorage(this);

            if (uploadProgress.getErrors().size() == 0) {
                Intent backToHome = new Intent(getApplicationContext(), MainActivity.class);
                backToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backToHome);
            } else {
                uploadProgressInfo.setText(getString(R.string.uploadInfoFailed));
                uploadProgressInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getUploadErrorDialog().show();
                    }
                });
            }
        }
    }

    @Override
    public void setMax(int max) {
        uploadProgressBar.setMax(max);
    }

    @Override
    public FormContentUploadProgress getProgress() {
        return uploadProgress;
    }
}
