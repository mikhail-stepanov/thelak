package com.thelak.payments.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbCertificate;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.payments.interfaces.ICertificateService;
import com.thelak.route.payments.models.CertificateModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.thelak.payments.services.PaymentsHelper.buildCertificateModel;

@RestController
@Api(value = "Certificate API", produces = "application/json")
public class CertificateEndpoint extends AbstractMicroservice implements ICertificateService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(CertificateEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @CrossOrigin
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
    @CrossOrigin
    @ApiOperation(value = "Get list of certificate")
    @RequestMapping(value = CERTIFICATE_LIST, method = {RequestMethod.GET})
    public List<CertificateModel> list() throws MicroServiceException {

        try {
            List<DbCertificate> dbCertificates = ObjectSelect.query(DbCertificate.class).select(objectContext);

            List<CertificateModel> certificateModels = new ArrayList<>();

            dbCertificates.forEach(dbCertificate -> {
                certificateModels.add(buildCertificateModel(dbCertificate));
            });

            return certificateModels;

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
