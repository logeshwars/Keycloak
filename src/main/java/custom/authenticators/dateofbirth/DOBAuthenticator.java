package custom.authenticators.dateofbirth;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class DOBAuthenticator implements Authenticator {

    Logger logger = Logger.getLogger(DOBAuthenticator.class);
    @Override
    public void authenticate(AuthenticationFlowContext context)
    {
        Response challenge = context.form().createForm("dob.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap <String,String> formData = context.getHttpRequest().getDecodedFormParameters();
        String userDob = context.getUser().getFirstAttribute("DOB");
        String formDob = formData.getFirst("dob").toString();
        if(userDob!=null && userDob.equals(formDob)) {
           context.success();
        }
        else
        {
            Response challengeResponse = challenge(context, "Date of birth is not valid", "dob.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
            context.challenge(challengeResponse);
        }
    }

    @Override
    public boolean requiresUser() {
        return false;
    }
    private Response challenge(AuthenticationFlowContext context, String error, String fileName) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        form.addError(new FormMessage("dob", error));
        return form.createForm(fileName);
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {

    }
}
