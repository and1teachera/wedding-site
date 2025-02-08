FROM node:lts-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN npm run build

FROM node:lts-alpine
WORKDIR /app

RUN npm install -g serve

COPY --from=builder /app/dist/frontend /app

EXPOSE 4200

CMD ["serve", "-s", ".", "-l", "4200"]