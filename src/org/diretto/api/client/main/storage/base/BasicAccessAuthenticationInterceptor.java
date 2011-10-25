package org.diretto.api.client.main.storage.base;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * A {@code BasicAccessAuthenticationInterceptor} can be used to perform a
 * <i>preemptive</i> {@code Basic Access Authentication}. Thus one request and
 * one response can be saved. <br/><br/>
 * 
 * <i>Important:</i> This method works only if the mandatory credentials, which
 * are necessary for the authentication, have been set before.
 * 
 * @author Tobias Schlecht
 */
public class BasicAccessAuthenticationInterceptor implements HttpRequestInterceptor
{
	private static final BasicScheme BASIC_ACCESS_AUTHENTICATION_SCHEME = new BasicScheme();

	@Override
	public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException
	{
		AuthState authState = (AuthState) httpContext.getAttribute(ClientContext.TARGET_AUTH_STATE);

		if(authState.getAuthScheme() == null)
		{
			HttpHost httpHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

			CredentialsProvider credentialsProvider = (CredentialsProvider) httpContext.getAttribute(ClientContext.CREDS_PROVIDER);

			Credentials credentials = credentialsProvider.getCredentials(new AuthScope(httpHost.getHostName(), httpHost.getPort()));

			if(credentials == null)
			{
				throw new HttpException("No credentials for the preemptive Basic Access Authentication were found.");
			}

			authState.setAuthScheme(BASIC_ACCESS_AUTHENTICATION_SCHEME);
			authState.setCredentials(credentials);
		}
	}
}
