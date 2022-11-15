package Onol.onol.Jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomEmailPasswordAuthToken extends AbstractAuthenticationToken {
    private final Object principal;

    private Object credentials;

    public CustomEmailPasswordAuthToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public CustomEmailPasswordAuthToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorites){
        super(authorites);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);  //must use super, as we override
    }

    @Override
    public Object getCredentials(){
        return this.credentials;
    }

    @Override
    public Object getPrincipal(){
        return this.principal;
    }
}
