import { useAuth0 } from "@auth0/auth0-react";
import { ReactNode, useEffect } from "react";

export default function RequireLogin({ children }: { children: ReactNode }) {
  const { isLoading, isAuthenticated, loginWithRedirect } = useAuth0();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      void loginWithRedirect();
    }
  }, [isLoading, isAuthenticated, loginWithRedirect]);

  if (isLoading || !isAuthenticated) {
    return <div>Loading...</div>;
  }

  return <>{children}</>;
}
