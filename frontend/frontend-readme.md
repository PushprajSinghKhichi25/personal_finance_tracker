# Personal Finance Tracker - Frontend

A modern React/TypeScript frontend for the Personal Finance Tracker application.

## Tech Stack

- **React 18** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool
- **Tailwind CSS** - Styling
- **React Router** - Client-side routing
- **Zustand** - State management
- **Axios** - HTTP client
- **Recharts** - Data visualization

## Project Structure

```
frontend/
├── src/
│   ├── components/       # Reusable components (ProtectedRoute, etc)
│   ├── pages/           # Page components (Login, Register, Dashboard)
│   ├── services/        # API services (authService, api instance)
│   ├── store/           # Zustand stores (authStore)
│   ├── types/           # TypeScript type definitions
│   ├── styles/          # CSS files (Tailwind)
│   ├── App.tsx          # Main app component with routing
│   └── main.tsx         # Entry point
├── public/              # Static assets
├── index.html           # HTML entry file
├── package.json         # Dependencies
├── tsconfig.json        # TypeScript config
├── vite.config.ts       # Vite config
├── tailwind.config.js   # Tailwind config
└── postcss.config.js    # PostCSS config
```

## Getting Started

### Prerequisites

- Node.js 18+ and npm

### Installation

1. Install dependencies:
```bash
npm install
```

2. Create `.env` file (copy from `.env.example`):
```bash
cp .env.example .env
```

3. Update the API URL if needed in `.env`:
```
VITE_API_URL=http://localhost:8080/api
```

### Development

Start the development server:
```bash
npm run dev
```

The app will be available at `http://localhost:5173`

### Build

Build for production:
```bash
npm run build
```

### Preview

Preview the production build locally:
```bash
npm run preview
```

## Features

- **Authentication**: Login and registration with JWT tokens
- **Protected Routes**: Dashboard is protected and requires authentication
- **State Management**: Zustand for global auth state
- **API Integration**: Axios with automatic token injection
- **Error Handling**: Global error handling with 401 redirect
- **Responsive Design**: Mobile-friendly with Tailwind CSS

## API Integration

The frontend automatically:
- Injects JWT token in Authorization header
- Handles token expiration (401 responses)
- Redirects to login on auth failure
- Manages token storage in localStorage

## Next Steps

Phase 5.2 - Authentication Pages (detailed component implementation)
Phase 5.3 - State Management (global state setup)
Phase 6 - Dashboard & Features