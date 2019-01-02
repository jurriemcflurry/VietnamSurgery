package toning.juriaan.vietnamsurgery.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.io.File;
import java.io.FileOutputStream;

import toning.juriaan.vietnamsurgery.model.Field;
import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.model.Section;

public class OverviewFormActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FormTemplate form;
    private LinearLayout layout;
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout mFormOverview;
    LayoutInflater mInflator;

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


    private void loadIntent(){
        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");
    }
    private void setupFields() {
        mFormOverview = findViewById(R.id.formLayout);
        mInflator = getLayoutInflater();
        toolbar = findViewById(R.id.form_toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String name = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        ab.setTitle(getString(R.string.form_name, form.getFormName(), name, birthYear));
    }

    private void loadForm(){
        placeFieldsInOverview();
        placePicturesInOverview();
    }

    private void placeFieldsInOverview() {
        for (Section sec : form.getSections()) {
            View view = mInflator.inflate(R.layout.overview_grid_item_list, mFormOverview, false);
            TextView txtView = view.findViewById(R.id.section_name);
            txtView.setText(sec.getSectionName());
            ImageButton editBtn = view.findViewById(R.id.edit_btn);
            editBtn.setOnClickListener((View v) ->
                    Toast.makeText(OverviewFormActivity.this, "I pressed the editBtn!", Toast.LENGTH_LONG).show()
            );

            GridLayout mGridLayout = view.findViewById(R.id.grid_view);
            for( Field f : sec.getFields()) {
                View itemView = getLayoutInflater().inflate(R.layout.overview_grid_item, mGridLayout, false);
                TextView fieldName = itemView.findViewById(R.id.field_name);
                fieldName.setText(f.getFieldName());

                TextView fieldAnswer = itemView.findViewById(R.id.field_answer);
                fieldAnswer.setText(f.getAnswer());
                mGridLayout.addView(itemView);
            }

            mFormOverview.addView(view);
        }
    }

    private void placePicturesInOverview() {
        View headerView = mInflator.inflate(R.layout.overview_grid_item_list, mFormOverview, false);
        TextView txtView = headerView.findViewById(R.id.section_name);
        txtView.setText(R.string.section_name_photos);

        ImageButton editBtn = headerView.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener((View v) ->
                Toast.makeText(OverviewFormActivity.this, "I pressed the editBtn!", Toast.LENGTH_LONG).show()
        );

        mFormOverview.addView(headerView);

        LinearLayout mGallery = findViewById(R.id.photo_gallery);
        LayoutInflater mInflator = getLayoutInflater();
        int index = 0;
        for( String pathToFile : form.getThumbImages()) {
            try {
                View view = mInflator.inflate(R.layout.photo_gallery_item, mGallery, false);
                ImageView imageView = view.findViewById(R.id.image_list_iv);
                File file = new File(form.getPictures().get(index));
                imageView.setOnClickListener((View v) ->
                        goToDetailPage(file)
                );

                Bitmap pic = BitmapFactory.decodeFile(pathToFile);
                imageView.setImageBitmap(pic);
                mGallery.addView(view);
                index++;
            } catch (Exception ex) {
                Log.e("TESTT", "oops " + ex.getMessage());
            }
        }
    }

    private void saveForm(FormTemplate form) {
        Toast.makeText(OverviewFormActivity.this, "Saving the form. Wait a moment until it's finished.", Toast.LENGTH_LONG).show();
        String root = Environment.getExternalStorageDirectory().toString() + "/LenTab/lentab-susanne";
        File file = new File(root, form.getFileName());
        try{
            Workbook wb = WorkbookFactory.create(file);
            Sheet s = wb.getSheet(form.getSheetName());
            int firstRowNum = 5;
            int lastRowNum = getLastRowNum(firstRowNum, s);

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

            // Save the file! And delete the "new" file - Because of a f-up in POI we have to do this unfortunately
            File file2 = new File(root, "test.xlsx");
            FileOutputStream out = new FileOutputStream(file2);
            file2.delete();
            wb.write(out);
            wb.close();
            out.flush();
            out.close();
            goToTheStart();
        } catch (Exception ex) {
            Log.i("TESTT", ex.getMessage() + " -- " + ex.getCause());
        }
    }

    public static int getLastRowNum(int startRow, Sheet s) {
        if(startRow > s.getLastRowNum()) {
            return startRow;
        } else if(startRow == s.getLastRowNum()) {
            return startRow+1;
        } else {
            int rowNumEmptyRow = startRow;
            for (int i = startRow; i <= s.getLastRowNum(); i++) {
                Row r = s.getRow(i);
                if(isRowEmpty(r)) {
                    rowNumEmptyRow = r.getRowNum();
                    break;
                } else {
                    rowNumEmptyRow = r.getRowNum() + 1;
                }
            }
            return rowNumEmptyRow;
        }
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

    public void goToTheStart() {
        // Todo: Make it work?
    }

    private void goToDetailPage(File photoFile) {
        Intent intent = new Intent(this, DetailPhotoActivity.class);
        intent.putExtra("obj_form", form);
        intent.putExtra("photoUrl", photoFile.getAbsolutePath());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_next:
                new AlertDialog.Builder(OverviewFormActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to save the form?")
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveForm(form);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null).show();
                return true;
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
    public void onBackPressed() {
        Intent cameraActivity = new Intent(getApplicationContext(), CameraActivity.class);
        cameraActivity.putExtra("obj_form", form);
        startActivity(cameraActivity);
    }

}
