package custom.task;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class NewPasswordFactory implements RequiredActionFactory {
    public static String PROVIDER_ID = "newPasswordRequiredAction";
    public final String DISPLAY_TYPE="New password required Action";

    @Override
    public String getDisplayText() {
        return DISPLAY_TYPE;
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return new NewPassword();
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
        return PROVIDER_ID;
    }
    @Override
    public boolean isOneTimeAction() {
        return true;
    }
}
