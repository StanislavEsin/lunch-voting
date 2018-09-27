[![Build Status](https://travis-ci.org/StanislavEsin/lunch-voting.svg?branch=master)](https://travis-ci.org/StanislavEsin/lunch-voting)

Voting system for deciding where to have lunch.
==================

Task: Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) without frontend.

 * 2 types of users: admin and regular users
 * Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
 * Menu changes each day (admins do the updates)
 * Users can vote on which restaurant they want to have lunch at
 * Only one vote counted per user
 * If user votes again the same day:
    - If it is before 11:00 we asume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides new menu each day.

As a result, provide a link to github repository.

It should contain the code and README.md with API documentation and curl commands to get data for voting and vote.

P.S.: Make sure everything works with latest version that is on github :)

P.P.S.: Asume that your API will be used by a frontend developer to build frontend on top of that.

---

#### Used technologies
- Java 8
- Maven
- Spring Boot 2.0
- Spring Security
- Spring 5
  * Spring Core (Beans, Context)
  * Spring Data Access (JdbcTemplate, ORM, JPA (Hibernate), Transactions)
- DBs: H2
- RESTful services
- SLF4J2
- Spring Boot Starter Test / Spring Security Test / JUnit 5
- Tomcat

## Credentionals

  User(role, email, password):
  * UserOne, "user_one@yandex.ru", "password"
  * UserTwo, "user_two@yandex.ru", "password"
  * Admin, "admin@gmail.com", "admin"
  
# CURL Commands

---
## User:

- #### Get restaurants with menu and voices on the current date
    `curl -X GET http://localhost:8080/api/profile/restaurants -H 'Authorization: Basic dXNlcl9vbmVAeWFuZGV4LnJ1OnBhc3N3b3Jk'`

- #### Get a menu for a restaurant with ID 100002 at the current date
    `curl -X GET http://localhost:8080/api/profile/restaurants/100002/menu -H 'Authorization: Basic dXNlcl9vbmVAeWFuZGV4LnJ1OnBhc3N3b3Jk'`

- #### Check voting for the restaurant with ID 100002 for the current date
    `curl -X GET http://localhost:8080/api/profile/restaurants/100002/vote -H 'Authorization: Basic dXNlcl9vbmVAeWFuZGV4LnJ1OnBhc3N3b3Jk'`

- #### Vote for the restaurant with ID 100002 on the current date
    `curl -X POST http://localhost:8080/api/profile/restaurants/100002/vote -H 'Authorization: Basic dXNlcl9vbmVAeWFuZGV4LnJ1OnBhc3N3b3Jk'`

---
## Admin:

###  OPERATIONS on users

- #### Get all users
    `curl -X GET http://localhost:8080/api/admin/users -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`
    
- #### Get user with ID 100001
    `curl -X GET http://localhost:8080/api/admin/users/100001 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`    
    
- #### Get user by email: user_one@yandex.ru
    `curl -X GET 'http://localhost:8080/api/admin/users/by-email?email=user_one@yandex.ru' -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'` 
    
- #### Create a new user
    `curl -X POST http://localhost:8080/api/admin/users -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' -H 'Content-Type: application/json' -d '{"name": "new-user","email": "new-email@new.com","password": "newPassword"}'`   
    
- #### Update user with ID 100002
    `curl -X PUT http://localhost:8080/api/admin/users/100002 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' -H 'Content-Type: application/json' -d '{"id": 100002,"name": "userUpdate","email": "user@yandex.ru","password": "password","roles": ["ROLE_USER"]}'`    
    
- #### Delete user with ID 100002
    `curl -X DELETE http://localhost:8080/api/admin/users/100002 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'` 
    
    
### OPERATIONS on restaurants    
 
- #### Get all restaurants
    `curl -X GET http://localhost:8080/api/admin/restaurants -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

- #### Create new restaurant
    `curl -X POST http://localhost:8080/api/admin/restaurants -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' -H 'Content-Type: application/json' -d '{"name": "New restaurant"}'`

- #### Update restaurant with ID 100002
    `curl -X PUT http://localhost:8080/api/admin/restaurants/100002 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' -H 'Content-Type: application/json' -d '{"name": "restaurant update"}'`

- #### Delete restaurant with ID of 100002
    `curl -X DELETE http://localhost:8080/api/admin/restaurants/100002 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`
    
- #### Get a vote-counting for a restaurant with ID 100001 for the whole period
    `curl -X GET http://localhost:8080/api/admin/restaurants/100001/votes -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

- #### Get a vote-counting for the restaurant with ID 100001 on the 2017/05/20
    `curl -X GET 'http://localhost:8080/api/admin/restaurants/100001/votes/by-date?date=2017-05-20' -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

- #### Get a vote-counting for a restaurant with ID 100001 from 2010/01/01 to 2018/12/31
    `curl -X GET 'http://localhost:8080/api/admin/restaurants/100002/votes/by-period?startDate=2010-01-01&endDate=2018-12-31' -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`


### OPERATIONS on menu

- #### Get the menu for all restaurants on 2017/05/20
    `curl -X GET 'http://localhost:8080/api/admin/restaurants/menu/by-date?date=2017-05-20' -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

- #### Get the menu for all restaurants from 2010/01/01 to 2018/12/31
    `curl -X GET 'http://localhost:8080/api/admin/restaurants/menu/by-period?startDate=2010-01-01&endDate=2018-12-31' -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

- #### Get the menu for a the restaurant with ID 100000 for the whole period
    `curl -X GET http://localhost:8080/api/admin/restaurants/100000/menu/ -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu`  

- #### Get the menu with ID 100002 
    `curl -X GET http://localhost:8080/api/admin/restaurants/100001/menu/100002 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu`

- #### Get the menu for a the restaurant with ID 100000 on 2018/08/14
    `curl -X GET 'http://localhost:8080/api/admin/restaurants/100000/menu/by-date?date=2018-08-14' -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

- #### Get the menu for a the restaurant with ID 100000 from 2010/01/01 to 2018/12/31
    `curl -X GET 'http://localhost:8080/api/admin/restaurants/100000/menu/by-period?startDate=2010-01-01&endDate=2018-12-31' -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

- #### Create new menu for a the restaurant with ID 100000 on 2018/09/19
    `curl -X POST http://localhost:8080/api/admin/restaurants/100000/menu -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' -H 'Content-Type: application/json' -d '{"date":"2018-09-19"}'`

- #### Update menu with ID 100001
    `curl -X PUT http://localhost:8080/api/admin/restaurants/100000/menu/100001 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' -H 'Content-Type: application/json' -d '{"date":"2018-09-21"}'`

- #### Delete menu with ID 100001
    `curl -X DELETE http://localhost:8080/api/admin/restaurants/100000/menu/100001 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`

   
### OPERATIONS on dishes

- #### Add new dish to menu with ID 100001
    `curl -X POST http://localhost:8080/api/admin/restaurants/100000/menu/100001/dishes/ -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' -H 'Content-Type: application/json' -d '{"name":"New item", "price":950}'`
    
- #### Update dish with ID 100001
    `curl -X PUT http://localhost:8080/api/admin/restaurants/100000/menu/100000/dishes/100001 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu' -H 'Content-Type: application/json' -d '{"name":"Update item", "price":950}'`    
    
- #### Delete dish with ID 100000
    `curl -X DELETE http://localhost:8080/api/admin/restaurants/100000/menu/100000/dishes/100000 -H 'Authorization: Basic YWRtaW5AZ21haWwuY29tOmFkbWlu'`    