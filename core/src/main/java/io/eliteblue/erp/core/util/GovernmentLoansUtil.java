package io.eliteblue.erp.core.util;

import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.service.GovernmentLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

@Component
public class GovernmentLoansUtil implements Serializable {

    private final DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    private List<String> sheets = Arrays.asList(
            "ABB BATAAN",
            "ARCHEN",
            "E-FARE(BIAAN)",
            "EMD BATAAN",
            "JE MANALO",
            "MAYANTOC - CHAREON",
            "PBR",
            "PETRO GAZZ",
            "PLT",
            "PNOC IP",
            "SEASIA", "SMILSI", "SMC CPC - BAGTAS", "SMC GPH - GLOBAL", "TMG TRUCKING", "TRIVEA");

    @Autowired
    private ErpEmployeeService employeeService;

    @Autowired
    private GovernmentLoanService governmentLoanService;

    public void startProcessLoans() throws Exception {
        ExcelUtils.initializeWithFilename("MONITORING OF HDMF CALAMITY LOAN", "SOUTHERN CROSS");
    }
}
