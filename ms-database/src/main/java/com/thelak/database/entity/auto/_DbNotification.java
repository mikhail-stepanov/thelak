package com.thelak.database.entity.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.thelak.database.entity.DbUser;

/**
 * Class _DbNotification was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DbNotification extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Boolean> CONTENT = Property.create("content", Boolean.class);
    public static final Property<Boolean> NEWS = Property.create("news", Boolean.class);
    public static final Property<Boolean> RECOMENDATION = Property.create("recomendation", Boolean.class);
    public static final Property<Boolean> SALES = Property.create("sales", Boolean.class);
    public static final Property<DbUser> NOTIFICATION_TO_USER = Property.create("notificationToUser", DbUser.class);

    public void setContent(boolean content) {
        writeProperty("content", content);
    }
	public boolean isContent() {
        Boolean value = (Boolean)readProperty("content");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setNews(boolean news) {
        writeProperty("news", news);
    }
	public boolean isNews() {
        Boolean value = (Boolean)readProperty("news");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setRecomendation(boolean recomendation) {
        writeProperty("recomendation", recomendation);
    }
	public boolean isRecomendation() {
        Boolean value = (Boolean)readProperty("recomendation");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setSales(boolean sales) {
        writeProperty("sales", sales);
    }
	public boolean isSales() {
        Boolean value = (Boolean)readProperty("sales");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setNotificationToUser(DbUser notificationToUser) {
        setToOneTarget("notificationToUser", notificationToUser, true);
    }

    public DbUser getNotificationToUser() {
        return (DbUser)readProperty("notificationToUser");
    }


}
