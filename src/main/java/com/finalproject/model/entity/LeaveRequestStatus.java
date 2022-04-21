package com.finalproject.model.entity;

/**
 * Statuses for activity request
 *
 * @see LeaveRequest
 */
public enum LeaveRequestStatus {

    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String simpleName;

    LeaveRequestStatus(String simpleName) {
        this.simpleName = simpleName;
    }

    @Override
    public String toString() {
        return simpleName;
    }
}
