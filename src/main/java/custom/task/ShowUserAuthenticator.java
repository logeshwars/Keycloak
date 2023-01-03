package custom.task;

import custom.authenticators.emailAuth.Constants;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.models.utils.KeycloakModelUtils;


import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class ShowUserAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator, Constants {

    private final Logger logger = Logger.getLogger(ShowUserAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        Response challenge = context.form().createForm(SHOW_USER_FORM_FTL);
        context.challenge(challenge);
    }

    protected UserModel getUser (KeycloakSession session,RealmModel realm,String userName)
    {
        return KeycloakModelUtils.findUserByNameOrEmail(session,realm,userName);
    }
    protected boolean validateForm(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        String userName = formData.getFirst(USERNAME_FIELD);
        String password = formData.getFirst(PASSWORD_FIELD);
        KeycloakSession session = context.getSession();
        RealmModel realm = context.getRealm();
        UserModel user = getUser(session,realm,userName);
        if(user != null)
        {
            return password != null && !password.isEmpty() && user.credentialManager().isValid(UserCredentialModel.password(password));
        }
        return false;
    }

    public Response errorChallenge(AuthenticationFlowContext context, String error, String fileName) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        form.addError(new FormMessage(PASSWORD_FIELD, error));
        return form.createForm(fileName);
    }


    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String,String> formData = context.getHttpRequest().getDecodedFormParameters();
        if(validateForm(context,formData))
        {
            UserModel user = getUser(context.getSession(),context.getRealm(),formData.getFirst(USERNAME_FIELD));
            context.setUser(user);
            LoginFormsProvider userDetailsProvider = context.form();
            Map<String, String> userDetails = new HashMap<>();
            userDetails.put("USERNAME",user.getUsername());
            userDetails.put("FIRST_NAME",user.getFirstName());
            userDetails.put("LAST_NAME",user.getFirstName());
            userDetails.put("EMAIL",user.getEmail());
            userDetails.put("EMAIL_VERIFIED",(user.isEmailVerified()?"Verified":"Not Verified"));
            userDetails.put("ENABLED",(user.isEnabled()?"Enabled":"Disabled"));
            userDetails.put("REALM",context.getRealm().getName());
            userDetailsProvider.setAttribute("userDetails",userDetails);
            Response challenge = userDetailsProvider.createForm(SHOW_USER_DETAILS_FTL);
            context.challenge(challenge);
            return;
        }
        logger.error(INVALID_CREDENTIALS);
        Response challengeResponse = errorChallenge(context, INVALID_CREDENTIALS, SHOW_USER_FORM_FTL);
        context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
    }

    @Override
    public boolean requiresUser() {
        return false;
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
