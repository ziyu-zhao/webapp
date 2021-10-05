# webapp 
Ziyu Zhao 001306157

### Prerequisites

#### mysql
Set username and password for database connection in application-dev.properties (maybe timezone)

#### java 8
Springboot project using java 8

### Deploy

Using maven to get all dependencies.

### Test

Visit http://localhost:8081/swagger-ui/ to test API

Example data for testing API of creating user:
```json
{
  "email": "zhao.ziyu2@northeastern.edu",
  "password": "123456",
  "firstname": "ziyu",
  "lastname": "zhao"
}
```

Because no UI is created for the webApp, we cannot send request which includes token in http headers even though 
I implemented CSRF.
Tokens can be tested in http response of user create.