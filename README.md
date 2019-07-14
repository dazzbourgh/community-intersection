# Community Intersection
A back end application that allows you to find users that are members of multiple communities on a social network website. Currently only VK is supported.

## Tech
Kotlin based, Spring WebFlux + Coroutines. VK API is used alongside stored procedures that are called from this app.

## UI
For UI and an example please refer to [UI project page](https://github.com/dazzbourgh/community-intersection-ui).

## Run the app
####From IDE
To run from IDE, run CommunityScannerApplication class with the following JVM args:

**-Dspring.profiles.active=local -Dclient_id=___ -Dclient_secret=___**

For actual values of client_id & client_secret refer to repo owner.

####As docker container
Run the following command from app source root to build an image:

**./gradlew jib**

Use the same JVM args on container startup:

**-Dspring.profiles.active=local -Dclient_id=___ -Dclient_secret=___**

## Implementation details

`PeopleController` provides API for client application to get a stream (list) of users that are subscribed to all communities specified in the `Request` object, passed as body in POST request.

Since VK doesn't allow sending request more than 3 times a second, `DelayingRequestSender` is a service that queues all the requests from this back end application to VK API and executes them after a delay if such is needed.

`UserService` is responsible for fetching information about VK users.
