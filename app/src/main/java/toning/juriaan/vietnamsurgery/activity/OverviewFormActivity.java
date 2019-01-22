package toning.juriaan.vietnamsurgery.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;

import toning.juriaan.vietnamsurgery.MainActivity;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.Utility.ExcelUtils;
import toning.juriaan.vietnamsurgery.Utility.PhotoUtils;
import toning.juriaan.vietnamsurgery.Utility.Utils;
import toning.juriaan.vietnamsurgery.model.Field;
import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.model.Section;

public class OverviewFormActivity extends AppCompatActivity {

    static final int REQUEST_DELETE_IMAGE = 2;
    static final int REQUEST_ADJUST_FORM = 3;
    private final String TAG = this.getClass().getSimpleName();
    private Toolbar toolbar;
    private FormTemplate form;
    private LinearLayout layout;
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout mFormOverview;
    LayoutInflater mInflator;
    private File storageDirPng;
    LinearLayout mGallery;
    int requestCode;
    private String root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_form);

        loadIntent();
        setupFields();
        setupToolbar();
        loadForm();
    }

    /**
     * Method to load the intent
     */
    private void loadIntent(){
        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");
        requestCode = i.getIntExtra("requestCode", 0);
    }

    /**
     * Method to set up the fields
     */
    private void setupFields() {
        mFormOverview = findViewById(R.id.formLayout);
        mInflator = getLayoutInflater();
        toolbar = findViewById(R.id.form_toolbar);
        mGallery = findViewById(R.id.photo_gallery);
        Utils.setSharedPrefs(this);
        root = Utils.getRootDir();
        storageDirPng = new File(root + File.separator + form.getFormName() + File.separator + "thumbs");
    }

    /**
     * Method to set up the toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String name = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        ab.setTitle(getString(R.string.form_name, form.getFormName(), name, birthYear));
    }

    /**
     * Method to load the form
     */
    private void loadForm(){
        placeFieldsInOverview();
        placePicturesInOverview();
    }

    /**
     * Method to place the fields in the overview
     */
    private void placeFieldsInOverview() {
        for (Section sec : form.getSections()) {
            View view = mInflator.inflate(R.layout.overview_grid_item_list, mFormOverview, false);
            TextView txtView = view.findViewById(R.id.section_name);
            txtView.setText(sec.getSectionName());
            ImageButton editBtn = view.findViewById(R.id.edit_btn);
            editBtn.setOnClickListener((View v) ->
                    goBackToForm(sec.getNumber())
            );

            GridLayout mGridLayout = view.findViewById(R.id.grid_view);
            for( Field f : sec.getFields()) {
                if(!f.getAnswer().equals("false")){
                    View itemView = getLayoutInflater().inflate(R.layout.overview_grid_item, mGridLayout, false);
                    TextView fieldName = itemView.findViewById(R.id.field_name);
                    fieldName.setText(f.getFieldName());

                    TextView fieldAnswer = itemView.findViewById(R.id.field_answer);
                    if(f.getAnswer().equals("true")) {
                        fieldAnswer.setText("");
                    } else {
                        fieldAnswer.setText(f.getAnswer());
                    }

                    mGridLayout.addView(itemView);
                }

            }

            mFormOverview.addView(view);
        }
    }

    /**
     * Method to set up the pictures gallery part
     */
    private void placePicturesInOverview() {
        View headerView = mInflator.inflate(R.layout.overview_grid_item_list, mFormOverview, false);
        TextView txtView = headerView.findViewById(R.id.section_name);
        txtView.setText(R.string.section_name_photos);

        ImageButton editBtn = headerView.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener((View v) ->
                goBackToForm(form.getSections().size() + 1)
        );

        mFormOverview.addView(headerView);

        putPicturesInGallery();
    }

    /**
     * Methods to place the pictures in the gallery
     */
    private void putPicturesInGallery(){
        mGallery.removeAllViews();
        LayoutInflater mInflator = getLayoutInflater();
        int index = 0;
        for( String pathToFile : form.getThumbImages()) {
            int photoIndex = index;
            View view = mInflator.inflate(R.layout.photo_gallery_item, mGallery, false);
            ImageView imageView = view.findViewById(R.id.image_list_iv);
            imageView.setOnClickListener((View v) ->
                PhotoUtils.goToDetailPage(photoIndex, this, form)
            );

            Bitmap pic = BitmapFactory.decodeFile(pathToFile);
            imageView.setImageBitmap(pic);
            mGallery.addView(view);
            index++;
        }
    }

    /**
     * Method to save the form
     * @param form FormTemplate that has to be saevd
     */
    private void saveForm(FormTemplate form) {
        File file = new File(root, form.getFileName());
        try{
            Workbook wb = WorkbookFactory.create(file);
            Sheet s = wb.getSheet(form.getSheetName());
            int firstRowNum = 5;
            int lastRowNum = getLastRowNum(firstRowNum, s);

            if (form.getRowNumber() > 0) {
                Row r = s.getRow(form.getRowNumber());
                int lastColumn = 0;
                String birthYear = "";

                for (Section sec : form.getSections()) {
                    for (Field f : sec.getFields()) {
                        if(!f.getAnswer().isEmpty() && f.getRow() == 3) {
                            r.getCell(f.getColumn()).setCellValue(f.getAnswer());
                        }
                        if(f.getColumn() == lastColumn && lastColumn != 0) {
                            birthYear = sec.getFields().get(f.getColumn()).getAnswer();
                        }
                        if(f.getRow() == 4) {
                            if(f.getAnswer().equals("true")) {
                                r.getCell(f.getColumn()).setCellValue(birthYear);
                            } else {
                                r.getCell(f.getColumn()).setCellValue("");
                            }
                        }
                        lastColumn = f.getColumn();
                    }
                }
            } else {
                Row r = s.createRow(lastRowNum);
                int lastColumn = 0;
                String birthYear = "";

                for (Section sec : form.getSections()) {
                    for (Field f : sec.getFields()) {
                        if(!f.getAnswer().isEmpty() && f.getRow() == 3) {
                            r.createCell(f.getColumn()).setCellValue(f.getAnswer());
                        }
                        if(f.getColumn() == lastColumn && lastColumn != 0) {
                            birthYear = sec.getFields().get(f.getColumn()).getAnswer();
                        }
                        if(f.getRow() == 4) {
                            if(f.getAnswer().equals("true")) {
                                r.createCell(f.getColumn()).setCellValue(birthYear);
                            } else {
                                r.createCell(f.getColumn()).setCellValue("");
                            }
                        }
                        lastColumn = f.getColumn();
                    }
                }
            }

            // Save the file! And delete the "new" file - Because of a f-up in POI we have to do this unfortunately
            File file2 = new File(root, "dummyfile.xlsx");
            FileOutputStream out = new FileOutputStream(file2);
            file2.delete();
            wb.write(out);
            wb.close();
            out.flush();
            out.close();
            if(form.getRowNumber() > 0) {
                Toast.makeText(this, R.string.saving_form_success, Toast.LENGTH_SHORT).show();
                renamePictureFiles();
                Intent formListActivity = new Intent(this, FormListActivity.class);
                formListActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(formListActivity, REQUEST_ADJUST_FORM);
                finish();
            } else {
                goToTheStart();
            }
        } catch (Exception ex) {
            Log.i(TAG, getString(R.string.error_default, ex.getMessage(), ex.getCause()));
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_warning_title)
                    .setMessage(getString(R.string.error_default, ex.getMessage(), ex.getCause()))
                    .setPositiveButton(getString(R.string.dialog_ok), null).show();
        }
    }

    /**
     * Method to rename the pictureFiles if some of the information is changed
     */
    private void renamePictureFiles() {
        String patientName = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        String district = form.getSections().get(1).getFields().get(3).getAnswer();
        int picIndex = 0;
        for(String pFile : form.getPictures()) {
            File oldFile = new File(pFile);
            int endIndex = oldFile.getName().indexOf("_", oldFile.getName().indexOf("_", oldFile.getName().indexOf("_") + 1) + 1 );
            String endName = oldFile.getName().substring(endIndex);
            String newFileName = patientName + "_" + birthYear + "_" + district + endName;
            File newFile = new File(oldFile.getParentFile(), newFileName);
            oldFile.renameTo(newFile);
            form.getPictures().set(picIndex, newFile.getAbsolutePath());
            picIndex++;
        }

        int thumbIndex = 0;
        for(String tFile : form.getThumbImages()) {
            File oldFile = new File(tFile);
            int endIndex = oldFile.getName().indexOf("_", oldFile.getName().indexOf("_", oldFile.getName().indexOf("_") + 1) + 1 );
            String endName = oldFile.getName().substring(endIndex);
            String newFileName = patientName + "_" + birthYear + "_" + district + endName;
            File newFile = new File(oldFile.getParentFile(), newFileName);
            oldFile.renameTo(newFile);
            form.getPictures().set(thumbIndex, newFile.getAbsolutePath());
            thumbIndex++;
        }
    }

    /**
     * Method to get the last filled in rownumber
     * @param startRow int with the row where it has to start
     * @param s Sheet that contains the rows
     * @return int with last Row number
     */
    public static int getLastRowNum(int startRow, Sheet s) {
        if(startRow > s.getLastRowNum()) {
            return startRow;
        } else if(startRow == s.getLastRowNum()) {
            return startRow+1;
        } else {
            int rowNumEmptyRow = startRow;
            for (int i = startRow; i <= s.getLastRowNum(); i++) {
                Row r = s.getRow(i);
                if(ExcelUtils.isRowEmpty(r)) {
                    rowNumEmptyRow = r.getRowNum();
                    break;
                } else {
                    rowNumEmptyRow = r.getRowNum() + 1;
                }
            }
            return rowNumEmptyRow;
        }
    }

    /**
     * Method to go back to the start or fill in a next form
     */
    public void goToTheStart() {
        String name = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        new AlertDialog.Builder(OverviewFormActivity.this)
                .setTitle(getString(R.string.dialog_save_form_title))
                .setMessage(getString(R.string.dialog_save_form_text, getString(R.string.form_name, form.getFormName(), name, birthYear)))
                .setPositiveButton(getString(R.string.dialog_save_form_positive), (DialogInterface dialog, int which) -> {
                        FormTemplate tempForm = new FormTemplate();
                        tempForm.setFileName(form.getFileName());
                        tempForm.setSheetName(form.getSheetName());
                        tempForm.setFormName(form.getFileName());
                        tempForm.setSections(ExcelUtils.createDeepCopyOfSections(form));

                        Intent intent = new Intent(getApplicationContext(), FormActivity.class);
                        intent.putExtra("obj_form", tempForm);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                })
                .setNegativeButton(getString(R.string.dialog_save_form_negative), (DialogInterface dialog, int which) -> {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                })
                .setOnCancelListener((DialogInterface dialog) -> {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                }).show();
    }

    /**
     * Method to go back to the form
     * @param step int stepNumber
     */
    private void goBackToForm(int step) {
        if(step > form.getSections().size()) {
            Intent formIntent = new Intent(getApplicationContext(), CameraActivity.class);
            formIntent.putExtra("obj_form", form);
            formIntent.putExtra("step", step);
            startActivity(formIntent);
            finish();
        } else {
            Intent formIntent = new Intent(getApplicationContext(), FormActivity.class);
            formIntent.putExtra("obj_form", form);
            formIntent.putExtra("step", step);
            startActivity(formIntent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_next:
                if(mGallery.getChildCount() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_not_enough_pics), Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    new AlertDialog.Builder(OverviewFormActivity.this)
                            .setTitle(R.string.dialog_confirm_title)
                            .setMessage(R.string.dialog_confirm_save_form_text)
                            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveForm(form);
                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel, null).show();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_menu_form_overview, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 100) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                form = data.getParcelableExtra("obj_form");
                putPicturesInGallery();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(requestCode == REQUEST_ADJUST_FORM) {
            setResult(RESULT_OK, getIntent());
            finish();
        } else {
            Intent cameraActivity = new Intent(getApplicationContext(), CameraActivity.class);
            cameraActivity.putExtra("obj_form", form);
            startActivity(cameraActivity);
        }
    }
}
