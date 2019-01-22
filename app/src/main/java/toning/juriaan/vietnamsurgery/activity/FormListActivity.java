package toning.juriaan.vietnamsurgery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import toning.juriaan.vietnamsurgery.MainActivity;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.Utility.ExcelUtils;
import toning.juriaan.vietnamsurgery.Utility.Utils;
import toning.juriaan.vietnamsurgery.adapter.FileNameAdapter;
import toning.juriaan.vietnamsurgery.adapter.FormListAdapter;
import toning.juriaan.vietnamsurgery.adapter.SheetAdapter;
import toning.juriaan.vietnamsurgery.listener.FileNameListener;
import toning.juriaan.vietnamsurgery.listener.FormListListener;
import toning.juriaan.vietnamsurgery.listener.SheetListener;
import toning.juriaan.vietnamsurgery.model.Field;
import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.model.Section;

public class FormListActivity extends AppCompatActivity implements FormListListener, SheetListener, FileNameListener {

    final static int REQUEST_ADJUST_FORM = 3;
    private final String TAG = this.getClass().getSimpleName();
    private FormTemplate form;
    private List<Section> sections = new ArrayList<>();
    private ArrayList<FormTemplate> formList = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private File root;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
    private XSSFWorkbook mWorkbook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);

        setupFields();
        setupToolbar();
        setupNavigation();
        chooseExcelFile();

    }

    /**
     * Method to load the intent
     */
    private void loadIntent() {
        Intent i = getIntent();
        if(i.hasExtra("fileName") && i.hasExtra("sheetName")) {
            String wbName = i.getStringExtra("fileName");
            String sheetName = i.getStringExtra("sheetName");
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(new File(root, wbName));
                readExcelFile(workbook.getSheet(sheetName));
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_warning_title)
                        .setMessage(getString(R.string.error_while_opening_xlsx, ex.getMessage()))
                        .setPositiveButton(getString(R.string.dialog_ok), null).show();
            }
        } else {
            chooseExcelFile();
        }
    }

    /**
     * Method to set up the fields
     */
    private void setupFields(){
        form = new FormTemplate();
        mRecyclerView = findViewById(R.id.grid_view_form_list_files);
        Utils.setSharedPrefs(this);
        root = new File(Utils.getRootDir());
    }

    /**
     * Method to set up the toolbar for this activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.form_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle(R.string.filled_in_forms_name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to setup the navigation/menu for this activity
     */
    private void setupNavigation(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.filled_in_forms).setVisible(false);

        navigationView.setNavigationItemSelectedListener((MenuItem menuItem)->{
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers();

            switch(menuItem.getItemId()){
                case R.id.main_activity:
                    Intent mainActivity = new Intent(FormListActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                    break;
                default: break;
            }

            return true;
        });
    }

    /**
     * Method to choose an Excel file
     */
    private void chooseExcelFile() {
        try{
            List<File> files = Utils.getListOfExcelFiles(root);

            // Check if there are more than 1 file, if so, show clickables for all files. If not: load the first one directly
            if(files.size() > 1) {
                mRecyclerView.setLayoutManager(mLayoutManager);
                FileNameAdapter mAdapterFiles = new FileNameAdapter(this, files, this);
                mRecyclerView.setAdapter(mAdapterFiles);
            } else {
                form.setFileName(files.get(0).getName());
                form.setFormName(files.get(0).getName().substring(0, files.get(0).getName().lastIndexOf('.')));
                createExcelWorkbook(new File(root, files.get(0).getName()));
            }
        } catch (Exception ex) {
            Toast.makeText(this, getString(R.string.error_while_finding_xlsx, ex.getMessage(), root.getPath()), Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_warning_title)
                    .setMessage(getString(R.string.error_while_finding_xlsx, ex.getMessage(), root.getPath()))
                    .setPositiveButton(getString(R.string.dialog_ok), null).show();
        }
    }

    /**
     * Method to create an Excel workbook
     * @param file File that contains an xlsx file
     */
    private void createExcelWorkbook(File file) {
        try {
            // Create a workbook object
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            chooseExcelSheet(workbook);
        } catch (Exception ex) {
            Log.i(TAG, getString(R.string.error_while_opening_xlsx, ex.getMessage()));
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_warning_title)
                    .setMessage(getString(R.string.error_while_opening_xlsx, ex.getMessage()))
                    .setPositiveButton(getString(R.string.dialog_ok), null).show();
        }
    }

    /**
     * Method to choose an Excel sheet
     * @param workbook XSSFWorkBook of the ExcelFile
     */
    private void chooseExcelSheet(XSSFWorkbook workbook) {
        mWorkbook = workbook;
        if(workbook.getNumberOfSheets() == 1) {
            readExcelFile(workbook.getSheetAt(0));
        } else {
            List<String> sheets = new ArrayList<>();
            for( int sheetNumber = 0; sheetNumber < workbook.getNumberOfSheets(); sheetNumber++) {
                sheets.add(workbook.getSheetAt(sheetNumber).getSheetName());
            }
            mRecyclerView.setLayoutManager(mLayoutManager);
            SheetAdapter mAdapterSheets = new SheetAdapter(this, sheets, this);
            mRecyclerView.setAdapter(mAdapterSheets);
        }
    }

    /**
     * Method to read the ExcelFile
     * @param sheet XSSFSheet that holds the sheet that needs to be read
     */
    private void readExcelFile(XSSFSheet sheet) {
        try {
            form.setSheetName(sheet.getSheetName());

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = 4;

            List<List<String>> rows = new ArrayList<>();

            for(int i = firstRowNum; i <= lastRowNum; i++)
            {
                Row row = sheet.getRow(i);

                int firstCellNum = row.getFirstCellNum();
                int lastCellNum = row.getLastCellNum();

                List<String> rowDataList = new ArrayList<>();
                for(int j = firstCellNum; j < lastCellNum; j++)
                {
                    String cellValue = row.getCell(j).getStringCellValue();
                    rowDataList.add(cellValue);
                }
                rows.add(rowDataList);
            }

            // Get the name of the different parts of the form
            List<String> stringsWithSections = rows.get(2);
            List<String> firstFieldRow = rows.get(3);
            List<String> secondFieldRow = rows.get(4);

            Section section = null;
            List<Field> fields = null;

            int sectionCounter = 1;
            for (int column = 0; column < stringsWithSections.size(); column++) {
                if(!stringsWithSections.get(column).isEmpty()) {
                    if(section != null) {
                        section.setFields(fields);
                        sections.add(section);
                    }
                    section = new Section();
                    section.setSectionName(stringsWithSections.get(column));
                    section.setNumber(sectionCounter);
                    section.setColumn(column);
                    fields = new ArrayList<>();
                    sectionCounter++;
                }

                if(fields != null && column < firstFieldRow.size() && !firstFieldRow.get(column).isEmpty()) {
                    Field field = new Field();
                    field.setFieldName(firstFieldRow.get(column));
                    field.setColumn(column);
                    field.setRow(3);
                    fields.add(field);
                }

                if(fields != null && column < secondFieldRow.size() && !secondFieldRow.get(column).isEmpty()) {
                    Field field = new Field();
                    field.setFieldName(secondFieldRow.get(column));
                    field.setColumn(column);
                    field.setRow(4);
                    fields.add(field);
                }

                if(column == stringsWithSections.size()-1 && section != null) {
                    section.setFields(fields);
                    sections.add(section);
                }
            }


            form.setSections(sections);

            putAnswers(sheet);
            XSSFWorkbook wb = sheet.getWorkbook();
            wb.close();
        } catch (Exception ex) {
            Log.e(TAG, getString(R.string.error_while_reading_xlsx, ex.getMessage()));
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_warning_title)
                    .setMessage(getString(R.string.error_while_reading_xlsx, ex.getMessage()))
                    .setPositiveButton(getString(R.string.dialog_ok), null).show();
        }
    }

    /**
     * Method to set the answers for all the filled in forms that are available
     * @param sheet Sheet that holds the necessary data
     */
    private void putAnswers(Sheet sheet) {

        int firstRowNum = 5;
        int lastRowNum = sheet.getLastRowNum();
        formList.clear();

        for(int i = firstRowNum; i <= lastRowNum; i++)
        {
            Row row = sheet.getRow(i);
            if(!ExcelUtils.isRowEmpty(row)) {
                FormTemplate tempForm = new FormTemplate();
                tempForm.setFileName(form.getFileName());
                tempForm.setSheetName(form.getSheetName());
                tempForm.setFormName(form.getFormName());

                tempForm.setSections(ExcelUtils.createDeepCopyOfSections(form));
                tempForm.setRowNumber(row.getRowNum());
                String birthYear = "";

                for (Section sec : tempForm.getSections()) {
                    for (Field f : sec.getFields()) {
                        String answer;
                        if (row.getCell(f.getColumn()) != null) {
                            answer = row.getCell(f.getColumn()).getStringCellValue();
                            if(f.getRow() == 4 && f.getColumn() == 2) {
                                if(!row.getCell(f.getColumn()).getStringCellValue().isEmpty()) {
                                    birthYear = answer;
                                    answer = "true";
                                } else {
                                    answer = "false";
                                }
                            }
                            if(f.getRow() == 4 && f.getColumn() == 3) {
                                if(!row.getCell(f.getColumn()).getStringCellValue().isEmpty()) {
                                    birthYear = answer;
                                    answer = "true";
                                } else {
                                    answer = "false";
                                }
                            }
                        } else {
                            answer = "";
                        }

                        f.setAnswer(answer);
                    }
                }
                tempForm.getSections().get(0).getFields().get(2).setAnswer(birthYear);
                tempForm = setListOfPicturesAndThumbs(tempForm);
                formList.add(tempForm);
            }
        }

        if(formList.size() == 0) {
            Toast.makeText(this, R.string.no_filled_in_forms, Toast.LENGTH_LONG).show();
        }

        RecyclerView mRecyclerView = findViewById(R.id.form_list_grid_view);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        FormListAdapter mAdapter = new FormListAdapter(this, formList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Method to set the list of Pictures and thums in a FormTemplate
     * @param patientForm FormTemplate that has to be set
     * @return FormTemplate with added information
     */
    private FormTemplate setListOfPicturesAndThumbs(FormTemplate patientForm) {
        List<String> pictures = new ArrayList<>();
        List<String> thumbs = new ArrayList<>();

        File storageDirJpg = new File( root + File.separator + form.getFormName());
        File[] files = storageDirJpg.listFiles();

        // Todo: Wanneer ik Jo zover krijg -> Create the name of the pictures with name, birthyear and district dynamically from Excel
        String patientName = patientForm.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = patientForm.getSections().get(0).getFields().get(2).getAnswer();
        String district = patientForm.getSections().get(1).getFields().get(3).getAnswer();

        for( File file : files) {
            if(file.getName().contains( patientName + "_" + birthYear + "_" + district )) {
                pictures.add(file.getAbsolutePath());
                File pngFile = new File(root + File.separator + form.getFormName() + File.separator  + "thumbs", file.getName().replace("jpg", "png"));
                thumbs.add(pngFile.getAbsolutePath());
            }
        }

        patientForm.setThumbImages(thumbs);
        patientForm.setPictures(pictures);

        return patientForm;
    }

    /**
     * Method to reload the list
     */
    private void reloadList() {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new File(root, form.getFileName()));

            XSSFSheet sheet = wb.getSheet(form.getSheetName());
            formList.clear();
            sections = new ArrayList<>();
            readExcelFile(sheet);
        } catch (Exception ex) {
            Log.e(TAG, getString(R.string.error_while_reading_xlsx, ex.getMessage()));
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_warning_title)
                    .setMessage(getString(R.string.error_while_reading_xlsx, ex.getMessage()))
                    .setPositiveButton(getString(R.string.dialog_ok), null).show();
        }

    }

    @Override
    public void onItemClick(View view, FormTemplate form) {
        Intent intent = new Intent(this, OverviewFormActivity.class);
        intent.putExtra("obj_form", form);
        intent.putExtra("requestCode", REQUEST_ADJUST_FORM);
        startActivityForResult(intent, REQUEST_ADJUST_FORM);
    }

    @Override
    public void onBackPressed() {
        Intent mainActivity = new Intent(FormListActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i = getIntent();
        if(i.hasExtra("fileName") && i.hasExtra("sheetName")) {
            loadIntent();
        } else{
            if(formList.size() != 0) {
                reloadList();
            }
        }

    }

    @Override
    public void onItemClick(View view, File file) {
        form.setFileName(file.getName());
        form.setFormName(file.getName().substring(0, file.getName().lastIndexOf('.')));
        createExcelWorkbook(new File(root, file.getName()));
    }

    @Override
    public void onItemClick(View view, String sheetName) {
        sections = new ArrayList<>();
        readExcelFile(mWorkbook.getSheet(sheetName));
    }
}
