package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.ErpSil;
import io.eliteblue.erp.core.model.view.SilMonitoring;
import io.eliteblue.erp.core.service.ErpDetachmentService;
import io.eliteblue.erp.core.service.ErpSilService;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class SilMonitorList implements Serializable {

    private final SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private ErpSilService silService;

    private Long id;

    private ErpDetachment erpDetachment;

    private List<ErpSil> erpSils;

    private List<SilMonitoring> monitoringList;

    public void init() {
        if (Faces.isAjaxRequest()) {
            return;
        }
        erpSils = new ArrayList<>();
        if (has(id)) {
            erpDetachment = erpDetachmentService.findById(Long.valueOf(id));
            if(has(erpDetachment) && has(erpDetachment.getAssignedEmployees())) {
                monitoringList = new ArrayList<>();
                erpSils = silService.findAllByDetachment(erpDetachment);
                erpSils.sort(Comparator.comparing(sil -> sil.getEmployee().getLastname()));
                List<ErpEmployee> employees = new ArrayList<>(erpDetachment.getAssignedEmployees());
                employees.sort(Comparator.comparing(ErpEmployee::getLastname));
                for(ErpEmployee emp: employees) {
                    List<ErpSil> empSil = erpSils.stream().filter(s -> s.getEmployee().equals(emp)).collect(Collectors.toList());
                    if(has(empSil)) {
                        SilMonitoring monitoring = new SilMonitoring(empSil);
                        monitoringList.add(monitoring);
                    }
                }
            }
        }
        else {
            erpDetachment = new ErpDetachment();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpDetachment getErpDetachment() {
        return erpDetachment;
    }

    public void setErpDetachment(ErpDetachment erpDetachment) {
        this.erpDetachment = erpDetachment;
    }

    public List<ErpSil> getErpSils() {
        return erpSils;
    }

    public void setErpSils(List<ErpSil> erpSils) {
        this.erpSils = erpSils;
    }

    public List<SilMonitoring> getMonitoringList() {
        return monitoringList;
    }

    public void setMonitoringList(List<SilMonitoring> monitoringList) {
        this.monitoringList = monitoringList;
    }

    public void downloadSil() throws Exception {
        LocalDate today = LocalDate.now();
        if(has(monitoringList) && monitoringList.size() > 0) {
            ExcelUtils.initializeWithFilename("sil_monitor_template.xlsx", "Sheet1");
            ExcelUtils.setCell(0,0, today.getYear()+" SIL MONITORING");
            int x = 5;
            for(SilMonitoring sm: monitoringList) {
                ExcelUtils.setCell(x, 1, sm.getName());
                if(has(sm.getDateHired())) {
                    ExcelUtils.setCell(x, 2, format.format(sm.getDateHired()));
                    ExcelUtils.setCell(x, 4, format.format(sm.getDateOfRegularity()));
                }
                ExcelUtils.setCell(x, 3, erpDetachment.getName());
                ExcelUtils.setCell(x, 5, sm.getDayOne());
                ExcelUtils.setCell(x, 6, sm.getPaymentDateOne());
                ExcelUtils.setCell(x, 7, sm.getDayTwo());
                ExcelUtils.setCell(x, 8, sm.getPaymentDateTwo());
                ExcelUtils.setCell(x, 9, sm.getDayThree());
                ExcelUtils.setCell(x, 10, sm.getPaymentDateThree());
                ExcelUtils.setCell(x, 11, sm.getDayFour());
                ExcelUtils.setCell(x, 12, sm.getPaymentDateFour());
                ExcelUtils.setCell(x, 13, sm.getDayFive());
                ExcelUtils.setCell(x, 14, sm.getPaymentDateFive());
                ExcelUtils.setCell(x, 15, sm.getTotalAvailment());
                x++;
            }
            String detachmentName = erpDetachment.getName().replaceAll(" ", "_");
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "SIL_MONITOR_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            ExcelUtils.workbook.close();
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
}
