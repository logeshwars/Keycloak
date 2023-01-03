package custom.requiredactions;

import custom.authenticators.dateofbirth.DOBAuthenticator;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Collections;

public class AddDOB implements RequiredActionProvider, RequiredActionFactory {
    Logger logger = Logger.getLogger(AddDOB.class);
    private final String NAME = "Date of birth required action";
    public static final String ID = "dob-required-action";
    @Override
    public String getDisplayText() {
        return NAME;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {

    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        Response challenge = context.form()
                .setAttribute("username", context.getAuthenticationSession().getAuthenticatedUser().getUsername())
                .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
        context.challenge(challenge);

        context.challenge(context.form().createForm("dob.ftl"));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        MultivaluedMap<String,String> formData = context.getHttpRequest().getDecodedFormParameters();
        String dob = formData.getFirst("dob").toString();
        context.getUser().setAttribute("DOB", Collections.singletonList(dob));
        logger.info(dob);
        context.success();

    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isOneTimeAction() {
        return true;
    }
}
