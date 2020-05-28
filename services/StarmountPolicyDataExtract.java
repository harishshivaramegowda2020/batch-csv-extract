package com.custom.java.preconfig.policy.group.services.starmount.services;

import com.custom.java.preconfig.policy.group.services.starmount.csvFramework.CsvExtract;
import com.custom.java.preconfig.policy.group.services.starmount.dto.UnmStarmountFeedDto;
import com.custom.java.preconfig.policy.group.services.starmount.dto.UnmStarmountFeedInsuredDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;

public class StarmountPolicyDataExtract extends CsvExtract<UnmStarmountFeedDto> {

    private static final String RECORD_TYPE_D = "D";
    private int recordCount = 1;

    enum Headers implements ColumnMetadata {
        RECORD_TYPE_D("Record Type");

        private final String columnName;

        Headers(String name) {
            columnName = name;
        }

        @Override
        public String getColumnName() {
            return columnName;
        }

        @Override
        public int getColumnNumber() {
            return this.ordinal();
        }
    }

    public StarmountPolicyDataExtract(Path fileName, int size) throws IOException {
        super(fileName, size);
    }

    @Override
    public ColumnMetadata[] getHeaders() {
        return Headers.values();
    }


    @Override
    public void add(UnmStarmountFeedDto unmStarmountFeedDto) throws IOException {

        for (UnmStarmountFeedInsuredDto insured : CollectionUtils.emptyIfNull(unmStarmountFeedDto.getInsuredDtoList())) {
            if(null != insured){
            setCellValue(Headers.RECORD_TYPE_D, StringUtils.join(StarmountPolicyDataExtract.RECORD_TYPE_D,
                    unmStarmountFeedDto.getPolicyNumber(),
                    unmStarmountFeedDto.getMainInsuredIPN(),
                    unmStarmountFeedDto.getBillingAccountNumber(),
                    unmStarmountFeedDto.getUnderwritingCd(),
                    unmStarmountFeedDto.getRiskStateCode(),
                    unmStarmountFeedDto.getPolicyStatusCode(),
                    unmStarmountFeedDto.getPlanLevelCode(),
                    unmStarmountFeedDto.getCoverageTier(),
                    unmStarmountFeedDto.getBillingPaidDate(),
                    unmStarmountFeedDto.getDisasterInd(),
                    unmStarmountFeedDto.getSourcePolicyIndicator(),
                    unmStarmountFeedDto.getBpPlanCode(),
                    unmStarmountFeedDto.getBpCoverageEffectiveDate(),
                    unmStarmountFeedDto.getBpTerminationDate(),
                    unmStarmountFeedDto.getBpWaitingPeriodInd(),
                    unmStarmountFeedDto.getBpWaitingPeriodStartDate(),
                    unmStarmountFeedDto.getOrthoPlanCode(),
                    unmStarmountFeedDto.getOrthoVisionRiderEffectiveDate(),
                    unmStarmountFeedDto.getOrthoTerminationDate(),
                    unmStarmountFeedDto.getOrthoWaitingPeriodDate(),
                    unmStarmountFeedDto.getLapseStartDateOne(),
                    unmStarmountFeedDto.getLapseStopDateOne(),
                    unmStarmountFeedDto.getLapseDurationOne(),
                    unmStarmountFeedDto.getLapseStartDateTwo(),
                    unmStarmountFeedDto.getLapseStopDateTwo(),
                    unmStarmountFeedDto.getLapseDurationTwo(),
                    unmStarmountFeedDto.getLapseStartDateThree(),
                    unmStarmountFeedDto.getLapseStopDateThree(),
                    unmStarmountFeedDto.getLapseDurationThree(),
                    unmStarmountFeedDto.getInsuredIPN(),
                    insured.getInsuredSSN(),
                    unmStarmountFeedDto.getInsuredCoverageEffectiveDate(),
                    unmStarmountFeedDto.getInsuredTerminationdate(),
                    unmStarmountFeedDto.getInsuredDomesticAbuseInd(),
                    unmStarmountFeedDto.getDisabledDependentInd(),
                    insured.getInsuredRelCode(),
                    insured.getInsuredLifeCode(),
                    insured.getInsuredSex(),
                    insured.getInsuredDob(),
                    insured.getInsuredLName(),
                    insured.getInsuredFName(),
                    insured.getInsuredMName(),
                    insured.getInsuredSuffix(),
                    insured.getInsuredAddressLineOne(),
                    insured.getInsuredAddressLineTwo(),
                    insured.getInsuredCity(),
                    insured.getInsuredState(),
                    insured.getInsuredZip()));

            writeLine(recordCount);
            recordCount++;
            }
        }
    }
}
