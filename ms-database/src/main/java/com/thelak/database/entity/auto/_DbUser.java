package com.thelak.database.entity.auto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.thelak.database.entity.DbNotification;
import com.thelak.database.entity.DbUserSession;

/**
 * Class _DbUser was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DbUser extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<LocalDate> BIRTHDAY = Property.create("birthday", LocalDate.class);
    public static final Property<String> CITY = Property.create("city", String.class);
    public static final Property<String> COUNTRY = Property.create("country", String.class);
    public static final Property<LocalDateTime> CREATED_DATE = Property.create("createdDate", LocalDateTime.class);
    public static final Property<LocalDateTime> DELETED_DATE = Property.create("deletedDate", LocalDateTime.class);
    public static final Property<String> EMAIL = Property.create("email", String.class);
    public static final Property<Boolean> IS_ADMIN = Property.create("isAdmin", Boolean.class);
    public static final Property<Boolean> IS_SUBSCRIBE = Property.create("isSubscribe", Boolean.class);
    public static final Property<LocalDateTime> MODIFIED_DATE = Property.create("modifiedDate", LocalDateTime.class);
    public static final Property<String> NAME = Property.create("name", String.class);
    public static final Property<String> PASSWORD = Property.create("password", String.class);
    public static final Property<String> PHONE = Property.create("phone", String.class);
    public static final Property<Boolean> RENEW = Property.create("renew", Boolean.class);
    public static final Property<String> SALT = Property.create("salt", String.class);
    public static final Property<String> SUB_TYPE = Property.create("subType", String.class);
    public static final Property<LocalDateTime> SUBSCRIPTION_DATE = Property.create("subscriptionDate", LocalDateTime.class);
    public static final Property<List<DbNotification>> USER_TO_NOTIFICATION = Property.create("userToNotification", List.class);
    public static final Property<List<DbUserSession>> USER_TO_SESSION = Property.create("userToSession", List.class);

    public void setBirthday(LocalDate birthday) {
        writeProperty("birthday", birthday);
    }
    public LocalDate getBirthday() {
        return (LocalDate)readProperty("birthday");
    }

    public void setCity(String city) {
        writeProperty("city", city);
    }
    public String getCity() {
        return (String)readProperty("city");
    }

    public void setCountry(String country) {
        writeProperty("country", country);
    }
    public String getCountry() {
        return (String)readProperty("country");
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        writeProperty("createdDate", createdDate);
    }
    public LocalDateTime getCreatedDate() {
        return (LocalDateTime)readProperty("createdDate");
    }

    public void setDeletedDate(LocalDateTime deletedDate) {
        writeProperty("deletedDate", deletedDate);
    }
    public LocalDateTime getDeletedDate() {
        return (LocalDateTime)readProperty("deletedDate");
    }

    public void setEmail(String email) {
        writeProperty("email", email);
    }
    public String getEmail() {
        return (String)readProperty("email");
    }

    public void setIsAdmin(boolean isAdmin) {
        writeProperty("isAdmin", isAdmin);
    }
	public boolean isIsAdmin() {
        Boolean value = (Boolean)readProperty("isAdmin");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setIsSubscribe(boolean isSubscribe) {
        writeProperty("isSubscribe", isSubscribe);
    }
	public boolean isIsSubscribe() {
        Boolean value = (Boolean)readProperty("isSubscribe");
        return (value != null) ? value.booleanValue() : false;
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

    public void setPassword(String password) {
        writeProperty("password", password);
    }
    public String getPassword() {
        return (String)readProperty("password");
    }

    public void setPhone(String phone) {
        writeProperty("phone", phone);
    }
    public String getPhone() {
        return (String)readProperty("phone");
    }

    public void setRenew(boolean renew) {
        writeProperty("renew", renew);
    }
	public boolean isRenew() {
        Boolean value = (Boolean)readProperty("renew");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setSalt(String salt) {
        writeProperty("salt", salt);
    }
    public String getSalt() {
        return (String)readProperty("salt");
    }

    public void setSubType(String subType) {
        writeProperty("subType", subType);
    }
    public String getSubType() {
        return (String)readProperty("subType");
    }

    public void setSubscriptionDate(LocalDateTime subscriptionDate) {
        writeProperty("subscriptionDate", subscriptionDate);
    }
    public LocalDateTime getSubscriptionDate() {
        return (LocalDateTime)readProperty("subscriptionDate");
    }

    public void addToUserToNotification(DbNotification obj) {
        addToManyTarget("userToNotification", obj, true);
    }
    public void removeFromUserToNotification(DbNotification obj) {
        removeToManyTarget("userToNotification", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbNotification> getUserToNotification() {
        return (List<DbNotification>)readProperty("userToNotification");
    }


    public void addToUserToSession(DbUserSession obj) {
        addToManyTarget("userToSession", obj, true);
    }
    public void removeFromUserToSession(DbUserSession obj) {
        removeToManyTarget("userToSession", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbUserSession> getUserToSession() {
        return (List<DbUserSession>)readProperty("userToSession");
    }


}
