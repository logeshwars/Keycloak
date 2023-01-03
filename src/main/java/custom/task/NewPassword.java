package custom.task;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.PasswordCredentialProvider;
import org.keycloak.credential.PasswordCredentialProviderFactory;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.ModelException;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

public class NewPassword implements RequiredActionProvider {
    private static final Logger logger = Logger.getLogger(NewPassword.class);

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        int daysToExpirePassword = context.getRealm().getPasswordPolicy().getDaysToExpirePassword();
        if (daysToExpirePassword != -1) {
            PasswordCredentialProvider passwordProvider = (PasswordCredentialProvider) context.getSession().getProvider(CredentialProvider.class, PasswordCredentialProviderFactory.PROVIDER_ID);
            CredentialModel password = passwordProvider.getPassword(context.getRealm(), context.getUser());
            if (password != null) {
                if (password.getCreatedDate() == null) {
                    context.getUser().addRequiredAction(NewPasswordFactory.PROVIDER_ID);
                    logger.debug("User need to update the password");
                } else {
                    long timeElapsed = Time.toMillis(Time.currentTime()) - password.getCreatedDate();
                    long timeToExpire = TimeUnit.DAYS.toMillis(daysToExpirePassword);
                    if (timeElapsed > timeToExpire) {
                        context.getUser().addRequiredAction(NewPasswordFactory.PROVIDER_ID);
                        logger.debug("User need to update the password");
                    }
                }
            }
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        LoginFormsProvider form = context.form().setExecution(NewPasswordFactory.PROVIDER_ID);
        form.setAttribute(Details.USERNAME, context.getAuthenticationSession().getAuthenticatedUser().getUsername());
        Response challenge = form.createForm("login-update-password.ftl");
        context.challenge(challenge);
    }

    @Override
    public void processAction(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String newPassword = formData.getFirst("password-new");
        String confirmPassword = formData.getFirst("password-confirm");
        UserModel userModel = context.getUser();
        EventBuilder event = context.getEvent();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        EventBuilder errorEvent = event.clone().event(EventType.UPDATE_PASSWORD_ERROR).
                client(authSession.getClient()).
                user(authSession.getAuthenticatedUser());

        if (newPassword.isEmpty()) {
            logger.info("password field is empty");
            Response challenge = responseCreator(context,authSession.getAuthenticatedUser().getUsername(),Details.USERNAME,Validation.FIELD_PASSWORD,Messages.MISSING_PASSWORD);
            context.challenge(challenge);
            errorEvent.error(Errors.PASSWORD_MISSING);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            logger.info("password and confirm password is same");
            Response challenge = responseCreator(context,authSession.getAuthenticatedUser().getUsername(),Details.USERNAME,Validation.FIELD_PASSWORD_CONFIRM,Messages.NOTMATCH_PASSWORD);
            context.challenge(challenge);
            errorEvent.error(Errors.PASSWORD_CONFIRM_ERROR);
            return;
        }
        try {
            userModel.credentialManager().updateCredential(UserCredentialModel.password(newPassword));
            context.success();
        } catch (ModelException me) {
            errorEvent.detail(Details.REASON, me.getMessage()).error(Errors.PASSWORD_REJECTED);

            Response challenge = context.form()
                    .setAttribute(Details.USERNAME, authSession.getAuthenticatedUser().getUsername())
                    .setError(me.getMessage(), me.getParameters())
                    .createForm("login-update-password.ftl");
            context.challenge(challenge);

        } catch (Exception e) {
            errorEvent.detail(Details.REASON, e.getMessage()).error(Errors.PASSWORD_REJECTED);
            Response challenge = context.form()
                    .setAttribute(Details.USERNAME, authSession.getAuthenticatedUser().getUsername())
                    .setError(e.getMessage())
                    .createForm("login-update-password.ftl");
            context.challenge(challenge);
        }

    }

    @Override
    public void close() {

    }
    public Response responseCreator(RequiredActionContext context,String atrValue,String atrKey ,String field,String msg)
    {
       return context.form()
                .setAttribute(atrKey, atrValue)
                .addError(new FormMessage(field, msg))
                .createForm("login-update-password.ftl");
    }

}
