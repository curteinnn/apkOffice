MYIN ADMIN DESKTOP - SETUP

1. Install JDK 17 or newer (JDK 17 is recommended).
2. Open NetBeans.
3. Choose File > Open Project and select this admin-desktop folder
   (the folder containing pom.xml).
4. Wait for Maven dependencies to load. The JCalendar library is bundled
   in lib/jcalendar-0.8.1.jar.
5. Right-click the project > Clean and Build.
6. Run the project. The configured entry point opens the existing loginadmin form.

IMPORTANT STATUS
- The original Java Swing screens and .form files have been preserved.
- The Maven dependency declaration has been repaired; Java release is set to 17.
- AdminDesktop now opens the existing login screen.
- The login button is still a placeholder. It is NOT yet authenticated against
  the Spring Boot backend. Do not treat this build as production-ready.
- The backend local profile expects MariaDB at localhost:3306, database myin,
  username root, and an empty password unless you change its configuration.
- Configure and start the backend/database separately before API integration.
