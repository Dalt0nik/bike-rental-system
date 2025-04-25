import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';  // Import HomePage component

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<HomePage />} />  {/* HomePage as the default route */}
            </Routes>
        </Router>
    );
}

export default App;