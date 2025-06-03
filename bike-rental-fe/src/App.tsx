import { Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import ExpensesPage from "./pages/ExpensesPage";
import RequireLogin from "./auth/RequireLogin";
import { Toaster } from "react-hot-toast";

function App() {
  return (
    <>
      <Toaster position="bottom-center" />
      <RequireLogin>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/expenses" element={<ExpensesPage />} />
        </Routes>
      </RequireLogin>
    </>
  );
}

export default App;
