# webapp 
Ziyu Zhao 001306157

### Prerequisites

#### mysql

#### java 8



### Test

Example data for testing API of creating user:
```json
{
  "email": "zhao.ziyu2@northeastern.edu",
  "password": "123456",
  "firstname": "ziyu",
  "lastname": "zhao"
}
```

Import certificate to aws
```shell
aws acm import-certificate --certificate fileb://certificate.crt --private-key fileb://private.key --certificate-chain fileb://certificate-chain.ca-bundle
```
