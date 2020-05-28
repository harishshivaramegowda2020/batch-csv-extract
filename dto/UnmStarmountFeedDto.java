package com.custom.java.preconfig.policy.group.services.starmount.dto;

import java.util.List;

public class UnmStarmountFeedDto {
    private String policyNumber;
    private String underwritingCd;
    private String coverageTier;
    private String disabledDependentInd;
    private String orthoVisionRiderEffectiveDate;
    private String orthoWaitingPeriodDate;
    private String orthoTerminationDate;
    private Boolean orthoPlanCode;
    private String planLevelCode;
    private String bpPlanCode;
    private String participantType;
    private String interestedPartyNumber;
    private String billingAccountNumber;
    private String billingPaidDate;
    private String bpWaitingPeriodInd;
    private String lapseStartDateOne;
    private String lapseStartDateTwo;
    private String lapseStartDateThree;
    private String lapseStopDateOne;
    private String lapseStopDateTwo;
    private String lapseStopDateThree;
    private String lapseDurationOne;
    private String lapseDurationTwo;
    private String lapseDurationThree;
    private List<UnmStarmountFeedInsuredDto> insuredDtoList;

    public List<UnmStarmountFeedInsuredDto> getInsuredDtoList() {
        return insuredDtoList;
    }

    public void setInsuredDtoList(List<UnmStarmountFeedInsuredDto> insuredDtoList) {
        this.insuredDtoList = insuredDtoList;
    }

    public String getInsuredDomesticAbuseInd() {
        return insuredDomesticAbuseInd;
    }

    public void setInsuredDomesticAbuseInd(String insuredDomesticAbuseInd) {
        this.insuredDomesticAbuseInd = insuredDomesticAbuseInd;
    }

    private String insuredDomesticAbuseInd;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getRiskStateCode() {
        return riskStateCode;
    }

    public void setRiskStateCode(String riskStateCode) {
        this.riskStateCode = riskStateCode;
    }

    private String riskStateCode;

    public String getPolicyStatusCode() {
        return policyStatusCode;
    }

    public void setPolicyStatusCode(String policyStatusCode) {
        this.policyStatusCode = policyStatusCode;
    }

    private String policyStatusCode;

    public String getMainInsuredIPN() {
        return mainInsuredIPN;
    }

    public void setMainInsuredIPN(String mainInsuredIPN) {
        this.mainInsuredIPN = mainInsuredIPN;
    }

    private String mainInsuredIPN;

    public String getUnderwritingCd() {
        return underwritingCd;
    }

    public void setUnderwritingCd(String underwritingCd) {
        this.underwritingCd = underwritingCd;
    }

    public String getInsuredTerminationdate() {
        return insuredTerminationdate;
    }

    public void setInsuredTerminationdate(String insuredTerminationdate) {
        this.insuredTerminationdate = insuredTerminationdate;
    }

    private String insuredTerminationdate;

    public String getInsuredCoverageEffectiveDate() {
        return insuredCoverageEffectiveDate;
    }

    public void setInsuredCoverageEffectiveDate(String insuredCoverageEffectiveDate) {
        this.insuredCoverageEffectiveDate = insuredCoverageEffectiveDate;
    }

    private String insuredCoverageEffectiveDate;


    public String getInsuredIPN() {
        return insuredIPN;
    }

    public void setInsuredIPN(String insuredIPN) {
        this.insuredIPN = insuredIPN;
    }

    private String insuredIPN;

    public String getLapseStartDateOne() {
        return lapseStartDateOne;
    }

    public void setLapseStartDateOne(String lapseStartDateOne) {
        this.lapseStartDateOne = lapseStartDateOne;
    }

    public String getLapseStartDateTwo() {
        return lapseStartDateTwo;
    }

    public void setLapseStartDateTwo(String lapseStartDateTwo) {
        this.lapseStartDateTwo = lapseStartDateTwo;
    }

    public String getLapseStartDateThree() {
        return lapseStartDateThree;
    }

    public void setLapseStartDateThree(String lapseStartDateThree) {
        this.lapseStartDateThree = lapseStartDateThree;
    }

    public String getLapseStopDateOne() {
        return lapseStopDateOne;
    }

    public void setLapseStopDateOne(String lapseStopDateOne) {
        this.lapseStopDateOne = lapseStopDateOne;
    }

    public String getLapseStopDateTwo() {
        return lapseStopDateTwo;
    }

    public void setLapseStopDateTwo(String lapseStopDateTwo) {
        this.lapseStopDateTwo = lapseStopDateTwo;
    }

    public String getLapseStopDateThree() {
        return lapseStopDateThree;
    }

    public void setLapseStopDateThree(String lapseStopDateThree) {
        this.lapseStopDateThree = lapseStopDateThree;
    }

    public String getLapseDurationOne() {
        return lapseDurationOne;
    }

    public void setLapseDurationOne(String lapseDurationOne) {
        this.lapseDurationOne = lapseDurationOne;
    }

    public String getLapseDurationTwo() {
        return lapseDurationTwo;
    }

    public void setLapseDurationTwo(String lapseDurationTwo) {
        this.lapseDurationTwo = lapseDurationTwo;
    }

    public String getLapseDurationThree() {
        return lapseDurationThree;
    }

    public void setLapseDurationThree(String lapseDurationThree) {
        this.lapseDurationThree = lapseDurationThree;
    }



    public String getBpWaitingPeriodInd() {
        return bpWaitingPeriodInd;
    }

    public void setBpWaitingPeriodInd(String bpWaitingPeriodInd) {
        this.bpWaitingPeriodInd = bpWaitingPeriodInd;
    }

    public String getBpWaitingPeriodStartDate() {
        return bpWaitingPeriodStartDate;
    }

    public void setBpWaitingPeriodStartDate(String bpWaitingPeriodStartDate) {
        this.bpWaitingPeriodStartDate = bpWaitingPeriodStartDate;
    }

    private String bpWaitingPeriodStartDate;

    public String getBpCoverageEffectiveDate() {
        return bpCoverageEffectiveDate;
    }

    public void setBpCoverageEffectiveDate(String bpCoverageEffectiveDate) {
        this.bpCoverageEffectiveDate = bpCoverageEffectiveDate;
    }

    public String getBpTerminationDate() {
        return bpTerminationDate;
    }

    public void setBpTerminationDate(String bpTerminationDate) {
        this.bpTerminationDate = bpTerminationDate;
    }

    private String bpCoverageEffectiveDate;
    private String bpTerminationDate;

    public String getSourcePolicyIndicator() {
        return sourcePolicyIndicator;
    }

    public void setSourcePolicyIndicator(String sourcePolicyIndicator) {
        this.sourcePolicyIndicator = sourcePolicyIndicator;
    }

    private String sourcePolicyIndicator;

    public String getDisasterInd() {
        return disasterInd;
    }

    public void setDisasterInd(String disasterInd) {
        this.disasterInd = disasterInd;
    }

    private String disasterInd;

    public String getBillingPaidDate() {
        return billingPaidDate;
    }

    public void setBillingPaidDate(String billingPaidDate) {
        this.billingPaidDate = billingPaidDate;
    }

    public String getInterestedPartyNumber() {
        return interestedPartyNumber;
    }

    public void setInterestedPartyNumber(String interestedPartyNumber) {
        this.interestedPartyNumber = interestedPartyNumber;
    }

    public String getBillingAccountNumber() {
        return billingAccountNumber;
    }

    public void setBillingAccountNumber(String billingAccountNumber) {
        this.billingAccountNumber = billingAccountNumber;
    }

    public String getParticipantType() {
        return participantType;
    }

    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    public String getPlanLevelCode() {
        return planLevelCode;
    }

    public void setPlanLevelCode(String planLevelCode) {
        this.planLevelCode = planLevelCode;
    }

    public String getBpPlanCode() {
        return bpPlanCode;
    }

    public void setBpPlanCode(String bpPlanCode) {
        this.bpPlanCode = bpPlanCode;
    }

    public String getOrthoVisionRiderEffectiveDate() {
        return orthoVisionRiderEffectiveDate;
    }

    public void setOrthoVisionRiderEffectiveDate(String orthoVisionRiderEffectiveDate) {
        this.orthoVisionRiderEffectiveDate = orthoVisionRiderEffectiveDate;
    }

    public String getOrthoTerminationDate() {
        return orthoTerminationDate;
    }

    public void setOrthoTerminationDate(String orthoTerminationDate) {
        this.orthoTerminationDate = orthoTerminationDate;
    }

    public String getOrthoWaitingPeriodDate() {
        return orthoWaitingPeriodDate;
    }

    public void setOrthoWaitingPeriodDate(String orthoWaitingPeriodDate) {
        this.orthoWaitingPeriodDate = orthoWaitingPeriodDate;
    }

    public Boolean getOrthoPlanCode() {
        return orthoPlanCode;
    }

    public void setOrthoPlanCode(Boolean orthoPlanCode) {
        this.orthoPlanCode = orthoPlanCode;
    }

    public String getDisabledDependentInd() {
        return disabledDependentInd;
    }

    public void setDisabledDependentInd(String disabledDependentInd) {
        this.disabledDependentInd = disabledDependentInd;
    }

    public String getCoverageTier() {
        return coverageTier;
    }

    public void setCoverageTier(String coverageTier) {
        this.coverageTier = coverageTier;
    }

}
