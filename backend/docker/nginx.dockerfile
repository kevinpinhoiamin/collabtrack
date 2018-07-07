FROM nginx:1.13.12-alpine
EXPOSE 80

RUN apk add --update apache2-utils \
	&& htpasswd -cbB /etc/nginx/.htpasswd collabtrack NEiVyMFkeTCVHqHJIMyfGiAiRz4IvymaCnmdimi8

WORKDIR /etc/nginx/conf.d
COPY nginx.conf collabtrack.conf

ENTRYPOINT ["nginx", "-g", "daemon off;"]