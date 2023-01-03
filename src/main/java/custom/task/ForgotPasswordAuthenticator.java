package custom.task;

import custom.authenticators.emailAuth.Constants;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgotPasswordAuthenticator implements Authenticator, Constants {

    public final Logger logger = Logger.getLogger(ForgotPasswordAuthenticator.class);
    public int OTP_LENGTH = 4;

    public void sendMail(AuthenticationFlowContext context, String otp, String email) {

        KeycloakSession session = context.getSession();
        Map<String,Object> bodyAttributes = new HashMap<>();
        bodyAttributes.put("code",otp);
        bodyAttributes.put("realm",context.getRealm().getName());
        logger.info("sending email");
        try {
            session
                    .getProvider(EmailTemplateProvider.class)
                    .setAuthenticationSession(context.getAuthenticationSession())
                    .setRealm(context.getRealm())
                    .setUser(context.getUser())
                    .send("emailOtpSubject","email-otp.ftl",bodyAttributes);
             } catch (EmailException e) {
            logger.info(e.getCause());
            Response emailFailedResponse = errorChallenge(context,EMAIL_SENT_FAILURE,FORGOT_PASSWORD_FTL,"");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, emailFailedResponse);
            return;
        }

    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel sessionModel = context.getAuthenticationSession();
        LoginFormsProvider challengeForm = context.form();
        if(sessionModel.getAuthNote("otp")== null) {
            String otp = generateRandomOTP(OTP_LENGTH);
            sendMail(context,otp, context.getUser().getEmail());
            logger.info("sent otp"+otp);
            sessionModel.setAuthNote("otp", otp);
            challengeForm.setInfo("Otp sent to your email");
        }
        else {
            challengeForm.setInfo("Otp already sent to your email");
        }
        Response challenge =  challengeForm.createForm(FORGOT_PASSWORD_FTL);
        context.challenge(challenge);
    }
    public boolean validateOtp(String otp,String formOtp,int len)
    {
        return otp.length()==len&&otp.equals(formOtp);
    }
    @Override
    public void action(AuthenticationFlowContext context) {

        MultivaluedMap<String,String> formData= context.getHttpRequest().getDecodedFormParameters();
        AuthenticationSessionModel sessionModel = context.getAuthenticationSession();
        String otp =  sessionModel.getAuthNote("otp");
        String formOtp = formData.getFirst("otp");
        UserModel userModel  = context.getUser();
        LoginFormsProvider challengeForm = context.form();

        if (formData.containsKey(RESEND_FIELD)) {
            logger.info("Resending email");
            String resendOtp = generateRandomOTP(OTP_LENGTH);
            sessionModel.setAuthNote("otp", resendOtp);
            sendMail(context,resendOtp, context.getUser().getEmail());
            challengeForm.setInfo("Otp sent to your email");
            context.challenge(challengeForm.createForm(FORGOT_PASSWORD_FTL));
            logger.info("Resented otp"+resendOtp);
            return;
        }

        if(validateOtp(otp,formOtp,OTP_LENGTH))
        {
            logger.info("Otp validated successfully");
            userModel.addRequiredAction(NewPasswordFactory.PROVIDER_ID);
            context.success();
            return;
        }

        logger.info(OTP_NOT_VALID);
        Response challengeResponse = errorChallenge(context, OTP_NOT_VALID, FORGOT_PASSWORD_FTL,"otp");
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

    public String generateRandomOTP(int len)
    {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(RANDOM_NUM.charAt(rnd.nextInt(RANDOM_NUM.length())));
        return sb.toString();
    }

    public Response errorChallenge(AuthenticationFlowContext context, String error, String fileName,String field) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        form.addError(new FormMessage(field, error));
        return form.createForm(fileName);
    }

}
