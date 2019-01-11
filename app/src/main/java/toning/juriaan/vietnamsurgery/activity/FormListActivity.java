package toning.juriaan.vietnamsurgery.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import toning.juriaan.vietnamsurgery.adapter.FileNameAdapter;
import toning.juriaan.vietnamsurgery.adapter.SheetAdapter;
import toning.juriaan.vietnamsurgery.listener.FileNameListener;
import toning.juriaan.vietnamsurgery.listener.FormListListener;
import toning.juriaan.vietnamsurgery.MainActivity;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.adapter.FormListAdapter;
import toning.juriaan.vietnamsurgery.listener.SheetListener;
import toning.juriaan.vietnamsurgery.model.Field;
import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.model.Section;

public class FormListActivity extends AppCompatActivity implements FormListListener, SheetListener, FileNameListener {

    final static int REQUEST_ADJUST_FORM = 3;
    private final String TAG = this.getClass().getSimpleName();
    FormTemplate form;
    private List<Section> sections = new ArrayList<>();
    private ArrayList<FormTemplate> formList = new ArrayList<>();
    Toolbar toolbar;
    private ActionBar ab;
    private DrawerLayout mDrawerLayout;
    final private File root =  new File(Environment.getExternalStorageDirectory().toString() + "/LenTab/lentab-susanne");
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
    FileNameAdapter mAdapterFiles;
    SheetAdapter mAdapterSheets;
    XSSFWorkbook mWorkbook;

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
            }
        } else {
            chooseExcelFile();
        }
    }

    private void setupFields(){
        toolbar = findViewById(R.id.form_toolbar);
        form = new FormTemplate();
        mRecyclerView = findViewById(R.id.grid_view_form_list_files);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
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

    private void setupNavigation(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.filled_in_forms).setVisible(false);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
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
                    }
                });
    }

    private void chooseExcelFile() {
        try{
            List<File> files = getListFiles(root);

            // Check if there are more than 1 file, if so, show clickables for all files. If not: load the first one directly
            if(files.size() > 1) {
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapterFiles = new FileNameAdapter(this, files, this);
                mRecyclerView.setAdapter(mAdapterFiles);
            } else {
                form.setFileName(files.get(0).getName());
                form.setFormName(files.get(0).getName().substring(0, files.get(0).getName().lastIndexOf('.')));
                createExcelWorkbook(new File(root, files.get(0).getName()));
            }
        } catch (Exception ex) {
            Toast.makeText(this, getString(R.string.error_while_finding_xlsx, ex.getMessage(), root.getPath()), Toast.LENGTH_LONG).show();
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private List<File> getListFiles(File parentDir) {
        // Check if we can read/write to the storage. If so, continue; if not; prompt the user
        verifyStoragePermissions(this);

        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();

        for( File file : files) {
            if(file.getName().substring(file.getName().lastIndexOf('.') + 1).equals("xlsx")) {
                inFiles.add(file);
            }
        }

        return inFiles;
    }

    private void createExcelWorkbook(File file) {
        try {
            // Create a workbook object
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            chooseExcelSheet(workbook);
        } catch (Exception ex) {
            Log.i(TAG, getString(R.string.error_while_opening_xlsx, ex.getMessage()));
        }
    }

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
            mAdapterSheets = new SheetAdapter(this, sheets, this);
            mRecyclerView.setAdapter(mAdapterSheets);
        }
    }

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
        }
    }

    private void putAnswers(Sheet sheet) {

        int firstRowNum = 5;
        int lastRowNum = sheet.getLastRowNum();

        for(int i = firstRowNum; i <= lastRowNum; i++)
        {
            Row row = sheet.getRow(i);
            if(!isRowEmpty(row)) {
                FormTemplate tempForm = new FormTemplate();
                tempForm.setFileName(form.getFileName());
                tempForm.setSheetName(form.getSheetName());
                tempForm.setFormName(form.getFormName());

                tempForm.setSections(createDeepCopyOfSections());
                tempForm.setRowNumber(row.getRowNum());
                String birthYear = "";

                for (Section sec : tempForm.getSections()) {
                    for (Field f : sec.getFields()) {
                        String answer;
                        if (row.getCell(f.getColumn()) != null) {
                            answer = row.getCell(f.getColumn()).getStringCellValue();
                            if(f.getRow() == 4 && f.getColumn() == 2 && !row.getCell(f.getColumn()).getStringCellValue().isEmpty()) {
                                birthYear = answer;
                                answer = "true";
                            }
                            if(f.getRow() == 4 && f.getColumn() == 3 && !row.getCell(f.getColumn()).getStringCellValue().isEmpty()) {
                                birthYear = answer;
                                answer = "true";
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

    private List<Section> createDeepCopyOfSections() {
        List<Section> l = new ArrayList<>();

        for(int k = 0; k < form.getSections().size(); k++) {
            Section s = new Section();
            s.setSectionName(form.getSections().get(k).getSectionName());
            s.setNumber(form.getSections().get(k).getNumber());
            s.setColumn(form.getSections().get(k).getColumn());
            List<Field> fields = new ArrayList<>();
            for(int counter = 0; counter < form.getSections().get(k).getFields().size(); counter++) {
                Field field = new Field();
                field.setColumn(form.getSections().get(k).getFields().get(counter).getColumn());
                field.setFieldName(form.getSections().get(k).getFields().get(counter).getFieldName());
                field.setRow(form.getSections().get(k).getFields().get(counter).getRow());
                fields.add(field);
            }
            s.setFields(fields);
            l.add(s);
        }

        return l;
    }

    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    private FormTemplate setListOfPicturesAndThumbs(FormTemplate patientForm) {
        List<String> pictures = new ArrayList<>();
        List<String> thumbs = new ArrayList<>();

        File storageDir = new File(Environment.getExternalStorageDirectory().toString() + "/LenTab/lentab-susanne/VietnamSurgery");
        File[] files = storageDir.listFiles();

        String patientName = patientForm.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = patientForm.getSections().get(0).getFields().get(2).getAnswer();

        for( File file : files) {
            // Todo: NAME OF PICTURES!
            if(file.getName().contains( patientName + "_" + birthYear + "_" )) {
                pictures.add(file.getAbsolutePath());
                File pngFile = new File(Environment.getExternalStorageDirectory().toString() + "/LenTab/lentab-susanne/VietnamSurgery/thumbs", file.getName().replace("jpg", "png"));
                thumbs.add(pngFile.getAbsolutePath());
            }
        }

        patientForm.setThumbImages(thumbs);
        patientForm.setPictures(pictures);

        return patientForm;
    }

    private void reloadList() {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new File(root, form.getFileName()));

            XSSFSheet sheet = wb.getSheet(form.getSheetName());
            formList.clear();
            sections = new ArrayList<>();
            readExcelFile(sheet);
        } catch (Exception ex) {
            Log.e(TAG, getString(R.string.error_while_reading_xlsx, ex.getMessage()));
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
        readExcelFile(mWorkbook.getSheet(sheetName));
    }
}
