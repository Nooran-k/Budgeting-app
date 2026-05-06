package model;

/**
 * Enum defining the types of reports the system can generate.
 * Referenced in ReportController and the sequence diagram for US#7.
 * Matches the class diagram in the SDS.
 */
public enum ReportType {
    MONTHLY_SUMMARY,     // income vs expenses for one month
    YEARLY_SUMMARY,      // income vs expenses for a full year
    CATEGORY_BREAKDOWN,  // how much spent per category (pie chart data)
    CUSTOM_RANGE         // user-specified start and end date
}