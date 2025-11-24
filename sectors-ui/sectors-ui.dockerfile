# Use official Node.js image for build
FROM node:20 AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build -- --configuration production

# Use nginx to serve static files
FROM nginx:alpine

COPY --from=build /app/dist/sectors-ui/browser /usr/share/nginx/html

EXPOSE 4200
CMD ["nginx", "-g", "daemon off;"]
