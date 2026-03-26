package com.project.traffic_enforcement.models.enums;

/**
 * Represents the lifecycle status of a traffic violation.
 * 
 * Status Flow:
 * - UNRESOLVED: Violation issued by officer, awaiting payment or action
 * - APPEAL_PENDING: Appeal submitted by violator, under review
 * - APPEAL_APPROVED: Appeal accepted, violation dismissed or modified
 * - APPEAL_REJECTED: Appeal denied, violation stands
 * - PAID: Fine/penalty successfully paid
 * - DISMISSED: Violation cancelled or invalidated (officer error, etc.)
 * - RESOLVED: Violation finalized and closed
 */
public enum ViolationStatus {
    /**
     * Violation issued by officer; awaiting payment, appeal, or other action.
     */
    UNRESOLVED,
    
    /**
     * Appeal has been submitted and is under administrative review.
     */
    APPEAL_PENDING,
    
    /**
     * Appeal was approved; violation dismissed or modified.
     */
    APPEAL_APPROVED,
    
    /**
     * Appeal was rejected; original violation stands.
     */
    APPEAL_REJECTED,
    
    /**
     * Fine/penalty has been paid in full.
     */
    PAID,
    
    /**
     * Violation cancelled due to officer error, procedural issue, or other reason.
     */
    DISMISSED,
    
    /**
     * Violation finalized and closed (terminal state).
     */
    RESOLVED;
}
