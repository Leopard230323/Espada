# ⚔️Espada

[![Gradle](https://github.com/Grimoire-of-Chemist/Espada/actions/workflows/gradle.yml/badge.svg)](https://github.com/Grimoire-of-Chemist/Espada/actions/workflows/gradle.yml)
[![Static Badge](https://img.shields.io/badge/QQ_Group-join_chat-%2300A4FF?link=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3F_wv%3D1027%26k%3DHwExqNhCk2P2aLDnE-t_KYX904nWRMb1%26authKey%3D%252BxC5VQqqVYDqgGZ96jZhO%252F5juOXWCWslitBP3szTW%252Fb%252FoTPO5hY2eiytdOQo2Lop%26noverify%3D0%26group_code%3D534084193)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=GQDkktfBmlgJuEfPVLwL72kX7dx4EYRn&authKey=bMIZnia8bSiT1Va08t3MnD%2F0qVpNEyXAOu1bHYgTm%2BB7eVvwmQPPA%2BJjgLBfD%2FBO&noverify=0&group_code=534084193)
[![Static Badge](https://img.shields.io/badge/link-wilsay.cn-37%2C192%2C158?link=https%3A%2F%2Fwilsay.cn)](https://wilsay.cn)
[![Static Badge](https://img.shields.io/badge/link-996.icu-%23FF4D5B.svg?style=flat-square)](https://996.icu)

**Espada** helps you to create Ktor-based, database-oriented web applications and services with absolute minimum fuss.

## Getting Started

The framework works well with PostgreSQL 16.2 and MySQL 8.0.22. Availability on other databases is not guaranteed and
NoSQLs are not supported.

### Installing Ktorm-generator

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "
  ktorm-generator"</kbd> >
  <kbd>Install</kbd>

- Manually:

  Download the [latest release](https://github.com/aooohan/ktorm-generator/releases/latest) of Ktorm-generator and
  install it manually
  using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

### Generating objects for table schemas

1. Select table schemas from Database Tools view, right click and select "Ktorm-Generator" in the popup menu.

2. Specify the target package and path, and click "OK".

### Implementing Traits

The so-called **traits** are basically on-demand database operation functions in Ktorm SQL DSL. No configuration files,
no annotation, no XML or SQL statements is required. Runtime injections aren't even needed to invoke the methods.

The best practice is to import the **table schemas** (instead of entity interfaces) and rename them properly with the
keyword `as`.

Whether collect and encode the result into JSON in place is recommended, depending on the demands.

```kotlin
import com.wkerein.espada.core.Database.defaultDatabase
import generator.domain.tblstuds as Students
import generator.domain.tblktcontestprize_24 as Winners

fun getWinnerStudents(
    name: String? = null,
) = defaultDatabase.from(Students)
    .leftJoin(
        Winners, Students.sId eq Winners.sId
                and (Winners.prizeType lessEq 3)
    )
    .select(Students)
    .whereWithConditions { conditions ->
        if (name != null)
            conditions += (Students.sName eq name)
    }
    .collect()
    .encoded()
```

The returned type of the `select()` statement is the collection of rows, with primitive data types in each field. You
may want to cast it into `kotlinx.serialization.json.JsonArray` with the method `collect()`, and finally stringify it
with the method extension `encoded()` for JSON elements.

### Implementing Controllers

With the `postRecall` statement, any exception in the lambda expression is intercepted, responding a `400 Bad Request`
to the origin.

The best practice is to make assertions anywhere a normal response may not be expected. You can also implement a custom
interceptor of exceptions.

```kotlin
fun Route.queryWinnerStudents() = postRecall("winner_students") {
    MyLogger.increment(data["uid"]!!.toInt())
    getWinnerStudents(data["name"])
}
```

## License

The framework is distributed under the [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html) license.

## Contribution

Espada is currently in its early versions. Critical data loss, unexpected behaviors and instability might occur. Further
tests are demanded to compatibility with other databases.

If you want to submit a pull request, make sure you have compiled the project with Java11+ and Kotlin 1.9.20 or newer.
DO NOT modify `gradle` files.

To compile the project, simply execute the following command in project root directory:

```
./gradlew clean build
```


