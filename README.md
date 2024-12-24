# Mystical Object Emporium

## Project Overview
Mystical Object Emporium is a full-stack web application for sharing and discovering unique objects. Users can post items with detailed descriptions, physical attributes, and AI-assisted object recognition through WikiData integration.

My client project is in here : https://github.com/Buzzy89/client

## Technical Stack

### Frontend (Next.js)
- **Framework**: Next.js 15.0.3
- **Language**: TypeScript
- **Styling**: 
  - Tailwind CSS
  - Custom theme with neon effects
  - Responsive design
- **State Management**: React Context API
- **Authentication**: JWT-based with secure storage
- **API Integration**: Axios with interceptors
- **Image Handling**: Next.js Image optimization
- **Form Handling**: Custom form components with validation

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.3.4
- **Language**: Java 17
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT
- **Cloud Storage**: Google Cloud Storage
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **API Documentation**: OpenAPI/Swagger

## Core Features

### Authentication System
- JWT-based authentication
- Token refresh mechanism
- Protected routes
- Role-based access control

### Post Management
- Create/Edit/Delete posts
- Rich media upload
- Physical attributes:
  - Dimensions (height, width, depth)
  - Weight
  - Materials
  - Colors
  - Shapes
- WikiData integration for object labeling
- Tag system

### Comment System
- Nested comments
- Real-time updates
- User mentions
- Rich text formatting

### Search & Discovery
- Full-text search
- Tag-based filtering
- Pagination
- Infinite scroll

## API Endpoints

### Authentication
```
POST /api/auth/register
POST /api/auth/login
```

### Posts
```
GET /api/posts
POST /api/posts/create
GET /api/posts/{id}
PUT /api/posts/{id}
DELETE /api/posts/{id}
```

### Comments
```
GET /api/comments/post/{postId}
POST /api/comments/create
PUT /api/comments/{id}
DELETE /api/comments/{id}
```

## Database Schema

### Users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Posts
```sql
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Setup Instructions

### Prerequisites
- Node.js 21+
- Java 17
- PostgreSQL 15
- Google Cloud account
- Docker (optional)

### Local Development

1. Backend Setup
```bash
cd mystricalObject
mvn clean install
mvn spring-boot:run -Dspring.profiles.active=dev
```

2. Frontend Setup
```bash
cd client
npm install
npm run dev
```

3. Environment Variables

Backend (.env):
```
POSTGRES_URL=jdbc:postgresql://localhost:5432/db
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password
JWT_SECRET=your_secret
GCP_BUCKET_NAME=your_bucket
```

Frontend (.env.local):
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Docker Deployment
```bash
docker-compose up -d
```

## Testing

### Backend Tests
```bash
mvn test
```

### Frontend Tests
```bash
npm run test
```

## Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## License
MIT License

## Contact
- Author: Yusuf
- Email: [osmanyusufyildirim@gmail.com]
