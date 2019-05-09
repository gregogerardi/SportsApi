# Sports API

## List of features 

* [RESTful URLs](#restful-urls)
* [HTTP Verbs](#http-verbs)
* [Responses](#responses)
* [Error handling](#error-handling)
* [Authentication and authorization](#authentication-and-authorization)
* [Response's body size limits](#response-body-size-limits)
* [Endpoints List](#endpoints-list)

## RESTful URLs

### General guidelines for the use of this RESTful URLs

* Use plural nouns always (no singular nouns).
* Use HTTP verbs (GET, POST, DELETE) to operate on the collections and elements.
* You can´t go deeper than resource/identifier/resource.
* HTTP headers: our API will ignore any aditional header in the requests. It will make use of them in the responses

### Good URL examples

* List of sports:
    * GET http://www.example.gov/sports
* A single sport:
    * GET http://www.example.gov/sports/nameofasport
* List of schedules for a sport:
    * GET http://www.example.gov/sports/nameofasport/schedules
* Delete a sport:
    * DELETE http://www.example.gov/sports/nameofasport
    
### Bad URL examples

* Non-plural noun:
    * http://www.example.gov/sport
    * http://www.example.gov/sports/nameofasport/schedule
* An unrecognized path:
    * http://www.example.gov/anyotherpath

## HTTP Verbs

HTTP verbs, or methods, are used in compliance with their definitions under the [HTTP/1.1](http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html) standard.

The action taken will be contextual to the type being worked on and its current state. Here's an example of how HTTP verbs map to create, read and delete operations in a particular context:

| HTTP METHOD             | POST              | GET             | DELETE      |
| ----------------------- | ----------------- | --------------- | ----------- |
| CRUD OP                 | CREATE            | READ            | DELETE      |
| /sports                 | Create new sport  | List sports     | Error       |
| /sports/name            | Error             | show sport name | Delete name |


## Responses

Responses uses HTTP status codes to indicate they type. Bodies -if presents- are always in Json format
We Use five simple, common response codes (besides the ones for authentication):

* 404 - the resource can't be found
* 200 - OK
* 201 - Resource created (for POST only)
* 400 - Bad Request
* 500 - Internal Server Error

## Error handling

Error responses include an error HTTP status code and optionaly a message into their body as a string

from the previous responses codes, the ones for errors are

* 404 - the resource can't be found
* 400 - Bad Request
* 500 - Internal Server Error

## Authentication and authorization

For actions that modify the information in the server will require authorization.
This are the ones related to POST and DELETE HTTP verbs. 
For them, we use an implementation of Apache Shiro to authenticate and authorise users based on an username-password pair.
When an unauthenticated user tries to make a POST request, the api will retrieve a 401 response with the header WWW-Authenticate set.
This prompts the browser to show a log-in dialogue and prompt the user to enter their username and password. The request is made to the resource again, this time with the Authorization header set, containing the username and password encoded in Base64.
When we receive this information, we check the username and password to authenticate the user and check if it is authorised to do such acction. If this is successful then the routing of the request is allowed to continue, otherwise a 403 response is returned to signify that access is denied.

### Session and cookies

To remain the authentication and authorization alive between requests, we use cookies to save the session of the current user

## Response body size limits

The only endpoints which could return an arbitrary long response are

* GET http://www.example.gov/sports
* GET http://www.example.gov/sports/nameofasport/schedules

in the context of our problem, neither of them will never return a large amount of data. 

* For the list of sports, it could never be larger than the number of sports that can be planned in a single multi-sport space over a week
* For the list of schedules, it could never be larger than the number schedules that a sport can has over a week

for this reasons we decide to not implement paging at this version of the api

## Endpoints list

the format of this list is 

* ### /url
   * ### HTTP Verb 1
      * ### Posible Case 1
          explination
      * ### Posible Case 2
        explination
   * ### HTTP Verb 2
      * ### Posible Case 1
        explination
      * ### Posible Case 1
        explination

 

Any combination of the nexts urls and HTTP Verbs that are not listed are forbidden and will returns an 404 error

* ### /sports
  * ### GET 
    * ### Common answer
        Returns an HTTP Response with status code 200 and the requested sport parsed as Json into its body
    * ### Internal error
        Returns an HTTP Response with status code 500 and the exception's details into its body
* ### /sports/:name
  * ### GET
    * ### Common answer
        Returns an HTTP Response with status code 200 and a list of all the sports parsed as Json into its body
    * ### Internal error
        Returns an HTTP Response with status code 500 and the exception's details into its body
    * ### The sport isn´t registered
        Returns an HTTP Response with status code 404
  * ### POST
    * ### Common answer
        Returns an HTTP Response with status code 201, and a header "LOCATION" indicating the url for the new sport
    * ### Internal error
        Returns an HTTP Response with status code 500 and the exception's details into its body
    * ### The sport is already registered
        Returns an HTTP Response with status code 400 and a "duplicated sport" message into its body
    * ### The sport is already registered
        Returns an HTTP Response with status code 400 and a "duplicated sport" message into its body
    * ### The sport is bad parser into the request
        Returns an HTTP Response with status code 400
  * ### DELETE
    * ### Common answer
        Returns an HTTP Response with status code 200 
    * ### Internal error
        Returns an HTTP Response with status code 500 and the exception's details into its body
    * ### The sport isn´t registered
        Returns an HTTP Response with status code 404

* ### /sports/:name/schedules
  * ### GET
    * ### Common answer 
        Returns an HTTP Response with status code 200 and the requested sport's schedules parsed as Json into its body
    * ### Internal error
        Returns an HTTP Response with status code 500 and the exception details into its body
  * ### POST
    * ### Common answer
        Returns an HTTP Response with status code 201
    * ### Internal error
        Returns an HTTP Response with status code 500 and the exception's details into its body
    * ### The schedule is already registered or the sport :name isn't registered
        Returns an HTTP Response with status code 400 and a "the specified schedule is already registered or the specified sport is not registered" message into its body
    * ### The schedule is bad parser into the request
        Returns an HTTP Response with status code 400
  * ### DELETE
    * ### Common answer
        Returns an HTTP Response with status code 200 
    * ### Internal error
        Returns an HTTP Response with status code 500 and the exception's details into its body
    * ### The sport isn´t registered
        Returns an HTTP Response with status code 404
    * ### The schedule is bad parser into the request
        Returns an HTTP Response with status code 400
