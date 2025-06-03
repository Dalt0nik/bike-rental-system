import { useAuth0 } from "@auth0/auth0-react";
import { useState } from "react";
import { Menu, X, Receipt, Map } from "lucide-react";
import { Link } from "react-router-dom";

export default function Header() {
  const { user, isAuthenticated, logout } = useAuth0();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const toggleMenu = () => setIsMenuOpen(v => !v);

  return (
    <>
      <header className="bg-blue-main text-white h-[60px] w-full flex items-center justify-between px-4 shadow-md z-50">
        {/* title */}
        <Link to="/" className="font-bold text-base sm:text-xl whitespace-nowrap hover:text-blue-50 transition-colors">
          Bike Rental Map
        </Link>

        {/* inline nav ≥601px */}
        {isAuthenticated && (
          <div className="hidden sm:flex items-center gap-4">
            <Link 
              to="/" 
              className="flex items-center gap-1 text-sm hover:text-blue-50 transition-colors"
            >
              <Map size={16} />
              Map
            </Link>
            <Link 
              to="/expenses" 
              className="flex items-center gap-1 text-sm hover:text-blue-50 transition-colors"
            >
              <Receipt size={16} />
              Expenses
            </Link>
            <span className="text-sm md:text-base">
              Welcome, {user?.email}
            </span>
            <button
              onClick={() => void logout({ logoutParams: { returnTo: window.location.origin } })}
              className="bg-white text-blue-main py-1.5 px-6 rounded font-bold text-sm hover:bg-blue-50 transition-colors"
            >
              Log Out
            </button>
          </div>
        )}

        {/* burger ≤600px */}
        <button
          onClick={toggleMenu}
          aria-label="Toggle menu"
          className="block sm:hidden ml-auto mr-2 p-1.5"
        >
          {isMenuOpen ? (
            <X size={24} className="text-white" />
          ) : (
            <Menu size={24} className="text-white" />
          )}
        </button>
      </header>

      {isAuthenticated && (
        <div
          className={`
            fixed top-[60px] right-0 h-screen
            w-[80%] max-w-xs p-6
            bg-white shadow-lg
            transform transition-transform duration-300 ease-in-out
            flex sm:hidden flex-col gap-6
            ${isMenuOpen ? "translate-x-0" : "translate-x-full"}
          `}
          style={{ zIndex: 2000 }}
        >
          <div className="text-lg font-medium text-blue-main">
            Welcome, {user?.email}
          </div>
          
          <Link 
            to="/" 
            onClick={() => setIsMenuOpen(false)}
            className="flex items-center gap-2 text-blue-main hover:text-blue-darker transition-colors"
          >
            <Map size={18} />
            View Map
          </Link>
          
          <Link 
            to="/expenses" 
            onClick={() => setIsMenuOpen(false)}
            className="flex items-center gap-2 text-blue-main hover:text-blue-darker transition-colors"
          >
            <Receipt size={18} />
            View Expenses
          </Link>
          
          <button
            onClick={() =>
              void logout({ logoutParams: { returnTo: window.location.origin } })
            }
            className="bg-blue-main text-white py-2 px-6 rounded font-bold w-full hover:bg-blue-dark transition-colors"
          >
            Log Out
          </button>
        </div>
      )}
    </>
  );
}