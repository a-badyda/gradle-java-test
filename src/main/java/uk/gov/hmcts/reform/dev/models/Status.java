package uk.gov.hmcts.reform.dev.models;

/**
 * These states define what actions can be taken by solicitors and caseworkers, such as submitting documents,
 * assigning tasks, or scheduling hearings.
 * Key states of proceedings include:
 * <ul>
 *  <li>Draft/Unsubmitted: The case has been created in MyHMCTS but not yet formally filed with the court.</li>
 *  <li>Submitted/Issued: The application has been submitted to HMCTS and officially issued.</li>
 *  <li>Gatekeeping: The case is referred to a judge for initial directions.</li>
 *  <li>Awaiting Respondent Response: The claim has been served and the court is awaiting a response </li>
 *  <li>Case Management/Directions: The court is managing the case and allocating the case to a track.</li>
 *  <li>Stayed/Paused: The proceedings are on hold to allow for mediation or to wait for a specific event.</li>
 *  <li>Listed for Hearing/Trial: A  (e.g., Case Management Hearing, Final Hearing).</li>
 *  <li>Ready for Listing: All directions are complied with, and the case is waiting for a trial date.</li>
 *  <li>Final Order/Consent Order: The case has been resolved, often by a consent order filed by parties.</li>
 *  <li>Closed/Disposed: Final results have been entered, and the case is complete.</li>
 * </ul>
 * Specific Contexts:
 * <ul>
 *  <li>Financial Remedy: Cases can be in a 'contested' state, requiring uploading of evidence or a consent order.</li>
 *  <li>Family Public Law: Includes steps like 'Awaiting SWET' (Social Work Evidence Template) or 'Gatekeeping'.</li>
 *  <li>Criminal: Uses states like 'Preparation for Effective Trial' (PET) and 'Better Case Management' (BCM)</li>
 * </ul>
 */

//todo: likely some states are missing as im just using publicly available data
public enum Status {
    DRAFT, // case has been created but not yet formally filed
    SUBMITTED, // application has been submitted to HMCTS and officially issued.
    GATEKEEPING, //referred to a judge for initial directions
    AWAITING_RESPONDENT_RESPONSE, //court is awaiting a response (e.g., acknowledgement or defense)
    CASE_MANAGEMENT, //court is managing the case,
    PAUSED, //proceedings are on hold
    LISTED_FOR_HEARING, //date has been set for a hearing
    READY_FOR_LISTING, //case is waiting for a trial date
    FINAL_ORDER, //case has been resolved
    CLOSED, //closed or disposed - final results have been entered

    //specific cases
    AWAITING_SWET, // SWET = (Social Work Evidence Template)
    CONTESTED,//requiring uploading of evidence or a consent order
    PET, //'Preparation for Effective Trial' (PET)
    BCM, // 'Better Case Management' (BCM)
    //system
    DELETED //in case we want to 'delete' records from user view as filter but without actually getting rid of them

    //todo ideally, this should contain the allowed paths a status can traverse. eg - some statuses are terminal
}

