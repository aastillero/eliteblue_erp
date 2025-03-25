package io.eliteblue.erp.core.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

@Component
@PropertySource("classpath:admin-config.properties")
public class ExcelUtils {

    private static String pathProp;

    @Value("${data.dir}")
    public void setPathProp(String prop) {
        pathProp = prop;
    }

    private static final String propDir = System.getProperty("user.dir");
    private static final String filepath = propDir+"/data/empmast-1.xlsx";
    private static final Path currentRelativePath = Paths.get("");
    private static final String s = currentRelativePath.toAbsolutePath().toString()+"/";
    private static final String sheetName = "empmast";
    private static final DataFormatter format = new DataFormatter();

    public static Workbook workbook;
    public static Sheet worksheet;

    public static int sheetIndex;

    public static void initializeWorkbook() throws Exception {
        //System.out.println("Current absolute path is: " + s);
        //InputStream file = new FileInputStream(filepath);
        if(workbook == null && worksheet == null) {
            workbook = new XSSFWorkbook(pathProp+"/data/empmast-1.xlsx");
            worksheet = workbook.getSheet(sheetName);
        }
    }

    public static void initializeWithFilename(String fileName, String sname) throws Exception {
        //System.out.println("Current absolute path is: " + s);
        //System.out.println("USERNAME: "+pathProp);
        String path = pathProp+"/data/"+fileName;
        //File file = new File(path);
        //InputStream file = new FileInputStream(path);
        //if(workbook == null && worksheet == null) {
            //OPCPackage pkg = OPCPackage.open(file);
            //workbook = new XSSFWorkbook(file);
            workbook = new XSSFWorkbook(new FileInputStream(path));
            //workbook = WorkbookFactory.create(file);
            worksheet = workbook.getSheet(sname);
        //}
    }

    public static void initializeWithInputStream(InputStream file, String sname) throws Exception {
        ZipSecureFile.setMinInflateRatio(0.00001);
        workbook = WorkbookFactory.create(file);
        worksheet = workbook.getSheet(sname);
    }

    public static void initializeUploadUtility(InputStream file) throws Exception {
        ZipSecureFile.setMinInflateRatio(0.00001);
        workbook = WorkbookFactory.create(file);
        if(workbook.getNumberOfSheets() > 0) {
            sheetIndex = 0;
            worksheet = workbook.getSheetAt(sheetIndex);
        }
    }

    public static void nextSheetUploadUtility() throws Exception {
        sheetIndex += 1;
        if(sheetIndex < workbook.getNumberOfSheets()) {
            worksheet = workbook.getSheetAt(sheetIndex);
        }
        //System.out.println("Traverse next sheet");
    }

    public static XSSFWorkbook getWorkBook() throws Exception {
        initializeWorkbook();
        return (XSSFWorkbook) workbook;
    }

    public static void createWorkSheet() throws Exception {
        // get number of sheets on XSSFWorkbook
        int numSheets = workbook.getNumberOfSheets();
        createWorkSheet("Sheet "+numSheets+1);
    }

    public static void createWorkSheet(String sheetName) throws Exception {
        worksheet = workbook.createSheet(sheetName);
    }

    public static void setWorksheet(String sheetName) throws Exception {
        worksheet = workbook.getSheet(sheetName);
    }

    public static Object getCellData(int row, int cell) throws Exception {
        initializeWorkbook();
        try {
            Object value = format.formatCellValue(worksheet.getRow(row).getCell(cell));
            return value;
        } catch (Exception e) {
            //System.out.println("Exception Message: "+e.getMessage());
            return null;
        }
        //System.out.println("DATA CELL VALUE: "+value);
    }

    public static int getLastRowNum() throws Exception {
        return worksheet.getLastRowNum();
    }

    public static int getLastCellNum(int row) throws Exception {
        try {
            int lastCellNum = worksheet.getRow(row).getLastCellNum();
            return lastCellNum;
        } catch (Exception e) {
            return -1;
        }
    }

    public static Cell getCell(int row, int cell) throws Exception {
        initializeWorkbook();
        Row _row = worksheet.getRow(row);
        Cell _cell;
        if(_row != null) {
            _cell = _row.getCell(cell);
            if(_cell != null) {
                return _cell;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getCellDate(int row, int cell) throws Exception {
        initializeWorkbook();
        try {
            Cell _cell = getCell(row, cell);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String formattedDate = dateFormat.format(_cell.getDateCellValue());
            return formattedDate;
        } catch (Exception e) {
            //System.out.println("Exception Message: "+e.getMessage());
            return null;
        }
    }

    public static void setAllowCellsFormat() throws Exception{
        ((XSSFSheet) worksheet).lockFormatCells(false);
    }

    public static void setCell(int row, int cell, Object val, CellStyle... style) throws Exception{
        initializeWorkbook();
        //System.out.println("ROW: "+row+" CELL: "+cell);
        Row _row = worksheet.getRow(row);
        Cell _cell;
        if(_row == null) {
            _row = worksheet.createRow(row);
            _cell = _row.createCell(cell);
        }
        else {
            _cell = _row.getCell(cell);
            if(_cell == null) {
                _cell = _row.createCell(cell);
            }
        }
        if (val instanceof String)
            _cell.setCellValue((String) val);
        if (val instanceof Long)
            _cell.setCellValue((Long) val);
        if (val instanceof Integer)
            _cell.setCellValue((Integer) val);
        if (val instanceof Double)
            _cell.setCellValue((Double) val);
        if(style != null && style.length > 0) {
            _cell.setCellStyle(style[0]);
        }
    }

    public static void setCellFormula(int row, int cell, String val, CellStyle... style) throws Exception {
        initializeWorkbook();
        Row _row = worksheet.getRow(row);
        Cell _cell;
        if(_row == null) {
            _row = worksheet.createRow(row);
            _cell = _row.createCell(cell);
        }
        else {
            _cell = _row.getCell(cell);
            if(_cell == null) {
                _cell = _row.createCell(cell);
            }
        }
        if(style != null && style.length > 0)
            _cell.setCellStyle(style[0]);
        _cell.setCellFormula(val);
    }

    public static void setCellTime(int row, int cell, String val, CellStyle... style) throws Exception {
        initializeWorkbook();
        Row _row = worksheet.getRow(row);
        Cell _cell;
        if(_row == null) {
            _row = worksheet.createRow(row);
            _cell = _row.createCell(cell);
        }
        else {
            _cell = _row.getCell(cell);
            if(_cell == null) {
                _cell = _row.createCell(cell);
            }
        }
        double time = DateUtil.convertTime(val);
        CellStyle cellStyle = (style.length > 0 ) ? style[0] : workbook.createCellStyle();
        CreationHelper createHelper1 = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper1.createDataFormat().getFormat("HH:MM"));
        _cell.setCellValue(time);
        _cell.setCellStyle(cellStyle);
    }

    public static void evaluateCell(int row, int cell) throws Exception {
        initializeWorkbook();
        Cell _cell = worksheet.getRow(row).getCell(cell);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateFormulaCell(_cell);
    }

    public static CellValue evaluateCellFormula(int row, int cell) throws Exception {
        initializeWorkbook();
        Cell _cell = worksheet.getRow(row).getCell(cell);
        //System.out.println("CELL: "+_cell);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        //System.out.println("Evaluator: "+evaluator);
        return evaluator.evaluate(_cell);
    }

    public static String getEvaluatedValue(int row, int cell) throws Exception {
        initializeWorkbook();
        try {
            Cell _cell = worksheet.getRow(row).getCell(cell);
            //System.out.println("CELL: "+_cell);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            //System.out.println("Evaluator: "+evaluator);
            CellValue cellValue = evaluator.evaluate(_cell);
            if(cellValue == null) {
                return null;
            }
            String computedValue;
            if (cellValue.getCellType() == CellType.NUMERIC) {
                computedValue = String.valueOf(cellValue.getNumberValue());
            } else if (cellValue.getCellType() == CellType.STRING) {
                computedValue = cellValue.getStringValue();
            } else {
                // Handle other types if needed
                computedValue = ""; // Empty string as a default value
            }
            return computedValue;
        } catch (Exception e) {
            return null;
        }
    }

    public static CellStyle getCellStyle(int row, int cell) throws Exception {
        initializeWorkbook();
        //System.out.println("worksheet: "+worksheet);
        //System.out.println("getRow(): "+worksheet.getRow(row));
        try {
            Row r = worksheet.getRow(row);
            //System.out.println("getCell"+r.getCell(cell, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            Cell _cell = worksheet.getRow(row).getCell(cell, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            return _cell.getCellStyle();
        } catch (Exception e) {
            return null;
        }
    }

    public static void evaluateAllCells() throws Exception {
        initializeWorkbook();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
    }

    public static void copyRange(int srcStartRow, int srcEndRow, int destRow) throws Exception {
        initializeWorkbook();
        ((XSSFSheet) worksheet).copyRows(srcStartRow, srcEndRow, destRow, new CellCopyPolicy());
    }

    public static void insertRow(int rowIndexToInsert, int... row) throws Exception {
        initializeWorkbook();
        Row currentRow = worksheet.getRow(0);
        if(row != null) {
            currentRow = worksheet.getRow(row[0]);
        }
        worksheet.shiftRows(rowIndexToInsert, worksheet.getLastRowNum(), 1, true, true);
        Row newRow = worksheet.createRow(rowIndexToInsert);
        // Copy style from the current row to the new row
        for (int i = 0; i < currentRow.getLastCellNum(); i++) {
            Cell currentCell = currentRow.getCell(i);
            if (currentCell != null) {
                Cell newCell = newRow.createCell(i);
                newCell.setCellStyle(currentCell.getCellStyle());
                newCell.setCellType(currentCell.getCellType());
            }
        }
    }

    public static Integer getRowCount() throws Exception {
        initializeWorkbook();
        Integer rowCount = worksheet.getPhysicalNumberOfRows();
        System.out.println("ROW COUNT: "+rowCount.toString());
        return rowCount;
    }
}
