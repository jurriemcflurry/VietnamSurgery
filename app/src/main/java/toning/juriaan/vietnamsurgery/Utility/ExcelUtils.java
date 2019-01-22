package toning.juriaan.vietnamsurgery.Utility;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

import toning.juriaan.vietnamsurgery.model.Field;
import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.model.Section;

public final class ExcelUtils {

    /**
     * Method to create a deep copy of the sections for a new form
     * @param form FormTemplate to copy from
     * @return List with Sections for the new form
     */
    public static List<Section> createDeepCopyOfSections(FormTemplate form) {
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

    /**
     * Method to check if the row is empty
     * @param row Row to check
     * @return boolean
     */
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }
}
