package com.smokpromotion.SmokProm.domain.entity;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

    public class DE_ApplicationFeature {
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");

        private int id;
        private String url;
        private String description;
        private int menuItemId;

        private boolean hasUserPermissions;

        private boolean deleted;

        private boolean customOnly;

        private boolean hideOnNoAccess;
        private boolean customerRelations;
        private boolean accessEnabled;
        private LocalDateTime created;
        private LocalDateTime updated;

        private transient boolean isInSubscriptionRoleGroup;


        public DE_ApplicationFeature(int id, String url, String description, boolean deleted, boolean customOnly, boolean customerRelations,
                                     boolean hasUserPermissions, boolean hideOnNoAccess) {
            this.id = id;
            this.url = url;
            this.description = description;

            this.deleted = deleted;
            this.customOnly = customOnly;
            this.customerRelations = customerRelations;
            this.hasUserPermissions = hasUserPermissions;
            this.hideOnNoAccess = hideOnNoAccess;
        }

        public boolean isAccessEnabled() {
            return accessEnabled;
        }

        public void setAccessEnabled(boolean accessEnabled) {
            this.accessEnabled = accessEnabled;
        }

        public DE_ApplicationFeature(int id) {
            this.id = id;
        }

        public DE_ApplicationFeature() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }



        public int getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(int menuItemId) {
            this.menuItemId = menuItemId;
        }


        public static DateTimeFormatter getDateTimeFormatter() {
            return DATE_TIME_FORMATTER;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public LocalDateTime getCreated() {
            return created;
        }

        public void setCreated(LocalDateTime created) {
            this.created = created;
        }

        public String getCreatedFormatted()
        {
            if (created==null){ return ""; }
            return created.format(DATE_TIME_FORMATTER);
        }

        public LocalDateTime getUpdated() {
            return updated;
        }

        public String getUpdatedFormatted() {
            if (updated==null){ return ""; }
            return updated.format(DATE_TIME_FORMATTER);
        }

        public void setUpdated(LocalDateTime updated) {
            this.updated = updated;
        }

 //       public AppFeatureType getFeatureType() {
 //           return featureType;
 //       }

 //       public void setFeatureType(AppFeatureType featureType) {
 //           this.featureType = featureType;
 //       }

        public boolean isCustomOnly() {
            return customOnly;
        }

        public void setCustomOnly(boolean customOnly) {
            this.customOnly = customOnly;
        }

        public boolean isCustomerRelations() {
            return customerRelations;
        }

        public void setCustomerRelations(boolean customerRelations) {
            this.customerRelations = customerRelations;
        }


        public boolean isHasUserPermissions() {
            return hasUserPermissions;
        }

        public void setHasUserPermissions(boolean hasUserPermissions) {
            this.hasUserPermissions = hasUserPermissions;
        }
        public boolean isInSubscriptionRoleGroup() {
            return isInSubscriptionRoleGroup;
        }
        public void setInSubscriptionRoleGroup(boolean inSubscriptionRoleGroup) {
            isInSubscriptionRoleGroup = inSubscriptionRoleGroup;
        }
        public boolean isHideOnNoAccess() {
            return hideOnNoAccess;
        }

        public void setHideOnNoAccess(boolean hideOnNoAccess) {
            this.hideOnNoAccess = hideOnNoAccess;
        }

        @Override
        public String toString() {
            return "DE_ApplicationFeature{" +
                    "id=" + id +
                    ", url='" + url + '\'' +
                    ", description='" + description + '\'' +
                    ", menuItemId=" + menuItemId +
                    ", hasUserPermissions=" + hasUserPermissions +
                    ", deleted=" + deleted +
                    ", customOnly=" + customOnly +
                    ", hideOnNoAccess=" + hideOnNoAccess +
                    ", customerRelations=" + customerRelations +
                    ", accessEnabled=" + accessEnabled +
                    ", created=" + created +
                    ", updated=" + updated +
                    '}';
        }
    }
