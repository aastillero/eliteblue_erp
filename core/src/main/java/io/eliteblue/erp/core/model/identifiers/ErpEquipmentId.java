package io.eliteblue.erp.core.model.identifiers;

import java.io.Serializable;
import java.util.Objects;

public class ErpEquipmentId implements Serializable {

    private Long id;
    private String refNum;

    public ErpEquipmentId() {}

    public ErpEquipmentId(Long id, String refNum) {
        this.id = id;
        this.refNum = refNum;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ErpEquipmentId))
            return false;
        ErpEquipmentId other = (ErpEquipmentId) o;
        boolean itemIdEquals = (this.refNum == null && other.refNum == null)
                || (this.refNum != null && this.refNum.equals(other.refNum));
        return this.id.equals(other.id) && itemIdEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, refNum);
    }
}
