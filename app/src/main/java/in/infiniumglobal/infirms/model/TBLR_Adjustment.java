package in.infiniumglobal.infirms.model;

/**
 * Created by Hiral on 1/26/2016.
 */
public class TBLR_Adjustment {

    private String AdjustmentId;
    private String CustomerId;
    private String RevenueTypeId;
    private String AdjustmentDate;
    private String AdjustmentType;
    private String Amount;
    private String Remarks;
    private String CreatedBy;
    private String CreatedDate;

    public String getAdjustmentId() {
        return AdjustmentId;
    }

    public void setAdjustmentId(String adjustmentId) {
        AdjustmentId = adjustmentId;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }

    public String getRevenueTypeId() {
        return RevenueTypeId;
    }

    public void setRevenueTypeId(String revenueTypeId) {
        RevenueTypeId = revenueTypeId;
    }

    public String getAdjustmentDate() {
        return AdjustmentDate;
    }

    public void setAdjustmentDate(String adjustmentDate) {
        AdjustmentDate = adjustmentDate;
    }

    public String getAdjustmentType() {
        return AdjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        AdjustmentType = adjustmentType;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}