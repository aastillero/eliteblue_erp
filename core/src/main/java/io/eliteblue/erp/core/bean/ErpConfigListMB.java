package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ErpConfig;
import io.eliteblue.erp.core.service.ErpConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpConfigListMB implements Serializable {

    private final String pattern = "MMMM dd,yyyy";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    @Autowired
    private ErpConfigService erpConfigService;

    private List<ErpConfig> erpConfigs;

    @PostConstruct
    public void init() {
        erpConfigs = erpConfigService.getAll();
    }

    public List<ErpConfig> getErpConfigs() {
        return erpConfigs;
    }

    public void setErpConfigs(List<ErpConfig> erpConfigs) {
        this.erpConfigs = erpConfigs;
    }

    public String editConfPressed(Long id) {
        return "config-form?id="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public String getConfigValue(ErpConfig conf) {
        if(has(conf.getDateValue())) {
            return simpleDateFormat.format(conf.getDateValue());
        } else if(has(conf.getNumValue())) {
            return conf.getNumValue().toString();
        } else if(has(conf.getStrValue())) {
            return conf.getStrValue();
        }
        return "Empty Value";
    }
}
