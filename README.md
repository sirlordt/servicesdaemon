servicesdaemon (Atlas Middleware)
=================================

All code in this repo is licensed to GNU/GPL version 2

HTTP JSON/XML/CSV Restfull interface middleware for RDMS using JDBC

```
  +---------+        +----------------------------------+                     +-----------------------------------------------+
  |         |  JDBC  |         ServicesDaemon           |       HTTP(S)       |           Standard HTTP(S) client             | 
  |  RDBMS  |<------>|  http(s)://server_ip/DBServices/ |<------------------->|  (Java/Curl/Ruby/Perl/Python/Javascript/Php)  |
  |         |        |         (Jetty/Servlet)          |    (XML/CSV/JSON)   |                                               | 
  +---------+        +----------------------------------+                     +-----------------------------------------------+

```
More info on http://www.maindataservices.net/servicesdaemon/?q=es/proyecto


