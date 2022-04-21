package com.finalproject.model.entity;


/**
 * Statuses for leave
 *
 * @see Leave
 */
public enum LeaveStatus {

    INACTIVE("Pending"),
    ACTIVE("Approved"),
    REJECTED("Rejected");



    private final String simpleName;

    LeaveStatus(String simpleName) {
        this.simpleName = simpleName;
    }

    @Override
    public String toString() {
        return simpleName;
    }
}


