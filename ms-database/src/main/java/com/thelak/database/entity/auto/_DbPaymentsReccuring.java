package com.thelak.database.entity.auto;

import java.time.LocalDateTime;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.thelak.database.entity.DbPaymentType;

/**
 * Class _DbPaymentsReccuring was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DbPaymentsReccuring extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Integer> AMOUNT = Property.create("amount", Integer.class);
    public static final Property<LocalDateTime> CREATED_DATE = Property.create("createdDate", LocalDateTime.class);
    public static final Property<String> CURRENCY = Property.create("currency", String.class);
    public static final Property<LocalDateTime> DELETED_DATE = Property.create("deletedDate", LocalDateTime.class);
    public static final Property<String> DESCRIPTION = Property.create("description", String.class);
    public static final Property<LocalDateTime> MODIFIED_DATE = Property.create("modifiedDate", LocalDateTime.class);
    public static final Property<Boolean> STATUS = Property.create("status", Boolean.class);
    public static final Property<Long> TOKEN = Property.create("token", Long.class);
    public static final Property<DbPaymentType> RECURRING_TO_TYPE = Property.create("recurringToType", DbPaymentType.class);

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

    public void setModifiedDate(LocalDateTime modifiedDate) {
        writeProperty("modifiedDate", modifiedDate);
    }
    public LocalDateTime getModifiedDate() {
        return (LocalDateTime)readProperty("modifiedDate");
    }

    public void setStatus(boolean status) {
        writeProperty("status", status);
    }
	public boolean isStatus() {
        Boolean value = (Boolean)readProperty("status");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setToken(long token) {
        writeProperty("token", token);
    }
    public long getToken() {
        Object value = readProperty("token");
        return (value != null) ? (Long) value : 0;
    }

    public void setRecurringToType(DbPaymentType recurringToType) {
        setToOneTarget("recurringToType", recurringToType, true);
    }

    public DbPaymentType getRecurringToType() {
        return (DbPaymentType)readProperty("recurringToType");
    }


}
