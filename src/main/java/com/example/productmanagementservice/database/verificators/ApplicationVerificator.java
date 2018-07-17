package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationVerificator {

    private final ApplicationsRepository applicationsRepository;
    private final UserVerificator userVerificator;

    @Autowired
    public ApplicationVerificator(UserVerificator userVerificator, ApplicationsRepository applicationsRepository) {
        this.userVerificator = userVerificator;
        this.applicationsRepository = applicationsRepository;
    }

    public boolean isExistsApplication(long idApplication, int status) {
        Application application = getApplicationOfIdAndStatus(idApplication, status);
        return !(application == null);
    }

    public boolean verificationOfBelongingApplicationToClient(long idApplication, String token) {
        User user = userVerificator.getUserOfToken(token);

        List<Application> applications = applicationsRepository.getUserApplicationsById(idApplication, user.getId());

        return !applications.isEmpty();
    }

    public boolean isExistsApplication(long idApplication) {
        Application application = getApplicationOfId(idApplication);
        return !(application == null);
    }

    public boolean checkForChangeStatusApplication(long idApplication) {
        Application application = getApplicationOfIdAndStatus(idApplication, Application.status.SENT.ordinal());
        return !(application == null);
    }

    public boolean checkIsEmptyOfApplication(long idApplication) {
        Application application = getApplicationOfId(idApplication);
        String product = application.getProduct();
        return (product == null);
    }

    public Application getApplicationOfId(long idApplication) {
        List<Application> applications = applicationsRepository.getApplicationsById(idApplication);

        if (applications.isEmpty()) {
            return null;
        }

        return applications.get(0);
    }

    public Application getApplicationOfIdAndStatus(long idApplication, int status) {
        List<Application> applications = applicationsRepository.getApplicationsByIdAndStatus(idApplication, status);

        if (applications.isEmpty()) {
            return null;
        }

        return applications.get(0);
    }
}
