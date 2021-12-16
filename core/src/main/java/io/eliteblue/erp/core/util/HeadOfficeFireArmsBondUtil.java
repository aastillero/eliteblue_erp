package io.eliteblue.erp.core.util;

import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.service.HeadOfficeLoanService;
import org.apache.poi.ss.usermodel.CellValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

@Component
public class HeadOfficeFireArmsBondUtil {

    private final DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");

    @Autowired
    private ErpEmployeeService employeeService;

    @Autowired
    private HeadOfficeLoanService headOfficeLoanService;

    public void startProcessBonds() throws Exception {
        List<String> sheets = Arrays.asList("ABB BATAAN", "ARCHEN", "E-FARE(BIAAN)", "EMD BATAAN", "JE MANALO", "MAYANTOC - CHAREON", "PBR", "PETRO GAZZ", "PLT", "PNOC IP", "SEASIA", "SMILSI", "SMC CPC - BAGTAS", "SMC GPH - GLOBAL", "TMG TRUCKING", "TRIVEA");
        String filename = "BATAAN AREA - MONITORING BONDS.xlsx";
        startProcessBataan(filename, sheets, 2, 3, 8);
        sheets = Arrays.asList("SOUTHERN CROSS", "VILOIL", "SM", "PNOC PROVINCIAL (LEGAZPI)");
        filename = "BICOL- MONITORING BONDS.xlsx";
        List<Integer> idxLn = Arrays.asList(2,2,2,4);
        List<Integer> idxFn = Arrays.asList(3,3,3,5);
        List<Integer> idxBonds = Arrays.asList(8,8,8,10);
        startProcessBataanDiffIndex(filename, sheets, idxLn, idxFn, idxBonds);
        sheets = Arrays.asList("PNOC HEAD OFFICE", "INSULAR CALACA", "PNOC MABINI", "KOUFU", "ANGAT", "PNOC PROVINCIAL", "PNN MALVAR");
        filename = "CALABARZON - MONITORING BONDS.xlsx";
        idxLn = Arrays.asList(4,2,2,2,2,4,4);
        idxFn = Arrays.asList(5,3,3,3,3,5,5);
        idxBonds = Arrays.asList(10,8,8,8,8,10,10);
        startProcessBataanDiffIndex(filename, sheets, idxLn, idxFn, idxBonds);
        sheets = Arrays.asList("Boracay Airport", "SMPI Yapak", "SMPI Denpasar", "Projecsite", "La Belle-Akean", "La Belle-Gateway", "Marriot Hotel", "Safe Air Corp.", "Staging Area ");
        filename = "CATICLAN - MONITORING BONDS.xlsx";
        startProcessBataan(filename, sheets, 2, 3, 7);
        sheets = Arrays.asList("NEGROS");
        filename = "NEGROS _ BOHOL - MONITORING BONDS.xlsx";
        startProcessBataan(filename, sheets, 2, 3, 7);
        sheets = Arrays.asList("SAMAR & LEYTE");
        filename = "SAMAR _ LEYTE - MONITORING BONDS.xlsx";
        startProcessBataan(filename, sheets, 2, 3, 8);
    }

    public void startProcessBataan(String filename, List<String> sheets, int lnIndex, int fnIndex, int bondsIndex) throws Exception {
        ExcelUtils.initializeWithFilename(filename, sheets.get(0));
        for(String sh: sheets) {
            ExcelUtils.worksheet = ExcelUtils.workbook.getSheet(sh);
            System.out.println("SHEET: "+sh);
            //Integer rows = ExcelUtils.getRowCount();
            for (int x = 3; x < 180; x++) {
                processCellData(x, lnIndex, fnIndex, bondsIndex);
            }
        }
        ExcelUtils.workbook.close();
    }

    public void startProcessBataanDiffIndex(String filename, List<String> sheets, List<Integer> lnIndex, List<Integer> fnIndex, List<Integer> bondsIndex) throws Exception {
        ExcelUtils.initializeWithFilename(filename, sheets.get(0));
        for(String sh: sheets) {
            ExcelUtils.worksheet = ExcelUtils.workbook.getSheet(sh);
            System.out.println("SHEET: "+sh);
            //Integer rows = ExcelUtils.getRowCount();
            for (int x = 3, y = 0; x < 180; x++, y++) {
                processCellData(x, lnIndex.get(y), fnIndex.get(y), bondsIndex.get(y));
            }
        }
        ExcelUtils.workbook.close();
    }

    public void processCellData(int x, Integer lnIndex, Integer fnIndex, Integer bondsIndex) throws Exception {
        String lastName = (String) ExcelUtils.getCellData(x, lnIndex);
        String firstName = (String) ExcelUtils.getCellData(x, fnIndex);
        if(!(has(lastName) && has(firstName))) {
            return;
        }
        CellValue totBondsVal = ExcelUtils.evaluateCellFormula(x, bondsIndex);
        Double totalBonds = has(totBondsVal) ? totBondsVal.getNumberValue() : 0.0;
        System.out.println("NAME: "+firstName+" "+lastName);
        System.out.println("TOTAL BONDS: "+totalBonds);
    }
}
