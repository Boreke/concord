import { env } from "../constants/env";

export  class AuthService {
  static isAuthenticated: boolean=false;
  constructor() {}

  static async login(formData:FormData) {
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
        AuthService.isAuthenticated = true;
        localStorage.setItem("refreshToken", data.refreshToken);
    } else {
        throw new Error(data.message);
    }
  }

  static logout() {
    AuthService.isAuthenticated = false;
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  }

  static checkAuth() {
    return AuthService.isAuthenticated;
  }
  
  static async refreshToken() {
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
          AuthService.isAuthenticated = true;
      } else {
          throw new Error(data.message);
      }
    }

    static async register(formData:FormData) {
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
          AuthService.isAuthenticated = true;
      } else {
          throw new Error(data.message);
      }
    }
}