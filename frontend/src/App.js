import React, { useState, useEffect } from 'react';
import {
  ThemeProvider,
  createTheme,
  CssBaseline,
  Box,
  AppBar,
  Toolbar,
  Typography,
  Container,
  Paper,
  TextField,
  Button,
  Card,
  CardContent,
  Grid,
  Chip,
  CircularProgress,
  Alert,
  Tabs,
  Tab,
  List,
  ListItem,
  ListItemText,
  Divider,
  IconButton,
  Drawer,
  ListItemIcon,
  Switch,
  FormControlLabel
} from '@mui/material';
import {
  Security,
  Language,
  History,
  Analytics,
  Menu,
  Close,
  CheckCircle,
  Cancel,
  Warning,
  Info,
  Translate,
  Speed,
  Psychology
} from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import axios from 'axios';

// Create theme
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    h4: {
      fontWeight: 600,
    },
  },
});

function App() {
  const [activeTab, setActiveTab] = useState(0);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [darkMode, setDarkMode] = useState(false);
  const [newsTitle, setNewsTitle] = useState('');
  const [newsContent, setNewsContent] = useState('');
  const [sourceUrl, setSourceUrl] = useState('');
  const [author, setAuthor] = useState('');
  const [selectedLanguage, setSelectedLanguage] = useState('auto');
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisResult, setAnalysisResult] = useState(null);
  const [error, setError] = useState('');
  const [analysisHistory, setAnalysisHistory] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [supportedLanguages, setSupportedLanguages] = useState([]);

  // Sample data for charts
  const chartData = [
    { name: 'Real News', value: 65, color: '#4caf50' },
    { name: 'Fake News', value: 35, color: '#f44336' },
  ];

  const confidenceData = [
    { name: 'High', value: 45, color: '#4caf50' },
    { name: 'Medium', value: 35, color: '#ff9800' },
    { name: 'Low', value: 20, color: '#f44336' },
  ];

  useEffect(() => {
    fetchSupportedLanguages();
    fetchStatistics();
    fetchAnalysisHistory();
  }, []);

  const fetchSupportedLanguages = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/languages');
      setSupportedLanguages(Object.values(response.data.languages));
    } catch (error) {
      console.error('Error fetching languages:', error);
    }
  };

  const fetchStatistics = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/news/statistics');
      setStatistics(response.data);
    } catch (error) {
      console.error('Error fetching statistics:', error);
    }
  };

  const fetchAnalysisHistory = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/news/history');
      setAnalysisHistory(response.data.content || []);
    } catch (error) {
      console.error('Error fetching history:', error);
    }
  };

  const handleAnalyze = async () => {
    if (!newsTitle.trim() || !newsContent.trim()) {
      setError('Please provide both title and content');
      return;
    }

    setIsAnalyzing(true);
    setError('');
    setAnalysisResult(null);

    try {
      const requestData = {
        title: newsTitle,
        content: newsContent,
        sourceUrl: sourceUrl || null,
        author: author || null,
        language: selectedLanguage === 'auto' ? null : selectedLanguage
      };

      const response = await axios.post('http://localhost:8080/api/news/analyze', requestData);
      setAnalysisResult(response.data);
      
      // Refresh data
      fetchStatistics();
      fetchAnalysisHistory();
      
    } catch (error) {
      setError('Error analyzing news. Please try again.');
      console.error('Analysis error:', error);
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleClear = () => {
    setNewsTitle('');
    setNewsContent('');
    setSourceUrl('');
    setAuthor('');
    setSelectedLanguage('auto');
    setAnalysisResult(null);
    setError('');
  };

  const getResultIcon = (isFakeNews) => {
    return isFakeNews ? <Cancel color="error" /> : <CheckCircle color="success" />;
  };

  const getResultColor = (isFakeNews) => {
    return isFakeNews ? 'error' : 'success';
  };

  const getConfidenceColor = (confidence) => {
    if (confidence >= 0.8) return 'success';
    if (confidence >= 0.6) return 'warning';
    return 'error';
  };

  const renderAnalysisForm = () => (
    <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
      <Typography variant="h5" gutterBottom>
        <Psychology sx={{ mr: 1, verticalAlign: 'middle' }} />
        Analyze News Content
      </Typography>
      
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <TextField
            fullWidth
            label="News Title"
            value={newsTitle}
            onChange={(e) => setNewsTitle(e.target.value)}
            placeholder="Enter the news title..."
            variant="outlined"
          />
        </Grid>
        
        <Grid item xs={12}>
          <TextField
            fullWidth
            label="News Content"
            value={newsContent}
            onChange={(e) => setNewsContent(e.target.value)}
            placeholder="Enter the news content..."
            multiline
            rows={6}
            variant="outlined"
          />
        </Grid>
        
        <Grid item xs={12} md={6}>
          <TextField
            fullWidth
            label="Source URL (Optional)"
            value={sourceUrl}
            onChange={(e) => setSourceUrl(e.target.value)}
            placeholder="https://example.com"
            variant="outlined"
          />
        </Grid>
        
        <Grid item xs={12} md={6}>
          <TextField
            fullWidth
            label="Author (Optional)"
            value={author}
            onChange={(e) => setAuthor(e.target.value)}
            placeholder="Author name"
            variant="outlined"
          />
        </Grid>
        
        <Grid item xs={12}>
          <TextField
            select
            fullWidth
            label="Language"
            value={selectedLanguage}
            onChange={(e) => setSelectedLanguage(e.target.value)}
            variant="outlined"
          >
            <option value="auto">Auto-detect</option>
            {supportedLanguages.map((lang) => (
              <option key={lang.code} value={lang.code}>
                {lang.name} ({lang.code.toUpperCase()})
              </option>
            ))}
          </TextField>
        </Grid>
        
        <Grid item xs={12}>
          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
            <Button
              variant="contained"
              size="large"
              onClick={handleAnalyze}
              disabled={isAnalyzing}
              startIcon={isAnalyzing ? <CircularProgress size={20} /> : <Security />}
            >
              {isAnalyzing ? 'Analyzing...' : 'Analyze News'}
            </Button>
            <Button
              variant="outlined"
              size="large"
              onClick={handleClear}
              disabled={isAnalyzing}
            >
              Clear
            </Button>
          </Box>
        </Grid>
      </Grid>
    </Paper>
  );

  const renderAnalysisResult = () => {
    if (!analysisResult) return null;

    return (
      <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
        <Typography variant="h5" gutterBottom>
          Analysis Results
        </Typography>
        
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  {getResultIcon(analysisResult.isFakeNews)}
                  <Typography variant="h6" sx={{ ml: 1 }}>
                    {analysisResult.isFakeNews ? 'Likely Fake News' : 'Likely Real News'}
                  </Typography>
                </Box>
                
                <Chip
                  label={`${(analysisResult.confidenceScore * 100).toFixed(1)}% Confidence`}
                  color={getConfidenceColor(analysisResult.confidenceScore)}
                  variant="outlined"
                  sx={{ mb: 2 }}
                />
                
                <Typography variant="body2" color="text.secondary">
                  <strong>Detected Language:</strong> {analysisResult.detectedLanguage?.toUpperCase()}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  <strong>Model Used:</strong> {analysisResult.modelUsed}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  <strong>Analysis Time:</strong> {analysisResult.analysisDurationMs}ms
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Explanation
                </Typography>
                <Typography variant="body2" paragraph>
                  {analysisResult.explanation}
                </Typography>
                
                <Typography variant="h6" gutterBottom>
                  Recommendation
                </Typography>
                <Typography variant="body2">
                  {analysisResult.recommendation}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Paper>
    );
  };

  const renderDashboard = () => (
    <Grid container spacing={3}>
      <Grid item xs={12} md={6}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            <Analytics sx={{ mr: 1, verticalAlign: 'middle' }} />
            Analysis Statistics
          </Typography>
          {statistics ? (
            <Box>
              <Typography variant="h4" color="primary">
                {statistics.totalAnalyses}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Total Analyses
              </Typography>
              
              <Box sx={{ mt: 2 }}>
                <Typography variant="body2">
                  Fake News: {statistics.fakeNewsCount} ({statistics.fakeNewsPercentage}%)
                </Typography>
                <Typography variant="body2">
                  Real News: {statistics.realNewsCount}
                </Typography>
              </Box>
            </Box>
          ) : (
            <Typography>Loading statistics...</Typography>
          )}
        </Paper>
      </Grid>
      
      <Grid item xs={12} md={6}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            <Language sx={{ mr: 1, verticalAlign: 'middle' }} />
            Supported Languages
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {supportedLanguages.map((lang) => (
              <Chip
                key={lang.code}
                label={`${lang.name} (${(lang.accuracy * 100).toFixed(0)}%)`}
                color={lang.modelAvailable ? 'primary' : 'default'}
                variant="outlined"
                size="small"
              />
            ))}
          </Box>
        </Paper>
      </Grid>
      
      <Grid item xs={12} md={6}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            News Classification
          </Typography>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie
                data={chartData}
                cx="50%"
                cy="50%"
                outerRadius={60}
                dataKey="value"
                label={({ name, value }) => `${name}: ${value}%`}
              >
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>
      
      <Grid item xs={12} md={6}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Confidence Distribution
          </Typography>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie
                data={confidenceData}
                cx="50%"
                cy="50%"
                outerRadius={60}
                dataKey="value"
                label={({ name, value }) => `${name}: ${value}%`}
              >
                {confidenceData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>
    </Grid>
  );

  const renderHistory = () => (
    <Paper elevation={3} sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>
        <History sx={{ mr: 1, verticalAlign: 'middle' }} />
        Analysis History
      </Typography>
      
      {analysisHistory.length > 0 ? (
        <List>
          {analysisHistory.map((analysis, index) => (
            <React.Fragment key={analysis.id || index}>
              <ListItem>
                <ListItemIcon>
                  {getResultIcon(analysis.isFakeNews)}
                </ListItemIcon>
                <ListItemText
                  primary={analysis.newsTitle}
                  secondary={
                    <>
                      <Typography component="span" variant="body2" color="text.primary">
                        {analysis.isFakeNews ? 'Fake News' : 'Real News'} • 
                        {(analysis.confidenceScore * 100).toFixed(1)}% Confidence • 
                        {analysis.detectedLanguage?.toUpperCase()} • 
                        {new Date(analysis.createdAt).toLocaleDateString()}
                      </Typography>
                    </>
                  }
                />
              </ListItem>
              {index < analysisHistory.length - 1 && <Divider />}
            </React.Fragment>
          ))}
        </List>
      ) : (
        <Typography variant="body1" color="text.secondary" align="center">
          No analysis history yet. Start by analyzing some news content!
        </Typography>
      )}
    </Paper>
  );

  const renderDrawer = () => (
    <Drawer
      anchor="left"
      open={drawerOpen}
      onClose={() => setDrawerOpen(false)}
    >
      <Box sx={{ width: 250, p: 2 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6">TruthGuard</Typography>
          <IconButton onClick={() => setDrawerOpen(false)}>
            <Close />
          </IconButton>
        </Box>
        
        <List>
          <ListItem button onClick={() => { setActiveTab(0); setDrawerOpen(false); }}>
            <ListItemIcon><Security /></ListItemIcon>
            <ListItemText primary="Analyze News" />
          </ListItem>
          <ListItem button onClick={() => { setActiveTab(1); setDrawerOpen(false); }}>
            <ListItemIcon><Analytics /></ListItemIcon>
            <ListItemText primary="Dashboard" />
          </ListItem>
          <ListItem button onClick={() => { setActiveTab(2); setDrawerOpen(false); }}>
            <ListItemIcon><History /></ListItemIcon>
            <ListItemText primary="History" />
          </ListItem>
        </List>
        
        <Divider sx={{ my: 2 }} />
        
        <FormControlLabel
          control={
            <Switch
              checked={darkMode}
              onChange={(e) => setDarkMode(e.target.checked)}
            />
          }
          label="Dark Mode"
        />
      </Box>
    </Drawer>
  );

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ flexGrow: 1 }}>
        <AppBar position="static">
          <Toolbar>
            <IconButton
              edge="start"
              color="inherit"
              onClick={() => setDrawerOpen(true)}
              sx={{ mr: 2 }}
            >
              <Menu />
            </IconButton>
            <Security sx={{ mr: 1 }} />
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              TruthGuard - AI Fake News Detector
            </Typography>
            <Chip
              icon={<Translate />}
              label="12 Languages"
              color="secondary"
              variant="outlined"
            />
          </Toolbar>
        </AppBar>

        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
          <Tabs
            value={activeTab}
            onChange={(e, newValue) => setActiveTab(newValue)}
            sx={{ mb: 3 }}
          >
            <Tab label="Analyze News" />
            <Tab label="Dashboard" />
            <Tab label="History" />
          </Tabs>

          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          {activeTab === 0 && (
            <>
              {renderAnalysisForm()}
              {renderAnalysisResult()}
            </>
          )}
          
          {activeTab === 1 && renderDashboard()}
          {activeTab === 2 && renderHistory()}
        </Container>

        {renderDrawer()}
      </Box>
    </ThemeProvider>
  );
}

export default App; 