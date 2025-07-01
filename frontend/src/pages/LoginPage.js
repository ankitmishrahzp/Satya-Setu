import React, { useState } from 'react';
import { Card, TextField, Button, Typography, Box, Link, Alert } from '@mui/material';
import SportsCricketIcon from '@mui/icons-material/SportsCricket';
import { useNavigate } from 'react-router-dom';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    try {
      // Demo credentials for testing
      if ((username === 'demo' && password === 'demo') || 
          (username === 'admin' && password === 'admin') ||
          (username === 'user' && password === 'password') ||
          (username === 'test' && password === 'test')) {
        navigate('/dashboard');
      } else {
        setError('Invalid username or password. Try: demo/demo, admin/admin, user/password, or test/test');
      }
    } catch (err) {
      setError('Login failed. Please try again.');
    }
  };

  return (
    <Box display="flex" justifyContent="center" alignItems="center" minHeight="70vh">
      <Card sx={{ maxWidth: 400, p: 4, boxShadow: 3 }}>
        <Box display="flex" alignItems="center" justifyContent="center" mb={2}>
          <SportsCricketIcon color="primary" sx={{ fontSize: 40, mr: 1 }} />
          <Typography variant="h5" fontWeight={700} color="primary">
            BoundaryBox
          </Typography>
        </Box>
        <Typography variant="subtitle1" align="center" color="text.secondary" mb={2}>
          Secure the match like Surya did
        </Typography>
        <Alert severity="info" sx={{ mb: 2 }}>
          Demo credentials: <strong>demo</strong> / <strong>demo</strong>
        </Alert>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <form onSubmit={handleLogin}>
          <TextField
            label="Username"
            fullWidth
            margin="normal"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
          />
          <TextField
            label="Password"
            type="password"
            fullWidth
            margin="normal"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            sx={{ mt: 2, fontWeight: 700 }}
          >
            Login
          </Button>
        </form>
        <Typography variant="body2" align="center" sx={{ mt: 2 }}>
          Don't have an account?{' '}
          <Link href="/register" underline="hover" color="primary">
            Register
          </Link>
        </Typography>
      </Card>
    </Box>
  );
};

export default LoginPage; 