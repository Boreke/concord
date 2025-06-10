FROM node:20-alpine

WORKDIR /app

# Copy package files
COPY package.json pnpm-lock.yaml* ./

# Install pnpm and dependencies
RUN npm install -g pnpm@10.11.1
RUN pnpm install --frozen-lockfile

# Copy source code
COPY . .

# Build the application
RUN pnpm run build

# Expose port
EXPOSE 4321

# Set environment variable for the host
ENV HOST=0.0.0.0
ENV PORT=4321

# Start the application
CMD ["node", "./dist/server/entry.mjs"]
