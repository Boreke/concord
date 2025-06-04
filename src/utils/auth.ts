import { AuthService } from '../services/AuthService';

export async function ensureValidToken(Astro: any) {
  const token = Astro.cookies.get('token')?.value;
  const refreshToken = Astro.cookies.get('refreshToken')?.value;
  if (!token && refreshToken) {
    try {
      await AuthService.refreshToken(refreshToken).then((data: any) => {
        if (data) {
          Astro.cookies.set('token', data.token, { path: '/', maxAge: 3600, sameSite: 'strict' });
          Astro.cookies.set('refreshToken', data.refreshToken, { path: '/', maxAge: 604800, sameSite: 'strict' });
        }
      });
    } catch (error) {
      console.error("Error refreshing token:", error);
      return Astro.redirect('/login');
    }
  }
  if (!token) {
    return Astro.redirect('/login');
  }
}