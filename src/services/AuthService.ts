import { env } from "../constants/env";

export  class AuthService {
  isAuthenticated: boolean;
  constructor() {
    this.isAuthenticated = false;
  }

  async login(formData:FormData) {
    const response = await fetch(`${env.API_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData),
    });
    const data = await response.json();
    if (data.success) {
        localStorage.setItem('token', data.token);
        this.isAuthenticated = true;
        localStorage.setItem("refreshToken", data.refreshToken);
    } else {
        throw new Error(data.message);
    }
  }

  logout() {
    this.isAuthenticated = false;
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  }

  checkAuth() {
    return this.isAuthenticated;
  }
    async refreshToken() {
        const response = await fetch(`${env.API_URL}/auth/refresh`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                refreshToken: localStorage.getItem('refreshToken'),
            }),
        });
        const data = await response.json();
        if (data.success) {
            localStorage.setItem('token', data.token);
            localStorage.setItem("refreshToken", data.refreshToken);
            this.isAuthenticated = true;
        } else {
            throw new Error(data.message);
        }
    }
    async register(formData:FormData) {
        const response = await fetch(`${env.API_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData),
        });
        const data = await response.json();
        if (data.success) {
            localStorage.setItem('token', data.token);
            localStorage.setItem("refreshToken", data.refreshToken);
            this.isAuthenticated = true;
        } else {
            throw new Error(data.message);
        }
    }
}