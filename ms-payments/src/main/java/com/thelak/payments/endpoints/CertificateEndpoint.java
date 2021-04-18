package com.thelak.payments.endpoints;

import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbCertificate;
import com.thelak.database.entity.DbIssuedCertificate;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsBadRequestException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.message.IMessageService;
import com.thelak.route.payments.interfaces.ICertificateService;
import com.thelak.route.payments.models.certificate.CertificateModel;
import com.thelak.route.payments.models.certificate.CertificateViewType;
import com.thelak.route.payments.models.certificate.CreateCertificateRequest;
import com.thelak.route.payments.models.certificate.EmailCertificateRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.thelak.payments.services.PaymentsHelper.buildCertificateModel;

@RestController
@Api(value = "Certificate API", produces = "application/json")
public class CertificateEndpoint extends MicroserviceAdvice implements ICertificateService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private IAuthenticationService authenticationService;


    @Autowired
    private IMessageService messageService;

    @Value("${user.certificate.queue:#{null}}")
    private String userCertificateQueue;

    @Override
    @ApiOperation(value = "Get certificate by id")
    @RequestMapping(value = CERTIFICATE_GET, method = {RequestMethod.GET})
    public CertificateModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

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
            ObjectContext objectContext = databaseService.getContext();

            List<DbCertificate> dbCertificates = ObjectSelect.query(DbCertificate.class)
                    .where(DbCertificate.DELETED_DATE.isNull())
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
    public CertificateModel create(@RequestBody CreateCertificateRequest request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbCertificate dbCertificate = objectContext.newObject(DbCertificate.class);
            dbCertificate.setName(request.getName());
            dbCertificate.setPriceStr(request.getPriceStr());
            dbCertificate.setPriceStr2(request.getPriceStr2());
            dbCertificate.setPrice(request.getPrice());
            dbCertificate.setCover(request.getCover());
            dbCertificate.setMonths(request.getMonths());
            dbCertificate.setDescription(request.getDescription());
            dbCertificate.setLength(request.getLength());
            dbCertificate.setCreatedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildCertificateModel(dbCertificate);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Update Certificate")
    @RequestMapping(value = CERTIFICATE_UPDATE, method = {RequestMethod.PUT})
    public CertificateModel update(@RequestBody CertificateModel request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbCertificate dbCertificate = SelectById.query(DbCertificate.class, request.getId())
                    .selectFirst(objectContext);

            dbCertificate.setName(Optional.ofNullable(request.getName()).orElse(dbCertificate.getName()));
            dbCertificate.setPriceStr(Optional.ofNullable(request.getPriceStr()).orElse(dbCertificate.getPriceStr()));
            dbCertificate.setPriceStr2(Optional.ofNullable(request.getPriceStr2()).orElse(dbCertificate.getPriceStr2()));
            dbCertificate.setPrice(Optional.ofNullable(request.getPrice()).orElse(dbCertificate.getPrice()));
            dbCertificate.setCover(Optional.ofNullable(request.getCover()).orElse(dbCertificate.getCover()));
            dbCertificate.setMonths(Optional.ofNullable(request.getMonths()).orElse(dbCertificate.getMonths()));
            dbCertificate.setDescription(Optional.ofNullable(request.getDescription()).orElse(dbCertificate.getDescription()));
            dbCertificate.setLength(Optional.ofNullable(request.getLength()).orElse(dbCertificate.getLength()));
            dbCertificate.setModifiedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildCertificateModel(dbCertificate);
        } catch (Exception e) {
            throw new MicroServiceException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Delete Certificate")
    @RequestMapping(value = CERTIFICATE_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbCertificate dbCertificate = SelectById.query(DbCertificate.class, id).selectFirst(objectContext);

            dbCertificate.setDeletedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Generate certificate")
    @RequestMapping(value = CERTIFICATE_GENERATE, method = {RequestMethod.POST})
    public IssuedCertificateModel generate(@RequestParam Long certificateId) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

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
    @ApiOperation(value = "Get certificate by uuid")
    @RequestMapping(value = CERTIFICATE_GET_UUID, method = {RequestMethod.GET})
    public IssuedCertificateModel getByUUID(@RequestParam String uuid) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbIssuedCertificate dbIssuedCertificate = ObjectSelect.query(DbIssuedCertificate.class)
                    .where(DbIssuedCertificate.UUID.eq(uuid))
                    .selectFirst(objectContext);

            return IssuedCertificateModel.builder()
                    .id((Long) dbIssuedCertificate.getObjectId().getIdSnapshot().get("id"))
                    .active(dbIssuedCertificate.isActive())
                    .activeDate(dbIssuedCertificate.getActiveDate())
                    .uuid(dbIssuedCertificate.getUuid())
                    .fio(dbIssuedCertificate.getFio())
                    .description(dbIssuedCertificate.getDescription())
                    .type(CertificateViewType.valueOf(dbIssuedCertificate.getType()))
                    .certificateModel(buildCertificateModel(dbIssuedCertificate.getIssuedToCertificate()))
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
    public Boolean activate(@RequestParam String uuid) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

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

    @Override
    @ApiOperation(value = "Send certificate over email")
    @RequestMapping(value = CERTIFICATE_EMAIL, method = {RequestMethod.POST})
    public Boolean email(@RequestBody EmailCertificateRequest request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            request.getEmail().forEach(email -> {
                DbCertificate dbCertificate = SelectById.query(DbCertificate.class, request.getId())
                        .selectFirst(objectContext);

                int leftLimit = 48; // numeral '0'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 10;
                Random random = new Random();

                String generatedString = random.ints(leftLimit, rightLimit + 1)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();


                DbIssuedCertificate certificate = objectContext.newObject(DbIssuedCertificate.class);
                certificate.setActive(true);
                certificate.setActiveDate(LocalDateTime.now().plusMonths(1L));
                certificate.setCreatedDate(LocalDateTime.now());
                certificate.setUuid(generatedString);
                certificate.setIssuedToCertificate(dbCertificate);
                certificate.setFio(request.getFio().get(0) != null ? request.getFio().get(0) : "");
                certificate.setDescription(request.getDescription());
                certificate.setType(request.getType().name());
                certificate.setEmail(email);
                objectContext.commitChanges();


                IssuedCertificateModel issuedCertificateModel = IssuedCertificateModel.builder()
                        .id((Long) certificate.getObjectId().getIdSnapshot().get("id"))
                        .buyerEmail(certificate.getEmail())
                        .uuid(certificate.getUuid())
                        .fio(certificate.getFio())
                        .description(certificate.getDescription())
                        .active(certificate.isActive())
                        .activeDate(certificate.getActiveDate())
                        .type(CertificateViewType.valueOf(certificate.getType()))
                        .certificateModel(buildCertificateModel(certificate.getIssuedToCertificate()))
                        .build();

                messageService.publish(userCertificateQueue, issuedCertificateModel);
            });

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
