MYIN ADMIN DESKTOP - SETUP

1. Install JDK 17 or newer and open this folder as a Maven project in NetBeans.
2. Wait for Maven dependencies to load. JCalendar is bundled in lib/.
3. Copy config.properties.example to config.properties in the admin-desktop
   folder (the application's working directory), then set API_BASE_URL manually.
4. For local development, point API_BASE_URL to the same local backend used by
   Employee Web. Start the backend and its configured database separately.
5. Build and run the application. The login uses the backend JWT endpoint.

Configuration format:
API_BASE_URL=http://localhost:8080

For a deployed backend, set API_BASE_URL to its HTTPS origin, for example:
API_BASE_URL=https://<backend-host>

Do not put passwords, JWT secrets, or database credentials in config.properties.
The local config.properties file is ignored by Git. The checked-in example is
only a development template. Restart Admin Desktop after changing the URL.

Admin Desktop communicates with the backend through the shared REST API. It
does not connect to MariaDB directly.
