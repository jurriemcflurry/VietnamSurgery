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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STDataValidationErrorStyleImpl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //thema moet altijd worden gezet naar AppTheme, zodat de Launcher van het splashscreen niet bij elke actie wordt getoond
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigation();

        Button OpenCamera = findViewById(R.id.ToCamera);
        OpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCamera = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(toCamera);
            }
        });

        Button toFormActivityButton = findViewById(R.id.toFormActivity);
        toFormActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFormActivityIntent = new Intent(MainActivity.this, FormActivity.class);
                startActivity(toFormActivityIntent);
            }
        });

        Button readExcel = findViewById(R.id.toReadExcel);
        readExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readExcelFile();
            }
        });
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
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.headerlayout);
        final TextView login = (TextView) header.findViewById(R.id.Logintext);
        final TextView loggedInUser = (TextView) header.findViewById(R.id.LoggedinUser);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ga naar pagina om in te loggen
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);

                loggedInUser.setText("Ingelogde Gebruiker"); //set text to logged in username
                login.setText("Log out"); //change text when logging in/out
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch(menuItem.getItemId()){
                            case 2131230828: //Bovenste Item
                                break;
                            case 2131230829: //2e item
                                break;
                            case 2131230830: //3e item
                                break;
                            case 2131230831: //4e item
                                break;
                            default: break;
                        }

                        return true;
                    }
                });
    }

    private void readExcelFile() {
        File root = Environment.getExternalStorageDirectory();
        File file = new File(root, "Screening.xlsx");

        try {
            // Check if we can read/write to the storage. If so, continue; if not; prompt the user
            verifyStoragePermissions(this);

            // Open the file
            FileInputStream fileInputStream = new FileInputStream(file);

            // Create a workbook object
            Workbook workbook = new XSSFWorkbook(file);

            XSSFSheet sheet = ((XSSFWorkbook) workbook).getSheetAt(0);

            int rowsCount = sheet.getPhysicalNumberOfRows();
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            List<List<String>> ret = new ArrayList<>();

            for(int i=firstRowNum+1; i<lastRowNum+1; i++)
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

                ret.add(rowDataList);
            }

            List<String> te = ret.get(1);

            for (int i = 0; i < te.size(); i++) {
                if(!te.get(i).isEmpty()) {
                    Log.i("TESTT",te.get(i) + " --- " + Integer.toString(i));
                }
            }

            List<String> tete = ret.get(2);

            for (int i = 0; i < tete.size(); i++) {
                if(!tete.get(i).isEmpty()) {
                    Log.i("TESTT",tete.get(i) + " --- " + Integer.toString(i));
                }
            }


            Toast.makeText(getApplicationContext(), "Finished", Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            Log.e("CHECKKK", ex.getMessage() + " -- " + ex.getCause());
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
}
