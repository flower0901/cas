package org.apereo.cas.adaptors.azure;

import net.phonefactor.pfsdk.PFAuth;
import net.phonefactor.pfsdk.PFAuthParams;
import net.phonefactor.pfsdk.PFAuthResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

/**
 * An authentication handler that uses the token provided
 * to authenticator against azure authN for MFA.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class AzureAuthenticatorAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    private final PFAuth azureAuthenticatorInstance;
    private final AzureAuthenticatorAuthenticationRequestBuilder authenticationRequestBuilder;

    public AzureAuthenticatorAuthenticationHandler(final PFAuth azureAuthenticatorInstance,
                                                   final AzureAuthenticatorAuthenticationRequestBuilder builder) {
        this.azureAuthenticatorInstance = azureAuthenticatorInstance;
        this.authenticationRequestBuilder = builder;
    }

    @Override
    protected HandlerResult doAuthentication(final Credential credential) throws GeneralSecurityException, PreventedException {
        try {
            final AzureAuthenticatorTokenCredential c = (AzureAuthenticatorTokenCredential) credential;
            final RequestContext context = RequestContextHolder.getRequestContext();
            final Principal principal = WebUtils.getAuthentication(context).getPrincipal();

            logger.debug("Received principal id [{}]", principal.getId());
            final PFAuthParams params = authenticationRequestBuilder.build(principal, c);
            final PFAuthResult r = azureAuthenticatorInstance.authenticate(params);

            if (r.getAuthenticated()) {
                return createHandlerResult(c, principalFactory.createPrincipal(principal.getId()), null);
            }
            logger.error("Authentication failed. Call status: [{}]-[{}]. Error: [{}]", r.getCallStatus(),
                    r.getCallStatusString(), r.getMessageError());

        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
        throw new FailedLoginException("Failed to authenticate user");
    }

    @Override
    public boolean supports(final Credential credential) {
        return AzureAuthenticatorTokenCredential.class.isAssignableFrom(credential.getClass());
    }
}
