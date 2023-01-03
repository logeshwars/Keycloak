package custom.authenticators.emailAuth;

public interface Constants {

    // FTL
    public final String EMAIL_FORM_FTL = "emailForm.ftl";
    public final String PASSWORD_FORM_FTL = "passwordForm.ftl";

    public final String SHOW_USER_FORM_FTL = "showUser.ftl";
    public final String SHOW_USER_DETAILS_FTL = "userDetails.ftl";

    public final String FORGOT_PASSWORD_FTL = "forgotPassword.ftl";

    //Regex
    public static final String VALIDATE_EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    //Fields
    public final String EMAIL_FIELD = "email";
    public final String PASSWORD_FIELD = "password";

    public final String USERNAME_FIELD = "username";
    public final String RESEND_FIELD = "resend-email";

    //Errors
    public final String EMAIL_NOT_VALID = "Email is  not valid";
    public final String PASSWORD_NOT_VALID = "password is  not valid";
    public final String EMAIL_SENT_FAILURE = "Couldn't send email,Try again";
    public final String OTP_NOT_VALID = "Otp is not valid";

    //Info

    public final String EMAIL_SENT = "Temporary password has been send to your email";
    public final String EMAIL_ALREADY_SENT = "Temporary password has been send to your email already";
    public final String FINISH_EMAIL_SETUP = "Finish the setup using Temporary Password has been send to your email";
    //Email

    public final String EMAIL_TEXT_BODY = "Your temporary password is ";
    public final String EMAIL_SUBJECT = "Login password";

    public final String RANDOM_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi";

    public final String RANDOM_NUM = "0123456789";
    public static final String TEMP_PASSWORD = "temp-password";
    public static final String USER_LABEL = "Temporary Password";

    //user details

    public static final String INVALID_CREDENTIALS = "user name or password is in valid";

}