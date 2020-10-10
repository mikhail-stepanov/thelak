package com.thelak.database.entity.auto;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.thelak.database.entity.DbOptionSubscription;
import com.thelak.database.entity.DbPaymentsCryptogrammSubscription;

/**
 * Class _DbSubscription was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DbSubscription extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Integer> COVER = Property.create("cover", Integer.class);
    public static final Property<LocalDateTime> CREATED_DATE = Property.create("createdDate", LocalDateTime.class);
    public static final Property<Integer> DAYS = Property.create("days", Integer.class);
    public static final Property<LocalDateTime> DELETED_DATE = Property.create("deletedDate", LocalDateTime.class);
    public static final Property<LocalDateTime> MODIFIED_DATE = Property.create("modifiedDate", LocalDateTime.class);
    public static final Property<Integer> MONTHS = Property.create("months", Integer.class);
    public static final Property<String> NEXT = Property.create("next", String.class);
    public static final Property<String> PRE = Property.create("pre", String.class);
    public static final Property<Integer> PRICE = Property.create("price", Integer.class);
    public static final Property<String> TYPE = Property.create("type", String.class);
    public static final Property<List<DbPaymentsCryptogrammSubscription>> DB_PAYMENTS_CRYPTOGRAMM_SUBSCRIPTIONS = Property.create("dbPaymentsCryptogrammSubscriptions", List.class);
    public static final Property<List<DbOptionSubscription>> SUBSCRIPTION_TO_OPSUB = Property.create("subscriptionToOpsub", List.class);

    public void setCover(int cover) {
        writeProperty("cover", cover);
    }
    public int getCover() {
        Object value = readProperty("cover");
        return (value != null) ? (Integer) value : 0;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        writeProperty("createdDate", createdDate);
    }
    public LocalDateTime getCreatedDate() {
        return (LocalDateTime)readProperty("createdDate");
    }

    public void setDays(int days) {
        writeProperty("days", days);
    }
    public int getDays() {
        Object value = readProperty("days");
        return (value != null) ? (Integer) value : 0;
    }

    public void setDeletedDate(LocalDateTime deletedDate) {
        writeProperty("deletedDate", deletedDate);
    }
    public LocalDateTime getDeletedDate() {
        return (LocalDateTime)readProperty("deletedDate");
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        writeProperty("modifiedDate", modifiedDate);
    }
    public LocalDateTime getModifiedDate() {
        return (LocalDateTime)readProperty("modifiedDate");
    }

    public void setMonths(int months) {
        writeProperty("months", months);
    }
    public int getMonths() {
        Object value = readProperty("months");
        return (value != null) ? (Integer) value : 0;
    }

    public void setNext(String next) {
        writeProperty("next", next);
    }
    public String getNext() {
        return (String)readProperty("next");
    }

    public void setPre(String pre) {
        writeProperty("pre", pre);
    }
    public String getPre() {
        return (String)readProperty("pre");
    }

    public void setPrice(int price) {
        writeProperty("price", price);
    }
    public int getPrice() {
        Object value = readProperty("price");
        return (value != null) ? (Integer) value : 0;
    }

    public void setType(String type) {
        writeProperty("type", type);
    }
    public String getType() {
        return (String)readProperty("type");
    }

    public void addToDbPaymentsCryptogrammSubscriptions(DbPaymentsCryptogrammSubscription obj) {
        addToManyTarget("dbPaymentsCryptogrammSubscriptions", obj, true);
    }
    public void removeFromDbPaymentsCryptogrammSubscriptions(DbPaymentsCryptogrammSubscription obj) {
        removeToManyTarget("dbPaymentsCryptogrammSubscriptions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbPaymentsCryptogrammSubscription> getDbPaymentsCryptogrammSubscriptions() {
        return (List<DbPaymentsCryptogrammSubscription>)readProperty("dbPaymentsCryptogrammSubscriptions");
    }


    public void addToSubscriptionToOpsub(DbOptionSubscription obj) {
        addToManyTarget("subscriptionToOpsub", obj, true);
    }
    public void removeFromSubscriptionToOpsub(DbOptionSubscription obj) {
        removeToManyTarget("subscriptionToOpsub", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbOptionSubscription> getSubscriptionToOpsub() {
        return (List<DbOptionSubscription>)readProperty("subscriptionToOpsub");
    }


}
