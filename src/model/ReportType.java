package model;

/**
 * Represents the different types of reports that can be generated.
 * 
 * Each type defines how financial data is grouped and displayed.
 */
public enum ReportType {

    /** Summary of income vs expenses for a single month */
    MONTHLY_SUMMARY,

    /** Summary of income vs expenses for a full year */
    YEARLY_SUMMARY,

    /** Breakdown of spending by category */
    CATEGORY_BREAKDOWN,

    /** Report generated for a custom date range */
    CUSTOM_RANGE
}
