import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { authApi } from '../api/auth';
import { Zap } from 'lucide-react';
import toast from 'react-hot-toast';

export default function Login() {
    const [isRegister, setIsRegister] = useState(false);
    const [form, setForm] = useState({ fullName: '', email: '', password: '', roles: ['ADMIN'] });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login: authLogin } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            let result;
            if (isRegister) {
                result = await authApi.register(form);
            } else {
                result = await authApi.login(form.email, form.password);
            }
            authLogin(result, result.accessToken, result.refreshToken);
            toast.success(isRegister ? 'Account created!' : 'Welcome back!');
            navigate('/');
        } catch (err) {
            setError(err.response?.data?.message || err.message || 'Login failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-page">
            <div className="login-card">
                <div className="login-logo">
                    <div className="logo-icon"><Zap size={28} color="white" /></div>
                    <h1>RepairBro</h1>
                    <p>{isRegister ? 'Create your account' : 'Sign in to your account'}</p>
                </div>

                {error && <div className="login-error">{error}</div>}

                <form onSubmit={handleSubmit}>
                    {isRegister && (
                        <div className="form-group">
                            <label className="form-label">Full Name</label>
                            <input
                                className="form-input"
                                placeholder="John Doe"
                                value={form.fullName}
                                onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                                required
                            />
                        </div>
                    )}
                    <div className="form-group">
                        <label className="form-label">Email</label>
                        <input
                            className="form-input"
                            type="email"
                            placeholder="admin@repairbro.com"
                            value={form.email}
                            onChange={(e) => setForm({ ...form, email: e.target.value })}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Password</label>
                        <input
                            className="form-input"
                            type="password"
                            placeholder="••••••••"
                            value={form.password}
                            onChange={(e) => setForm({ ...form, password: e.target.value })}
                            required
                        />
                    </div>
                    {isRegister && (
                        <div className="form-group">
                            <label className="form-label">Role</label>
                            <select
                                className="form-select"
                                value={form.roles[0]}
                                onChange={(e) => setForm({ ...form, roles: [e.target.value] })}
                            >
                                <option value="ADMIN">Admin</option>
                                <option value="BRANCH_MANAGER">Branch Manager</option>
                                <option value="TECHNICIAN">Technician</option>
                                <option value="FRANCHISEE">Franchisee</option>
                                <option value="CUSTOMER">Customer</option>
                            </select>
                        </div>
                    )}
                    <button className="btn btn-primary" style={{ width: '100%', justifyContent: 'center', marginTop: 8 }} disabled={loading}>
                        {loading ? 'Please wait...' : (isRegister ? 'Create Account' : 'Sign In')}
                    </button>
                </form>

                <p style={{ textAlign: 'center', marginTop: 20, fontSize: 13, color: 'var(--text-muted)' }}>
                    {isRegister ? 'Already have an account?' : "Don't have an account?"}{' '}
                    <a href="#" onClick={(e) => { e.preventDefault(); setIsRegister(!isRegister); setError(''); }}>
                        {isRegister ? 'Sign In' : 'Register'}
                    </a>
                </p>
            </div>
        </div>
    );
}
