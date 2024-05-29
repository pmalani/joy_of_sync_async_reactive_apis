# Sync, Async, and Reactive APIs
Look at a simple as well as more involved example. As well as, exception / error handling.

## Simple
- Example with 1 key-value

### Sync
- Blocking
- Straightforward
- [SimpleSyncTest.java](src%2Ftest%2Fjava%2FSimpleSyncTest.java)

### Async
- Using CompletableFuture
- [SimpleAsyncTest.java](src%2Ftest%2Fjava%2FSimpleAsyncTest.java)

### Reactive
- Using reactor framework
- [SimpleReactiveTest.java](src%2Ftest%2Fjava%2FSimpleReactiveTest.java)

## Involved
- Examples with multiple keys and values
- Use `scan`
  - The `scan` command returns a cursor
  - Process the keys
  - Call `scan` again, passing in the cursor
  - Stop, then the cursor `isFinished`
- Examples use `unlink` instead of `del`

### Sync
- [SyncTest.java](src%2Ftest%2Fjava%2FSyncTest.java)

### Async
- `compose` future chain with (recursive) `process` method
- Example of pipelining / batch with `awaitAll`
- [AsyncTest.java](src%2Ftest%2Fjava%2FAsyncTest.java)

### Reactive
- `expand` `Mono` to build the chain
- [ReactiveTest.java](src%2Ftest%2Fjava%2FReactiveTest.java)

## Error / Exception Handling
- Calling `incr` on a value of `string` type results in an exception

### Sync
- catch the exception using `try..catch`
- [ExceptionSyncTest.java](src%2Ftest%2Fjava%2FExceptionSyncTest.java)

### Async
- using `exceptionally`
  - only provides the exception
  - Example: `whenExceptionally`
  - [ExceptionAsyncTest.java](src%2Ftest%2Fjava%2FExceptionAsyncTest.java)
- using `handle`
  - only provides the return value in addition to exception
  - Example: `whenHandle`
  - [ExceptionAsyncTest.java](src%2Ftest%2Fjava%2FExceptionAsyncTest.java)

### Reactive
- using `onErrorResume` if we care about the exception
  - Example: `whenOnErrorResume`
  - [ExceptionReactiveTest.java](src%2Ftest%2Fjava%2FExceptionReactiveTest.java)
- using `onErrorReturn` if we don't care about the exception
  - Example: `whenOnError`
  - [ExceptionReactiveTest.java](src%2Ftest%2Fjava%2FExceptionReactiveTest.java)
- very rich API using `onError...`
- very rich API supporting retry immediately or with delay

## Recommendations
- Use the simpler synchronous API
  - Async and reactive versions can get complicated
  - More difficult to learn and debug
- Use the API type that fits into your project
- Don't use the async or reactive API's in a (completely) blocking way
- Threads
  - Understand use of Executors / Executor Service when using futures
  - Understand use of Schedulers when using reactive API
- Use pipelining
- Use `unlink` instead of `del`
- Use `scan` instead of `keys`
  - Consider tuning the scan limit

## References
- [Lettuce](https://lettuce.io/core/5.3.7.RELEASE/reference/index.html#overview)
- [Redisson even supports RxJava](https://github.com/redisson/redisson/wiki/3.-operations-execution/#32-reactive-way)




