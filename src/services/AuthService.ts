import { env } from "../constants/env";

export  class AuthService {
  static isAuthenticated: boolean;
  constructor() {}

  static async login(formData:{userTag: string,password: string}) {
    const response = await fetch(`${env.API_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData),
    });
    const data = await response.json();
    if (data) {
      document.cookie = `token=${data.token}; path=/; max-age=3600; samesite=strict`;
      document.cookie = `refreshToken=${data.refreshToken}; path=/; max-age=604800; samesite=strict`;
      window.location.href = '/';
    } else {
      throw new Error(data.message);
    }
  }

  static logout() {
    document.cookie = "token=; path=/; max-age=0; samesite=strict";
    document.cookie = `refreshToken=; path=/; max-age=0; samesite=strict`;
  }

  static checkAuthClient(){
    const token = document.cookie.split('; ').find(row => row.startsWith('token='));
    if (token) {
      AuthService.isAuthenticated = true;
      return true;
    } else {
      AuthService.isAuthenticated = false;
      return false;
    }
  }

  static checkAuthServer(token: string | undefined) {
    return token !== undefined ? true : false;
  }

  static async refreshToken(refreshToken: string) {
    const response = await fetch(`${env.API_URL}/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        refreshToken: refreshToken,
      }),
    });
    const data = await response.json();
    if (data.success) {
      document.cookie = `token=${data.token}; path=/; max-age=3600; samesite=strict`;
      document.cookie = `refreshToken=${data.refreshToken}; path=/; max-age=604800; samesite=strict`;
    } else {
      throw new Error(data.message);
    }
  }

  static async register(formData:{email: string, password: string,userTag:string, displayName: string}) {
    const response = await fetch(`${env.API_URL}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
    });
    const data = await response.json();
    
    if (data) {
      document.cookie = `token=${data.token}; path=/; max-age=3600; samesite=strict`;
      document.cookie = `refreshToken=${data.refreshToken}; path=/; max-age=604800; samesite=strict`;
      window.location.href = '/';
    } else {
      throw new Error(data.message);
    }
  }
}