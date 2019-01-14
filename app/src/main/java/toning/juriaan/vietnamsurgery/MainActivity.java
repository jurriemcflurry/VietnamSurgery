package toning.juriaan.vietnamsurgery;

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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import toning.juriaan.vietnamsurgery.activity.FormActivity;
import toning.juriaan.vietnamsurgery.activity.FormListActivity;
import toning.juriaan.vietnamsurgery.activity.OverviewFormActivity;
import toning.juriaan.vietnamsurgery.adapter.FileNameAdapter;
import toning.juriaan.vietnamsurgery.adapter.FormListAdapter;
import toning.juriaan.vietnamsurgery.adapter.SheetAdapter;
import toning.juriaan.vietnamsurgery.listener.FileNameListener;
import toning.juriaan.vietnamsurgery.listener.SheetListener;
import toning.juriaan.vietnamsurgery.model.Field;
import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.model.Section;

public class MainActivity extends AppCompatActivity implements FileNameListener, SheetListener {

    private DrawerLayout mDrawerLayout;
    private static final String TAG = "MyActivity";
    private FormTemplate form = new FormTemplate();
    private List<Section> sections = new ArrayList<>();
    Toolbar toolbar;
    private ActionBar ab;
    File root = new File(Environment.getExternalStorageDirectory().toString() + "/LenTab/lentab-susanne");
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
    FileNameAdapter mAdapterFiles;
    SheetAdapter mAdapterSheets;
    TextView chooseText;
    XSSFWorkbook mWorkbook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //thema moet altijd worden gezet naar AppTheme, zodat de Launcher van het splashscreen niet bij elke actie wordt getoond
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFields();
        setupToolbar();
        setupNavigation();
        chooseExcelFile();
    }

    private void setupFields(){
        toolbar = findViewById(R.id.form_toolbar);
        mRecyclerView = findViewById(R.id.grid_view_main);
        chooseText = findViewById(R.id.choose_text);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle(R.string.main_title_name);
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
        menu.findItem(R.id.main_activity).setVisible(false);

        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    switch(menuItem.getItemId()){
                        case R.id.filled_in_forms:
                            Intent formOverview = new Intent(MainActivity.this, FormListActivity.class);
                            startActivity(formOverview);
                            finish();
                            break;
                        default: break;
                    }

                    return true;
                }
            });
    }

    // Get all the files with xlsx extension
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

    private void chooseExcelFile() {
        try{
            List<File> files = getListFiles(root);

            // Check if there are more than 1 file, if so, show clickables for all files. If not: load the first one directly
            if(files.size() > 1) {
                chooseText.setText(R.string.choose_file_text);
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
            chooseText.setText(R.string.choose_sheet_text);
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

            XSSFWorkbook wb = sheet.getWorkbook();
            wb.close();
            Intent i = new Intent(this, FormActivity.class);
            i.putExtra("obj_form", form);
            startActivity(i);
            finish();
        } catch (Exception ex) {
            Log.e(TAG, getString(R.string.error_while_reading_xlsx, ex.getMessage()));
        }
    }

    // Storage Permissions
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
