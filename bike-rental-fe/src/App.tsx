import { Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import RequireLogin from "./auth/RequireLogin";
import { Toaster } from "react-hot-toast";

function App() {
  return (
    <>
      <Toaster position="bottom-center" />
      <RequireLogin>
        <Routes>
          <Route path="/" element={<HomePage />} />
        </Routes>
      </RequireLogin>
    </>
  );
}

export default App;
