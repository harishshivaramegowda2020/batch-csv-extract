package com.custom.java.preconfig.policy.group.services.starmount.job;

import com.custom.java.preconfig.policy.group.domain.*;
import com.custom.java.preconfig.policy.group.services.starmount.csvFramework.UnmDateUtils;
import com.custom.java.preconfig.policy.group.services.starmount.dto.UnmStarmountFeedDto;
import com.custom.java.preconfig.policy.group.services.starmount.dto.UnmStarmountFeedInsuredDto;
import com.custom.java.preconfig.policy.group.services.starmount.services.StarmountPolicyDataExtract;
import com.custom.java.preconfig.policy.group.services.starmount.services.UnumStarmountPolicyService;
import com.custom.ipb.base.annotations.Job;
import com.custom.ipb.base.datatypes.DateUtils;
import com.custom.ipb.crm.domain.AddressEntity;
import com.custom.ipb.crm.domain.Customer;
import com.custom.ipb.policy.domain.Coverage;
import com.custom.ipb.policy.domain.GeneralPartyInfoEntity;
import com.custom.ipb.policy.domain.RiskItem;
import com.custom.ipb.policy.group.domain.*;
import com.custom.scheduler.JobResults;
import com.custom.scheduler.SchedulerJobException;
import com.custom.scheduler.UnableToInterruptJobException;
import com.custom.scheduler.audit.Auditor;
import com.custom.scheduler.job.BaseAuditingTaskGenerationJob;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author hgowda
 * Job to extract cp's based on transactiondate.
 */
@Job
public class UnumStarmountPolicyGenerationJob extends BaseAuditingTaskGenerationJob {

    private static final Logger LOG = LoggerFactory.getLogger(UnumStarmountPolicyGenerationJob.class);
    private boolean interrupted;
    protected static final String CLOSE_FILE_ERROR = "{0} - ERROR: not able to close file : {1}";
    private UnumStarmountPolicyService starmountPolicyService;
    private static final String UNDERWRITING_CD = "00";
    private static final String ONE = "1";
    private static final String PLAN_FOUR = "Plan 4";
    private static final String PLAN_FIVE = "Plan 5";
    private static final String PLAN_FOUR_VAL = "PL4 W/RO";
    private static final String PLAN_FIVE_VAL = "PL5 W/RO";
    private static final String LAPSE_DURATION = "000";
    private static final String INSURED_SELF = "SELF";
    private static final String INSURED_SPOUSE = "SPOUSE";
    private static final String ISSUED = "issued";
    private static final String CANCELLED = "cancelled";

    @Override
    protected JobResults doExecute(Map context, Auditor auditor) throws SchedulerJobException {
        StarmountPolicyDataExtract starmountDataFeedExtract = null;
        LOG.info("Starting StarmountPolicyDataGenerationJob");
        auditor.info("Starmount Data Generation Job processing - " + DateUtils.getCurrentTimestamp());
        setInterrupted(false);
        String message;


        try {
            UnmJobUtils.logInfoMessage(LOG, auditor, MessageFormat.format("JOB_STARTED", getJobName()));
            Date endDate = Date.from(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toInstant());
            Date startDate = org.apache.commons.lang3.time.DateUtils.addDays(endDate, -1);

            List<Long> certificateIds = starmountPolicyService.getCertificatePolicyIdByTransactionDate(startDate, endDate);
            CCGPolicyIssuedEventProcessor.java
            try {
                starmountDataFeedExtract = starmountPolicyService.createStarmountDataFeedExtract(certificateIds.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!certificateIds.isEmpty()) {
                for (Long certificateId : certificateIds) {
                    CertificatePolicy certificatePolicy = starmountPolicyService.getCertificatePolicyByCertificateId(certificateId);
                    if (null != certificatePolicy) {
                        UnmStarmountFeedDto starmountData = prepareStarmountData(certificatePolicy);
                        starmountDataFeedExtract.add(starmountData);
                    }
                    if (isInterrupted()) {
                        return JobResults.JOB_INTERRUPTED;
                    }
                }
            }
        } catch (Exception e) {
            message = "Job failed: " + getJobName();
            UnmJobUtils.logErrorMessage(LOG, auditor, message, e);
            throw new SchedulerJobException(message, e);
        } finally {
            if (starmountDataFeedExtract != null && !starmountDataFeedExtract.close()) {
                LOG.error(MessageFormat.format(CLOSE_FILE_ERROR, getJobName(), starmountDataFeedExtract));
                auditor.error(MessageFormat.format(CLOSE_FILE_ERROR, getJobName(), starmountDataFeedExtract));
            }
        }
        UnmJobUtils.logInfoMessage(LOG, auditor, MessageFormat.format("JOB_COMPLETED", getJobName()));
        return JobResults.JOB_SUCCESSFUL;
    }


    @Override
    protected String getJobName() {
        return getClass().getSimpleName();
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        setInterrupted(true);
    }

    /**
     * @return check if thread is interrupted
     */
    private synchronized boolean isInterrupted() {
        return interrupted;
    }

    /**
     * @param interrupted check if thread is interrupted
     */
    private synchronized void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public void setStarmountPolicyService(UnumStarmountPolicyService starmountPolicyService) {
        this.starmountPolicyService = starmountPolicyService;
    }

    private UnmStarmountFeedDto prepareStarmountData(CertificatePolicy certificatePolicy) {

        List<PreconfigGroupBenefit> benefitList = new ArrayList<>();
        UnmStarmountFeedDto unmStarmountFeedDto = new UnmStarmountFeedDto();
        GroupCoverage groupCoverage = new GroupCoverage();
        PreconfigGroupCoverage preconfigGroupCoverage = new PreconfigGroupCoverage();
        List<AddressEntity> address = null;
        GroupCoverageDefinition groupCoverageDefinition = null;

        if (null != certificatePolicy) {

            /*set certificate policy number*/
            if (null != certificatePolicy.getPolicyNumber()) {
                unmStarmountFeedDto.setPolicyNumber(certificatePolicy.getPolicyNumber());
            } else {
                unmStarmountFeedDto.setPolicyNumber(StringUtils.rightPad(StringUtils.EMPTY, 12));
            }

            /*Pending Business Decision for IPIN*/
            unmStarmountFeedDto.setMainInsuredIPN(StringUtils.rightPad(StringUtils.EMPTY, 12));

            /*set billing details*/
            if (null != certificatePolicy.getBillingInfo()) {

                if (null != certificatePolicy.getBillingInfo().getBillingAccountNo()) {
                    unmStarmountFeedDto.setBillingAccountNumber(certificatePolicy.getBillingInfo().getBillingAccountNo());
                } else {
                    unmStarmountFeedDto.setBillingAccountNumber(StringUtils.rightPad(StringUtils.EMPTY, 12));
                }
                if (null != certificatePolicy.getBillingInfo().getDefaultPaymentMethod()) {
                    Date paidDueDate = certificatePolicy.getBillingInfo().getDefaultPaymentMethod().getPaymentMethodEffectiveDate();
                    if (null != paidDueDate) {
                        unmStarmountFeedDto.setBillingPaidDate(UnmDateUtils.format(paidDueDate, UnmDateUtils.YYYYMMDD_PATTERN));
                    } else {
                        unmStarmountFeedDto.setBillingPaidDate(StringUtils.rightPad(StringUtils.EMPTY, 8));
                    }
                } else {
                    unmStarmountFeedDto.setBillingPaidDate(StringUtils.rightPad(StringUtils.EMPTY, 8));
                }
            } else {
                unmStarmountFeedDto.setBillingAccountNumber(StringUtils.rightPad(StringUtils.EMPTY, 12));
                unmStarmountFeedDto.setBillingPaidDate(StringUtils.rightPad(StringUtils.EMPTY, 8));
            }

            /*set underWritingCompany*/
            unmStarmountFeedDto.setUnderwritingCd(UnumStarmountPolicyGenerationJob.UNDERWRITING_CD);

            /*set issueState*/
            if (null != certificatePolicy.getRiskStateCd()) {
                unmStarmountFeedDto.setRiskStateCode(certificatePolicy.getRiskStateCd());
            } else {
                unmStarmountFeedDto.setRiskStateCode(StringUtils.rightPad(StringUtils.EMPTY, 2));
            }

            /*set policystatus*/
            if (null != certificatePolicy.getPolicyStatusCd()) {
                if(certificatePolicy.getPolicyStatusCd().getName().equalsIgnoreCase(UnumStarmountPolicyGenerationJob.ISSUED)){
                    unmStarmountFeedDto.setPolicyStatusCode("00");
                }else if(certificatePolicy.getPolicyStatusCd().getName().equalsIgnoreCase(UnumStarmountPolicyGenerationJob.CANCELLED)){
                    unmStarmountFeedDto.setPolicyStatusCode("42");
                }else{
                    unmStarmountFeedDto.setPolicyStatusCode(StringUtils.rightPad(StringUtils.EMPTY, 2));
                }
            }else{
                unmStarmountFeedDto.setPolicyStatusCode(StringUtils.rightPad(StringUtils.EMPTY, 2));
            }

            /*set disasterIndicator*/
            unmStarmountFeedDto.setDisasterInd("N");

            /*set sourcePolicyIndicator to one always*/
            unmStarmountFeedDto.setSourcePolicyIndicator(UnumStarmountPolicyGenerationJob.ONE);

            /*set planlevelCode & bpPlanlevelcode*/
            if (!certificatePolicy.getPackageInfos().isEmpty() && null != certificatePolicy.getPackageInfos()) {
                String planLevelCode = certificatePolicy.getPackageInfos().get(0).getPackageLevelCd();
                if (null != planLevelCode) {
                    if (planLevelCode.equalsIgnoreCase(UnumStarmountPolicyGenerationJob.PLAN_FOUR)) {
                        unmStarmountFeedDto.setPlanLevelCode(StringUtils.rightPad(UnumStarmountPolicyGenerationJob.PLAN_FOUR_VAL, 8, StringUtils.EMPTY));
                    } else if (planLevelCode.equalsIgnoreCase(UnumStarmountPolicyGenerationJob.PLAN_FIVE)) {
                        unmStarmountFeedDto.setPlanLevelCode(StringUtils.rightPad(UnumStarmountPolicyGenerationJob.PLAN_FIVE_VAL, 8, StringUtils.EMPTY));
                    } else {
                        unmStarmountFeedDto.setPlanLevelCode(StringUtils.rightPad(planLevelCode, 8, StringUtils.EMPTY));
                    }
                    unmStarmountFeedDto.setBpPlanCode(StringUtils.rightPad(planLevelCode, 8, StringUtils.EMPTY));
                } else {
                    unmStarmountFeedDto.setPlanLevelCode(StringUtils.rightPad(StringUtils.EMPTY, 8));
                    unmStarmountFeedDto.setBpPlanCode(StringUtils.rightPad(StringUtils.EMPTY, 8));
                }
            } else {
                unmStarmountFeedDto.setPlanLevelCode(StringUtils.rightPad(StringUtils.EMPTY, 8));
                unmStarmountFeedDto.setBpPlanCode(StringUtils.rightPad(StringUtils.EMPTY, 8));
            }

            /*set coverage effective date*/
            if (null != certificatePolicy.getContractTerm().getEffective() && null != certificatePolicy.getContractTerm()) {
                unmStarmountFeedDto.setBpCoverageEffectiveDate(UnmDateUtils.format(certificatePolicy.getContractTerm().getEffective(), UnmDateUtils.YYYYMMDD_PATTERN));
                unmStarmountFeedDto.setInsuredCoverageEffectiveDate(UnmDateUtils.format(certificatePolicy.getContractTerm().getEffective(), UnmDateUtils.YYYYMMDD_PATTERN));
            } else {
                unmStarmountFeedDto.setBpCoverageEffectiveDate(StringUtils.rightPad(StringUtils.EMPTY, 8));
                unmStarmountFeedDto.setInsuredCoverageEffectiveDate(StringUtils.rightPad(StringUtils.EMPTY, 8));
            }

            /*set bp&Insured TerminationDate*/
            if (null != certificatePolicy.getLastCancellationDate()) {
                unmStarmountFeedDto.setBpTerminationDate(UnmDateUtils.format(certificatePolicy.getLastCancellationDate(), UnmDateUtils.YYYYMMDD_PATTERN));
                unmStarmountFeedDto.setInsuredTerminationdate(UnmDateUtils.format(certificatePolicy.getLastCancellationDate(), UnmDateUtils.YYYYMMDD_PATTERN));
            } else {
                unmStarmountFeedDto.setBpTerminationDate(StringUtils.rightPad(StringUtils.EMPTY, 8));
                unmStarmountFeedDto.setInsuredTerminationdate(StringUtils.rightPad(StringUtils.EMPTY, 8));
            }

            /*set bpWaitingPeriodInd & StartDate Optional for Starmount. Will need to add on Dental but not for MVP2*/
            unmStarmountFeedDto.setBpWaitingPeriodInd(StringUtils.rightPad(StringUtils.EMPTY, 1));
            unmStarmountFeedDto.setBpWaitingPeriodStartDate(StringUtils.rightPad(StringUtils.EMPTY, 8));

            /*set lapse data with hardcoded values*/
            unmStarmountFeedDto.setLapseStartDateOne(StringUtils.rightPad(StringUtils.EMPTY, 8));
            unmStarmountFeedDto.setLapseStopDateOne(StringUtils.rightPad(StringUtils.EMPTY, 8));
            unmStarmountFeedDto.setLapseDurationOne(UnumStarmountPolicyGenerationJob.LAPSE_DURATION);

            unmStarmountFeedDto.setLapseStartDateTwo(StringUtils.rightPad(StringUtils.EMPTY, 8));
            unmStarmountFeedDto.setLapseStopDateTwo(StringUtils.rightPad(StringUtils.EMPTY, 8));
            unmStarmountFeedDto.setLapseDurationTwo(UnumStarmountPolicyGenerationJob.LAPSE_DURATION);

            unmStarmountFeedDto.setLapseStartDateThree(StringUtils.rightPad(StringUtils.EMPTY, 8));
            unmStarmountFeedDto.setLapseStopDateThree(StringUtils.rightPad(StringUtils.EMPTY, 8));
            unmStarmountFeedDto.setLapseDurationThree(UnumStarmountPolicyGenerationJob.LAPSE_DURATION);

            /*pending business Decision for Insured IPN*/
            unmStarmountFeedDto.setInsuredIPN(StringUtils.rightPad(StringUtils.EMPTY, 12));

            /*In process- UNUM-13536*/
            unmStarmountFeedDto.setInsuredDomesticAbuseInd(StringUtils.rightPad(StringUtils.EMPTY, 1));

            if (null != certificatePolicy.getPolicyDetail()) {
                for (RiskItem riskItem : certificatePolicy.getPolicyDetail().getRiskItems()) {
                    List<Coverage> coverages = riskItem.getCoverages();
                    if (!coverages.isEmpty()) {

                        /*set familycoveragetypecode*/
                        unmStarmountFeedDto.setCoverageTier(((PreconfigGroupCoverage) (coverages.get(0))).getCoverageTier());

                        Coverage coverage = coverages.get(0);
                        if (coverage instanceof GroupCoverage) {
                            groupCoverage = (GroupCoverage) coverage;
                            if (groupCoverage instanceof PreconfigGroupCoverage) {
                                preconfigGroupCoverage = (PreconfigGroupCoverage) groupCoverage;
                                benefitList = preconfigGroupCoverage.getBenefits();
                                for (PreconfigGroupBenefit benefit : benefitList) {
                                    if (benefit instanceof PreconfigGroupDentalBenefit) {

                                        PreconfigGroupDentalBenefit dentalOrthoBenefit = (PreconfigGroupDentalBenefit) benefit;
                                        if (null != dentalOrthoBenefit.getApplied()) {
                                            unmStarmountFeedDto.setOrthoPlanCode(dentalOrthoBenefit.getApplied());
                                        } else {
                                            unmStarmountFeedDto.setOrthoPlanCode(Boolean.FALSE);
                                        }

                                    } else if (benefit instanceof PreconfigGrpVisionHealthBenefit) {

                                        PreconfigGrpVisionHealthBenefit dentalVisionBenefit = (PreconfigGrpVisionHealthBenefit) benefit;
                                        if (null != dentalVisionBenefit.getVisionRiderEffDt()) {

                                            unmStarmountFeedDto.setOrthoVisionRiderEffectiveDate(UnmDateUtils.format(dentalVisionBenefit.getVisionRiderEffDt(), UnmDateUtils.YYYYMMDD_PATTERN));
                                            unmStarmountFeedDto.setOrthoWaitingPeriodDate(UnmDateUtils.format(dentalVisionBenefit.getVisionRiderEffDt(), UnmDateUtils.YYYYMMDD_PATTERN));
                                        } else {
                                            unmStarmountFeedDto.setOrthoVisionRiderEffectiveDate(StringUtils.rightPad(StringUtils.EMPTY, 8));
                                            unmStarmountFeedDto.setOrthoWaitingPeriodDate(StringUtils.rightPad(StringUtils.EMPTY, 8));
                                        }
                                    }
                                }
                            }
                        }

                        String coverageCode = coverages.get(0).getCoverageCd();
                        if(null != coverageCode){
                         groupCoverageDefinition = starmountPolicyService.getGroupCoverageDefinition(certificatePolicy, coverageCode);
                        }

                        if (groupCoverageDefinition != null) {
                            PreconfigGroupEligibility eligibility = ((PreconfigGroupCoverageDefinition) groupCoverageDefinition).getEligibility();
                            if (null != eligibility) {
                                if (null != eligibility.getDisabledDependents() && eligibility.getDisabledDependents()) {
                                    unmStarmountFeedDto.setDisabledDependentInd("Y");
                                } else {
                                    unmStarmountFeedDto.setDisabledDependentInd("N");
                                }
                            }else{
                                unmStarmountFeedDto.setDisabledDependentInd(StringUtils.rightPad(StringUtils.EMPTY, 1));
                            }
                        }else{
                            unmStarmountFeedDto.setDisabledDependentInd(StringUtils.rightPad(StringUtils.EMPTY, 1));
                        }
                    }
                }
                int dependentCount = 1;
                List<UnmStarmountFeedInsuredDto> insuredDtoList = new ArrayList<>();
                for (GeneralPartyInfoEntity insureds : CollectionUtils.emptyIfNull(certificatePolicy.getPolicyDetail().getParties())) {
                    if (null != insureds) {
                        GroupPersonInfoEntity insured = ((GroupPersonInfoEntity) insureds);

                        UnmStarmountFeedInsuredDto insurerObj = new UnmStarmountFeedInsuredDto();

                        /*set Insured SSN*/
                        if (null != insured.getLegalIdentification()) {
                            insurerObj.setInsuredSSN(StringUtils.rightPad(insured.getLegalIdentification(), 8, StringUtils.EMPTY));
                        } else {
                            insurerObj.setInsuredSSN(StringUtils.rightPad(StringUtils.EMPTY, 8));
                        }

                        /*set insured relationship & life identification code*/
                        if (null != insured.getGeneralPartyInfoExt()) {
                            String relCode = ((GroupGeneralPartyInfoExt) insured.getGeneralPartyInfoExt()).getRelationshipToInsuredPrincipal();
                            if (null != relCode && relCode.startsWith(UnumStarmountPolicyGenerationJob.INSURED_SELF)) {
                                insurerObj.setInsuredRelCode(UnumStarmountPolicyGenerationJob.ONE);
                                insurerObj.setInsuredLifeCode("I0");
                            } else if (null != relCode && relCode.startsWith(UnumStarmountPolicyGenerationJob.INSURED_SPOUSE)) {
                                insurerObj.setInsuredRelCode("2");
                                insurerObj.setInsuredLifeCode("S0");
                            } else {
                                insurerObj.setInsuredRelCode("3");
                                insurerObj.setInsuredLifeCode(StringUtils.leftPad(String.valueOf(dependentCount++), 2, "0"));
                            }

                        } else {
                            insurerObj.setInsuredRelCode(StringUtils.rightPad(StringUtils.EMPTY, 1));
                            insurerObj.setInsuredLifeCode(StringUtils.rightPad(StringUtils.EMPTY, 2));
                        }

                        /*set insured gender*/
                        if (null != insured.getGender()) {
                            if (insured.getGender().name().equalsIgnoreCase("male")) {
                                insurerObj.setInsuredSex("M");
                            } else {
                                insurerObj.setInsuredSex("F");
                            }
                        } else {
                            insurerObj.setInsuredSex(StringUtils.rightPad(StringUtils.EMPTY, 1));
                        }

                        /*set insured DOB*/
                        if (null != insured.getDateOfBirth()) {
                            String dob = UnmDateUtils.format(insured.getDateOfBirth(), UnmDateUtils.YYYYMMDD_PATTERN);
                            insurerObj.setInsuredDob(dob);
                        } else {
                            insurerObj.setInsuredDob(StringUtils.rightPad(StringUtils.EMPTY, 8));
                        }


                        /*set insured first,middle,last,suffix names*/
                        if (null != insured.getNameInfo()) {
                            if (null != insured.getNameInfo().getFirstName()) {
                                insurerObj.setInsuredFName(StringUtils.rightPad(insured.getNameInfo().getFirstName(), 30, StringUtils.EMPTY));
                            } else {
                                insurerObj.setInsuredFName(StringUtils.rightPad(StringUtils.EMPTY, 30));
                            }
                            if (null != insured.getNameInfo().getLastName()) {
                                insurerObj.setInsuredLName(StringUtils.rightPad(insured.getNameInfo().getLastName(), 30, StringUtils.EMPTY));
                            } else {
                                insurerObj.setInsuredLName(StringUtils.rightPad(StringUtils.EMPTY, 30));
                            }
                            if (null != insured.getNameInfo().getMiddleName()) {
                                insurerObj.setInsuredMName(insured.getNameInfo().getMiddleName());
                            } else {
                                insurerObj.setInsuredMName(StringUtils.rightPad(StringUtils.EMPTY, 1));
                            }
                            if (null != insured.getNameInfo().getSuffix()) {
                                insurerObj.setInsuredSuffix(StringUtils.rightPad(insured.getNameInfo().getSuffix(), 5, StringUtils.EMPTY));
                            } else {
                                insurerObj.setInsuredSuffix(StringUtils.rightPad(StringUtils.EMPTY, 5));
                            }
                        }

                        /*set insured address details*/
                        Customer customer = starmountPolicyService.getPrimaryInsuredCustomer(certificatePolicy);
                        if (null != customer) {
                            address = customer.getAddressEntities();
                            if (null != address) {
                                if (null != address.get(0).getAddressLine1()) {
                                    insurerObj.setInsuredAddressLineOne(StringUtils.rightPad(address.get(0).getAddressLine1(), 30, StringUtils.EMPTY));
                                } else {
                                    insurerObj.setInsuredAddressLineOne(StringUtils.rightPad(StringUtils.EMPTY, 30));
                                }
                                if (null != address.get(0).getAddressLine2()) {
                                    insurerObj.setInsuredAddressLineTwo(StringUtils.rightPad(address.get(0).getAddressLine2(), 30, StringUtils.EMPTY));
                                } else {
                                    insurerObj.setInsuredAddressLineTwo(StringUtils.rightPad(StringUtils.EMPTY, 30));
                                }
                                if (null != address.get(0).getCity()) {
                                    insurerObj.setInsuredCity(StringUtils.rightPad(address.get(0).getCity(), 24, StringUtils.EMPTY));
                                } else {
                                    insurerObj.setInsuredCity(StringUtils.rightPad(StringUtils.EMPTY, 24));
                                }
                                if (null != address.get(0).getStateProvCd()) {
                                    insurerObj.setInsuredState(address.get(0).getStateProvCd());
                                } else {
                                    insurerObj.setInsuredState(StringUtils.rightPad(StringUtils.EMPTY, 2));
                                }
                                if (null != address.get(0).getPostalCode()) {
                                    insurerObj.setInsuredZip(StringUtils.rightPad(address.get(0).getPostalCode(), 71, StringUtils.EMPTY));
                                } else {
                                    insurerObj.setInsuredZip(StringUtils.rightPad(StringUtils.EMPTY, 71));
                                }
                            }
                        } else {
                            insurerObj.setInsuredAddressLineOne(StringUtils.rightPad(StringUtils.EMPTY, 30));
                            insurerObj.setInsuredAddressLineTwo(StringUtils.rightPad(StringUtils.EMPTY, 30));
                            insurerObj.setInsuredCity(StringUtils.rightPad(StringUtils.EMPTY, 24));
                            insurerObj.setInsuredState(StringUtils.rightPad(StringUtils.EMPTY, 2));
                            insurerObj.setInsuredZip(StringUtils.rightPad(StringUtils.EMPTY, 71));

                        }
                        insuredDtoList.add(insurerObj);
                    }
                }
                unmStarmountFeedDto.setInsuredDtoList(insuredDtoList);
            }
        }
        benefitList.clear();
        return unmStarmountFeedDto;
    }
}
