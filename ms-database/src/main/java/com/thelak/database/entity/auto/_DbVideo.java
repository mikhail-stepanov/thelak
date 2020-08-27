package com.thelak.database.entity.auto;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.thelak.database.entity.DbVideoFavorites;
import com.thelak.database.entity.DbVideoHistory;
import com.thelak.database.entity.DbVideoRating;
import com.thelak.database.entity.DbVideoTimecode;

/**
 * Class _DbVideo was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DbVideo extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> CATEGORY = Property.create("category", String.class);
    public static final Property<String> CONTENT_URL1080 = Property.create("contentUrl1080", String.class);
    public static final Property<String> CONTENT_URL360 = Property.create("contentUrl360", String.class);
    public static final Property<String> CONTENT_URL480 = Property.create("contentUrl480", String.class);
    public static final Property<String> CONTENT_URL720 = Property.create("contentUrl720", String.class);
    public static final Property<String> COUNTRY = Property.create("country", String.class);
    public static final Property<String> COVER_URL = Property.create("coverUrl", String.class);
    public static final Property<LocalDateTime> CREATED_DATE = Property.create("createdDate", LocalDateTime.class);
    public static final Property<LocalDateTime> DELETED_DATE = Property.create("deletedDate", LocalDateTime.class);
    public static final Property<String> DESCRIPTION = Property.create("description", String.class);
    public static final Property<String> DURATION = Property.create("duration", String.class);
    public static final Property<String> LANGUAGE = Property.create("language", String.class);
    public static final Property<LocalDateTime> MODIFIED_DATE = Property.create("modifiedDate", LocalDateTime.class);
    public static final Property<String> PARTNER_LOGO_URL = Property.create("partnerLogoUrl", String.class);
    public static final Property<String> PLAYGROUND = Property.create("playground", String.class);
    public static final Property<String> SPEAKER = Property.create("speaker", String.class);
    public static final Property<String> SPEAKER_INFORMATION = Property.create("speakerInformation", String.class);
    public static final Property<String> TITLE = Property.create("title", String.class);
    public static final Property<Integer> YEAR = Property.create("year", Integer.class);
    public static final Property<List<DbVideoFavorites>> VIDEO_TO_FAVORITE = Property.create("videoToFavorite", List.class);
    public static final Property<List<DbVideoHistory>> VIDEO_TO_HISTORY = Property.create("videoToHistory", List.class);
    public static final Property<List<DbVideoRating>> VIDEO_TO_RATING = Property.create("videoToRating", List.class);
    public static final Property<List<DbVideoTimecode>> VIDEO_TO_TIMECODE = Property.create("videoToTimecode", List.class);

    public void setCategory(String category) {
        writeProperty("category", category);
    }
    public String getCategory() {
        return (String)readProperty("category");
    }

    public void setContentUrl1080(String contentUrl1080) {
        writeProperty("contentUrl1080", contentUrl1080);
    }
    public String getContentUrl1080() {
        return (String)readProperty("contentUrl1080");
    }

    public void setContentUrl360(String contentUrl360) {
        writeProperty("contentUrl360", contentUrl360);
    }
    public String getContentUrl360() {
        return (String)readProperty("contentUrl360");
    }

    public void setContentUrl480(String contentUrl480) {
        writeProperty("contentUrl480", contentUrl480);
    }
    public String getContentUrl480() {
        return (String)readProperty("contentUrl480");
    }

    public void setContentUrl720(String contentUrl720) {
        writeProperty("contentUrl720", contentUrl720);
    }
    public String getContentUrl720() {
        return (String)readProperty("contentUrl720");
    }

    public void setCountry(String country) {
        writeProperty("country", country);
    }
    public String getCountry() {
        return (String)readProperty("country");
    }

    public void setCoverUrl(String coverUrl) {
        writeProperty("coverUrl", coverUrl);
    }
    public String getCoverUrl() {
        return (String)readProperty("coverUrl");
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

    public void setDescription(String description) {
        writeProperty("description", description);
    }
    public String getDescription() {
        return (String)readProperty("description");
    }

    public void setDuration(String duration) {
        writeProperty("duration", duration);
    }
    public String getDuration() {
        return (String)readProperty("duration");
    }

    public void setLanguage(String language) {
        writeProperty("language", language);
    }
    public String getLanguage() {
        return (String)readProperty("language");
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        writeProperty("modifiedDate", modifiedDate);
    }
    public LocalDateTime getModifiedDate() {
        return (LocalDateTime)readProperty("modifiedDate");
    }

    public void setPartnerLogoUrl(String partnerLogoUrl) {
        writeProperty("partnerLogoUrl", partnerLogoUrl);
    }
    public String getPartnerLogoUrl() {
        return (String)readProperty("partnerLogoUrl");
    }

    public void setPlayground(String playground) {
        writeProperty("playground", playground);
    }
    public String getPlayground() {
        return (String)readProperty("playground");
    }

    public void setSpeaker(String speaker) {
        writeProperty("speaker", speaker);
    }
    public String getSpeaker() {
        return (String)readProperty("speaker");
    }

    public void setSpeakerInformation(String speakerInformation) {
        writeProperty("speakerInformation", speakerInformation);
    }
    public String getSpeakerInformation() {
        return (String)readProperty("speakerInformation");
    }

    public void setTitle(String title) {
        writeProperty("title", title);
    }
    public String getTitle() {
        return (String)readProperty("title");
    }

    public void setYear(int year) {
        writeProperty("year", year);
    }
    public int getYear() {
        Object value = readProperty("year");
        return (value != null) ? (Integer) value : 0;
    }

    public void addToVideoToFavorite(DbVideoFavorites obj) {
        addToManyTarget("videoToFavorite", obj, true);
    }
    public void removeFromVideoToFavorite(DbVideoFavorites obj) {
        removeToManyTarget("videoToFavorite", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbVideoFavorites> getVideoToFavorite() {
        return (List<DbVideoFavorites>)readProperty("videoToFavorite");
    }


    public void addToVideoToHistory(DbVideoHistory obj) {
        addToManyTarget("videoToHistory", obj, true);
    }
    public void removeFromVideoToHistory(DbVideoHistory obj) {
        removeToManyTarget("videoToHistory", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbVideoHistory> getVideoToHistory() {
        return (List<DbVideoHistory>)readProperty("videoToHistory");
    }


    public void addToVideoToRating(DbVideoRating obj) {
        addToManyTarget("videoToRating", obj, true);
    }
    public void removeFromVideoToRating(DbVideoRating obj) {
        removeToManyTarget("videoToRating", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbVideoRating> getVideoToRating() {
        return (List<DbVideoRating>)readProperty("videoToRating");
    }


    public void addToVideoToTimecode(DbVideoTimecode obj) {
        addToManyTarget("videoToTimecode", obj, true);
    }
    public void removeFromVideoToTimecode(DbVideoTimecode obj) {
        removeToManyTarget("videoToTimecode", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbVideoTimecode> getVideoToTimecode() {
        return (List<DbVideoTimecode>)readProperty("videoToTimecode");
    }


}
