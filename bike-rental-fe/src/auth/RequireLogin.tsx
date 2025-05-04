import { useAuth0 } from "@auth0/auth0-react";
import { useEffect } from "react";

const RequireLogin = ({ children }: { children: React.ReactNode }) => {
    const { isLoading, isAuthenticated, loginWithRedirect } = useAuth0();

    useEffect(() => {
        if (!isLoading && !isAuthenticated) {
            loginWithRedirect();
        }
    }, [isLoading, isAuthenticated, loginWithRedirect]);

    if (isLoading || !isAuthenticated) {
        return <div>Loading...</div>; // Show something while waiting
    }

    return <>{children}</>;
};

export default RequireLogin;