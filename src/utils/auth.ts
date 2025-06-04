import { AuthService } from '../services/AuthService';

export async function ensureValidToken(Astro: any) {
  const token = Astro.cookies.get('token')?.value;
  const refreshToken = Astro.cookies.get('refreshToken')?.value;
  if (!token && refreshToken) {
    try {
      await AuthService.refreshToken(refreshToken);
      return true;
    } catch (error) {
      console.error("Error refreshing token:", error);
      return Astro.redirect('/login');
    }
  }
  if (!token) {
    return Astro.redirect('/login');
  }
  return true;
}