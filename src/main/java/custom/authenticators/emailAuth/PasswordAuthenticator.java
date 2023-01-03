package custom.authenticators.emailAuth;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class PasswordAuthenticator implements Authenticator, Constants {

    private final Logger logger = Logger.getLogger(PasswordAuthenticator.class);
    private final EmailAuthUtil emailAuthUtil = new EmailAuthUtil();

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        PasswordPolicy passwordPolicy = context.getRealm().getPasswordPolicy();
        Map<String, String> policies = new HashMap<>();
        for (String policy : passwordPolicy.getPolicies()) {
            Object config = passwordPolicy.getPolicyConfig(policy);
            policies.put(policy, config != null ? config.toString() : "null");
        }
        LoginFormsProvider formsProvider = context.form().setAttribute("password_policy", policies);
        Response challenge = formsProvider.createForm(PASSWORD_FORM_FTL);
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        UserModel userModel = context.getUser();
        CredentialModel randomPassword = emailAuthUtil.getTempPassword(userModel);
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String formPassword = formData.getFirst(PASSWORD_FIELD);
        if (formData.containsKey(RESEND_FIELD)) {
            logger.info("Resending email");
            String newRandomPassword = emailAuthUtil.generateRandomPassword(6);
            emailAuthUtil.sendMail(context, newRandomPassword, context.getUser().getEmail());
            emailAuthUtil.setTempPassword(userModel, newRandomPassword);
            Response challengeResponse = emailAuthUtil.infoChallenge(context, EMAIL_SENT, PASSWORD_FORM_FTL);
            context.challenge(challengeResponse);
            return;
        }
        if (userModel.credentialManager().isValid(UserCredentialModel.password(formPassword))) {
            context.success();
            return;
        } else if (emailAuthUtil.verifyPassword(formPassword,userModel))
        {
            userModel.credentialManager().removeStoredCredentialById(randomPassword.getId());
            userModel.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
            context.success();
            return;
        }
        Response challengeResponse = emailAuthUtil.errorChallenge(context, PASSWORD_NOT_VALID, PASSWORD_FORM_FTL);
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
