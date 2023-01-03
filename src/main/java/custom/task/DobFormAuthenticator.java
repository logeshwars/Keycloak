package custom.task;

import org.keycloak.authentication.FormAuthenticator;
import org.keycloak.authentication.FormContext;
import org.keycloak.forms.login.LoginFormsProvider;

import javax.ws.rs.core.Response;

public class DobFormAuthenticator implements FormAuthenticator {
    @Override
    public Response render(FormContext context, LoginFormsProvider form) {
        return null;
    }

    @Override
    public void close() {

    }
}
