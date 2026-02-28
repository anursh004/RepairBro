import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './hooks/useAuth';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import TicketList from './pages/tickets/TicketList';
import TicketDetail from './pages/tickets/TicketDetail';
import CustomerList from './pages/customers/CustomerList';
import BranchList from './pages/branches/BranchList';
import InventoryPage from './pages/inventory/InventoryPage';
import BillingPage from './pages/billing/BillingPage';
import DiagnosticsPage from './pages/diagnostics/DiagnosticsPage';
import FranchisePage from './pages/franchises/FranchisePage';
import SlaPage from './pages/sla/SlaPage';
import NotificationCenter from './pages/notifications/NotificationCenter';
import SimulationPage from './pages/simulation/SimulationPage';
import UserManagement from './pages/users/UserManagement';
import TechnicianList from './pages/technicians/TechnicianList';
import GroupManagement from './pages/groups/GroupManagement';

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

function RoleRoute({ section, children }) {
  const { hasAccess } = useAuth();
  if (!hasAccess(section)) {
    return (
      <div className="empty-state">
        <h3 className="empty-state-title">🔒 Access Denied</h3>
        <p>You don't have permission to view this page.</p>
      </div>
    );
  }
  return children;
}

function AppRoutes() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <Login />} />
      <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<RoleRoute section="dashboard"><Dashboard /></RoleRoute>} />
        <Route path="tickets" element={<RoleRoute section="tickets"><TicketList /></RoleRoute>} />
        <Route path="tickets/:id" element={<RoleRoute section="tickets"><TicketDetail /></RoleRoute>} />
        <Route path="customers" element={<RoleRoute section="customers"><CustomerList /></RoleRoute>} />
        <Route path="branches" element={<RoleRoute section="branches"><BranchList /></RoleRoute>} />
        <Route path="technicians" element={<RoleRoute section="technicians"><TechnicianList /></RoleRoute>} />
        <Route path="invoices" element={<RoleRoute section="billing"><BillingPage /></RoleRoute>} />
        <Route path="estimates" element={<RoleRoute section="billing"><BillingPage /></RoleRoute>} />
        <Route path="diagnostics" element={<RoleRoute section="diagnostics"><DiagnosticsPage /></RoleRoute>} />
        <Route path="inventory" element={<RoleRoute section="inventory"><InventoryPage /></RoleRoute>} />
        <Route path="franchises" element={<RoleRoute section="franchises"><FranchisePage /></RoleRoute>} />
        <Route path="sla" element={<RoleRoute section="sla"><SlaPage /></RoleRoute>} />
        <Route path="notifications" element={<RoleRoute section="notifications"><NotificationCenter /></RoleRoute>} />
        <Route path="simulation" element={<RoleRoute section="simulation"><SimulationPage /></RoleRoute>} />
        <Route path="groups" element={<RoleRoute section="groups"><GroupManagement /></RoleRoute>} />
        <Route path="users" element={<RoleRoute section="users"><UserManagement /></RoleRoute>} />
      </Route>
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
        <Toaster
          position="top-right"
          toastOptions={{
            style: {
              background: '#1e293b',
              color: '#f1f5f9',
              border: '1px solid rgba(255,255,255,0.08)',
              borderRadius: '10px',
              fontSize: '13px',
            },
          }}
        />
      </AuthProvider>
    </BrowserRouter>
  );
}
