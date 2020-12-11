package com.thelak.payments.endpoints;

import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbPromo;
import com.thelak.database.entity.DbPromoEmail;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.payments.interfaces.IPromoService;
import com.thelak.route.payments.models.promo.PromoCodeCreateRequest;
import com.thelak.route.payments.models.promo.PromoCodeModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.thelak.payments.services.PaymentsHelper.buildPromoModel;

@RestController
@Api(value = "Promo code API", produces = "application/json")
public class PromoEndpoint extends MicroserviceAdvice implements IPromoService {

    @Autowired
    private DatabaseService databaseService;

    @Override
    @ApiOperation(value = "Get promo by id")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PROMO_GET, method = {RequestMethod.GET})
    public PromoCodeModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbPromo dbPromo = SelectById.query(DbPromo.class, id).selectFirst(objectContext);

            return buildPromoModel(dbPromo);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of promo")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PROMO_LIST, method = {RequestMethod.GET})
    public List<PromoCodeModel> list() throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbPromo> dbPromos = ObjectSelect.query(DbPromo.class).select(objectContext);
            List<PromoCodeModel> result = new ArrayList<>();
            dbPromos.forEach(dbPromo -> {
                result.add(buildPromoModel(dbPromo));
            });
            return result;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Create Promo")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PROMO_CREATE, method = {RequestMethod.POST})    public PromoCodeModel create(@RequestBody PromoCodeCreateRequest request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbPromo dbPromo = objectContext.newObject(DbPromo.class);
            dbPromo.setActive(request.getActive());
            dbPromo.setCode(request.getCode());
            dbPromo.setMonths(request.getMonths());
            dbPromo.setDescription(request.getDescription());

            objectContext.commitChanges();

            if (request.getEmails() != null && !request.getEmails().isEmpty())
                request.getEmails().forEach(email -> {
                    DbPromoEmail dbPromoEmail = objectContext.newObject(DbPromoEmail.class);
                    dbPromoEmail.setEmail(email);
                    dbPromoEmail.setActive(true);
                    dbPromoEmail.setEmailToPromo(dbPromo);
                });
            objectContext.commitChanges();

            return buildPromoModel(dbPromo);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Update promo")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PROMO_UPDATE, method = {RequestMethod.PUT})
    public PromoCodeModel update(@RequestBody PromoCodeModel request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbPromo dbPromo = SelectById.query(DbPromo.class, request.getId()).selectFirst(objectContext);
            dbPromo.setActive(Optional.ofNullable(request.getActive()).orElse(dbPromo.isActive()));
            dbPromo.setCode(Optional.ofNullable(request.getCode()).orElse(dbPromo.getCode()));
            dbPromo.setMonths(Optional.ofNullable(request.getMonths()).orElse(dbPromo.getMonths()));
            dbPromo.setDescription(Optional.ofNullable(request.getDescription()).orElse(dbPromo.getDescription()));

            objectContext.commitChanges();

            if (request.getEmails() != null && !request.getEmails().isEmpty())
                request.getEmails().forEach(email -> {
                    DbPromoEmail dbPromoEmail = objectContext.newObject(DbPromoEmail.class);
                    dbPromoEmail.setEmail(email);
                    dbPromoEmail.setActive(true);
                    dbPromoEmail.setEmailToPromo(dbPromo);
                });
            objectContext.commitChanges();

            return buildPromoModel(dbPromo);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of promo")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PROMO_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbPromo dbPromo = SelectById.query(DbPromo.class, id).selectFirst(objectContext);
            objectContext.deleteObject(dbPromo);
            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
