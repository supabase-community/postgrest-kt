# Kotlin Client for PostgREST

Kotlin Multiplatform client for [PostgREST](https://postgrest.org/)

![Java CI with Gradle](https://img.shields.io/github/workflow/status/supabase/postgrest-kt/Java%20CI%20with%20Gradle?label=BUILD&style=for-the-badge)
![Gradle Package](https://img.shields.io/github/workflow/status/supabase/postgrest-kt/Gradle%20Package?label=PUBLISH&style=for-the-badge)
![Bintray](https://img.shields.io/bintray/v/supabase/supabase/postgrest-kt?style=for-the-badge)

## Installation
Gradle

```
implementation 'io.supabase:postgrest-kt:{version}'
```

## Usage

### Initializing the client

```kotlin
val postgrestClient = PostgrestDefaultClient(
    uri = Url("http://localhost:3111"),
    headersMap = mapOf("Authorization" to "foobar")
)
```

```kotlin
val postgrestClient = PostgrestDefaultClient(
    uri = Url("http://localhost:3111"),
    headers = headersOf()
)
```

You can also pass in a custom schema using the `schema` parameter.

### Entry points

There are two methods to start any call on the PostgresClient.

#### from<T>

The `from<T>` function is used for querying.
It takes a generic parameter to allow for auto-completion when defining column names.
However, you can always use `Any` if you are unsure about the data type.

```kotlin
postgresClient.from<Any>("messages")
    .select()
    .neq("content", "abc")
    .execute()

postgresClient.from<Message>("messages")
    .select()
    .neq(Message::content, "abc")
    .execute()
```

#### rpc<T>

The `rpc<T>` function executes a stored procedure.

```kotlin
postgresClient.rpc("get_status", mapOf("foo" to "bar"))
    .execute()
```

### Executing requests

There are three ways to execute requests and read the response.

#### execute

The `execute` method returns a [PostgrestHttpResponse](src/main/kotlin/io/supabase/postgrest/http/PostgrestHttpResponse.kt).
The HttpResponse contains the status code, body as string and the count (if you request the count).

```kotlin
val response = postgresClient.from<Any>("messages")
    .select()
    .execute()

println(response.body)
println(response.status)
println(response.count)
```

#### executeAndGetSingle<T>

The `executeAndGetSingle<T>` function returns `T`.
The JSON converter is used to convert the response to the DTO.

```kotlin
data class Message(
    val id: Long,
    val content: String
)

val message = postgresClient.from<Message>("messages")
    .select()
    .eq(Message::id, 123L)
    .limit(1)
    .single()
    .executeAndGetSingle<Message>()

println(message.content)
```

You can also use this function to convert your data to a Map, rather than a separate DTO.

```kotlin
val message = postgresClient.from<Message>("messages")
    .select()
    .eq(Message::id, 123L)
    .limit(1)
    .single()
    .executeAndGetSingle<Map<String, Any>>()

println(message["content"])
```

#### executeAndGetList<T>

The `executeAndGetList<T>` function is pretty much the same as the `executeAndGetSingle<T>` function, however,
this functions returns a list of `T`.

```kotlin
val messages = postgresClient.from<Any>("messages")
    .select()
    .executeAndGetList<Message>()

messages.forEach { message ->
    println(messege.content)
}
```

### CRUD

#### Selecting data

The `select` function is used for selecting data.

```kotlin
val response = postgresClient.from<Any>("messages")
    .select(count = Count.EXACT) // will allow accessing data AND count
    .eq("content", "foobar")
    .execute()
```

#### Inserting data

The `insert` function is used for inserting or upserting data.

```kotlin
val message = Message(
    id = 123L,
    content = "foobar"
)

val response = postgresClient.from<Message>("messages")
    .insert(message)
    .execute()

val response = postgresClient.from<Any>("messages")
    .insert(
        value = mapOf("id" to 123L, "content" to "foobar"),
        upsert = true
    )
```

#### Updating data

The `update` function is used for updating data.

```kotlin
val response = postgresClient.from<Any>("messages")
    .update(mapOf("content" to "hello"))
    .eq("id", 123L)
    .execute()
```

#### Deleting data

The `delete` function is used for deleting data.

```kotlin
val response = postgresClient.from<Any>("messages")
    .delete()
    .eq("id", 123L)
    .execute()
```