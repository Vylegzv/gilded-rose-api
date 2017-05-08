# Gilded Rose API

The Gilded Rose is a small inn in a prominent city that buys and sells only the finest items. The shopkeeper is looking to expand by providing merchants in other cities with access to the shop's inventory via a public HTTP-accessible API.

This API is written in Java using Spring Boot. It allows users to retrieve the current list of items and buy an item, in which case a user must be authenticated.

## Running the Project

### What you'll need

- JDK 1.8 or later
- Gradle 2.3+
- Git 2.0+
- Eclipse Neon IDE for Java Developers
- Postman

### Installing

- Clone the repository
- Start Eclipse
- Open "File -> Import..." dialog and select "Gradle" -> "Gradle Project"
- Click "Next" until you are asked to "Import Gradle Project"
- Browse to the root of gilded-rose-api you've just cloned
- Click "Finish"

### Running Locally

- Browse to the GildedRoseApplication.java under "src/main/java". This is the entry point to our application.
- Either right-click on GildedRoseApplication.java and select "Run As" - > "Java Application" or click on the green "run" button in Eclipse.
- Spring Boot will start an embedded Apache Tomcat server.

### Accessing the API

You need to use "https" and port "8443". You will most likely see a warning about the site's certificate in the browser. This warning is generated because the keystore includes a self-signed certificate (for demo purposes), and not the certificate that has been signed by a certificate authority.

#### Retrieving the current list of items

For the demo purposes, the project uses an H2 in-memory Java SQL database, which is pre-populated with a list of items when application starts.

In Postman, paste 

`https://localhost:8443/items/`

and make sure to select "GET" for the request type, and hit "Send". You should see the following JSON output:

```
{
  "_embedded": {
    "itemResources": [
      {
        "item": {
          "id": 1,
          "name": "Item1",
          "description": "Item1 Description",
          "price": 10
        },
        "_links": {
          "items": {
            "href": "https://localhost:8443/items"
          },
          "self": {
            "href": "https://localhost:8443/items/buy?id=1"
          }
        }
      },
      // more items
    ]
  }
}
```
We can see that our API leverages a Hypermedia As The Engine Of Application State (HATEOAS) functionality to ensure that future changes in the API don't break clients. It ensures that clients make state transitions only through actions that are dynamically identified within hypermedia by the server (e.g. the navigation links provided as part of the payload metadata). 

For example, we get a JSON array of resource objects, where each resource describes an item and contains an embedded link to buy this item as well as a link to view an entire collection of available items.

#### Buying an Item

For demo purposes, the API allows you to buy only 1 item at a time. In Postman, paste 

`https://localhost:8443/items/buy?id=1`

and make sure to select "GET" for the request type, and hit "Send". Because you haven't been authenticated, you should get a `"401 Unauthorized"` error.

For authentication, the application uses OAuth2 `resource owner password credentials` grant type. These credentials are hardcoded in the application.yml file and a list of users is pre-populated in an in-memory database on the application start. In a real-world scenario, we would encrypt passwords and validate data against a real database. 

To buy an item, users must request access through the "/oauth/token" endpoint and then insert the obtained token into future requests for authorization. In Postman, paste 

`https://localhost:8443/oauth/token?grant_type=password&username=john&password=pwd123`

and select "POST" for the request type. Click "Authorization" and enter the following information:

- Type: Basic Auth
- Username: trustedclient
- Password: trustedclient123

Click "Send". You should see a similar JSON response:

```
{
  "access_token": "c715d12d-26a2-4e70-99f3-384b62c8cf19",
  "token_type": "bearer",
  "refresh_token": "cef2a893-8459-4792-91b5-73f54eace731",
  "expires_in": 119,
  "scope": "buy"
}
```

Save this information somewhere as you will need it soon. Now to buy an item, paste the following url by appending the value of "access_token": 

`https://localhost:8443/items/buy?id=1&access_token=<access_token>`

Select "GET" and click "Send". You should see the following JSON response:

```
{
  "item": {
    "id": 1,
    "name": "Item1",
    "description": "Item1 Description",
    "price": 10
  },
  "_links": {
    "items": {
      "href": "https://localhost:8443/items"
    },
    "self": {
      "href": "https://localhost:8443/items/buy?id=1"
    }
  }
}
```

For demo purposes you will get back an item that you just 'bought' (in real-world scenario, the logic will may be more complex). When you buy an item, the application deletes this item from the database, so if you follow the link to buy the item again, you will receive a `"410 Gone"` error message with the following response:

```
[
  {
    "logref": "error",
    "message": "Item 1 is out of stock",
    "links": []
  }
]
```

Also, if you try to see the collection of items again, you will notice that Item1 is no longer available.

The access-token expires after 2 minutes, so if you try to buy another item using the same access token after 2 minutes, you will get the `"401 Unauthorized"` error with the following response:

```
{
  "error": "invalid_token",
  "error_description": "Access token expired: 711af5a2-bb3e-453e-9c74-10add8d03bc9"
}
```

We can request for a new access-token using our refresh-token by making a "POST" request in Postman:

`
https://localhost:8443/oauth/token?grant_type=refresh_token&refresh_token=<refresh_token>
`

Again, we should see a response similar to this:

```
{
  "access_token": "6c069550-0e13-44eb-8728-fe46bbc299bd",
  "token_type": "bearer",
  "refresh_token": "4f42c9c7-665a-4e47-8a88-88d2f3aa4940",
  "expires_in": 120,
  "scope": "buy"
}
```

and we can use the new access-token to buy more items.

Our request-token expires in 10 minutes, so if we are renewing an access-token using the expired refresh-token, we would get:

```
{
  "error": "invalid_token",
  "error_description": "Invalid refresh token (expired): 4f42c9c7-665a-4e47-8a88-88d2f3aa4940"
}
```

In this case we would need to request a new pair of access/refresh tokens as we did earlier.

### Running Tests

Double click on the GildedRoseApplicationTests.java under "src/main/java" and click the green "run" button, or right click on the  GildedRoseApplicationTests.java and select "Run As" -> "JUnit Test". 

### Design Decisions and Technology Choices

#### Spring Framework (Spring Boot)

Spring Framework is one of the most widely used Java EE framework. It comes with existing technologies, such as ORM and Spring Security OAuth, so we don't need to integrate these technologies explicitly. 

One of the core Spring concepts is "dependency injection", which allows to achieve loose coupling between different components, reduce boilerplate code, and make the application extendable and maintainable. It also makes unit testing easy, because we can just use mock objects.

Spring Boot is a utility framework from Spring team that removes the need to have a heavyweight application container and provides a means to deploy lightweight, server-less, and production-grade applications that you can simply run.

#### OAuth2

OAuth2 is a standardized authorization framework/protocol. For this demo application, we have chosen to use `resource owner password credentials` grant type, because we are not implementing a view that redirects a client to a login page. We simply allow a client (e.g. Postman) to have and provide the resource owner's credentials along with the client's credentials to the authorization server. The server in turn provides the client with an access-token (and refresh-token) the client must use to access protected resources.

Without HTTPS, security can be compromised even when using OAuth2 or any other security mechanism. Therefore, we leverage HTTPS to prevent Man-in-the-Middle attacks. For the demo purposes, we use a self-signed certificate.

#### HATEOAS 

A hypermedia-driven API allows an API to have a "staying power". In other words, it ensures that future changes in the API don't break clients; clients can dynamically navigate through the API's REST interfaces using the navigation links included with responses.

HAL (Hypertext Application Language) allows to implement HATEOAS by providing a standard way of representing resources that contain hypermedia information. It is one of the most popular hypermedia formats to emerge, and is a default media type served by Spring HATEOAS.

#### Retrofit

Retrofit turns an HTTP API into a Java type-safe interface that provides a contract for client/server interactions. The client/server interactions are thus simplified, because a client (e.g. a code that tests the API) can simply convert such interface into a client object.

#### Spring Data JPA

The Java Persistence API (JPA) is a specification for accessing, persisting, and managing data between Java objects and relational databases. Spring Data JPA provides JPA data access abstraction. It offers generic JPA-based repositories that you can easily customize, and it allows you to easily switch between different JPA providers.

#### Project Organization

The project is broken into the following packages:

- `domain`: This package contains a JPA Item entity that models our Gilded Rose item as it "appears" in a database.
- `repo`: This package contains a Spring Data JPA repository to abstract away the tedious database interactions. It provides to us the default methods, such as methods to retrieve all items and find an item by id. We can easily customize this repository with our own methods of accessing the database.
- `client`: This package contains an interface that defines a Gilded Rose API (ItemAPI). This interface is type-safe, and it allows clients to easily convert it into a client object.
- `service`: This package contains a configuration service that encapsulates the application configuration defined in application.yml file. This allows to inject and keep all configuration parameters in a central place, rather than scattering the configuration throughout the whole application. The package also contains a service class that implements ItemAPI interface, and encapsulates access to our items' repository.
- `controller`: This package contains Spring's MVC controller that implements ItemAPI and exposes annotated methods as HTTP endpoints. The controller accepts client requests and calls a service to process a request, which in turn may interact with a repository, which further interacts with an underlying database.
- `exception`: This package contains classes that allow us to nicely handle exceptions. For example, we use Spring's MVC "controller advice" class to translate "ItemOutOfStock" into an HTTP 410 status code (GONE) with a media type of application/vnd.error. The "ItemOutOfStock" exception is simply a runtime exception. 
- `resource`: This package contains an ItemResource class that declares the details of our REST resource for items.
- `security`: This package contains all configuration related to our OAuth2 implementation. In particular, it contains a Resource Server that hosts the resources (our API) that a client is interested in accessing. It contains an Authorization Server that is responsible for verifying credentials and providing the tokens; it also contains information about registered users and their possible access scopes and grant types. In real-world scenarios, we would have Authorization and Resource servers as separate applications.