package custom.authenticators.emailAuth;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class EmailAuthenticator implements Authenticator,Constants {
    private static final Logger logger = Logger.getLogger(EmailAuthenticator.class);

    private final EmailAuthUtil emailAuthUtil = new EmailAuthUtil();

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        Response challenge = context.form().createForm(EMAIL_FORM_FTL);
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String formEmail = formData.getFirst(EMAIL_FIELD);
        if (!formEmail.matches(VALIDATE_EMAIL_REGEX)) {
            logger.error("invalid email");
            Response challengeResponse = emailAuthUtil.errorChallenge(context, EMAIL_NOT_VALID, EMAIL_FORM_FTL);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
            return;
        }
        String randomPassword = emailAuthUtil.generateRandomPassword(6);
        logger.info("random password"+randomPassword);
        emailAuthUtil.setUserByEmail(context, formData);
        UserModel userModel = context.getUser();
        if (Boolean.FALSE.equals(emailAuthUtil.checkTempPassword(userModel))) {
            emailAuthUtil.sendMail(context, randomPassword, formEmail);
            emailAuthUtil.setTempPassword(userModel, randomPassword);
            context.form().setInfo(EMAIL_SENT);
        } else {
            context.form().setInfo(EMAIL_ALREADY_SENT);
        }

        context.success();
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
