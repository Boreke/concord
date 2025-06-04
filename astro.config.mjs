// @ts-check
import { defineConfig } from 'astro/config';

import tailwindcss from '@tailwindcss/vite';

process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0'; // Disable TLS verification for local development
// https://astro.build/config
export default defineConfig({
  output: 'server',
  vite: {
    plugins: [tailwindcss()]
  }
});