# === Build stage ===
FROM node:20-alpine AS build

WORKDIR /app

ENV PNPM_HOME=/usr/local/share/pnpm
ENV PATH="$PNPM_HOME:$PATH"
RUN corepack enable

COPY package.json pnpm-lock.yaml ./

COPY --from=pnpm-setup /root/.local/share/pnpm/pnpm /usr/local/bin/pnpm
COPY --from=pnpm-setup /root/.local/share/pnpm/state.json /root/.local/share/pnpm/state.json
RUN pnpm install --frozen-lockfile

COPY . .
RUN pnpm run build

# === Runtime stage ===
FROM node:20-alpine

WORKDIR /app

ENV NODE_ENV=production

COPY --from=build /app/public ./public
COPY --from=build /app/.next ./.next
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/package.json ./package.json

EXPOSE 3000

CMD ["node", "node_modules/.bin/next", "start"]