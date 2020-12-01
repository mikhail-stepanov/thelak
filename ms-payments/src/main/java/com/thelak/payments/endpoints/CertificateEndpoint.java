package com.thelak.payments.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbCertificate;
import com.thelak.database.entity.DbIssuedCertificate;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsBadRequestException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.payments.interfaces.ICertificateService;
import com.thelak.route.payments.models.certificate.CertificateModel;
import com.thelak.route.payments.models.certificate.CreateCertificateRequest;
import com.thelak.route.payments.models.certificate.IssuedCertificateModel;
import com.thelak.route.payments.models.subscription.SetSubscriptionModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.thelak.payments.services.PaymentsHelper.buildCertificateModel;

@RestController
@Api(value = "Certificate API", produces = "application/json")
public class CertificateEndpoint extends AbstractMicroservice implements ICertificateService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private IAuthenticationService authenticationService;

    ObjectContext objectContext;

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @ApiOperation(value = "Get certificate by id")
    @RequestMapping(value = CERTIFICATE_GET, method = {RequestMethod.GET})
    public CertificateModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            DbCertificate dbCertificate = SelectById.query(DbCertificate.class, id).selectFirst(objectContext);

            return buildCertificateModel(dbCertificate);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of certificate")
    @RequestMapping(value = CERTIFICATE_LIST, method = {RequestMethod.GET})
    public List<CertificateModel> list() throws MicroServiceException {

        try {
            List<DbCertificate> dbCertificates = ObjectSelect.query(DbCertificate.class)
                    .orderBy(DbCertificate.MONTHS.asc())
                    .select(objectContext);

            List<CertificateModel> certificateModels = new ArrayList<>();

            dbCertificates.forEach(dbCertificate -> {
                certificateModels.add(buildCertificateModel(dbCertificate));
            });

            return certificateModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Create Certificate")
    @RequestMapping(value = CERTIFICATE_CREATE, method = {RequestMethod.POST})
    public CertificateModel create(CreateCertificateRequest request) throws MicroServiceException {
        return null;
    }

    @Override
    @ApiOperation(value = "Update Certificate")
    @RequestMapping(value = CERTIFICATE_UPDATE, method = {RequestMethod.PUT})
    public CertificateModel update(CertificateModel request) throws MicroServiceException {
        return null;
    }

    @Override
    @ApiOperation(value = "Delete Certificate")
    @RequestMapping(value = CERTIFICATE_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(Long id) throws MicroServiceException {
        return null;
    }

    @Override
    @ApiOperation(value = "Generate certificate")
    @RequestMapping(value = CERTIFICATE_GENERATE, method = {RequestMethod.POST})
    public IssuedCertificateModel generate(Long certificateId) throws MicroServiceException {
        try {
            DbCertificate dbCertificate = SelectById.query(DbCertificate.class, certificateId)
                    .selectFirst(objectContext);

            DbIssuedCertificate dbIssuedCertificate = objectContext.newObject(DbIssuedCertificate.class);
            dbIssuedCertificate.setActive(true);
            dbIssuedCertificate.setActiveDate(LocalDateTime.now().plusMonths(1L));
            dbIssuedCertificate.setCreatedDate(LocalDateTime.now());
            dbIssuedCertificate.setUuid(UUID.randomUUID().toString());
            dbIssuedCertificate.setIssuedToCertificate(dbCertificate);
            objectContext.commitChanges();

            return IssuedCertificateModel.builder()
                    .id((Long) dbIssuedCertificate.getObjectId().getIdSnapshot().get("id"))
                    .active(dbIssuedCertificate.isActive())
                    .activeDate(dbIssuedCertificate.getActiveDate())
                    .uuid(dbIssuedCertificate.getUuid())
                    .fio(dbIssuedCertificate.getFio())
                    .certificateModel(buildCertificateModel(dbCertificate))
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Activate certificate")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = CERTIFICATE_ACTIVATE, method = {RequestMethod.GET})
    public Boolean activate(String uuid) throws MicroServiceException {
        try {
            DbIssuedCertificate dbIssuedCertificate = ObjectSelect.query(DbIssuedCertificate.class)
                    .where(DbIssuedCertificate.UUID.eq(uuid)).selectFirst(objectContext);

            if (dbIssuedCertificate.isActive()) {
                UserInfo userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

                authenticationService.setSubscription(SetSubscriptionModel.builder()
                        .userId(userInfo.getUserId())
                        .subscriptionDate(LocalDateTime.now().plusMonths(dbIssuedCertificate.getIssuedToCertificate().getMonths())).build());

                return true;
            } else throw new MsBadRequestException("Certificate has expired");

        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
