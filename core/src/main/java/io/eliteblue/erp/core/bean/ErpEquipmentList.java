package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.Archipelago;
import io.eliteblue.erp.core.lazy.LazyErpEquipmentModel;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpEquipmentList implements Serializable {

    private final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    @Autowired
    private ErpEquipmentService erpEquipmentService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Autowired
    private ErpDetachmentService detachmentService;

    @Autowired
    private OperationsAreaService operationsAreaService;

    @Autowired
    private EquipmentTypeService equipmentTypeService;

    @Autowired
    private EquipmentCategoryService equipmentCategoryService;

    @Autowired
    private EquipmentSubCategoryService equipmentSubCategoryService;

    private LazyDataModel<ErpEquipment> lazyErpEquipments;

    private List<ErpEquipment> filteredErpEquipments;

    private List<ErpEquipment> erpEquipments;

    private List<ErpDetachment> detachments;

    private ErpEquipment selectedErpEquipment;

    @PostConstruct
    public void init() {
        erpEquipments = erpEquipmentService.getAllFilteredByDetachmentLocation();
        detachments = CurrentUserUtil.getDetachments();
        detachments.sort(Comparator.comparing((ErpDetachment ewa) -> ewa.getName()));
        lazyErpEquipments = new LazyErpEquipmentModel(erpEquipments);
        lazyErpEquipments.setRowCount(10);
    }

    public LazyDataModel<ErpEquipment> getLazyErpEquipments() {
        return lazyErpEquipments;
    }

    public void setLazyErpEquipments(LazyDataModel<ErpEquipment> lazyErpEquipments) {
        this.lazyErpEquipments = lazyErpEquipments;
    }

    public List<ErpEquipment> getFilteredErpEquipments() {
        return filteredErpEquipments;
    }

    public void setFilteredErpEquipments(List<ErpEquipment> filteredErpEquipments) {
        this.filteredErpEquipments = filteredErpEquipments;
    }

    public List<ErpEquipment> getErpEquipments() {
        return erpEquipments;
    }

    public void setErpEquipments(List<ErpEquipment> erpEquipments) {
        this.erpEquipments = erpEquipments;
    }

    public ErpEquipment getSelectedErpEquipment() {
        return selectedErpEquipment;
    }

    public void setSelectedErpEquipment(ErpEquipment selectedErpEquipment) {
        this.selectedErpEquipment = selectedErpEquipment;
    }

    public List<ErpDetachment> getDetachments() {
        return detachments;
    }

    public void setDetachments(List<ErpDetachment> detachments) {
        this.detachments = detachments;
    }

    public void onRowSelect(SelectEvent<ErpEquipment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equipment-form.xhtml?id="+selectedErpEquipment.getId()+"&refNum="+selectedErpEquipment.getRefNum());
    }

    public void onRowUnselect(UnselectEvent<ErpEquipment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equipment-form.xhtml?id="+selectedErpEquipment.getId()+"&refNum="+selectedErpEquipment.getRefNum());
    }

    public long summaryCounter(List<EquipmentType> equipmentTypes, List<ErpEquipment> filteredEq) {
        long retVal = 0;
        for(EquipmentType eqType: equipmentTypes) {
            //System.out.println("Type: "+eqType.getName());
            retVal += filteredEq.stream().filter(eequip -> eequip.getEquipmentType().getName().equals(eqType.getName())).count();
        }
        return retVal;
    }

    public void downloadSummary() throws Exception {
        LocalDate today = LocalDate.now();
        if(has(erpEquipments)) {
            ExcelUtils.initializeWithFilename("summary_of_eq_template.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String romanCount = "";
            int x = 8;
            int a = x+1;
            for(Archipelago ar: Archipelago.values()) {
                romanCount += "I";
                ExcelUtils.setCell(x, 2, romanCount+". "+ar.name());
                x+=1;
                char letter = 'A';
                for(OperationsArea op: operationsAreaService.getAll()) {
                    if(op.getArchipelago() != null && op.getArchipelago().equals(ar)) {
                        ExcelUtils.setCell(x, 2, letter+". "+op.getLocation());
                        List<ErpEquipment> filteredEq = new ArrayList<>();
                        filteredEq = erpEquipments.stream()
                                .filter(erpEquipment -> erpEquipment.getLocation() != null && erpEquipment.getLocation().getLocation().equals(op.getLocation()))
                                .collect(Collectors.toList());
                        //System.out.println("FILTERED EQ SIZE: "+filteredEq.size());
                        // Communication + Hand held radio
                        EquipmentCategory communication = equipmentCategoryService.findByName("COMMUNICATION");
                        EquipmentSubCategory handHeldRadio = equipmentSubCategoryService.findByName("HAND HELD RADIO");
                        List<EquipmentType> typeList = equipmentTypeService.findByCategoryAndSubCategory(communication, handHeldRadio);
                        long handHeldRadioCount = summaryCounter(typeList, filteredEq);
                        //System.out.println("COUNT: "+handHeldRadioCount);
                        ExcelUtils.setCell(x, 3, handHeldRadioCount);
                        EquipmentSubCategory baseRadio = equipmentSubCategoryService.findByName("BASE RADIO");
                        List<EquipmentType> baseTypeList = equipmentTypeService.findByCategoryAndSubCategory(communication, baseRadio);
                        long baseRadioCount = summaryCounter(baseTypeList, filteredEq);
                        ExcelUtils.setCell(x, 4, baseRadioCount);
                        EquipmentSubCategory repeater = equipmentSubCategoryService.findByName("REPEATER");
                        List<EquipmentType> repeaterTypeList = equipmentTypeService.findByCategoryAndSubCategory(communication, repeater);
                        long repeaterCount = summaryCounter(repeaterTypeList, filteredEq);
                        ExcelUtils.setCell(x, 5, repeaterCount);
                        EquipmentSubCategory mobilePhone = equipmentSubCategoryService.findByName("MOBILE PHONE");
                        List<EquipmentType> mobileTypeList = equipmentTypeService.findByCategoryAndSubCategory(communication, mobilePhone);
                        long mobileCount = summaryCounter(mobileTypeList, filteredEq);
                        ExcelUtils.setCell(x, 6, mobileCount);
                        EquipmentCategory vehicle = equipmentCategoryService.findByName("VEHICLE");
                        EquipmentSubCategory twoWheels = equipmentSubCategoryService.findByName("2 WHEELS");
                        List<EquipmentType> twoTypeList = equipmentTypeService.findByCategoryAndSubCategory(vehicle, twoWheels);
                        long twoCount = summaryCounter(twoTypeList, filteredEq);
                        ExcelUtils.setCell(x, 7, twoCount);
                        EquipmentSubCategory fourWheels = equipmentSubCategoryService.findByName("4 WHEELS");
                        List<EquipmentType> fourTypeList = equipmentTypeService.findByCategoryAndSubCategory(vehicle, fourWheels);
                        long fourCount = summaryCounter(fourTypeList, filteredEq);
                        ExcelUtils.setCell(x, 8, fourCount);
                        EquipmentCategory officeEq = equipmentCategoryService.findByName("OFFICE EQUIPMENT");
                        EquipmentSubCategory comp = equipmentSubCategoryService.findByName("COMPUTER/LAPTOP");
                        List<EquipmentType> compTypeList = equipmentTypeService.findByCategoryAndSubCategory(officeEq, comp);
                        long compCount = summaryCounter(compTypeList, filteredEq);
                        ExcelUtils.setCell(x, 9, compCount);
                        EquipmentSubCategory printer = equipmentSubCategoryService.findByName("PRINTER");
                        List<EquipmentType> printTypeList = equipmentTypeService.findByCategoryAndSubCategory(officeEq, printer);
                        long printCount = summaryCounter(printTypeList, filteredEq);
                        ExcelUtils.setCell(x, 10, printCount);
                        x++;
                        letter++;
                    }
                }
                x+=1;
            }
            int b = x-1;
            // TOTALS
            ExcelUtils.setCellFormula(23, 3, "SUM(D"+a+":D"+b+")");
            ExcelUtils.setCellFormula(23, 4, "SUM(E"+a+":E"+b+")");
            ExcelUtils.setCellFormula(23, 5, "SUM(F"+a+":F"+b+")");
            ExcelUtils.setCellFormula(23, 6, "SUM(G"+a+":G"+b+")");
            ExcelUtils.setCellFormula(23, 7, "SUM(H"+a+":H"+b+")");
            ExcelUtils.setCellFormula(23, 8, "SUM(I"+a+":I"+b+")");
            ExcelUtils.setCellFormula(23, 9, "SUM(J"+a+":J"+b+")");
            ExcelUtils.setCellFormula(23, 10, "SUM(K"+a+":K"+b+")");

            String usersName = CurrentUserUtil.getName().toUpperCase();
            generateFile(ExcelUtils.workbook, "EQ_SUMMARY_"+usersName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadAllEquipments() throws Exception {
        LocalDate today = LocalDate.now();
        if(has(erpEquipments)) {
            // sort by Detachments
            Comparator<ErpEquipment> comparator = Comparator.comparing(eq -> Optional.ofNullable(eq.getDetachment()).map(ErpDetachment::getName).orElse(""), Comparator.nullsFirst(String::compareTo));
            //erpEquipments.sort(Comparator.comparing((ErpEquipment ewa) -> ewa.getDetachment().getName());
            erpEquipments.sort(comparator);
            ExcelUtils.initializeWithFilename("equipment_all.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            ExcelUtils.setCell(1, 1, format.format(new Date()));
            String currentUserName = CurrentUserUtil.getFullName();
            ExcelUtils.setCell(3, 1, currentUserName);
            int x = 7;
            for(ErpEquipment equip: erpEquipments) {
                ExcelUtils.setCell(x, 0, equip.getRefNum());
                ExcelUtils.setCell(x, 1, equip.getName());
                ExcelUtils.setCell(x, 2, equip.getEquipmentType().getName());
                ExcelUtils.setCell(x, 3, equip.getEquipmentStatus().getName());
                ExcelUtils.setCell(x, 4, equip.getSerialNo());
                ExcelUtils.setCell(x, 5, equip.getDescription());
                if(equip.getDetachment() != null)
                    ExcelUtils.setCell(x, 6, equip.getDetachment().getName());
                x++;
            }
            String usersName = CurrentUserUtil.getName().toUpperCase();
            generateFile(ExcelUtils.workbook, "EQ_"+usersName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        } else {
            addDetailMessage("NO EQUIPMENTS", "There are no Equipments currently assigned to you.", FacesMessage.SEVERITY_ERROR);
        }
    }

    public void downloadDetachmentEquipment(Long id) throws Exception {
        System.out.println("DETACHMENT ID: "+Long.valueOf(id));
        ErpDetachment detachment = detachmentService.findById(Long.valueOf(id));
        LocalDate today = LocalDate.now();
        if(has(detachment)) {
            // filter equipment with the detachment
            //List<ErpEquipment> filterEquips = erpEquipments.stream().filter(itm->itm.getDetachment().getId().equals(detachment.getId())).collect(Collectors.toList());
            List<ErpEquipment> filterEquips = erpEquipments.stream().filter(
                    itm->itm.getDetachment() != null && Objects.equals(itm.getDetachment().getId() ,detachment.getId())
            ).collect(Collectors.toList());
            ExcelUtils.initializeWithFilename("equipment_detachment.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            ExcelUtils.setCell(1, 1, format.format(new Date()));
            ExcelUtils.setCell(2,1, capitalizeString(detachment.getName().toUpperCase()));
            String currentUserName = CurrentUserUtil.getFullName();
            ExcelUtils.setCell(3, 1, currentUserName);
            int x = 7;
            for(ErpEquipment equip: filterEquips) {
                ExcelUtils.setCell(x, 0, equip.getRefNum());
                ExcelUtils.setCell(x, 1, equip.getName());
                ExcelUtils.setCell(x, 2, equip.getEquipmentType().getName());
                ExcelUtils.setCell(x, 3, equip.getEquipmentStatus().getName());
                ExcelUtils.setCell(x, 4, equip.getSerialNo());
                ExcelUtils.setCell(x, 5, equip.getDescription());
                x++;
            }
            String detachmentName = detachment.getName().replaceAll(" ", "_");
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "EQ_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        } else {
            addDetailMessage("INTERNAL ERROR", "Cannot find detachment.", FacesMessage.SEVERITY_ERROR);
        }
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpEquipment erpEquipment = (ErpEquipment) value;
        return erpEquipment.getName().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
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

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
