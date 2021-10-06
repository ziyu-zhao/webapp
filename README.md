# webapp 
Ziyu Zhao 001306157

### Prerequisites

#### mysql
- Set username and password for database connection in application-dev.properties (maybe timezone)

#### java 8
- Springboot project based on java 8

### Deploy

- Use maven to download all dependencies.

- Execute SQL script
```sql
create database csye6225;
use csye6225;
drop table user;
create table user(
    ID varchar(50) primary key,
    firstname varchar(20),
    lastname varchar(20),
    password varchar(100),
    username varchar(50),
    accountCreated varchar(40),
    accountUpdated varchar(40)
)
```

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
Tokens can be viewed in http response of user creation.

### PS: email to TAs

Hi TAs,

Due to the timezone I am now staying, I cannot join in TA meetings during TA office hours.
And I cannot find emails of TAs on Canvas. I found an email address of Jai on his GitHub page and sent 
AWS account files to him using that address. I don't know whether it would work.
I hope I can contact TAs by email when I have questions.
My email is zhao.ziyu2@northeastern.edu. Could you send your email addresses to me or just email me so that
I can contact TAs. I am looking forward to hearing from TAs.

Dear,
Zhao

