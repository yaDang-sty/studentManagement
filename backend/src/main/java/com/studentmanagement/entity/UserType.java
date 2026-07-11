package com.studentmanagement.entity;

public enum UserType {
    TECH_ADMIN("\u6280\u672f\u7ba1\u7406"),
    BUSINESS_ADMIN("\u4e1a\u52a1\u7ba1\u7406"),
    VISITOR("\u6e38\u5ba2");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
