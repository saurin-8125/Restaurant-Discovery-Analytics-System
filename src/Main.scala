//> using scala 2.13.16
//> using dep org.mongodb.scala::mongo-scala-driver:5.13.0

import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Indexes._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.bson.Document

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import java.util.Date

// ============================================================
// RESTAURANT DISCOVERY & ANALYTICS SYSTEM
// Advanced Big Data Analytics - Scala + MongoDB
// ============================================================

// ------------------------------------------------------------
// CASE CLASS
// Demonstrates classes / case classes / data types
// ------------------------------------------------------------

case class Restaurant(
    name: String,
    cuisine: String,
    borough: String,
    zipcode: String,
    score: Int
)

// ------------------------------------------------------------
// TRAIT
// Demonstrates trait / abstraction
// ------------------------------------------------------------

trait RestaurantOperations {

  def addRestaurant(): Unit

  def viewRestaurants(): Unit

  def searchRestaurants(): Unit

  def updateRestaurant(): Unit

  def deleteRestaurant(): Unit

}

// ------------------------------------------------------------
// RESTAURANT MANAGER
// Encapsulation + methods + MongoDB operations
// ------------------------------------------------------------

class RestaurantManager(
    private val collection: MongoCollection[Document]
) extends RestaurantOperations {

  private val timeout: FiniteDuration = 30.seconds

  // ==========================================================
  // HELPER METHODS
  // ==========================================================

  private def waitFor[T](result: Observable[T]): Seq[T] = {
    Await.result(result.toFuture(), timeout)
  }

  private def printSeparator(): Unit = {
    println("-" * 75)
  }

  private def printRestaurant(doc: Document): Unit = {

    val name =
      doc.getString("name")

    val cuisine =
      doc.getString("cuisine")

    val borough =
      doc.getString("borough")

    val restaurantId =
      doc.getString("restaurant_id")

    val zipcode: String =
      doc.get("address")
        .map(_.asDocument().getString("zipcode").getValue)
        .getOrElse("N/A")

    println(
      s"ID       : ${Option(restaurantId).getOrElse("N/A")}\n" +
      s"Name     : ${Option(name).getOrElse("N/A")}\n" +
      s"Cuisine  : ${Option(cuisine).getOrElse("N/A")}\n" +
      s"Borough  : ${Option(borough).getOrElse("N/A")}\n" +
      s"ZIP Code : $zipcode"
    )

    printSeparator()
  }

  private def readInput(message: String): String = {
    print(message)
    scala.io.StdIn.readLine().trim
  }

  // ==========================================================
  // CREATE
  // ==========================================================

  override def addRestaurant(): Unit = {

    println("\n========== ADD RESTAURANT ==========")

    val restaurantId =
      readInput("Enter Restaurant ID: ")

    val name =
      readInput("Enter Restaurant Name: ")

    val cuisine =
      readInput("Enter Cuisine: ")

    val borough =
      readInput("Enter Borough: ")

    val zipcode =
      readInput("Enter ZIP Code: ")

    val scoreInput =
      readInput("Enter Score: ")

    try {

      val score = scoreInput.toInt

      val restaurant = Document(
        "restaurant_id" -> restaurantId,
        "name" -> name,
        "cuisine" -> cuisine,
        "borough" -> borough,
        "address" -> Document(
          "building" -> "1",
          "street" -> "Main Street",
          "zipcode" -> zipcode,
          "coord" -> Seq(0.0, 0.0)
        ),
        "grades" -> Seq(
          Document(
            "date" -> new Date(),
            "grade" -> "A",
            "score" -> score
          )
        )
      )

      val result =
        waitFor(collection.insertOne(restaurant))

      println("\nRestaurant added successfully!")
      println(s"Inserted ID: ${result.head.getInsertedId}")

    } catch {

      case e: NumberFormatException =>
        println("Invalid score. Please enter a numeric value.")

      case e: Exception =>
        println(s"Error while adding restaurant: ${e.getMessage}")
    }
  }

  // ==========================================================
  // READ
  // ==========================================================

  override def viewRestaurants(): Unit = {

    println("\n========== VIEW RESTAURANTS ==========")

    try {

      val results =
        waitFor(
          collection
            .find()
            .limit(10)
        )

      if (results.isEmpty) {

        println("No restaurants found.")

      } else {

        results.foreach(printRestaurant)
      }

    } catch {

      case e: Exception =>
        println(s"Error while reading restaurants: ${e.getMessage}")
    }
  }

  // ==========================================================
  // SEARCH & FILTERING
  // ==========================================================

  override def searchRestaurants(): Unit = {

    println("\n========== SEARCH RESTAURANTS ==========")

    println(
      """
        |1. Search by Restaurant Name
        |2. Search by Cuisine
        |3. Search by Borough
        |4. Search by ZIP Code
        |5. Search by Score
        |6. Back
        |""".stripMargin
    )

    val choice =
      readInput("Enter choice: ")

    choice match {

      // ------------------------------------------------------
      // SEARCH BY NAME
      // ------------------------------------------------------

      case "1" =>

        val name =
          readInput("Enter restaurant name: ")

        try {

          val results =
            waitFor(
              collection.find(
                regex("name", name, "i")
              )
            )

          println(s"\nFound ${results.size} restaurant(s).")

          results.foreach(printRestaurant)

        } catch {

          case e: Exception =>
            println(s"Error: ${e.getMessage}")
        }

      // ------------------------------------------------------
      // SEARCH BY CUISINE
      // ------------------------------------------------------

      case "2" =>

        val cuisine =
          readInput("Enter cuisine: ")

        try {

          val results =
            waitFor(
              collection.find(
                regex("cuisine", cuisine, "i")
              )
            )

          println(s"\nFound ${results.size} restaurant(s).")

          results.foreach(printRestaurant)

        } catch {

          case e: Exception =>
            println(s"Error: ${e.getMessage}")
        }

      // ------------------------------------------------------
      // SEARCH BY BOROUGH
      // ------------------------------------------------------

      case "3" =>

        val borough =
          readInput("Enter borough: ")

        try {

          val results =
            waitFor(
              collection.find(
                regex("borough", borough, "i")
              )
            )

          println(s"\nFound ${results.size} restaurant(s).")

          results.foreach(printRestaurant)

        } catch {

          case e: Exception =>
            println(s"Error: ${e.getMessage}")
        }

      // ------------------------------------------------------
      // SEARCH BY ZIP
      // ------------------------------------------------------

      case "4" =>

        val zipcode =
          readInput("Enter ZIP code: ")

        try {

          val results =
            waitFor(
              collection.find(
                equal("address.zipcode", zipcode)
              )
            )

          println(s"\nFound ${results.size} restaurant(s).")

          results.foreach(printRestaurant)

        } catch {

          case e: Exception =>
            println(s"Error: ${e.getMessage}")
        }

      // ------------------------------------------------------
      // SEARCH BY SCORE
      // ------------------------------------------------------

      case "5" =>

        val scoreInput =
          readInput("Enter minimum score: ")

        try {

          val score =
            scoreInput.toInt

          val results =
            waitFor(
              collection.find(
                gte("grades.score", score)
              )
            )

          println(
            s"\nRestaurants with score >= $score: ${results.size}"
          )

          results.foreach(printRestaurant)

        } catch {

          case _: NumberFormatException =>
            println("Please enter a valid numeric score.")

          case e: Exception =>
            println(s"Error: ${e.getMessage}")
        }

      case "6" =>
        return

      case _ =>
        println("Invalid choice.")

    }
  }

  // ==========================================================
  // UPDATE
  // ==========================================================

  override def updateRestaurant(): Unit = {

    println("\n========== UPDATE RESTAURANT ==========")

    val restaurantId =
      readInput("Enter Restaurant ID to update: ")

    // First check whether the restaurant exists
    try {

      val existing =
        waitFor(
          collection.find(
            equal("restaurant_id", restaurantId)
          ).limit(1)
        )

      if (existing.isEmpty) {

        println("Restaurant not found.")
        return

      }

      println("\nRestaurant found:")
      printRestaurant(existing.head)

      println(
        """
          |What do you want to update?
          |
          |1. Restaurant Name
          |2. Cuisine
          |3. Borough
          |4. ZIP Code
          |5. Score
          |6. Cancel
          |""".stripMargin
      )

      val choice =
        readInput("Enter choice: ")

      choice match {

        // ------------------------------------------------------
        // UPDATE NAME
        // ------------------------------------------------------

        case "1" =>

          val newName =
            readInput("Enter new restaurant name: ")

          if (newName.isEmpty) {

            println("Restaurant name cannot be empty.")

          } else {

            val result =
              waitFor(
                collection.updateOne(
                  equal("restaurant_id", restaurantId),
                  set("name", newName)
                )
              )

            if (result.head.getModifiedCount > 0)
              println("Restaurant name updated successfully.")
            else
              println("No changes were made.")
          }

        // ------------------------------------------------------
        // UPDATE CUISINE
        // ------------------------------------------------------

        case "2" =>

          val newCuisine =
            readInput("Enter new cuisine: ")

          if (newCuisine.isEmpty) {

            println("Cuisine cannot be empty.")

          } else {

            val result =
              waitFor(
                collection.updateOne(
                  equal("restaurant_id", restaurantId),
                  set("cuisine", newCuisine)
                )
              )

            if (result.head.getModifiedCount > 0)
              println("Cuisine updated successfully.")
            else
              println("No changes were made.")
          }

        // ------------------------------------------------------
        // UPDATE BOROUGH
        // ------------------------------------------------------

        case "3" =>

          val newBorough =
            readInput("Enter new borough: ")

          if (newBorough.isEmpty) {

            println("Borough cannot be empty.")

          } else {

            val result =
              waitFor(
                collection.updateOne(
                  equal("restaurant_id", restaurantId),
                  set("borough", newBorough)
                )
              )

            if (result.head.getModifiedCount > 0)
              println("Borough updated successfully.")
            else
              println("No changes were made.")
          }

        // ------------------------------------------------------
        // UPDATE ZIP CODE
        // ------------------------------------------------------

        case "4" =>

          val newZip =
            readInput("Enter new ZIP code: ")

          if (newZip.isEmpty) {

            println("ZIP code cannot be empty.")

          } else {

            val result =
              waitFor(
                collection.updateOne(
                  equal("restaurant_id", restaurantId),
                  set("address.zipcode", newZip)
                )
              )

            if (result.head.getModifiedCount > 0)
              println("ZIP code updated successfully.")
            else
              println("No changes were made.")
          }

        // ------------------------------------------------------
        // UPDATE SCORE
        // ------------------------------------------------------

        case "5" =>

          val scoreInput =
            readInput("Enter new score: ")

          try {

            val newScore =
              scoreInput.toInt

            if (newScore < 0) {

              println("Score cannot be negative.")

            } else {

              val result =
                waitFor(
                  collection.updateOne(
                    equal("restaurant_id", restaurantId),
                    set("grades.0.score", newScore)
                  )
                )

              if (result.head.getModifiedCount > 0)
                println("Score updated successfully.")
              else
                println("No changes were made.")
            }

          } catch {

            case _: NumberFormatException =>
              println("Invalid score. Please enter a number.")
          }

        // ------------------------------------------------------
        // CANCEL
        // ------------------------------------------------------

        case "6" =>

          println("Update cancelled.")

        // ------------------------------------------------------
        // INVALID CHOICE
        // ------------------------------------------------------

        case _ =>

          println("Invalid choice.")

      }

    } catch {

      case e: Exception =>
        println(
          s"Error while updating restaurant: ${e.getMessage}"
        )
    }
  }
  // ==========================================================
  // DELETE
  // ==========================================================

  override def deleteRestaurant(): Unit = {

    println("\n========== DELETE RESTAURANT ==========")

    val restaurantId =
      readInput("Enter Restaurant ID to delete: ")

    try {

      val result =
        waitFor(
          collection.deleteOne(
            equal("restaurant_id", restaurantId)
          )
        )

      if (result.head.getDeletedCount > 0) {

        println("Restaurant deleted successfully.")

      } else {

        println("Restaurant not found.")

      }

    } catch {

      case e: Exception =>
        println(s"Error while deleting: ${e.getMessage}")
    }
  }

  // ==========================================================
  // INDEXING
  // ==========================================================

  def createIndexes(): Unit = {

    println("\n========== CREATE INDEXES ==========")

    try {

      // Index 1: Cuisine
      val cuisineIndex =
        waitFor(
          collection.createIndex(
            Sorts.ascending("cuisine")
          )
        )

      // Index 2: Borough
      val boroughIndex =
        waitFor(
          collection.createIndex(
            Sorts.ascending("borough")
          )
        )

      // Compound Index: Cuisine + Borough
      val compoundIndex =
        waitFor(
          collection.createIndex(
            Indexes.compoundIndex(
              Indexes.ascending("cuisine"),
              Indexes.ascending("borough")
            )
          )
        )

      println(s"Created index: $cuisineIndex")
      println(s"Created index: $boroughIndex")
      println(s"Created compound index: $compoundIndex")

      println("\nAll indexes:")

      val indexes =
        waitFor(
          collection.listIndexes()
        )

      indexes.foreach { index =>
        println(index.toJson())
      }

    } catch {

      case e: Exception =>
        println(s"Error while creating indexes: ${e.getMessage}")
    }
  }

  // ==========================================================
  // ANALYTICS MENU
  // ==========================================================

  def analyticsMenu(): Unit = {

    var running = true

    while (running) {

      println(
        """
          |
          |========== RESTAURANT ANALYTICS ==========
          |
          |1. Restaurants by Cuisine
          |2. Restaurants by Borough
          |3. Average Score by Cuisine
          |4. Top Rated Restaurants
          |5. Grade Distribution
          |6. Scala Collection Analytics
          |7. Back
          |""".stripMargin
      )

      val choice =
        readInput("Enter choice: ")

      choice match {

        case "1" =>
          restaurantsByCuisine()

        case "2" =>
          restaurantsByBorough()

        case "3" =>
          averageScoreByCuisine()

        case "4" =>
          topRatedRestaurants()

        case "5" =>
          gradeDistribution()

        case "6" =>
          collectionAnalytics()

        case "7" =>
          running = false

        case _ =>
          println("Invalid choice.")
      }
    }
  }

  // ==========================================================
  // SCALA COLLECTION ANALYTICS
  // Demonstrates map, filter and groupBy
  // ==========================================================

  def collectionAnalytics(): Unit = {

    println("\n========== SCALA COLLECTION ANALYTICS ==========")

    try {

      val restaurants =
        waitFor(collection.find().limit(1000))

      // filter() - find restaurants serving Italian cuisine
      val italianRestaurants =
        restaurants.filter { doc =>
          Option(doc.getString("cuisine"))
            .exists(_.equalsIgnoreCase("Italian"))
        }

      // map() - extract restaurant names
      val restaurantNames =
        restaurants.map { doc =>
          Option(doc.getString("name")).getOrElse("Unknown")
        }

      // groupBy() - group restaurants by borough
      val restaurantsByBorough =
        restaurants.groupBy { doc =>
          Option(doc.getString("borough")).getOrElse("Unknown")
        }

      println(s"Total restaurants analysed: ${restaurants.size}")

      println(
        s"\nItalian restaurants (filter): ${italianRestaurants.size}"
      )

      println("\nFirst 10 restaurant names (map):")
      restaurantNames.take(10).foreach { name =>
        println(s"- $name")
      }

      println("\nRestaurants by borough (groupBy):")
      restaurantsByBorough.toSeq
        .sortBy { case (_, docs) => -docs.size }
        .foreach { case (borough, docs) =>
          println(f"${borough}%-25s ${docs.size}%d")
        }

    } catch {

      case e: Exception =>
        println(
          s"Collection analytics error: ${e.getMessage}"
        )
    }
  }

  // ==========================================================
  // AGGREGATION 1
  // RESTAURANTS BY CUISINE
  // ==========================================================

  def restaurantsByCuisine(): Unit = {

    println("\n========== RESTAURANTS BY CUISINE ==========")

    try {

      val pipeline = Seq(
        Aggregates.group(
          "$cuisine",
          Accumulators.sum("count", 1)
        ),
        Aggregates.sort(
          Sorts.descending("count")
        )
      )

      val results =
        waitFor(
          collection.aggregate(pipeline)
        )

      results.foreach { doc =>

        val cuisine =
          doc.getString("_id")

        val count =
          doc.getInteger("count")

        println(
          f"${Option(cuisine).getOrElse("Unknown")}%-30s $count%d"
        )
      }

    } catch {

      case e: Exception =>
        println(s"Aggregation error: ${e.getMessage}")
    }
  }

  // ==========================================================
  // AGGREGATION 2
  // RESTAURANTS BY BOROUGH
  // ==========================================================

  def restaurantsByBorough(): Unit = {

    println("\n========== RESTAURANTS BY BOROUGH ==========")

    try {

      val pipeline = Seq(
        Aggregates.group(
          "$borough",
          Accumulators.sum("count", 1)
        ),
        Aggregates.sort(
          Sorts.descending("count")
        )
      )

      val results =
        waitFor(
          collection.aggregate(pipeline)
        )

      results.foreach { doc =>

        val borough =
          doc.getString("_id")

        val count =
          doc.getInteger("count")

        println(
          f"${Option(borough).getOrElse("Unknown")}%-30s $count%d"
        )
      }

    } catch {

      case e: Exception =>
        println(s"Aggregation error: ${e.getMessage}")
    }
  }

  // ==========================================================
  // AGGREGATION 3
  // AVERAGE SCORE BY CUISINE
  // ==========================================================

  def averageScoreByCuisine(): Unit = {

    println("\n========== AVERAGE SCORE BY CUISINE ==========")

    try {

      val pipeline = Seq(

        Aggregates.unwind("$grades"),

        Aggregates.group(
          "$cuisine",
          Accumulators.avg(
            "averageScore",
            "$grades.score"
          )
        ),

        Aggregates.sort(
          Sorts.descending("averageScore")
        )
      )

      val results =
        waitFor(
          collection.aggregate(pipeline)
        )

      results.foreach { doc =>

        val cuisine =
          doc.getString("_id")

        val averageScore =
          doc.getDouble("averageScore")

        println(
          f"${Option(cuisine).getOrElse("Unknown")}%-30s " +
          f"$averageScore%.2f"
        )
      }

    } catch {

      case e: Exception =>
        println(s"Aggregation error: ${e.getMessage}")
    }
  }

  // ==========================================================
  // AGGREGATION 4
  // TOP RATED RESTAURANTS
  // ==========================================================

  def topRatedRestaurants(): Unit = {

    println("\n========== TOP RATED RESTAURANTS ==========")

    try {

      val pipeline = Seq(

        Aggregates.unwind("$grades"),

        Aggregates.sort(
          Sorts.descending("grades.score")
        ),

        Aggregates.limit(10),

        Aggregates.project(
          Projections.fields(
            Projections.include(
              "name",
              "cuisine",
              "borough",
              "grades"
            )
          )
        )
      )

      val results =
        waitFor(
          collection.aggregate(pipeline)
        )

      results.foreach { doc =>

      val name =
        doc.getString("name")

      val cuisine =
        doc.getString("cuisine")

      val borough =
        doc.getString("borough")

      val grades: Int =
      doc.get("grades")
        .map(_.asDocument().getInteger("score"))
        .map(_.intValue())
        .getOrElse(0)

      println(
        f"${Option(name).getOrElse("Unknown")}%-35s " +
        f"${Option(cuisine).getOrElse("Unknown")}%-20s " +
        f"$grades%d"
      )

      println(
        s"   Borough: ${Option(borough).getOrElse("Unknown")}"
      )
    }

    } catch {

      case e: Exception =>
        println(s"Aggregation error: ${e.getMessage}")
    }
  }

  // ==========================================================
  // AGGREGATION 5
  // GRADE DISTRIBUTION
  // ==========================================================

  def gradeDistribution(): Unit = {

    println("\n========== GRADE DISTRIBUTION ==========")

    try {

      val pipeline = Seq(

        Aggregates.unwind("$grades"),

        Aggregates.group(
          "$grades.grade",
          Accumulators.sum("count", 1)
        ),

        Aggregates.sort(
          Sorts.descending("count")
        )
      )

      val results =
        waitFor(
          collection.aggregate(pipeline)
        )

      results.foreach { doc =>

        val grade =
          doc.getString("_id")

        val count =
          doc.getInteger("count")

        println(
          f"Grade ${Option(grade).getOrElse("Unknown")}%-5s $count%d"
        )
      }

    } catch {

      case e: Exception =>
        println(s"Aggregation error: ${e.getMessage}")
    }
  }

  // ==========================================================
  // INDEX INFORMATION
  // ==========================================================

  def showIndexes(): Unit = {

    println("\n========== INDEX INFORMATION ==========")

    try {

      val indexes =
        waitFor(
          collection.listIndexes()
        )

      println(s"Total indexes: ${indexes.size}\n")

      indexes.foreach { index =>

        println(index.toJson())
        printSeparator()
      }

    } catch {

      case e: Exception =>
        println(s"Error while reading indexes: ${e.getMessage}")
    }
  }
}

// ============================================================
// MAIN OBJECT
// ============================================================

object Main {

  def main(args: Array[String]): Unit = {

    println(
      """
        |
        |============================================================
        |       RESTAURANT DISCOVERY & ANALYTICS SYSTEM
        |============================================================
        |       Advanced Big Data Analytics
        |       Scala + MongoDB
        |============================================================
        |""".stripMargin
    )

    // ----------------------------------------------------------
    // MONGODB CONNECTION
    // ----------------------------------------------------------

    /*
     * IMPORTANT:
     * Replace the value below with your MongoDB Atlas
     * connection string.
     *
     * Example:
     *
     * mongodb+srv://username:password@cluster.mongodb.net/
     *
     * Do NOT upload your real password to GitHub.
     */

   val connectionString = "YOUR_MONGODB_ATLAS_CONNECTION_STRING"


    if (
      connectionString ==
        "YOUR_MONGODB_ATLAS_CONNECTION_STRING"
    ) {

      println(
        """
          |ERROR:
          |MongoDB connection string is not configured.
          |
          |Either:
          |1. Replace the placeholder in Main.scala
          |OR
          |2. Set the MONGODB_URI environment variable.
          |""".stripMargin
      )

      return
    }

    var mongoClient: MongoClient = null

    try {

      // --------------------------------------------------------
      // CONNECT TO MONGODB
      // --------------------------------------------------------

      mongoClient =
        MongoClient(connectionString)

      val database =
        mongoClient.getDatabase(
          "sample_restaurants"
        )

      val collection =
        database.getCollection(
          "restaurants"
        )

      // --------------------------------------------------------
      // TEST CONNECTION
      // --------------------------------------------------------

      Await.result(
        database.runCommand(
          Document("ping" -> 1)
        ).toFuture(),
        30.seconds
      )

      println("MongoDB connection successful!")
      println("Database   : sample_restaurants")
      println("Collection : restaurants")

      // --------------------------------------------------------
      // CREATE MANAGER
      // --------------------------------------------------------

      val manager =
        new RestaurantManager(collection)

      // --------------------------------------------------------
      // MAIN MENU
      // --------------------------------------------------------

      var running = true

      while (running) {

        println(
        """
          |
          |============================================================
          |                     MAIN MENU
          |============================================================
          |
          |1. Add Restaurant
          |2. View Restaurants
          |3. Search / Filter Restaurants
          |4. Update Restaurant
          |5. Delete Restaurant
          |6. Restaurant Analytics
          |7. Create Indexes
          |8. View Index Information
          |9. Exit
          |
          |============================================================
          |""".stripMargin
      )

        print("Enter your choice: ")

        val choice =
          scala.io.StdIn.readLine().trim

        choice match {

          case "1" =>
            manager.addRestaurant()

          case "2" =>
            manager.viewRestaurants()

          case "3" =>
            manager.searchRestaurants()

          case "4" =>
            manager.updateRestaurant()

          case "5" =>
            manager.deleteRestaurant()

          case "6" =>
            manager.analyticsMenu()

          case "7" =>
            manager.createIndexes()

          case "8" =>
            manager.showIndexes()

          case "9" =>
            println("\nThank you for using Restaurant Discovery & Analytics System!")
            running = false

          case _ =>
            println("\nInvalid choice. Please enter a valid option.")
        }
      }

    } catch {

      case e: Exception =>

        println(
          s"""
             |
             |============================================================
             |ERROR
             |============================================================
             |${e.getMessage}
             |============================================================
             |""".stripMargin
        )

    } finally {

      if (mongoClient != null) {

        mongoClient.close()

        println("\nMongoDB connection closed.")
      }
    }
  }
}