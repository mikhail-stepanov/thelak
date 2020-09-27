package com.thelak.database.entity.auto;

import java.time.LocalDateTime;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.thelak.database.entity.DbPaymentType;

/**
 * Class _DbPaymentsCryptogramm was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DbPaymentsCryptogramm extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Integer> AMOUNT = Property.create("amount", Integer.class);
    public static final Property<String> CARD_CRYPTOGRAM = Property.create("cardCryptogram", String.class);
    public static final Property<LocalDateTime> CREATED_DATE = Property.create("createdDate", LocalDateTime.class);
    public static final Property<String> CURRENCY = Property.create("currency", String.class);
    public static final Property<LocalDateTime> DELETED_DATE = Property.create("deletedDate", LocalDateTime.class);
    public static final Property<String> DESCRIPTION = Property.create("description", String.class);
    public static final Property<LocalDateTime> MODIFIED_DATE = Property.create("modifiedDate", LocalDateTime.class);
    public static final Property<String> NAME = Property.create("name", String.class);
    public static final Property<Boolean> STATUS = Property.create("status", Boolean.class);
    public static final Property<DbPaymentType> CRYPTOGRAMM_PAYMENT_TO_TYPE = Property.create("cryptogrammPaymentToType", DbPaymentType.class);

    public void setAmount(int amount) {
        writeProperty("amount", amount);
    }
    public int getAmount() {
        Object value = readProperty("amount");
        return (value != null) ? (Integer) value : 0;
    }

    public void setCardCryptogram(String cardCryptogram) {
        writeProperty("cardCryptogram", cardCryptogram);
    }
    public String getCardCryptogram() {
        return (String)readProperty("cardCryptogram");
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

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setStatus(boolean status) {
        writeProperty("status", status);
    }
	public boolean isStatus() {
        Boolean value = (Boolean)readProperty("status");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setCryptogrammPaymentToType(DbPaymentType cryptogrammPaymentToType) {
        setToOneTarget("cryptogrammPaymentToType", cryptogrammPaymentToType, true);
    }

    public DbPaymentType getCryptogrammPaymentToType() {
        return (DbPaymentType)readProperty("cryptogrammPaymentToType");
    }


}