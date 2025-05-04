import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';  // Import HomePage component
import RequireLogin from './auth/RequireLogin';  // Import HomePage component

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