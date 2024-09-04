package com.smokpromotion.SmokProm.scheduler.dao;

public enum ScheduleEnum {
    DAILY(0,"Daily", "Previous Day"),
    WEEKLY(1,"Weekly", "Previous 7 Days"),
    MONTHLY(2, "Monthly", "Previous Calendar Month"),
    YEARLY(3,"Yearly","Previous 365 Days"),
    NOW(4,"Now", "Selected Period");

    private int id;
    private String displayText;
    private String showingData;

    private ScheduleEnum(int id, String displayText, String showingData) {
        this.id = id;
        this.displayText = displayText;
        this.showingData = showingData;
    }

    public static ScheduleEnum fromId(int id) {
        for (ScheduleEnum type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public static String displayText(int id){
        ScheduleEnum scheduleEnum = fromId(id);
        return scheduleEnum == null ? "Unknown" : scheduleEnum.displayText;
    }

    public static String showingData(int id){
        ScheduleEnum scheduleEnum = fromId(id);
        return scheduleEnum == null ? "Unknown" : scheduleEnum.showingData;
    }

    public int getId() {
        return id;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getShowingData() {
        return showingData;
    }
}