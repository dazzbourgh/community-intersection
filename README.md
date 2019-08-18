# Community Intersection
A back end application that allows you to find users that are members of multiple communities on a social network website. Currently only VK is supported.

## Run

The startup sequence is:
 1. Config Service
 2. Discovery Service
 3. All other services
 
Use `gradlew bootRun -Dspring.profiles.active=local` for each service from service directory or your IDE with the same VM flags in run configuration.

For `gateway-service` use additional flags (with real values instead of empty strings):

`-Dclient_id= -Dclient_secret=`

## Tech
Kotlin based, Spring WebFlux + Coroutines. VK API is used alongside stored procedures that are called from this app.

## UI
For UI and an example please refer to [UI project page](https://github.com/dazzbourgh/community-intersection-ui).
