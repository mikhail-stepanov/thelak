package com.thelak.database.entity.auto;

import java.time.LocalDateTime;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.thelak.database.entity.DbPaymentType;

/**
 * Class _DbPaymentsReccurrent was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DbPaymentsReccurrent extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Integer> AMOUNT = Property.create("amount", Integer.class);
    public static final Property<LocalDateTime> CREATED_DATE = Property.create("createdDate", LocalDateTime.class);
    public static final Property<String> CURRENCY = Property.create("currency", String.class);
    public static final Property<LocalDateTime> DELETED_DATE = Property.create("deletedDate", LocalDateTime.class);
    public static final Property<String> DESCRIPTION = Property.create("description", String.class);
    public static final Property<String> EMAIL = Property.create("email", String.class);
    public static final Property<String> INTERVAL = Property.create("interval", String.class);
    public static final Property<LocalDateTime> MODIFIED_DATE = Property.create("modifiedDate", LocalDateTime.class);
    public static final Property<Integer> PERIOD = Property.create("period", Integer.class);
    public static final Property<Boolean> REQUIRE_CONFIRMATION = Property.create("requireConfirmation", Boolean.class);
    public static final Property<LocalDateTime> START_DATE = Property.create("startDate", LocalDateTime.class);
    public static final Property<Boolean> STATUS = Property.create("status", Boolean.class);
    public static final Property<DbPaymentType> RECURRENT_TO_TYPE = Property.create("recurrentToType", DbPaymentType.class);

    public void setAmount(int amount) {
        writeProperty("amount", amount);
    }
    public int getAmount() {
        Object value = readProperty("amount");
        return (value != null) ? (Integer) value : 0;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        writeProperty("createdDate", createdDate);
    }
    public LocalDateTime getCreatedDate() {
        return (LocalDateTime)readProperty("createdDate");
    }

    public void setCurrency(String currency) {
        writeProperty("currency", currency);
    }
    public String getCurrency() {
        return (String)readProperty("currency");
    }

    public void setDeletedDate(LocalDateTime deletedDate) {
        writeProperty("deletedDate", deletedDate);
    }
    public LocalDateTime getDeletedDate() {
        return (LocalDateTime)readProperty("deletedDate");
    }

    public void setDescription(String description) {
        writeProperty("description", description);
    }
    public String getDescription() {
        return (String)readProperty("description");
    }

    public void setEmail(String email) {
        writeProperty("email", email);
    }
    public String getEmail() {
        return (String)readProperty("email");
    }

    public void setInterval(String interval) {
        writeProperty("interval", interval);
    }
    public String getInterval() {
        return (String)readProperty("interval");
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        writeProperty("modifiedDate", modifiedDate);
    }
    public LocalDateTime getModifiedDate() {
        return (LocalDateTime)readProperty("modifiedDate");
    }

    public void setPeriod(int period) {
        writeProperty("period", period);
    }
    public int getPeriod() {
        Object value = readProperty("period");
        return (value != null) ? (Integer) value : 0;
    }

    public void setRequireConfirmation(boolean requireConfirmation) {
        writeProperty("requireConfirmation", requireConfirmation);
    }
	public boolean isRequireConfirmation() {
        Boolean value = (Boolean)readProperty("requireConfirmation");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setStartDate(LocalDateTime startDate) {
        writeProperty("startDate", startDate);
    }
    public LocalDateTime getStartDate() {
        return (LocalDateTime)readProperty("startDate");
    }

    public void setStatus(boolean status) {
        writeProperty("status", status);
    }
	public boolean isStatus() {
        Boolean value = (Boolean)readProperty("status");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setRecurrentToType(DbPaymentType recurrentToType) {
        setToOneTarget("recurrentToType", recurrentToType, true);
    }

    public DbPaymentType getRecurrentToType() {
        return (DbPaymentType)readProperty("recurrentToType");
    }


}
