package custom.authenticators.emailAuth;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.hash.Pbkdf2PasswordHashProvider;
import org.keycloak.credential.hash.Pbkdf2Sha256PasswordHashProviderFactory;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.stream.Stream;

public class EmailAuthUtil implements Constants {

     private static Logger logger = Logger.getLogger(EmailAuthUtil.class);
     public void setUserByEmail(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        String email = formData.getFirst(EMAIL_FIELD);
        UserModel userModel = KeycloakModelUtils.findUserByNameOrEmail(context.getSession(), context.getRealm(), email);
        if (userModel == null) {
            createNewUser(context, formData);
            context.form().setInfo(FINISH_EMAIL_SETUP);
            return;
        }
        context.setUser(userModel);
    }

    public void createNewUser(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        KeycloakSession session = context.getSession();
        UserProfileProvider profileProvider = session.getProvider(UserProfileProvider.class);
        UserProfile profile = profileProvider.create(UserProfileContext.REGISTRATION_USER_CREATION, formData);
        UserModel userModel = profile.create();
        userModel.setEnabled(true);
        context.setUser(userModel);
    }

    public Boolean checkTempPassword(UserModel userModel) {
        CredentialModel randPass = userModel.credentialManager().getStoredCredentialByNameAndType(USER_LABEL, TEMP_PASSWORD);
        return randPass != null;
    }

    public void sendMail(AuthenticationFlowContext context, String randomPassword, String email) {
        try {
            DefaultEmailSenderProvider emailSender = new DefaultEmailSenderProvider(context.getSession());
            emailSender.send(context.getRealm().getSmtpConfig(), email, EMAIL_SUBJECT, EMAIL_TEXT_BODY + randomPassword, "");
        } catch (EmailException e) {
           Response emailFailedResponse = errorChallenge(context,EMAIL_SENT_FAILURE,EMAIL_FORM_FTL);
           context.challenge(emailFailedResponse);
        }

    }

    public Response errorChallenge(AuthenticationFlowContext context, String error, String fileName) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        form.addError(new FormMessage(EMAIL_FIELD, error));
        return form.createForm(fileName);
    }

    public Response infoChallenge(AuthenticationFlowContext context, String info, String fileName) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        context.form().setInfo(info);
        return form.createForm(fileName);
    }

    public String generateRandomPassword(int len) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(RANDOM_CHARS.charAt(rnd.nextInt(RANDOM_CHARS.length())));
        return sb.toString();
    }

    public String generateRandomOTP(int len)
    {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(RANDOM_NUM.charAt(rnd.nextInt(RANDOM_NUM.length())));
        return sb.toString();
    }


    public CredentialModel getTempPassword(UserModel userModel) {
        return userModel.credentialManager().getStoredCredentialByNameAndType(USER_LABEL, TEMP_PASSWORD);
    }

    public void setTempPassword(UserModel userModel, String randomPassword) {
        PasswordCredentialModel pcrModel = encode(randomPassword,userModel);
        if (Boolean.TRUE.equals(checkTempPassword(userModel))) {
            userModel.credentialManager().updateStoredCredential(pcrModel);
            return;
        }
        userModel.credentialManager().createStoredCredential(pcrModel);
    }
    public PasswordCredentialModel encode(String password,UserModel userModel)
    {
        Pbkdf2PasswordHashProvider passwordHashProvider = new Pbkdf2PasswordHashProvider(Pbkdf2Sha256PasswordHashProviderFactory.ID,Pbkdf2Sha256PasswordHashProviderFactory.PBKDF2_ALGORITHM ,Pbkdf2Sha256PasswordHashProviderFactory.DEFAULT_ITERATIONS);
        PasswordCredentialModel pcrModel= passwordHashProvider.encodedCredential(password,Pbkdf2Sha256PasswordHashProviderFactory.DEFAULT_ITERATIONS);
        String id = getTempPassword(userModel).getId();
        pcrModel.setType(TEMP_PASSWORD);
        pcrModel.setUserLabel(USER_LABEL);
        pcrModel.setCreatedDate(Time.currentTimeMillis());
        pcrModel.setId(id);
        return pcrModel;
    }
    public boolean verifyPassword(String password, UserModel userModel)
    {
        Pbkdf2PasswordHashProvider hashProvider = new Pbkdf2PasswordHashProvider(Pbkdf2Sha256PasswordHashProviderFactory.ID,Pbkdf2Sha256PasswordHashProviderFactory.PBKDF2_ALGORITHM ,Pbkdf2Sha256PasswordHashProviderFactory.DEFAULT_ITERATIONS);
        boolean isValid = hashProvider.verify(password,PasswordCredentialModel.createFromCredentialModel(getTempPassword(userModel)));
        return isValid;
    }

}
