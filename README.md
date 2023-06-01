# Backend Challenge - Java

This repository is a supplemental option for those who don't feel comfortable working with either our TypeScript or Python problems. It should be noted that the Python project was used as the source of truth for implementation details.

## Context

We would like you to help us with a small service that we have for handling bookings. A booking for us simply tells us which guest will be staying in which unit, and when they arrive and the number of nights that guest will be enjoying our amazing suites, comfortable beds, great snac.. apologies - I got distracted. Bookings are at the very core of our business and it's important that we get these right - we want to make sure that guests always get what they paid for, and also trying to ensure that our unit are continually booked and have as few empty nights where no-one stays as possible. A unit is simply a location that can be booked, think like a hotel room or even a house. For the exercise today, we would like you to help us solve an issue we've been having with our example service, as well as implement a new feature to improve the code base. While this is an opportunity for you to showcase your skills, we also want to be respectful of your time and suggest spending no more than 3 hours on this (of course you may also spend longer if you feel that is necessary)

### You should help us:
Identify and fix a bug that we've been having with bookings - there seems to be something going wrong with the booking process where a guest will arrive at a unit only to find that it's already booked and someone else is there!

### Implement a new feature:
Allowing guests to extend their stays if possible. It happens that <strike>sometimes</strike> all the time people love staying at our locations so much that they want to extend their stay and remain there a while longer. We'd like a new API that will let them do that

While we provide a choice of projects to work with (either `TS`, `Python`, or `Java`), we understand if you want to implement this in something you're more comfortable with. You are free to re-implement the parts that we have provided in another language, however this may take some time and we would encourage you not spend more time than you're comfortable with!

When implementing, make sure you follow known best practices around architecture, testability, and documentation.


## How to run

### Prerequisutes

Make sure to have the following installed

- Java version 17 (make sure that your $JAVA_HOME env variable is set)
- git

### Setup

To get started, clone the repository locally and run the following

```shell
[~]$ ./gradlew bootRun

> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.0.6)

2023-06-01T23:59:47.507+02:00  INFO 20967 --- [           main] d.l.b.BackendChallengeJavaApplication    : Starting BackendChallengeJavaApplication using Java 17.0.6 with PID 20967 (/backend-challenge-java/build/classes/java/main started by limehome in /backend-challenge-java)
2023-06-01T23:59:47.509+02:00  INFO 20967 --- [           main] d.l.b.BackendChallengeJavaApplication    : The following 1 profile is active: "default"
2023-06-01T23:59:48.781+02:00  INFO 20967 --- [           main] d.l.b.BackendChallengeJavaApplication    : Started BackendChallengeJavaApplication in 1.412 seconds (process running for 1.576)
<==========---> 80% EXECUTING [1s]
```

To make sure that everything is setup properly, open http://localhost:8000 in your browser and you should see an OK message.
The logs should be looking like this

```shell
2023-06-02T00:14:34.975+02:00 DEBUG 22241 --- [nio-8000-exec-1] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to de.limehome.backendchallengejava.BackendChallengeJavaApplication#helloWorld()
2023-06-02T00:14:34.999+02:00 DEBUG 22241 --- [nio-8000-exec-1] m.m.a.RequestResponseBodyMethodProcessor : Using 'application/json;q=0.8', given [text/html, application/xhtml+xml, image/avif, image/webp, image/apng, application/xml;q=0.9, */*;q=0.8, application/signed-exchange;v=b3;q=0.7] and supported [application/json, application/*+json]
2023-06-02T00:14:35.000+02:00 DEBUG 22241 --- [nio-8000-exec-1] m.m.a.RequestResponseBodyMethodProcessor : Writing [BackendChallengeJavaApplication.HealthResponse(message=OK)]
2023-06-02T00:14:35.007+02:00 DEBUG 22241 --- [nio-8000-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
```

To navigate to the swagger docs, open the url http://localhost:8000/docs


### Running tests

Open your terminal and run the following commands in the cloned directory (ignore the failing test)

```shell
[~]$ source ./venv/bin/activate  # activate the virtual env shell
(venv)[~]$ pytest

=========================================================================== test session starts ============================================================================
platform darwin -- Python 3.10.9, pytest-7.3.1, pluggy-1.0.0
rootdir: /Users/saifmirza/work/backend-challenge-python
plugins: freezegun-0.4.2, asyncio-0.21.0, anyio-3.6.2
asyncio: mode=strict
collected 5 items                                                                                                                                                          

========================================================================= short test summary info ==========================================================================
FAILED app/test_bookings.py::test_different_guest_same_unit_booking_different_date - AssertionError: {"guest_name":"GuestB","unit_id":"1","check_in_date":"2023-05-22","number_of_nights":5}
================================================================= 1 failed, 4 passed, 21 warnings in 0.36s =================================================================

```
