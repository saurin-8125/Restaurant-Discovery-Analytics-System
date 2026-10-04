# Restaurant Discovery & Analytics System

## Advanced Big Data Analytics Assignment

A Scala-based CLI application for restaurant discovery and analytics
using MongoDB Atlas and MongoDB's `sample_restaurants` dataset.

### Technology Stack

-   **Language:** Scala 2.13.16
-   **MongoDB Scala Driver:** 5.13.0
-   **Runtime:** Java/JVM 17
-   **Build/Runner:** Scala CLI
-   **Database:** MongoDB Atlas
-   **Database Name:** `sample_restaurants`
-   **Collection:** `restaurants`

------------------------------------------------------------------------

## 1. Project Structure

``` text
Restaurant-Discovery-Analytics-System/
│
├── README.md
│
├── src/
│   └── Main.scala
│
├── screenshots/
│   ├── 01_application_startup.png
│   ├── 02_main_menu.png
│   ├── ...
│   └── 23_scala_collection_analytics.png
│
└── .gitignore
```

The application is implemented as a single `Main.scala` file containing
the MongoDB connection, Scala models, CRUD operations, search/filtering,
indexing, aggregation, and CLI menu.

------------------------------------------------------------------------

## 2. Prerequisites

Install the following software:

1.  Java JDK 17 or compatible JDK
2.  Scala CLI
3.  A MongoDB Atlas account
4.  Access to the MongoDB `sample_restaurants` database and
    `restaurants` collection

Check the installations using:

``` powershell
java -version
scala-cli version
```

------------------------------------------------------------------------

## 3. MongoDB Atlas Setup

The application connects to MongoDB Atlas using a MongoDB connection
string.

The required database and collection are:

``` text
Database:   sample_restaurants
Collection: restaurants
```

The application uses the official MongoDB sample restaurant dataset as
its starting data.

### Connection String

Open:

``` text
src/Main.scala
```

Configure the MongoDB connection string in the `connectionString`
variable.

Example format:

``` text
mongodb+srv://<username>:<password>@<cluster-url>/
```

Replace the placeholders with your own MongoDB Atlas credentials.

Then run the application.

> **Security Note:** Never commit your real MongoDB Atlas connection string, username, password, API key, or other credentials to GitHub. Use environment variables such as `MONGODB_URI` for public repositories.
------------------------------------------------------------------------

## 4. Run the Application

Open PowerShell in the project directory:


Run:

``` powershell
scala-cli run src\Main.scala
```

Scala CLI automatically downloads the required MongoDB Scala Driver
dependency specified at the top of `Main.scala`.

The application verifies the MongoDB connection before displaying the
main menu.

------------------------------------------------------------------------

## 5. Main Menu

After a successful connection, the application provides:

``` text
1. Add Restaurant
2. View Restaurants
3. Search / Filter Restaurants
4. Update Restaurant
5. Delete Restaurant
6. Restaurant Analytics
7. Create Indexes
8. View Index Information
9. Exit
```

------------------------------------------------------------------------

## 6. CRUD Operations

### Create

Select:

``` text
1. Add Restaurant
```

Enter the restaurant ID, name, cuisine, borough, ZIP code, and score.

The application can be used to add at least two new restaurant records,
satisfying the assignment requirement.

### Read

Select:

``` text
2. View Restaurants
```

The application displays restaurant records from the MongoDB collection.

### Update

Select:

``` text
4. Update Restaurant
```

Enter an existing restaurant ID.

The application allows multiple fields to be updated.
operation:

-   Restaurant name
-   Cuisine
-   Borough
-   ZIP code
-   Score

### Delete

Select:

``` text
5. Delete Restaurant
```

Enter the restaurant ID that should be removed.

------------------------------------------------------------------------

## 7. Search and Filtering

Select:

``` text
3. Search / Filter Restaurants
```

The application supports:

1.  Search by restaurant name
2.  Search by cuisine
3.  Search by borough
4.  Search by ZIP code
5.  Search by score

These operations demonstrate MongoDB filtering together with Scala
control flow and methods.

------------------------------------------------------------------------

## 8. Indexing

Select:

``` text
7. Create Indexes
```

The application creates:

-   An index on `cuisine`
-   An index on `borough`
-   A compound index on `cuisine` and `borough`

Example indexes:

``` text
cuisine_1
borough_1
cuisine_1_borough_1
```

To view the available indexes, select:

``` text
8. View Index Information
```

The application displays the MongoDB index definitions.

------------------------------------------------------------------------

## 9. Restaurant Analytics

Select:

``` text
6. Restaurant Analytics
```

The analytics menu provides:

``` text
1. Restaurants by Cuisine
2. Restaurants by Borough
3. Average Score by Cuisine
4. Top Rated Restaurants
5. Grade Distribution
6. Back
```

### Aggregation Operations

#### Restaurants by Cuisine

Groups restaurants according to cuisine and counts the number of
restaurants in each cuisine.

#### Restaurants by Borough

Groups restaurants according to borough and counts the number of
restaurants in each borough.

#### Average Score by Cuisine

Unwinds the restaurant grades and calculates the average score for each
cuisine.

#### Top Rated Restaurants

Uses the restaurant grade scores to display the top-rated restaurants.

#### Grade Distribution

Groups restaurant grades and counts their frequency.

------------------------------------------------------------------------

## 10. Scala Concepts Demonstrated

The application demonstrates the required Scala concepts, including:

-   Variables and data types
-   Conditions
-   Functions and methods
-   Collections
-   `map`
-   `filter`
-   `groupBy`
-   `Option`
-   Pattern matching
-   Exception handling
-   Case classes
-   Objects
-   Encapsulation
-   Trait-based abstraction

A `Restaurant` case class is used for representing restaurant data, and
a `RestaurantOperations` trait is used to demonstrate abstraction.

------------------------------------------------------------------------

## 11. Important Notes

-   The application must be connected to MongoDB Atlas before execution.
-   The database name is `sample_restaurants`.
-   The collection name is `restaurants`.
-   The application uses the MongoDB sample restaurant dataset as its
    initial data source.

------------------------------------------------------------------------

## Author

**Saurin Patel**

**Project:** Restaurant Discovery & Analytics System
