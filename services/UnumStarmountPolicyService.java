package com.custom.java.preconfig.policy.group.services.starmount.services;

import com.custom.java.preconfig.policy.group.services.starmount.csvFramework.UnmDateUtils;
import com.custom.java.preconfig.policy.group.starmount.dao.StarmountCertificatePolicyDAO;
import com.custom.ipb.crm.dao.CustomerDao;
import com.custom.ipb.crm.domain.Customer;
import com.custom.ipb.policy.group.domain.CertificatePolicy;

import com.custom.ipb.policy.group.domain.GroupCoverageDefinition;
import com.custom.ipb.policy.group.domain.MasterPolicy;
import com.custom.ipb.policy.group.services.MasterPolicyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Transactional
public class UnumStarmountPolicyService {
    private static final String java_ELIGIBILITY_DATA_FEED = "javaEligibility";
    private String jobFolder;
    private CustomerDao customerDao;
    private StarmountCertificatePolicyDAO certificatePolicyDao;
    private MasterPolicyService masterPolicyService;

    /**
     * Method to get certificatepolicy id's based on transaction date.
     *
     * @param startDate
     * @param endDate
     * @return List<Long>
     */
    public List<Long> getCertificatePolicyIdByTransactionDate(Date startDate, Date endDate) {
        return certificatePolicyDao.findCertificatesPolicyIdBytransactionDateBetween(startDate, endDate);
    }

    /**
     * Method to get certificatepolicy transactions data based on cp id.
     *
     * @param certificateId
     * @return List<Long>
     */
    public CertificatePolicy getCertificatePolicyByCertificateId(Long certificateId) {
        return certificatePolicyDao.findById(certificateId);
    }

    /**
     * Method to create flat file with headers and initialize flat file extract to write data in txt file
     *
     * @param size
     * @return StarmountPolicyDataExtract
     * @throws IOException
     */
    public StarmountPolicyDataExtract createStarmountDataFeedExtract(int size) throws IOException {
        System.out.println("creating csv extract");
        StarmountPolicyDataExtract csvExtract = new StarmountPolicyDataExtract(
                Paths.get(jobFolder,
                        StringUtils.join(java_ELIGIBILITY_DATA_FEED, getFormattedCurrentDate(), UnmDateUtils.TXT_EXTENSION)),
                size);
        System.out.println(ReflectionToStringBuilder.toString(csvExtract));
        return csvExtract;
    }

    /**
     * Method to fetch customer
     *
     * @param policy
     * @return Customer
     */
    public Customer getPrimaryInsuredCustomer(CertificatePolicy policy) {
        return customerDao.getCustomerByNumber(policy.getCustomerNumber());
    }

    /**
     * Method to fetch groupCoverageDefinition
     *
     * @param certificatePolicy
     * @param coverageCd
     * @return GroupCoverageDefinition
     */
    public GroupCoverageDefinition getGroupCoverageDefinition(CertificatePolicy certificatePolicy, String coverageCd) {
        MasterPolicy policy = masterPolicyService.findMasterPolicyById(certificatePolicy.getMasterPolicyId());
        for (GroupCoverageDefinition coverageDefinition : policy.getPolicyDetail().getCoverageDefinitions()) {
            if (coverageDefinition.getCoverageCd().equals(coverageCd)) {
                return coverageDefinition;
            }
        }
        return null;
    }

    /**
     * Method to format the date
     *
     * @return String
     */
    private static String getFormattedCurrentDate() {
        return UnmDateUtils.format(new Date(), UnmDateUtils.YYYYMMDD_PATTERN);
    }

    public String getJobFolder() {
        return jobFolder;
    }

    public void setJobFolder(String jobFolder) {
        this.jobFolder = jobFolder;
    }

    public void setCertificatePolicyDao(StarmountCertificatePolicyDAO certificatePolicyDao) {
        this.certificatePolicyDao = certificatePolicyDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void setMasterPolicyService(MasterPolicyService masterPolicyService) {
        this.masterPolicyService = masterPolicyService;
    }

}
