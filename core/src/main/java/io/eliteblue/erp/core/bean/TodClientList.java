package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyTODClientModel;
import io.eliteblue.erp.core.model.TODClient;
import io.eliteblue.erp.core.model.TODPersonnelShift;
import io.eliteblue.erp.core.service.TODClientService;
import io.eliteblue.erp.core.service.TODPersonnelShiftService;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class TodClientList implements Serializable {

    @Autowired
    private TODClientService todClientService;

    @Autowired
    private TODPersonnelShiftService todPersonnelShiftService;

    private LazyDataModel<TODClient> lazyTODClients;

    private List<TODClient> filteredTODClients;

    private List<TODClient> clients;

    private TODClient selectedClient;

    private UploadedFile file;

    @PostConstruct
    public void init() {
        clients = todClientService.getAll();
        clients.sort(Comparator.comparing(TODClient::getClientName));
        lazyTODClients = new LazyTODClientModel(clients);
        lazyTODClients.setRowCount(10);
    }

    public LazyDataModel<TODClient> getLazyTODClients() {
        return lazyTODClients;
    }

    public void setLazyTODClients(LazyDataModel<TODClient> lazyTODClients) {
        this.lazyTODClients = lazyTODClients;
    }

    public List<TODClient> getFilteredTODClients() {
        return filteredTODClients;
    }

    public void setFilteredTODClients(List<TODClient> filteredTODClients) {
        this.filteredTODClients = filteredTODClients;
    }

    public List<TODClient> getClients() {
        return clients;
    }

    public void setClients(List<TODClient> clients) {
        this.clients = clients;
    }

    public TODClient getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(TODClient selectedClient) {
        this.selectedClient = selectedClient;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void onRowSelect(SelectEvent<TODClient> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("tod-client-form.xhtml?id="+selectedClient.getId());
    }

    public void onRowUnselect(UnselectEvent<TODClient> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("tod-client-form.xhtml?id="+selectedClient.getId());
    }

    public String backBtnPressed() { return "tod-clients?faces-redirect=true&includeViewParams=true"; }

    public void downloadReport() throws Exception {
        LocalDate today = LocalDate.now();
        if (has(clients)) {
            ExcelUtils.initializeWithFilename("tod_report.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");

            int x = 6;
            CellStyle oldStyle = ExcelUtils.getCellStyle(x, 1);
            for(TODClient c: clients) {
                XSSFCellStyle style = ExcelUtils.getWorkBook().createCellStyle();
                XSSFCellStyle style2 = ExcelUtils.getWorkBook().createCellStyle();
                style.cloneStyleFrom(oldStyle);
                style2.cloneStyleFrom(oldStyle);
                XSSFFont font = ExcelUtils.getWorkBook().createFont();
                XSSFFont font2 = ExcelUtils.getWorkBook().createFont();
                font.setFamily(style.getFont().getFamily());
                font.setItalic(style.getFont().getItalic());
                font.setFontHeightInPoints(style.getFont().getFontHeightInPoints());
                font.setBold(true);
                style.setFont(font);
                style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                ExcelUtils.setCell(x,1, c.getClientName().toUpperCase(), style);
                font2.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                style2.setFont(font2);
                style2.setBorderTop(BorderStyle.NONE);
                style2.setBorderBottom(BorderStyle.NONE);
                ExcelUtils.setCell(x,0, c.getId(), style2);
                List<TODPersonnelShift> todPersonnelShifts = todPersonnelShiftService.findByClient(c);
                for(TODPersonnelShift s: todPersonnelShifts) {
                    String position = s.getPersonnelPosition().toUpperCase();
                    String personnelShift = s.getShift().name().replaceAll("_", " ").toUpperCase();
                    ExcelUtils.setCell(x,2, position+" "+personnelShift);
                    ExcelUtils.setCell(x,3, s.getNoSecurity());
                    ExcelUtils.setCell(x,4, s.getTod());
                    ExcelUtils.setCell(x,5, s.getTotalHrs1to15());
                    ExcelUtils.setCell(x,6, s.getTotalHrs16to28());
                    ExcelUtils.setCell(x,7, s.getTotalHrs16to29());
                    ExcelUtils.setCell(x,8, s.getTotalHrs16to30());
                    ExcelUtils.setCell(x,9, s.getTotalHrs16to31());
                    ExcelUtils.setCell(x,10, s.getId(), style2);
                    x++;
                }
                x++;
            }
            ExcelUtils.evaluateAllCells();
            generateFile(ExcelUtils.workbook, "TOD_SUMMARY_"+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void fileUpload() throws Exception {
        System.out.println("FILE: " + file);
        if (has(file) && file.getSize() > 0) {
            if(has(clients)) {
                ExcelUtils.initializeWithInputStream(file.getInputStream(), "Sheet1");
                int x = 6;
                for(TODClient c: clients) {
                    String clientId = (String) ExcelUtils.getCellData(x, 0);
                    if (!has(clientId) || clientId.isEmpty()) {
                        addDetailMessage("FAILED UPLOAD", file.getFileName() + "["+x+"] has no Client ID.", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        TODClient todClient = todClientService.findById(Long.valueOf(clientId));
                        List<TODPersonnelShift> todPersonnelShifts = todPersonnelShiftService.findByClient(todClient);
                        for(TODPersonnelShift perShift: todPersonnelShifts) {
                            String pps = (String) ExcelUtils.getCellData(x, 2);
                            String ppsId = (String) ExcelUtils.getCellData(x, 10);
                            String strNoSec = (String) ExcelUtils.getCellData(x, 3);
                            String strTod = (String) ExcelUtils.getCellData(x, 4);
                            Double tod = 0.0;
                            Double noSecurity = 0.0;
                            System.out.println("["+todClient.getClientName()+"] Personnel Shift: "+pps+" ID: "+ppsId);
                            TODPersonnelShift selected = getTODPersonnelShift(todPersonnelShifts, Long.valueOf(ppsId));
                            if(strTod != null && !strTod.isEmpty()) {
                                tod = Double.parseDouble(strTod);
                                selected.setTod(tod);
                            }
                            if(strNoSec != null && !strNoSec.isEmpty()) {
                                noSecurity = Double.parseDouble(strNoSec);
                                selected.setNoSecurity(noSecurity);
                            }
                            // calculate
                            selected.setTotalHrs1to15(tod * noSecurity * 15);
                            selected.setTotalHrs16to28(tod * noSecurity * 13);
                            selected.setTotalHrs16to29(tod * noSecurity * 14);
                            selected.setTotalHrs16to30(tod * noSecurity * 15);
                            selected.setTotalHrs16to31(tod * noSecurity * 16);
                            todPersonnelShiftService.save(selected);
                            x++;
                        }
                    }
                    x++;
                }
                ExcelUtils.workbook.close();
            }
        }
    }

    public TODPersonnelShift getTODPersonnelShift(List<TODPersonnelShift> shifts, Long id) {
        TODPersonnelShift retVal = null;
        for(TODPersonnelShift s: shifts) {
            if(s.getId() == id) {
                retVal = s;
                break;
            }
        }
        return retVal;
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        TODClient todClient = (TODClient) value;
        return todClient.getClientName().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public void generateFile(Workbook workbook, String fileName) throws IOException {
        final FacesContext fc = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = fc.getExternalContext();

        externalContext.responseReset();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");
        final HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        final ServletOutputStream out = response.getOutputStream();

        workbook.write(out);
        workbook.close();
        out.flush();
        fc.responseComplete();
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
