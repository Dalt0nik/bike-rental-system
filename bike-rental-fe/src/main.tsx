import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {BrowserRouter} from "react-router-dom";
import {Auth0ProviderWithHistory} from "./auth/Auth0ProviderWithHistory.tsx";
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient();

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <BrowserRouter>
            <Auth0ProviderWithHistory>
                <QueryClientProvider client={queryClient}>
                    <App />
                </QueryClientProvider>
            </Auth0ProviderWithHistory>
        </BrowserRouter>
    </StrictMode>
);