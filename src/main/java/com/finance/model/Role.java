package com.finance.model;

/**
 * Enum representing user roles in the system.
 * Each role has different access levels.
 */
public enum Role {
    VIEWER,   // Can only view dashboard data
    ANALYST,  // Can view records and access insights
    ADMIN     // Full management access
}
