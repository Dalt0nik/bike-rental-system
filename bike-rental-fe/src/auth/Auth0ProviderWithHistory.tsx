import { Auth0Provider, useAuth0 } from "@auth0/auth0-react";
import { useNavigate } from "react-router-dom";
import { PropsWithChildren, useEffect } from "react";
import { api, setTokenGetter } from "../api/Api";

const domain = import.meta.env.VITE_AUTH0_DOMAIN;
const clientId = import.meta.env.VITE_AUTH0_CLIENT_ID;
const audience = import.meta.env.VITE_AUTH0_AUDIENCE;

interface Auth0AppState {
  returnTo?: string;
}

function Auth0TokenProvider({ children }: PropsWithChildren) {
  const { getAccessTokenSilently, isAuthenticated } = useAuth0();

  useEffect(() => {
    setTokenGetter(async () =>
      await getAccessTokenSilently({
        authorizationParams: {
          audience: audience,
        },
      })
    );
  }, [getAccessTokenSilently]);

  useEffect(() => {
    if (!isAuthenticated) return;

    (async () => {
      try {
        await api.post("/users/register");
      } catch (err) {
        console.error("User registration failed", err);
      }
    })();
  }, [isAuthenticated]);


  return <>{children}</>;
};

export function Auth0ProviderWithHistory({ children }: PropsWithChildren) {
  const navigate = useNavigate();

  const onRedirectCallback = (appState: Auth0AppState | undefined) => {
    navigate(appState?.returnTo || window.location.pathname);
  };

  return (
    <Auth0Provider
      domain={domain}
      clientId={clientId}
      authorizationParams={{
        redirect_uri: window.location.origin,
        audience: audience,
      }}
      onRedirectCallback={onRedirectCallback}
    >
      <Auth0TokenProvider>
        {children}
      </Auth0TokenProvider>
    </Auth0Provider>
  );
};
