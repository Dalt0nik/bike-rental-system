import { Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import RequireLogin from "./auth/RequireLogin";

function App() {
  return (
    <RequireLogin>
      <Routes>
        <Route path="/" element={<HomePage />} />
      </Routes>
    </RequireLogin>
  );
}

export default App;
