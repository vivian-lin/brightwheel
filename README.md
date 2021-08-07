# Brightwheel take home project

### Steps to hit endpoint manually:
- Run application from command line: `./gradlew bootRun`
- cURL the endpoint from command line:

```
curl --location --request POST 'http://localhost:8080/email' \
--header 'Content-Type: application/json' \
--data-raw '{
    "to": "susan@preschool.com",
    "to_name": "Ms. Susan",
    "from": "no-reply@brightwheel.com",
    "from_name": "brightwheel",
    "subject": "Your Weekly Report",
    "body": "<h1>Weekly Report</h1><p>You saved 10 hours this week!</p>"
}'
```

### Switch default API service
Alter `default_api` value in `application.yml` - must be one of two options `SNAILGUN` or `SPENDGRID`

### Tests
Located in src/test/java. To run from command line, `./gradlew clean build`


### TODOs:
The EmailApiService implementations (SnailgunApiService and SpendgridApiService) assume that the 3rd party API will send either a 2xx response, OR times out and returns no response at all. If I had more time, I'd also cover other cases (i.e. API responds with 4xx or 5xx)

