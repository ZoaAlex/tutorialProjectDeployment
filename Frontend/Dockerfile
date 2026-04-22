# ── Stage 1 : Build Angular ───────────────────────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --prefer-offline
COPY . .
RUN npm run build -- --configuration production

# ── Stage 2 : Serve avec nginx ────────────────────────────────────────────────
FROM nginx:1.27-alpine
# Supprime la config par défaut
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copie le build Angular (dist/<project-name>/browser pour Angular 17+)
COPY --from=builder /app/dist/gestion-emploi-temps/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
