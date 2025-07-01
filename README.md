# SatyaSetu - Rural India's Bridge to Truth (सत्यसेतु)

**Connecting Villages to Verified News**

SatyaSetu is an AI-powered fake news detection platform designed for rural India. It empowers communities to verify news in their local languages, bridging the gap between misinformation and the truth. With a simple, mobile-friendly interface and support for 12+ Indian and global languages, SatyaSetu brings trust and clarity to every village.

---

## 🚀 Features
- Multi-language fake news detection (English, Hindi, and more)
- AI-powered analysis with confidence scores
- Simple, mobile-first UI for rural users
- News history and feedback
- RESTful API for integrations
- Secure authentication
- Dockerized for easy deployment

---

## 🏗️ Project Structure
```
backend/    # Spring Boot Java backend with AI/ML integration
frontend/   # React frontend with rural India theme
```

---

## 🌾 Rural India Focus
- Designed for low-bandwidth and mobile devices
- Local language support for Hindi, Bengali, Marathi, Tamil, Telugu, and more
- Simple, intuitive interface for all literacy levels
- Community feedback and trust-building features

---

## ⚡ Quick Start (Local)

### 1. Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 2. Frontend Setup
```bash
cd frontend
npm install
npm start
```

- Backend: http://localhost:8080
- Frontend: http://localhost:3000

---

## 🐳 Run with Docker
```bash
docker-compose up --build -d
```

---

## 🔗 API Endpoints
- `POST /api/news/analyze` — Analyze news content
- `GET /api/news/history` — User's analysis history
- `POST /api/news/feedback` — Feedback on analysis
- `GET /api/languages` — Supported languages
- `POST /api/languages/detect` — Auto-detect language

---

## 🌍 Supported Languages
- Hindi, English, Bengali, Marathi, Tamil, Telugu, Gujarati, Kannada, Malayalam, Odia, Punjabi, Urdu, and more

---

## 🎨 UI Features
- Rural India-inspired design
- Multi-language interface
- Real-time analysis
- Visual results and charts
- Responsive for mobile
- Light/dark mode

---

## 🔒 Security Features
- JWT-based authentication
- Input validation and sanitization
- Rate limiting
- CORS configuration
- Secure API endpoints

---

## 📊 Performance Metrics
- **Accuracy**: >90% on benchmark datasets
- **Response Time**: <2 seconds for analysis
- **Language Support**: 12+ languages
- **Model Size**: Optimized for production deployment

---

## 🤝 Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

---

## 📄 License
MIT License - see LICENSE file for details

---

## 🙌 Credits
Built for the villages of India, with a mission to connect every community to the truth.

